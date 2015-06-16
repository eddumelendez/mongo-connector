/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.CursorType;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoClientImpl implements MongoClient {

    private static final Logger logger = LoggerFactory.getLogger(MongoClientImpl.class);

    private static final String ID_FIELD_NAME = "_id";
    
    private static final Function<GridFSDBFile, DBObject> DUMMY_CAST_FUNCTION = new Function<GridFSDBFile, DBObject>() {
        @Override
        public DBObject apply(GridFSDBFile input) {
            return input;
        }
    };

    @Deprecated
    private final DB db;
    private final MongoDatabase database;
    private final com.mongodb.MongoClient mongo;

    public MongoClientImpl(com.mongodb.MongoClient mongo, final String db) {
        logger.info("Initializing MongoClientImpl");
        Validate.notNull(mongo, "Mongo instance cannot be null");
        Validate.notNull(db, "Database cannot be null");
        this.mongo = mongo;
        this.db = mongo.getDB(db);
        database = mongo.getDatabase(db);
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing MongoClientImpl");
        mongo.close();
    }

    @Override
    public long countObjects(@NotNull final String collection, final Bson query) {
        Validate.notNull(collection);
        if (query == null) {
            return database.getCollection(collection).count();
        }

        return database.getCollection(collection).count(query);
    }

    @Override
    public void createCollection(@NotNull final String collection, final boolean capped, final Integer maxObjects, final Integer size) {
        Validate.notNull(collection);
        final CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(capped);
        if (maxObjects != null) {
            options.maxDocuments(maxObjects);
        }
        if (size != null) {
            options.sizeInBytes(size);
        }
        database.createCollection(collection, options);
    }

    @Override
    public DBCollection getCollection(@NotNull final String collection) {
        Validate.notNull(collection);
        return db.getCollection(collection);
    }

    @Override
    public WriteResult addUser(final String username, final String password) {
        Validate.notNull(username);
        Validate.notNull(password);
        final WriteResult writeResult = db.addUser(username, password.toCharArray());
        // if (!writeResult.getLastError().ok())
        // {
        // throw new MongoException(writeResult.getLastError().getErrorMessage());
        // }
        return writeResult;
    }

    @Override
    public void dropDatabase() {
        db.dropDatabase();
    }

    @Override
    public void dropCollection(@NotNull final String collection) {
        Validate.notNull(collection);
        db.getCollection(collection).drop();
    }

    @Override
    public boolean existsCollection(@NotNull final String collection) {
        Validate.notNull(collection);
        return Iterables.find(database.listCollectionNames(), Predicates.equalTo(collection), null) != null;
    }

    @Override
    public Iterable<Document> findObjects(@NotNull final String collection, final Document query, final List<String> fields, final Integer numToSkip, final Integer limit,
            Document sortBy) {
        Validate.notNull(collection);

        FindIterable<Document> dbCursor = database.getCollection(collection).find(query).projection(FieldsSet.from(fields)).cursorType(CursorType.NonTailable);
        if (numToSkip != null) {
            dbCursor = dbCursor.skip(numToSkip);
        }
        if (limit != null) {
            dbCursor = dbCursor.limit(limit);
        }
        if (sortBy != null) {
            dbCursor.sort(sortBy);
        }
        return bug5588Workaround(dbCursor);
    }

    @Override
    public Document findOneObject(@NotNull final String collection, final Document query, final List<String> fields, boolean failOnNotFound) {
        Validate.notNull(collection);
        FindIterable<Document> findIterable = database.getCollection(collection).find(query).projection(FieldsSet.from(fields));
        final Document element = findIterable.first();
        if (element == null && failOnNotFound) {
            throw new MongoException("No object found for query " + query);
        }
        return element;
    }

    @Override
    public String insertObject(@NotNull final String collection, @NotNull final Document document) {
        Validate.notNull(collection);
        Validate.notNull(document);
        database.getCollection(collection).insertOne(document);

        final String id;
        final Object rawId = document.get("_id");

        if (rawId == null) {
            id = null;
        } else if (rawId instanceof ObjectId) {
            id = ((ObjectId) rawId).toHexString();
        } else {
            id = rawId.toString();
        }
        return id;
    }

    @Override
    public Collection<String> listCollections() {
        return Lists.newArrayList(database.listCollectionNames());
    }

    @Override
    public Iterable<Document> mapReduceObjects(@NotNull final String collection, @NotNull final String mapFunction, @NotNull final String reduceFunction,
            final String outputCollection) {
        Validate.notNull(collection);
        Validate.notEmpty(mapFunction);
        Validate.notEmpty(reduceFunction);

        MapReduceIterable<Document> mapReduceIterable = database.getCollection(collection).mapReduce(mapFunction, reduceFunction);
        if (outputCollection != null) {
            mapReduceIterable = mapReduceIterable.collectionName(outputCollection);
        }
        return bug5588Workaround(mapReduceIterable);
    }

    @Override
    public void removeObjects(@NotNull final String collection, final Bson query) {
        Validate.notNull(collection);
        database.getCollection(collection).deleteMany(query);
    }

    @Override
    public void saveObject(@NotNull final String collectionName, @NotNull final Document document) {
        Validate.notNull(collectionName);
        Validate.notNull(document);

        com.mongodb.client.MongoCollection<Document> collection = database.getCollection(collectionName);
        Object id = document.get(ID_FIELD_NAME);
        if (id == null) {
            collection.insertOne(document);
        } else {
            Bson filter = eq(ID_FIELD_NAME, id);
            FindIterable<Document> find = collection.find(filter);
            if (!find.iterator().hasNext()) {
                collection.insertOne(document);
            } else {
                collection.findOneAndReplace(find.iterator().next(), document);
            }
        }
    }

    @Override
    public void updateObjects(@NotNull final String collection, final Document query, final Document document, final boolean multi) {
        Validate.notNull(collection);
        if (!multi) {
            database.getCollection(collection).findOneAndReplace(query, document);
        } else {
            database.getCollection(collection).updateMany(query, document);
        }
    }

    @Override
    public void createIndex(final String collection, final String field, final IndexOrder order) {
        database.getCollection(collection).createIndex(new Document(field, order.getValue()));
    }

    @Override
    public void dropIndex(final String collection, final String name) {
        db.getCollection(collection).dropIndex(name);
    }

    @Override
    public Collection<Document> listIndices(final String collection) {
        // TODO See if we can change API to return an iterable, and prevent materializing the consumed list
        return Lists.newArrayList(database.getCollection(collection).listIndexes());
    }

    @Override
    public DBObject createFile(final InputStream content, final String filename, final String contentType, final DBObject metadata) {
        Validate.notNull(filename);
        Validate.notNull(content);
        final GridFSInputFile file = getGridFs().createFile(content);
        file.setFilename(filename);
        file.setContentType(contentType);
        if (metadata != null) {
            file.setMetaData(metadata);
        }
        file.save();
        return file;
    }

    @Override
    public Iterable<DBObject> findFiles(final DBObject query) {
        return Iterables.transform(bug5588Workaround(getGridFs().find(query)), DUMMY_CAST_FUNCTION);
    }

    @Override
    public DBObject findOneFile(final DBObject query) {
        Validate.notNull(query);
        final GridFSDBFile file = getGridFs().findOne(query);
        if (file == null) {
            throw new MongoException("No file found for query " + query);
        }
        return file;
    }

    @Override
    public InputStream getFileContent(final DBObject query) {
        Validate.notNull(query);
        return ((GridFSDBFile) findOneFile(query)).getInputStream();
    }

    @Override
    public Iterable<DBObject> listFiles(final DBObject query) {
        return bug5588Workaround(getGridFs().getFileList(query));
    }

    @Override
    public void removeFiles(final DBObject query) {
        getGridFs().remove(query);
    }

    @Override
    public Document executeCommand(final Document command) {
        return database.runCommand(command);
    }

    protected GridFS getGridFs() {
        return new GridFS(db);
    }

    private <T> Iterable<T> bug5588Workaround(final Iterable<? extends T> o) {
        return new MongoCollection<T>(o);
    }

    public DB getDb() {
        return db;
    }

}
