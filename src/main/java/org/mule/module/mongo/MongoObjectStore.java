/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static com.mongodb.client.model.Filters.lt;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.store.ObjectAlreadyExistsException;
import org.mule.api.store.ObjectDoesNotExistException;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.ObjectStoreNotAvaliableException;
import org.mule.api.store.PartitionableExpirableObjectStore;
import org.mule.config.i18n.MessageFactory;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * A PartitionableExpirableObjectStore backed by MongoDB.
 *
 * @author MuleSoft Inc.
 */
public class MongoObjectStore implements PartitionableExpirableObjectStore<Serializable>, MuleContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MongoObjectStore.class);

    private static final String OBJECTSTORE_COLLECTION_PREFIX = "mule.objectstore.";
    private static final String OBJECTSTORE_DEFAULT_PARTITION_NAME = "_default";

    private static final String ID_FIELD = "_id";
    private static final String KEY_FIELD = "key";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String VALUE_FIELD = "value";
    private static final List<String> NO_FIELD_LIST = Collections.emptyList();

    /**
     * The host of the Mongo server
     */
    private String host;

    /**
     * The port of the Mongo server
     */
    private int port;

    /**
     * The database name of the Mongo server
     */
    private String database;

    /**
     * The username used to connect to the Mongo server
     */
    private String username;

    /**
     * The password used to connect to the Mongo server
     */
    private String password;

    private MongoClient mongoClient;

    private MuleContext context;

    @PostConstruct
    public void initialize() throws UnknownHostException, ObjectStoreNotAvaliableException {

        final List<ServerAddress> addresses = ConnectionManagementStrategy.getAddresses(host, port);

        com.mongodb.MongoClient mongo;
        if (StringUtils.isNotBlank(password)) {
            Validate.notNull(username, "Username must not be null if password is set");
            logger.info("Connecting to MongoDB, authenticating as user '{}'", username);

            MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
            mongo = new com.mongodb.MongoClient(addresses, Lists.newArrayList(credential));
        } else {
            logger.info("Connecting to MongoDB, not using authentication");
            mongo = new com.mongodb.MongoClient(addresses);
        }
        mongoClient = new MongoClientImpl(mongo, database);

        // Verify it could connect
        if (mongoClient.isAlive())
            throw new ObjectStoreNotAvaliableException(MessageFactory.createStaticMessage("Cannot access MongoDB"));

        // Verify it can actually read from the database
        mongoClient.listCollections();
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void open() throws ObjectStoreException {
        open(OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public void close() throws ObjectStoreException {
        close(OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public List<Serializable> allKeys() throws ObjectStoreException {
        return allKeys(OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public void expire(final int entryTtl, final int maxEntries) throws ObjectStoreException {
        expire(entryTtl, maxEntries, OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public boolean contains(final Serializable key) throws ObjectStoreException {
        return contains(key, OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public void store(final Serializable key, final Serializable value) throws ObjectStoreException {
        store(key, value, OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public Serializable retrieve(final Serializable key) throws ObjectStoreException {
        return retrieve(key, OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public Serializable remove(final Serializable key) throws ObjectStoreException {
        return remove(key, OBJECTSTORE_DEFAULT_PARTITION_NAME);
    }

    @Override
    public void open(final String partitionName) throws ObjectStoreException {
        // NOOP
    }

    @Override
    public void close(final String partitionName) throws ObjectStoreException {
        // NOOP
    }

    @Override
    public boolean contains(final Serializable key, final String partitionName) throws ObjectStoreException {
        if (key == null) {
            throw new ObjectStoreException(MessageFactory.createStaticMessage("The key is null"));
        }
        try {
            final ObjectId objectId = getObjectIdFromKey(key);
            final Document query = getQueryForObjectId(objectId);
            final String collection = getCollectionName(partitionName);
            return mongoClient.findObjects(collection, query, NO_FIELD_LIST, null, null, null).iterator().hasNext();
        } catch (Exception ex) {
            throw new ObjectStoreNotAvaliableException(MessageFactory.createStaticMessage(ex.getMessage()), ex);
        }
    }

    @Override
    public List<Serializable> allKeys(final String partitionName) throws ObjectStoreException {
        try {
            final String collection = getCollectionName(partitionName);
            final Iterable<Document> keyObjects = mongoClient.findObjects(collection, new Document(), Arrays.asList(KEY_FIELD), null, null, null);

            final List<Serializable> results = new ArrayList<>();
            for (final Document keyObject : keyObjects) {
                results.add((Serializable) SerializationUtils.deserialize((byte[]) keyObject.get(KEY_FIELD)));
            }
            return results;
        } catch (Exception ex) {
            throw new ObjectStoreNotAvaliableException(MessageFactory.createStaticMessage(ex.getMessage()), ex);
        }
    }

    @Override
    public List<String> allPartitions() throws ObjectStoreException {
        try {
            final List<String> results = new ArrayList<String>();

            for (final String collection : mongoClient.listCollections()) {
                if (isPartition(collection)) {
                    results.add(getPartitionName(collection));
                }
            }
            return results;
        } catch (Exception ex) {
            throw new ObjectStoreNotAvaliableException(MessageFactory.createStaticMessage(ex.getMessage()), ex);
        }
    }

    @Override
    public void store(final Serializable key, final Serializable value, final String partitionName) throws ObjectStoreException {
        if (key == null) {
            throw new ObjectStoreException(MessageFactory.createStaticMessage("The key to the ObjectStore cannot be null"));
        }

        final String collection = getCollectionName(partitionName);
        if (!mongoClient.existsCollection(collection)) {
            mongoClient.createCollection(collection, false, null, null);
            mongoClient.createIndex(collection, TIMESTAMP_FIELD, IndexOrder.ASC);
        }

        final byte[] keyAsBytes = org.apache.commons.lang.SerializationUtils.serialize(key);
        final ObjectId objectId = getObjectIdFromKey(keyAsBytes);
        final Document query = getQueryForObjectId(objectId);
        if (mongoClient.findObjects(collection, query, null, null, null, null).iterator().hasNext()) {
            throw new ObjectAlreadyExistsException(MessageFactory.createStaticMessage("Duplicated key %s", key));
        }

        try {
            final Document document = new Document();
            document.put(ID_FIELD, objectId);
            document.put(TIMESTAMP_FIELD, System.currentTimeMillis());
            document.put(KEY_FIELD, keyAsBytes);
            document.put(VALUE_FIELD, SerializationUtils.serialize(value));
            mongoClient.updateObjects(collection, query, document, false);
        } catch (Exception ex) {
            throw new ObjectStoreNotAvaliableException(MessageFactory.createStaticMessage(ex.getMessage()), ex);
        }
    }

    @Override
    public Serializable retrieve(final Serializable key, final String partitionName) throws ObjectStoreException {
        if (key == null) {
            throw new ObjectStoreException(MessageFactory.createStaticMessage("The key to the ObjectStore cannot be null"));
        }

        final String collection = getCollectionName(partitionName);
        final ObjectId objectId = getObjectIdFromKey(key);
        final Document query = getQueryForObjectId(objectId);
        if (!mongoClient.findObjects(collection, query, null, null, null, null).iterator().hasNext()) {
            throw new ObjectDoesNotExistException(MessageFactory.createStaticMessage("Couldn't find key '%s' in the ObjectStore", key));
        }
        return retrieveSerializedObject(collection, query);
    }

    @Override
    public Serializable remove(final Serializable key, final String partitionName) throws ObjectStoreException {
        if (key == null) {
            throw new ObjectStoreException(MessageFactory.createStaticMessage("The key to the ObjectStore cannot be null"));
        }

        final String collection = getCollectionName(partitionName);
        final ObjectId objectId = getObjectIdFromKey(key);
        final Document query = getQueryForObjectId(objectId);
        if (!mongoClient.findObjects(collection, query, null, null, null, null).iterator().hasNext()) {
            throw new ObjectDoesNotExistException(MessageFactory.createStaticMessage("Couldn't find key '%s' in the ObjectStore", key));
        }

        final Serializable result = retrieveSerializedObject(collection, query);
        mongoClient.removeObjects(collection, query);
        return result;
    }

    @Override
    public void disposePartition(final String partitionName) throws ObjectStoreException {
        try {
            final String collection = getCollectionName(partitionName);
            mongoClient.dropCollection(collection);
        } catch (Exception ex) {
            throw new ObjectStoreException(ex);
        }
    }

    @Override
    public void clear(String s) throws ObjectStoreException {
        // NOOP
    }

    @Override
    public void clear() throws ObjectStoreException {
        // NOOP
    }

    @Override
    public void expire(final int entryTtl, final int ignoredMaxEntries, final String partitionName) throws ObjectStoreException {
        try {
            final String collection = getCollectionName(partitionName);
            final long expireAt = System.currentTimeMillis() - entryTtl;
            final Bson query = lt(TIMESTAMP_FIELD, expireAt);
            mongoClient.removeObjects(collection, query);
        } catch (Exception ex) {
            throw new ObjectStoreException(ex);
        }
    }

    // --------- Java Accessor Festival ---------

    public String getDatabase() {
        return database;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    // --------- Support Methods ---------

    private String getCollectionName(final String partitionName) {
        return OBJECTSTORE_COLLECTION_PREFIX + partitionName;
    }

    private String getPartitionName(final String collectionName) {
        return StringUtils.substringAfter(collectionName, OBJECTSTORE_COLLECTION_PREFIX);
    }

    private boolean isPartition(final String collectionName) {
        return StringUtils.startsWith(collectionName, OBJECTSTORE_COLLECTION_PREFIX);
    }

    private ObjectId getObjectIdFromKey(final Serializable key) {
        final byte[] keyAsBytes = SerializationUtils.serialize(key);
        return getObjectIdFromKey(keyAsBytes);
    }

    private ObjectId getObjectIdFromKey(final byte[] keyAsBytes) {
        // hash the key and combine the resulting 16 bytes down to 12
        final ObjectId objectId;
        final byte[] md5Digest = DigestUtils.md5Digest(keyAsBytes);
        final byte[] id = ArrayUtils.subarray(md5Digest, 0, 12);
        for (int i = 0; i < 4; i++) {
            id[i * 3] = (byte) (id[i * 3] ^ md5Digest[12 + i]);
        }
        objectId = new ObjectId(id);
        return objectId;
    }

    private Document getQueryForObjectId(final ObjectId objectId) {
        return new Document(ID_FIELD, objectId);
    }

    private Serializable retrieveSerializedObject(final String collection, final Document query) throws ObjectDoesNotExistException {
        final Iterator<Document> iterator = mongoClient.findObjects(collection, query, Arrays.asList(VALUE_FIELD), null, null, null).iterator();

        if (!iterator.hasNext()) {
            throw new ObjectDoesNotExistException();
        }

        final Document document = iterator.next();

        return (Serializable) SerializationUtils.deserialize((byte[]) document.get(VALUE_FIELD), context);
    }

    @Override
    public void setMuleContext(MuleContext context) {
        this.context = context;
    }
}
