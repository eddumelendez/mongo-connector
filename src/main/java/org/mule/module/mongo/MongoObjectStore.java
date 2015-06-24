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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.store.ObjectDoesNotExistException;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.PartitionableExpirableObjectStore;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.util.SerializationUtils;
import org.springframework.util.DigestUtils;

//import com.mongodb.DB;

/**
 * A PartitionableExpirableObjectStore backed by MongoDB.
 *
 * @author MuleSoft Inc.
 */
public class MongoObjectStore implements PartitionableExpirableObjectStore<Serializable>, MuleContextAware {

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
    public void initialize() throws UnknownHostException {
        // try (final com.mongodb.MongoClient client = new com.mongodb.MongoClient(host, port)) {
        // final DB db = client.getDB(database);
        // if (StringUtils.isNotEmpty(password))
        // {
        // Validate.notEmpty(username, "Username must not be empty if password is set");
        // db.authenticate(username, password.toCharArray());
        // }
        // }
        com.mongodb.MongoClient mongo = new com.mongodb.MongoClient(host, port);
        mongoClient = new MongoClientImpl(mongo, database);
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
        final ObjectId objectId = getObjectIdFromKey(key);
        final Document query = getQueryForObjectId(objectId);
        final String collection = getCollectionName(partitionName);
        return mongoClient.findObjects(collection, query, NO_FIELD_LIST, null, null, null).iterator().hasNext();
    }

    @Override
    public List<Serializable> allKeys(final String partitionName) throws ObjectStoreException {
        final String collection = getCollectionName(partitionName);
        final Iterable<Document> keyObjects = mongoClient.findObjects(collection, new Document(), Arrays.asList(KEY_FIELD), null, null, null);

        final List<Serializable> results = new ArrayList<Serializable>();
        for (final Document keyObject : keyObjects) {
            results.add((Serializable) SerializationUtils.deserialize((byte[]) keyObject.get(KEY_FIELD)));
        }
        return results;
    }

    @Override
    public List<String> allPartitions() throws ObjectStoreException {
        final List<String> results = new ArrayList<String>();

        for (final String collection : mongoClient.listCollections()) {
            if (isPartition(collection)) {
                results.add(getPartitionName(collection));
            }
        }

        return results;
    }

    @Override
    public void store(final Serializable key, final Serializable value, final String partitionName) throws ObjectStoreException {
        final String collection = getCollectionName(partitionName);
        if (!mongoClient.existsCollection(collection)) {
            mongoClient.createCollection(collection, false, null, null);
            mongoClient.createIndex(collection, TIMESTAMP_FIELD, IndexOrder.ASC);
        }

        final byte[] keyAsBytes = org.apache.commons.lang.SerializationUtils.serialize(key);
        final ObjectId objectId = getObjectIdFromKey(keyAsBytes);
        final Document query = getQueryForObjectId(objectId);
        final Document document = new Document();
        document.put(ID_FIELD, objectId);
        document.put(TIMESTAMP_FIELD, System.currentTimeMillis());
        document.put(KEY_FIELD, keyAsBytes);
        document.put(VALUE_FIELD, SerializationUtils.serialize(value));
        mongoClient.updateObjects(collection, query, document, false);
    }

    @Override
    public Serializable retrieve(final Serializable key, final String partitionName) throws ObjectStoreException {
        final String collection = getCollectionName(partitionName);
        final ObjectId objectId = getObjectIdFromKey(key);
        final Document query = getQueryForObjectId(objectId);
        return retrieveSerializedObject(collection, query);
    }

    @Override
    public Serializable remove(final Serializable key, final String partitionName) throws ObjectStoreException {
        final String collection = getCollectionName(partitionName);
        final ObjectId objectId = getObjectIdFromKey(key);
        final Document query = getQueryForObjectId(objectId);

        final Serializable result = retrieveSerializedObject(collection, query);
        mongoClient.removeObjects(collection, query);
        return result;
    }

    @Override
    public void disposePartition(final String partitionName) throws ObjectStoreException {
        final String collection = getCollectionName(partitionName);
        mongoClient.dropCollection(collection);
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
        final String collection = getCollectionName(partitionName);
        final long expireAt = System.currentTimeMillis() - entryTtl;
        final Bson query = lt(TIMESTAMP_FIELD, expireAt);
        mongoClient.removeObjects(collection, query);
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
