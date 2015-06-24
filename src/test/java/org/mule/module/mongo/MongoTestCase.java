/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientImpl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * Unit test for the {@link MongoClientImpl}
 *
 * @author flbulgarelli
 */
public class MongoTestCase {

    private static final String A_COLLECTION = "myCollection";
    private com.mongodb.MongoClient mongo;
    private MongoClientImpl client;
    private MongoCollection<Document> collectionMock;
    private MongoDatabase dbMock;
    private GridFS gridFsMock;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mongo = mock(com.mongodb.MongoClient.class);
        dbMock = mock(MongoDatabase.class);
        gridFsMock = mock(GridFS.class);
        collectionMock = mock(MongoCollection.class);
        when(mongo.getDatabase(A_COLLECTION)).thenReturn(dbMock);
        when(dbMock.getCollection(A_COLLECTION)).thenReturn(collectionMock);
        client = new MongoClientImpl(mongo, A_COLLECTION) {

            @Override
            protected GridFS getGridFs() {
                return gridFsMock;
            }
        };
    }

    /** Test {@link MongoClient#listCollections()} */
    @SuppressWarnings("unchecked")
    @Test
    public void listCollections() {
        MongoIterable<String> mongoIterable = mock(MongoIterable.class);
        when(dbMock.listCollectionNames()).thenReturn(mongoIterable);
        client.listCollections();
        verify(dbMock).listCollectionNames();
    }

    /** Test {@link MongoClient#existsCollection(String)} */
    @SuppressWarnings({
            "unchecked",
            "rawtypes" })
    @Test
    public void existsCollection() {
        MongoIterable<String> iterable = mock(MongoIterable.class);
        MongoCursor iterator = mock(MongoCursor.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn("Hello");
        when(dbMock.listCollectionNames()).thenReturn(iterable);
        client.existsCollection(A_COLLECTION);
        verify(dbMock).listCollectionNames();
    }

    /** Test {@link MongoClient#dropCollection(String)} */
    @Test
    public void dropCollection() {
        client.dropCollection(A_COLLECTION);
        verify(collectionMock).drop();
    }

    /** Test {@link MongoClient#saveObject(String, Document)} */
    @Test
    public void saveNewObject() throws Exception {
        Document document = new Document();
        client.saveObject(A_COLLECTION, document);
        verify(collectionMock).insertOne(document);
    }

    /** Test {@link MongoClient#saveObject(String, Document)} */
    @Test
    public void saveExistingObject() throws Exception {
        Document document = new Document("_id", "someId");
        client.saveObject(A_COLLECTION, document);
        verify(collectionMock).updateOne(eq("_id", "someId"), document);
    }

    /**
     * Test {@link MongoClient#insertObject(String, org.bson.Document)}
     */
    @Test
    public void insertObject() throws Exception {
        Document document = new Document();
        client.insertObject(A_COLLECTION, document);
        verify(collectionMock).insertOne(document);
    }

    @Test
    public void removeObjects() throws Exception {
        when(dbMock.getWriteConcern()).thenReturn(com.mongodb.WriteConcern.FSYNC_SAFE);
        Document query = new Document();
        client.removeObjects(A_COLLECTION, query);
        verify(collectionMock).deleteMany(query);
    }

    /** Test {@link MongoClient#countObjects(String, org.bson.Document)} */
    @Test
    public void countObjectsWithQuery() throws Exception {
        Document o = new Document();
        client.countObjects(A_COLLECTION, o);
        verify(collectionMock).count(o);
    }

    /** Test {@link MongoClient#countObjects(String, org.bson.Document)} */
    @Test
    public void countObjects() throws Exception {
        client.countObjects(A_COLLECTION, null);
        verify(collectionMock).count();
    }

    /**
     * Test {@link MongoClient#updateObjects(String, org.bson.Document, org.bson.Document, boolean)}
     */
    @Test
    public void updateOneObject() throws Exception {
        Document query = new Document();
        Document document = new Document();
        client.updateObjects(A_COLLECTION, query, document, false);
        verify(collectionMock).findOneAndReplace(query, document);
    }

    /**
     * Test {@link MongoClient#updateObjects(String, org.bson.Document, org.bson.Document, boolean)}
     */
    @Test
    public void updateManyObject() throws Exception {
        Document query = new Document();
        Document document = new Document();
        client.updateObjects(A_COLLECTION, query, document, true);
        verify(collectionMock).updateMany(query, document);
    }

    /** Test {@link MongoClient#createIndex(String, org.bson.Document)} */
    @Test
    public void createIndex() throws Exception {
        client.createIndex(A_COLLECTION, "i", IndexOrder.ASC);
        verify(collectionMock).createIndex(refEq(new Document("i", 1)));
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
        verify(collectionMock).listIndexes();
    }

    /**
     * Test for {@link MongoClient#removeFiles(DBObject)}
     *
     * @throws Exception
     */
    @Test
    public void removeFiles() throws Exception {
        DBObject someObject = mock(DBObject.class);
        client.removeFiles(someObject);
        verify(gridFsMock).remove(someObject);
    }

    /**
     * Test for {@link MongoClient#findFiles(DBObject)}
     *
     * @throws Exception
     */
    @Test
    public void findFiles() throws Exception {
        DBObject someObject = mock(DBObject.class);
        client.findFiles(someObject);
        verify(gridFsMock).find(someObject);
    }

    /**
     * Test for {@link MongoClient#getFileContent(DBObject)} when no object matches the query
     * 
     * @throws Exception
     */
    @Test(expected = MongoException.class)
    public void getFileContentNoFile() throws Exception {
        DBObject q = new BasicDBObject("foo", "bar");
        client.getFileContent(q);
        verify(gridFsMock).find(q);
    }

    /**
     * Test for {@link MongoClient#getFileContent(Document)}
     * 
     * @throws Exception
     */
    @Test
    public void getFileContent() throws Exception {
        DBObject q = new BasicDBObject("foo", "bar");
        when(gridFsMock.findOne(eq(q))).thenReturn(new GridFSDBFile());
        client.getFileContent(q);
        verify(gridFsMock).find(q);
    }

}
