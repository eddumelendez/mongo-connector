/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.mule.module.mongo.api.DBObjects.adapt;
import static org.mule.module.mongo.api.DBObjects.adaptToDbObject;
import static org.mule.module.mongo.api.DBObjects.from;
import static org.mule.module.mongo.api.DBObjects.fromCommand;
import static org.mule.module.mongo.api.DBObjects.fromFunction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Mime;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.ReconnectOn;
import org.mule.api.annotations.Transformer;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.Payload;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.tools.BackupConstants;
import org.mule.module.mongo.tools.IncrementalMongoDump;
import org.mule.module.mongo.tools.MongoDump;
import org.mule.module.mongo.tools.MongoRestore;
import org.mule.transformer.types.MimeTypes;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONSerializers;

/**
 * MongoDB is an open source, high-performance, schema-free, document-oriented database that manages collections of BSON documents.
 *
 * @author MuleSoft, inc.
 */
@Connector(name = "mongo", schemaVersion = "2.0", friendlyName = "Mongo DB", minMuleVersion = "3.6")
public class MongoCloudConnector {

    private static final String CAPPED_DEFAULT_VALUE = "false";
    private static final String WRITE_CONCERN_DEFAULT_VALUE = "DATABASE_DEFAULT";
    private static final String BACKUP_THREADS = "5";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "dump";

    @ConnectionStrategy
    private ConnectionManagementStrategy strategy;

    public ConnectionManagementStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ConnectionManagementStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Adds a new user for this db
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:add-user}
     * </pre>
     *
     * @param newUsername
     *            Name of the user
     * @param newPassword
     *            Password that will be used for authentication
     * @return Result of the operation
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public WriteResult addUser(final String newUsername, final String newPassword) {
        return strategy.getClient().addUser(newUsername, newPassword);
    }

    /**
     * Drop the current database
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-database}
     * </pre>
     *
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void dropDatabase() {
        strategy.getClient().dropDatabase();
    }

    /**
     * Lists names of collections available at this database
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-collections}
     * </pre>
     *
     * @return the list of names of collections available at this database
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Collection<String> listCollections() {
        return strategy.getClient().listCollections();
    }

    /**
     * Answers if a collection exists given its name
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:exists-collection}
     * </pre>
     *
     * @param collection
     *            the name of the collection
     * @return if the collection exists
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public boolean existsCollection(final String collection) {
        return strategy.getClient().existsCollection(collection);
    }

    /**
     * Deletes a collection and all the objects it contains. If the collection does not exist, does nothing.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-collection}
     * </pre>
     *
     * @param collection
     *            the name of the collection to drop
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void dropCollection(final String collection) {
        strategy.getClient().dropCollection(collection);
    }

    /**
     * Creates a new collection. If the collection already exists, a MongoException will be thrown.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-collection}
     * </pre>
     *
     * @param collection
     *            the name of the collection to create
     * @param capped
     *            if the collection will be capped
     * @param maxObjects
     *            the maximum number of documents the new collection is able to contain
     * @param size
     *            the maximum size of the new collection
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void createCollection(final String collection, @Default(CAPPED_DEFAULT_VALUE) final boolean capped, @Optional final Integer maxObjects, @Optional final Integer size) {
        strategy.getClient().createCollection(collection, capped, maxObjects, size);
    }

    /**
     * Inserts an object in a collection, setting its id if necessary.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:insert-object}
     * </pre>
     *
     * @param collection
     *            the name of the collection where to insert the given object
     * @param dbObject
     *            a {@link DBObject} instance.
     * @param writeConcern
     *            the optional write concern of insertion
     * @return the id that was just insterted
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public String insertObject(final String collection, @Default("#[payload]") final Document document) {
        return strategy.getClient().insertObject(collection, document);
    }

    /**
     * Inserts an object in a collection, setting its id if necessary.
     * <p/>
     * A shallow conversion into DBObject is performed - that is, no conversion is performed to its values.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:insert-object-from-map}
     * </pre>
     *
     * @param collection
     *            the name of the collection where to insert the given object
     * @param elementAttributes
     *            alternative way of specifying the element as a literal Map inside a Mule Flow
     * @param writeConcern
     *            the optional write concern of insertion
     * @return the id that was just insterted
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public String insertObjectFromMap(final String collection, @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes,
            @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        return strategy.getClient().insertObject(collection, adapt(elementAttributes));
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the first document matching it will be updated. Otherwise, all the documents matching
     * it will be updated.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects}
     * </pre>
     *
     * @param collection
     *            the name of the collection to update
     * @param query
     *            the {@link Document} query object used to detect the element to update. If the object Id is an instance of ObjectId you need to specify the value pair as map with
     *            the following structure: { "_id" : "ObjectId(OBJECT_ID_VALUE)"}
     * @param element
     *            the {@link Document} mandatory object that will replace that one which matches the query.
     * @param multi
     *            if all or just the first object matching the query will be updated
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void updateObjects(final String collection, final Document query, @Default("#[payload]") final Document element, @Default("true") final boolean multi) {
        strategy.getClient().updateObjects(collection, query, element, multi);
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the first document matching it will be updated. Otherwise, all the documents matching
     * it will be updated.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-using-query-map}
     * </pre>
     *
     * @param collection
     *            the name of the collection to update
     * @param queryAttributes
     *            the query object used to detect the element to update. If the object Id is an instance of ObjectId you need to specify the value pair as map with the following
     *            structure: { "_id" : "ObjectId(OBJECT_ID_VALUE)"}
     * @param element
     *            the {@link Document} mandatory object that will replace that one which matches the query.
     * @param upsert
     *            if the database should create the element if it does not exist
     * @param multi
     *            if all or just the first object matching the query will be updated
     * @param writeConcern
     *            the write concern used to update
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void updateObjectsUsingQueryMap(final String collection, final Map<String, Object> queryAttributes, final Document element,
            @Default(CAPPED_DEFAULT_VALUE) final boolean upsert, @Default("true") final boolean multi, @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        strategy.getClient().updateObjects(collection, adapt(queryAttributes), element, multi);
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the first document matching it will be updated. Otherwise, all the documents matching
     * it will be updated.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-using-map}
     * </pre>
     *
     * @param collection
     *            the name of the collection to update
     * @param queryAttributes
     *            the query object used to detect the element to update.
     * @param elementAttributes
     *            the mandatory object that will replace that one which matches the query.
     * @param upsert
     *            if the database should create the element if it does not exist
     * @param multi
     *            if all or just the first object matching the query will be updated
     * @param writeConcern
     *            the write concern used to update
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void updateObjectsUsingMap(final String collection, @Placement(group = "Query Attributes") final Map<String, Object> queryAttributes,
            @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes, @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
            @Default("true") final boolean multi, @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        strategy.getClient().updateObjects(collection, adapt(queryAttributes), adapt(elementAttributes), multi);
    }

    /**
     * Update objects using a mongo function
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-by-function}
     * </pre>
     *
     * @param collection
     *            the name of the collection to update
     * @param function
     *            the function used to execute the update
     * @param query
     *            the {@link Document} query object used to detect the element to update.
     * @param element
     *            the {@link Document} mandatory object that will replace that one which matches the query.
     * @param upsert
     *            if the database should create the element if it does not exist
     * @param multi
     *            if all or just the first object matching the query will be updated
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void updateObjectsByFunction(final String collection, final String function, final Document query, final Document element,
            @Default(CAPPED_DEFAULT_VALUE) final boolean upsert, @Default(value = "true") final boolean multi) {
        final Document functionDocument = fromFunction(function, element);

        strategy.getClient().updateObjects(collection, query, functionDocument, multi);
    }

    /**
     * Update objects using a mongo function
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-by-function-using-map}
     * </pre>
     *
     * @param collection
     *            the name of the collection to update
     * @param function
     *            the function used to execute the update
     * @param queryAttributes
     *            the query object used to detect the element to update.
     * @param elementAttributes
     *            the mandatory object that will replace that one which matches the query.
     * @param upsert
     *            if the database should create the element if it does not exist
     * @param multi
     *            if all or just the first object matching the query will be updated
     * @param writeConcern
     *            the write concern used to update
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void updateObjectsByFunctionUsingMap(final String collection, final String function, final Map<String, Object> queryAttributes,
            final Map<String, Object> elementAttributes, @Default(CAPPED_DEFAULT_VALUE) final boolean upsert, @Default(value = "true") final boolean multi,
            @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        final Document functionDocument = fromFunction(function, adapt(elementAttributes));

        strategy.getClient().updateObjects(collection, adapt(queryAttributes), functionDocument, multi);
    }

    /**
     * Inserts or updates an object based on its object _id.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:save-object}
     * </pre>
     *
     * @param collection
     *            the collection where to insert the object
     * @param document
     *            the mandatory {@link Document} object to insert.
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void saveObject(final String collection, @Default("#[payload]") final Document document) {
        strategy.getClient().saveObject(collection, from(document));
    }

    /**
     * Inserts or updates an object based on its object _id.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:save-object-from-map}
     * </pre>
     *
     * @param collection
     *            the collection where to insert the object
     * @param elementAttributes
     *            the mandatory object to insert.
     * @param writeConcern
     *            the write concern used to persist the object
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void saveObjectFromMap(final String collection, @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes,
            @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        strategy.getClient().saveObject(collection, adapt(elementAttributes));
    }

    /**
     * Removes all the objects that match the a given optional query. If query is not specified, all objects are removed. However, please notice that this is normally less
     * performant that dropping the collection and creating it and its indices again
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-objects}
     * </pre>
     *
     * @param collection
     *            the collection whose elements will be removed
     * @param query
     *            the optional {@link Document} query object. Objects that match it will be removed.
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void removeObjects(final String collection, @Default("#[payload]") final Document query) {
        strategy.getClient().removeObjects(collection, query);
    }

    /**
     * Removes all the objects that match the a given optional query. If query is not specified, all objects are removed. However, please notice that this is normally less
     * performant that dropping the collection and creating it and its indices again
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-objects-using-query-map}
     * </pre>
     *
     * @param collection
     *            the collection whose elements will be removed
     * @param queryAttributes
     *            the query object. Objects that match it will be removed.
     * @param writeConcern
     *            the write concern used to remove the object
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void removeObjectsUsingQueryMap(final String collection, @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes,
            @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern) {
        strategy.getClient().removeObjects(collection, adapt(queryAttributes));
    }

    /**
     * Transforms a collection into a collection of aggregated groups, by applying a supplied element-mapping function to each element, that transforms each one into a key-value
     * pair, grouping the resulting pairs by key, and finally reducing values in each group applying a suppling 'reduce' function.
     * <p/>
     * Each supplied function is coded in JavaScript.
     * <p/>
     * Note that the correct way of writing those functions may not be obvious; please consult MongoDB documentation for writing them.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:map-reduce-objects}
     * </pre>
     *
     * @param collection
     *            the name of the collection to map and reduce
     * @param mapFunction
     *            a JavaScript encoded mapping function
     * @param reduceFunction
     *            a JavaScript encoded reducing function
     * @param outputCollection
     *            the name of the output collection to write the results, replacing previous collection if existed, mandatory when results may be larger than 16MB. If
     *            outputCollection is unspecified, the computation is performed in-memory and not persisted.
     * @return an iterable that retrieves the resulting collection of {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<Document> mapReduceObjects(final String collection, final String mapFunction, final String reduceFunction, @Optional final String outputCollection) {
        return strategy.getClient().mapReduceObjects(collection, mapFunction, reduceFunction, outputCollection);
    }

    /**
     * Counts the number of objects that match the given query. If no query is passed, returns the number of elements in the collection
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:count-objects}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param query
     *            the optional {@link Document} query for counting objects. Only objects matching it will be counted. If unspecified, all objects are counted.
     * @return the amount of objects that matches the query
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public long countObjects(final String collection, @Default("#[payload]") final Bson query) {
        return strategy.getClient().countObjects(collection, query);
    }

    /**
     * Counts the number of objects that match the given query. If no query is passed, returns the number of elements in the collection
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:count-objects-using-query-map}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param queryAttributes
     *            the optional query for counting objects. Only objects matching it will be counted. If unspecified, all objects are counted.
     * @return the amount of objects that matches the query
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public long countObjectsUsingQueryMap(final String collection, @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes) {
        return strategy.getClient().countObjects(collection, adapt(queryAttributes));
    }

    /**
     * Finds all objects that match a given query. If no query is specified, all objects of the collection are retrieved. If no fields object is specified, all fields are
     * retrieved.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-objects}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param query
     *            the optional {@link Document} query object. If unspecified, all documents are returned.
     * @param fields
     *            alternative way of passing fields as a literal List
     * @param numToSkip
     *            number of objects skip (offset)
     * @param limit
     *            limit of objects to return
     * @param sortBy
     *            indicates the {@link Document} used to sort the results
     * @return an iterable of {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<Document> findObjects(final String collection, @Default("") final Document query, @Placement(group = "Fields") @Optional final List<String> fields,
            @Optional final Integer numToSkip, @Optional final Integer limit, @Optional Document sortBy) {
        return strategy.getClient().findObjects(collection, query, fields, numToSkip, limit, sortBy);
    }

    /**
     * Finds all objects that match a given query. If no query is specified, all objects of the collection are retrieved. If no fields object is specified, all fields are
     * retrieved.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-objects-using-query-map}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param queryAttributes
     *            the optional query object. If unspecified, all documents are returned.
     * @param fields
     *            alternative way of passing fields as a literal List
     * @param numToSkip
     *            number of objects skip (offset)
     * @param limit
     *            limit of objects to return
     * @param sortBy
     *            indicates the {@link Document} used to sort the results
     * @return an iterable of {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<Document> findObjectsUsingQueryMap(final String collection, @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes,
            @Placement(group = "Fields") @Optional final List<String> fields, @Optional final Integer numToSkip, @Optional final Integer limit, @Optional Document sortBy) {
        return strategy.getClient().findObjects(collection, adapt(queryAttributes), fields, numToSkip, limit, sortBy);
    }

    /**
     * Finds the first object that matches a given query. Throws a {@link MongoException} if no one matches the given query
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-object}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param query
     *            the mandatory {@link Document} query object that the returned object matches.
     * @param fields
     *            alternative way of passing fields as a literal List
     * @param failOnNotFound
     *            Flag to specify if an exception will be thrown when no object is found. For backward compatibility the default value is true.
     * @return a {@link Document} that matches the query. If nothing matches and the failOnNotFound is set to false, null will be returned
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Document findOneObject(final String collection, @Default("#[payload]") final Document query, @Placement(group = "Fields") @Optional final List<String> fields,
            @Default("true") Boolean failOnNotFound) {
        return strategy.getClient().findOneObject(collection, query, fields, failOnNotFound);

    }

    /**
     * Finds the first object that matches a given query. Throws a {@link MongoException} if no one matches the given query
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-object-using-query-map}
     * </pre>
     *
     * @param collection
     *            the target collection
     * @param queryAttributes
     *            the mandatory query object that the returned object matches.
     * @param fields
     *            alternative way of passing fields as a literal List
     * @param failOnNotFound
     *            Flag to specify if an exception will be thrown when no object is found. For backward compatibility the default value is true.
     * @return a {@link Document} that matches the query. If nothing matches and the failOnNotFound is set to false, null will be returned
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Document findOneObjectUsingQueryMap(final String collection, @Placement(group = "Query Attributes") final Map<String, Object> queryAttributes,
            @Placement(group = "Fields") @Optional final List<String> fields, @Default("true") Boolean failOnNotFound) {
        return strategy.getClient().findOneObject(collection, adapt(queryAttributes), fields, failOnNotFound);

    }

    /**
     * Creates a new index
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-index}
     * </pre>
     *
     * @param collection
     *            the name of the collection where the index will be created
     * @param field
     *            the name of the field which will be indexed
     * @param order
     *            the indexing order
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void createIndex(final String collection, final String field, @Default("ASC") final IndexOrder order) {
        strategy.getClient().createIndex(collection, field, order);
    }

    /**
     * Drops an existing index
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-index}
     * </pre>
     *
     * @param collection
     *            the name of the collection where the index is
     * @param index
     *            the name of the index to drop
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void dropIndex(final String collection, final String index) {
        strategy.getClient().dropIndex(collection, index);
    }

    /**
     * List existent indices in a collection
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-indices}
     * </pre>
     *
     * @param collection
     *            the name of the collection
     * @return a collection of {@link Document} with indices information
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Collection<Document> listIndices(final String collection) {
        return strategy.getClient().listIndices(collection);
    }

    /**
     * Creates a new GridFSFile in the database, saving the given content, filename, contentType, and extraData, and answers it.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-file-from-payload}
     * </pre>
     *
     * @param payload
     *            the mandatory content of the new gridfs file. It may be a java.io.File, a byte[] or an InputStream.
     * @param filename
     *            the mandatory name of new file.
     * @param contentType
     *            the optional content type of the new file
     * @param metadata
     *            the optional {@link Document} metadata of the new content type
     * @return the new GridFSFile {@link Document}
     * @throws IOException
     *             IOException
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public DBObject createFileFromPayload(@Payload final Object payload, final String filename, @Optional final String contentType, @Optional final DBObject metadata)
            throws IOException {
        try (InputStream stream = toStream(payload)) {
            return strategy.getClient().createFile(stream, filename, contentType, metadata);
        }
    }

    private InputStream toStream(final Object content) throws FileNotFoundException {
        if (content instanceof InputStream) {
            return (InputStream) content;
        }
        if (content instanceof byte[]) {
            return new ByteArrayInputStream((byte[]) content);
        }
        if (content instanceof File) {
            return new FileInputStream((File) content);
        }
        throw new IllegalArgumentException("Content " + content + " is not supported");
    }

    /**
     * Lists all the files that match the given query
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-files}
     * </pre>
     *
     * @param query
     *            a {@link Document} query the optional query
     * @return a {@link Document} files iterable
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<DBObject> findFiles(@Default("#[payload]") final DBObject query) {
        return strategy.getClient().findFiles(query);
    }

    /**
     * Lists all the files that match the given query
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-files-using-query-map}
     * </pre>
     *
     * @param queryAttributes
     *            the optional query attributes
     * @return a {@link Document} files iterable
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<DBObject> findFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes) {
        return strategy.getClient().findFiles(adaptToDbObject(queryAttributes));
    }

    /**
     * Answers the first file that matches the given query. If no object matches it, a MongoException is thrown.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-file}
     * </pre>
     *
     * @param query
     *            the {@link Document} mandatory query
     * @return a {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public DBObject findOneFile(final DBObject query) {
        return strategy.getClient().findOneFile(query);
    }

    /**
     * Answers the first file that matches the given query. If no object matches it, a MongoException is thrown.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-file-using-query-map}
     * </pre>
     *
     * @param queryAttributes
     *            the mandatory query
     * @return a {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public DBObject findOneFileUsingQueryMap(@Placement(group = "Query Attributes") final Map<String, Object> queryAttributes) {
        return strategy.getClient().findOneFile(adaptToDbObject(queryAttributes));
    }

    /**
     * Answers an inputstream to the contents of the first file that matches the given query. If no object matches it, a MongoException is thrown.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:get-file-content}
     * </pre>
     *
     * @param query
     *            the {@link Document} mandatory query
     * @return an InputStream to the file contents
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public InputStream getFileContent(@Default("#[payload]") final DBObject query) {
        return strategy.getClient().getFileContent(query);
    }

    /**
     * Answers an inputstream to the contents of the first file that matches the given queryAttributes. If no object matches it, a MongoException is thrown.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:get-file-content-using-query-map}
     * </pre>
     *
     * @param queryAttributes
     *            the mandatory query attributes
     * @return an InputStream to the file contents
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public InputStream getFileContentUsingQueryMap(@Placement(group = "Query Attributes") final Map<String, Object> queryAttributes) {
        return strategy.getClient().getFileContent(adaptToDbObject(queryAttributes));
    }

    /**
     * Lists all the files that match the given query, sorting them by filename. If no query is specified, all files are listed.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-files}
     * </pre>
     *
     * @param query
     *            the {@link Document} optional query
     * @return an iterable of {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<DBObject> listFiles(@Default("#[payload]") final DBObject query) {
        return strategy.getClient().listFiles(query);
    }

    /**
     * Lists all the files that match the given query, sorting them by filename. If no query is specified, all files are listed.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-files-using-query-map}
     * </pre>
     *
     * @param queryAttributes
     *            the optional query
     * @return an iterable of {@link Document}
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Iterable<DBObject> listFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes) {
        return strategy.getClient().listFiles(adaptToDbObject(queryAttributes));
    }

    /**
     * Removes all the files that match the given query. If no query is specified, all files are removed
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files}
     * </pre>
     *
     * @param query
     *            the {@link Document} optional query
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void removeFiles(@Default("#[payload]") final DBObject query) {
        strategy.getClient().removeFiles(query);
    }

    /**
     * Removes all the files that match the given query. If no query is specified, all files are removed
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files-using-query-map}
     * </pre>
     *
     * @param queryAttributes
     *            the optional query
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void removeFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes) {
        strategy.getClient().removeFiles(adaptToDbObject(queryAttributes));
    }

    /**
     * Executes a command on the database
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files}
     * </pre>
     *
     * @param commandName
     *            The command to execute on the database
     * @param commandValue
     *            The value for the command
     * @return The result of the command
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public Document executeCommand(final String commandName, @Optional final String commandValue) {
        final Document document = fromCommand(commandName, commandValue);
        return strategy.getClient().executeCommand(document);
    }

    /**
     * Executes a dump of the database to the specified output directory. If no output directory is provided then the default /dump directory is used.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:dump}
     * </pre>
     *
     * @param outputDirectory
     *            output directory path, if no output directory is provided the default /dump directory is assumed
     * @param outputName
     *            output file name, if it's not specified the database name is used
     * @param zip
     *            whether to zip the created dump file or not
     * @param oplog
     *            point in time backup (requires an oplog)
     * @param threads
     *            amount of threads to execute the dump
     * @throws IOException
     *             if an error occurs during the dump
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void dump(@Default(DEFAULT_OUTPUT_DIRECTORY) final String outputDirectory, @Optional final String outputName, @Default("false") final boolean zip,
            @Default("false") final boolean oplog, @Default(BACKUP_THREADS) final int threads) throws IOException {
        final MongoDump mongoDump = new MongoDump(strategy.getClient());
        mongoDump.setZip(zip);
        if (oplog) {
            mongoDump.setOplog(oplog);
            mongoDump.addDB(strategy.getMongo().getDB(BackupConstants.ADMIN_DB));
            mongoDump.addDB(strategy.getMongo().getDB(BackupConstants.LOCAL_DB));
        }
        mongoDump.dump(outputDirectory, strategy.getDatabase(), outputName != null ? outputName : strategy.getDatabase(), threads);
    }

    /**
     * Executes an incremental dump of the database
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:incremental-dump}
     * </pre>
     *
     * @param outputDirectory
     *            output directory path, if no output directory is provided the default /dump directory is assumed
     * @param incrementalTimestampFile
     *            file that keeps track of the last timestamp processed, if no file is provided one is created on the output directory
     * @throws IOException
     *             if an error occurs during the incremental dump
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void incrementalDump(@Default(DEFAULT_OUTPUT_DIRECTORY) final String outputDirectory, @Optional final String incrementalTimestampFile) throws IOException {
        final IncrementalMongoDump incrementalMongoDump = new IncrementalMongoDump();
        incrementalMongoDump.addDB(strategy.getMongo().getDB(BackupConstants.ADMIN_DB));
        incrementalMongoDump.addDB(strategy.getMongo().getDB(BackupConstants.LOCAL_DB));
        incrementalMongoDump.setIncrementalTimestampFile(incrementalTimestampFile);
        incrementalMongoDump.dump(outputDirectory, strategy.getDatabase());
    }

    /**
     * Takes the output from the dump and restores it. Indexes will be created on a restore. It only does inserts with the data to restore, if existing data is there, it will not
     * be replaced.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:restore}
     * </pre>
     *
     * @param inputPath
     *            input path to the dump files, it can be a directory, a zip file or just a bson file
     * @param drop
     *            whether to drop existing collections before restore
     * @param oplogReplay
     *            replay oplog for point-in-time restore
     * @throws IOException
     *             if an error occurs during restore of the database
     */
    @Processor
    @ReconnectOn(exceptions = IllegalStateException.class)
    public void restore(@Default(DEFAULT_OUTPUT_DIRECTORY) final String inputPath, @Default("false") final boolean drop, @Default("false") final boolean oplogReplay)
            throws IOException {
        final MongoRestore mongoRestore = new MongoRestore(strategy.getClient(), strategy.getDatabase());
        mongoRestore.setDrop(drop);
        mongoRestore.setOplogReplay(oplogReplay);
        mongoRestore.restore(inputPath);
    }

    /**
     * Convert JSON to Document.
     * <p/>
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:start-consistent-request}
     * </pre>
     *
     * @param input
     *            the input for this transformer
     * @return the converted {@link Document}
     */
    @Transformer(sourceTypes = { String.class })
    public static Document jsonToDocument(final String input) {
        Document o = null;
        BSONObject bsonObj = null;

        Object obj = JSON.parse(input);

        if (obj instanceof BasicDBList) {
            BasicDBList basicList = (BasicDBList) obj;

            if (basicList.size() > 1) {
                for (int i = 0; i < basicList.size(); i++) {
                    bsonObj = (BSONObject) basicList.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> entries = bsonObj.toMap();
                    if (i > 0) {
                        o.putAll(entries);
                    } else {
                        o = new Document(entries);
                    }
                }
            }
        } else {
            o = (Document) obj;
        }

        return o;
    }

    /**
     * Convert DBObject to Json.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:dbobjectToJson}
     * </pre>
     *
     * @param input
     *            the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = { Document.class })
    public static String documentToJson(final Document input) {
        return JSONSerializers.getStrict().serialize(input);
    }

    /**
     * Convert a BasicBSONList into Json.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:bsonListToJson}
     * </pre>
     *
     * @param input
     *            the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = { BasicBSONList.class })
    public static String bsonListToJson(final BasicBSONList input) {
        return JSONSerializers.getStrict().serialize(input);
    }

    /**
     * Convert a BasicBSONList into Json.
     *
     * <pre>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:mongoCollectionToJson}
     * </pre>
     *
     * @param input
     *            the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = { MongoCollection.class })
    public static String mongoCollectionToJson(final MongoCollection input) {
        return JSONSerializers.getStrict().serialize(input);
    }

}
