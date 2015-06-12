/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

/**
 * This file was automatically generated by the Mule Cloud Connector Development Kit
 */

package org.mule.module.mongo;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.module.mongo.api.WriteConcern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * Unit test for the {@link MongoClientImpl}
 *
 * @author flbulgarelli
 */
public class MongoTestCase {

    private static final String A_COLLECTION = "myCollection";
    private MongoClient client;
    private DBCollection collectionMock;
    private DB dbMock;
    private GridFS gridFsMock;

    @Before
    public void setup() {
        dbMock = mock(DB.class);
        gridFsMock = mock(GridFS.class);
        client = new MongoClientImpl(dbMock) {

            @Override
            protected GridFS getGridFs() {
                return gridFsMock;
            }
        };
        collectionMock = mock(DBCollection.class);
        when(dbMock.getCollection(A_COLLECTION)).thenReturn(collectionMock);
    }

    /** Test {@link MongoClient#listCollections()} */
    @Test
    public void listCollections() {
        client.listCollections();
        verify(dbMock).getCollectionNames();
    }

    /** Test {@link MongoClient#existsCollection(String)} */
    @Test
    public void existsCollection() {
        client.existsCollection(A_COLLECTION);
        verify(dbMock).collectionExists(A_COLLECTION);
    }

    /** Test {@link MongoClient#dropCollection(String)} */
    @Test
    public void dropCollection() {
        client.dropCollection(A_COLLECTION);
        verify(collectionMock).drop();
    }

    /** Test {@link MongoClient#saveObject(String, DBObject, WriteConcern)} */
    @Test
    public void saveObject() throws Exception {
        BasicDBObject dbObject = new BasicDBObject();
        client.saveObject(A_COLLECTION, dbObject, WriteConcern.NONE);
        verify(collectionMock).save(dbObject, com.mongodb.WriteConcern.NONE);
    }

    /**
     * Test {@link MongoClient#insertObject(String, com.mongodb.DBObject, org.mule.module.mongo.api.WriteConcern)}
     */
    @Test
    public void insertObject() throws Exception {
        BasicDBObject dbObject = new BasicDBObject();
        client.insertObject(A_COLLECTION, dbObject, WriteConcern.NONE);
        verify(collectionMock).insert(dbObject, com.mongodb.WriteConcern.NONE);
    }

    @Test
    public void removeObjects() throws Exception {
        when(dbMock.getWriteConcern()).thenReturn(com.mongodb.WriteConcern.FSYNC_SAFE);
        client.removeObjects(A_COLLECTION, null, WriteConcern.DATABASE_DEFAULT);
        verify(collectionMock).remove(refEq(new BasicDBObject()), eq(com.mongodb.WriteConcern.FSYNC_SAFE));
    }

    /** Test {@link MongoClient#countObjects(String, com.mongodb.DBObject)} */
    @Test
    public void countObjectsWithQuery() throws Exception {
        BasicDBObject o = new BasicDBObject();
        client.countObjects(A_COLLECTION, o);
        verify(collectionMock).count(o);
    }

    /** Test {@link MongoClient#countObjects(String, com.mongodb.DBObject)} */
    @Test
    public void countObjects() throws Exception {
        client.countObjects(A_COLLECTION, null);
        verify(collectionMock).count();
    }

    /**
     * Test {@link MongoClient#updateObjects(String, com.mongodb.DBObject, com.mongodb.DBObject, boolean, boolean, org.mule.module.mongo.api.WriteConcern)}
     */
    @Test
    public void updateObject() throws Exception {
        DBObject query = new BasicDBObject();
        DBObject dbObject = new BasicDBObject();
        client.updateObjects(A_COLLECTION, query, dbObject, false, true, WriteConcern.SAFE);
        verify(collectionMock).update(query, dbObject, false, true, com.mongodb.WriteConcern.SAFE);
    }

    /** Test {@link MongoClient#createIndex(String, com.mongodb.DBObject)} */
    @Test
    public void createIndex() throws Exception {
        client.createIndex(A_COLLECTION, "i", IndexOrder.ASC);
        verify(collectionMock).createIndex(refEq(new BasicDBObject("i", 1)));
    }

    /** Tests {@link MongoClient#dropIndex(String, String)} */
    @Test
    public void dropIndex() throws Exception {
        client.dropIndex(A_COLLECTION, "anIndex");
        verify(collectionMock).dropIndex(eq("anIndex"));
    }

    /** Tests {@link MongoClient#listIndices(String)} */
    @Test
    public void listIndices() throws Exception {
        client.listIndices(A_COLLECTION);
        verify(collectionMock).getIndexInfo();
    }

    /**
     * Test for {@link MongoClient#removeFiles(DBObject)}
     *
     * @throws Exception
     */
    @Test
    public void removeFiles() throws Exception {
        client.removeFiles(null);
        verify(gridFsMock).remove((DBObject) null);
    }

    /**
     * Test for {@link MongoClient#findFiles(DBObject)}
     *
     * @throws Exception
     */
    @Test
    public void findFiles() throws Exception {
        client.findFiles(null);
        verify(gridFsMock).find((DBObject) null);
    }

    /**
     * Test for {@link MongoClient#getFileContent(DBObject)} when no object matches the query
     *
     * @throws Exception
     */
    @Test(expected = MongoException.class)
    public void getFileContentNoFile() throws Exception {
        BasicDBObject q = new BasicDBObject("foo", "bar");
        client.getFileContent(q);
    }

    /**
     * Test for {@link MongoClient#getFileContent(DBObject)}
     *
     * @throws Exception
     */
    @Test
    public void getFileContent() throws Exception {
        BasicDBObject q = new BasicDBObject("foo", "bar");
        when(gridFsMock.findOne(eq(q))).thenReturn(new GridFSDBFile());
        client.getFileContent(q);
    }

    @Test
    public void startConsistentRequest() {
        client.requestStart();
        verify(dbMock).requestStart();
    }

    @Test
    public void endConsistentRequest() {
        client.requestDone();
        verify(dbMock).requestDone();
    }
}
