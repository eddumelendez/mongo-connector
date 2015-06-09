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

import jersey.repackaged.com.google.common.base.Predicates;
import jersey.repackaged.com.google.common.collect.Iterables;
import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoClientImpl implements MongoClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientImpl.class);

    private static final String ID_FIELD_NAME = "_id";

    @Deprecated
    private final DB db;
    private final MongoDatabase database;
    private final com.mongodb.MongoClient mongo;

    public MongoClientImpl(com.mongodb.MongoClient mongo, final String db)
    {
    	LOGGER.info("Initializing MongoClientImpl");
    	Validate.notNull(mongo, "Mongo instance cannot be null");
        Validate.notNull(db, "Database cannot be null");
        this.mongo = mongo;
        this.db = mongo.getDB(db);
        this.database = mongo.getDatabase(db);
    }

    @Override
	public void close() throws IOException
    {
    	LOGGER.info("Closing MongoClientImpl");
    	mongo.close();
    }

    @Override
	public long countObjects(@NotNull final String collection, final Bson query)
    {
        Validate.notNull(collection);
        if (query == null)
        {
            return database.getCollection(collection).count();
        }
        
		return database.getCollection(collection).count(query);
    }

    @Override
	public void createCollection(@NotNull final String collection,
                                 final boolean capped,
                                 final Integer maxObjects,
                                 final Integer size)
    {
        Validate.notNull(collection);
        final Document options = new Document("capped", capped);
        if (maxObjects != null)
        {
            options.put("maxObject", maxObjects);
        }
        if (size != null)
        {
            options.put("size", size);
        }
        db.createCollection(collection, options);
    }

    @Override
	public DBCollection getCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        return db.getCollection(collection);
    }

    @Override
	public WriteResult addUser(final String username, final String password)
    {
        Validate.notNull(username);
        Validate.notNull(password);
        final WriteResult writeResult = db.addUser(username, password.toCharArray());
//        if (!writeResult.getLastError().ok())
//        {
//            throw new MongoException(writeResult.getLastError().getErrorMessage());
//        }
        return writeResult;
    }

    @Override
	public void dropDatabase()
    {
        db.dropDatabase();
    }

    @Override
	public void dropCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        db.getCollection(collection).drop();
    }

    @Override
	public boolean existsCollection(@NotNull final String collection)
    {
        Validate.notNull(collection);
        return Iterables.find(database.listCollectionNames(), Predicates.equalTo(collection), null) != null;
    }

    @Override
    public Iterable<Document> findObjects(@NotNull final String collection,
                                          final Document query,
                                          final List<String> fields,
                                          final Integer numToSkip,
                                          final Integer limit,
                                          Document sortBy)
    {
        Validate.notNull(collection);

        DBCursor dbCursor = db.getCollection(collection).find(query, FieldsSet.from(fields));
        if (numToSkip != null)
        {
            dbCursor = dbCursor.skip(numToSkip);
        }
        if (limit != null)
        {
            dbCursor = dbCursor.limit(limit);
        }
        if(sortBy != null){
            dbCursor.sort(sortBy);
        }

        return bug5588Workaround(dbCursor);
    }

    @Override
	public Document findOneObject(@NotNull final String collection,
                                  final Document query,
                                  final List<String> fields, boolean failOnNotFound)
    {
        Validate.notNull(collection);
        final Document element = db.getCollection(collection).findOne(query,
            FieldsSet.from(fields));

        if (element == null && failOnNotFound)
		{
            throw new MongoException("No object found for query " + query);
		}
        return element;
    }

    @Override
	public String insertObject(@NotNull final String collection,
                               @NotNull final Document document,
                               @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(document);
        Validate.notNull(writeConcern);
        database.getCollection(collection).insertOne(document);

        final String id;
        final Object rawId = document.get("_id");

    	if (rawId == null)
    	{
    		id = null;
    	}
    	else if (rawId instanceof ObjectId)
    	{
    		id = ((ObjectId) rawId).toHexString();
    	}
    	else
    	{
    		id = rawId.toString();
    	}
    	return id;
    }

    @Override
	public Collection<String> listCollections()
    {
        return Lists.newArrayList(database.listCollectionNames());
    }

    @Override
	public Iterable<Document> mapReduceObjects(@NotNull final String collection,
                                               @NotNull final String mapFunction,
                                               @NotNull final String reduceFunction,
                                               final String outputCollection)
    {
        Validate.notNull(collection);
        Validate.notEmpty(mapFunction);
        Validate.notEmpty(reduceFunction);
        return bug5588Workaround(db.getCollection(collection)
            .mapReduce(mapFunction, reduceFunction, outputCollection, outputTypeFor(outputCollection), null)
            .results());
    }

    private OutputType outputTypeFor(final String outputCollection)
    {
        return outputCollection != null ? OutputType.REPLACE : OutputType.INLINE;
    }

    @Override
	public void removeObjects(@NotNull final String collection,
                              final Document query,
                              @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(writeConcern);
        db.getCollection(collection).remove(query != null ? query : new Document(),
            writeConcern.toMongoWriteConcern(db));
    }

    @Override
	public void saveObject(@NotNull final String collectionName,
                           @NotNull final Document object,
                           @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collectionName);
        Validate.notNull(object);
        Validate.notNull(writeConcern);

        com.mongodb.client.MongoCollection<Document> collection = database.getCollection(collectionName);
        Object id = object.get(ID_FIELD_NAME);
        WriteResult result;
        if (id == null) {
            collection.insertOne(object);
        } else {
            Bson filter = eq(ID_FIELD_NAME, id);
            FindIterable find = collection.find(filter);
            if (!find.iterator().hasNext()) {
                collection.insertOne(object);
            } else {
                collection.updateOne(filter, object);
            }
        }
//        db.getCollection(collectionName).save(object, writeConcern.toMongoWriteConcern(db));
    }

    @Override
	public void updateObjects(@NotNull final String collection,
                              final Document query,
                              final Document object,
                              final boolean upsert,
                              final boolean multi,
                              final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(writeConcern);
        db.getCollection(collection).update(query, object, upsert, multi,
            writeConcern.toMongoWriteConcern(db));

    }

    @Override
	public void createIndex(final String collection, final String field, final IndexOrder order)
    {
        db.getCollection(collection).createIndex(new Document(field, order.getValue()));
    }

    @Override
	public void dropIndex(final String collection, final String name)
    {
        db.getCollection(collection).dropIndex(name);
    }

    @Override
	public Collection<Document> listIndices(final String collection)
    {
        return db.getCollection(collection).getIndexInfo();
    }

    @Override
	public Document createFile(final InputStream content,
                               final String filename,
                               final String contentType,
                               final Document metadata)
    {
        Validate.notNull(filename);
        Validate.notNull(content);
        final GridFSInputFile file = getGridFs().createFile(content);
        file.setFilename(filename);
        file.setContentType(contentType);
        if (metadata != null)
        {
            file.setMetaData(metadata);
        }
        file.save();
        return file;
    }

    @Override
	public Iterable<Document> findFiles(final Document query)
    {
        return bug5588Workaround(getGridFs().find(query));
    }

    @Override
	public Document findOneFile(final Document query)
    {
        Validate.notNull(query);
        final GridFSDBFile file = getGridFs().findOne(query);
        if (file == null)
        {
            throw new MongoException("No file found for query " + query);
        }
        return file;
    }

    @Override
	public InputStream getFileContent(final Document query)
    {
        Validate.notNull(query);
        return ((GridFSDBFile) findOneFile(query)).getInputStream();
    }

    @Override
	public Iterable<Document> listFiles(final Document query)
    {
        return bug5588Workaround(getGridFs().getFileList(query));
    }

    @Override
	public void removeFiles(final Document query)
    {
        getGridFs().remove(query);
    }

    @Override
	public Document executeComamnd(final Document command)
    {
        return db.command(command);
    }

    protected GridFS getGridFs()
    {
        return new GridFS(db);
    }

    /*
     * see http://www.mulesoft.org/jira/browse/MULE-5588
     */
    @SuppressWarnings("unchecked")
    private Iterable<Document> bug5588Workaround(final Iterable<? extends Document> o)
    {
        if (o instanceof Collection<?>)
        {
            return (Iterable<Document>) o;
        }
        return new MongoCollection(o);
    }

    public DB getDb()
    {
        return db;
    }

}
