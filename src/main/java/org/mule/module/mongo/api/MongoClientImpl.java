/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoClientImpl implements MongoClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientImpl.class);

    private final DB db;

    public MongoClientImpl(final DB db)
    {
    	System.err.println("In MongoClientImpl constructor");
        Validate.notNull(db);
        this.db = db;
    }

    @Override
	public void close() throws IOException
    {
    	System.err.println("In MongoClientImpl close()");
    }

    @Override
	public long countObjects(@NotNull final String collection, final DBObject query)
    {
        Validate.notNull(collection);
        if (query == null)
        {
            return db.getCollection(collection).count();
        }
        return db.getCollection(collection).count(query);
    }

    @Override
	public void createCollection(@NotNull final String collection,
                                 final boolean capped,
                                 final Integer maxObjects,
                                 final Integer size)
    {
        Validate.notNull(collection);
        final BasicDBObject options = new BasicDBObject("capped", capped);
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
        return db.collectionExists(collection);
    }

    @Override
    public Iterable<DBObject> findObjects(@NotNull final String collection,
                                          final DBObject query,
                                          final List<String> fields,
                                          final Integer numToSkip,
                                          final Integer limit,
                                          DBObject sortBy)
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
	public DBObject findOneObject(@NotNull final String collection,
                                  final DBObject query,
                                  final List<String> fields, boolean failOnNotFound)
    {
        Validate.notNull(collection);
        final DBObject element = db.getCollection(collection).findOne(query,
            FieldsSet.from(fields));

        if (element == null && failOnNotFound)
		{
            throw new MongoException("No object found for query " + query);
		}
        return element;
    }

    @Override
	public String insertObject(@NotNull final String collection,
                               @NotNull final DBObject object,
                               @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(object);
        Validate.notNull(writeConcern);
        db.getCollection(collection).insert(object,
            writeConcern.toMongoWriteConcern(db));

        final ObjectId id;
        final Object rawId = object.get("_id");

    	if(rawId != null)
    	{
        	if(rawId instanceof ObjectId)
        	{
        		id = (ObjectId) rawId;
        	}
        	else
        	{
        		return rawId.toString();
        	}
        	return id.toStringMongod();
    	}
    	else
    	{
    		return null;
    	}
    }

    @Override
	public Collection<String> listCollections()
    {
        return db.getCollectionNames();
    }

    @Override
	public Iterable<DBObject> mapReduceObjects(@NotNull final String collection,
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
                              final DBObject query,
                              @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(writeConcern);
        db.getCollection(collection).remove(query != null ? query : new BasicDBObject(),
            writeConcern.toMongoWriteConcern(db));
    }

    @Override
	public void saveObject(@NotNull final String collection,
                           @NotNull final DBObject object,
                           @NotNull final WriteConcern writeConcern)
    {
        Validate.notNull(collection);
        Validate.notNull(object);
        Validate.notNull(writeConcern);
        db.getCollection(collection).save(object, writeConcern.toMongoWriteConcern(db));
    }

    @Override
	public void updateObjects(@NotNull final String collection,
                              final DBObject query,
                              final DBObject object,
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
        db.getCollection(collection).createIndex(new BasicDBObject(field, order.getValue()));
    }

    @Override
	public void dropIndex(final String collection, final String name)
    {
        db.getCollection(collection).dropIndex(name);
    }

    @Override
	public Collection<DBObject> listIndices(final String collection)
    {
        return db.getCollection(collection).getIndexInfo();
    }

    @Override
	public DBObject createFile(final InputStream content,
                               final String filename,
                               final String contentType,
                               final DBObject metadata)
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
	public Iterable<DBObject> findFiles(final DBObject query)
    {
        return bug5588Workaround(getGridFs().find(query));
    }

    @Override
	public DBObject findOneFile(final DBObject query)
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
	public InputStream getFileContent(final DBObject query)
    {
        Validate.notNull(query);
        return ((GridFSDBFile) findOneFile(query)).getInputStream();
    }

    @Override
	public Iterable<DBObject> listFiles(final DBObject query)
    {
        return bug5588Workaround(getGridFs().getFileList(query));
    }

    @Override
	public void removeFiles(final DBObject query)
    {
        getGridFs().remove(query);
    }

    @Override
	public DBObject executeComamnd(final DBObject command)
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
    private Iterable<DBObject> bug5588Workaround(final Iterable<? extends DBObject> o)
    {
        if (o instanceof Collection<?>)
        {
            return (Iterable<DBObject>) o;
        }
        return new MongoCollection(o);
    }

    public DB getDb()
    {
        return db;
    }

}
