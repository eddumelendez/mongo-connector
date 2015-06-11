/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.WriteConcern;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Integration test for the Connector
 * 
 * @author flbulgarelli
 */
@RunWith(Theories.class)
public class MongoTestDriver
{
    private static final String MAIN_COLLECTION = "aCollection";
    private static final String ANOTHER_COLLECTION = "anotherCollection";
    private MongoCloudConnector connector;

    /**
     * Tests methods in this test class assume that there is always a collection
     * {@link #MAIN_COLLECTION} available
     * 
     * @throws InitialisationException
     */
    @Before
    public void setup() throws Exception
    {
        connector = new MongoCloudConnector();
        connector.getStrategy().setHost("127.0.0.1");
        connector.getStrategy().setPort(27017);
        //connector.connect("admin", "pepe", "test");
        connector.getStrategy().connect("admin", "", "test");
        connector.createCollection(MAIN_COLLECTION, false, 100, 1000);
    }

    /**
     * Deletes the {@link #MAIN_COLLECTION}
     */
    @After
    public void tearDown()
    {
        connector.dropCollection(MAIN_COLLECTION);
        connector.dropCollection(ANOTHER_COLLECTION);
    }

    /**
     * Tests that a collection can be created, having side effects on
     * {@link MongoCloudConnector#existsCollection(String)} and
     * {@link MongoClient#listCollections()}
     */
    @Test
    public void createCollection() throws Exception
    {
        int originalSize = connector.listCollections().size();
        connector.createCollection(ANOTHER_COLLECTION, false, 100, 1000);
        assertTrue(connector.existsCollection(ANOTHER_COLLECTION));
        assertTrue(connector.listCollections().contains(ANOTHER_COLLECTION));
        assertEquals(originalSize + 1, connector.listCollections().size());
    }

    /**
     * Tests that a collection can not be created if it already exists
     */
    @Test(expected = MongoException.class)
    public void createCollectionAlreadyExists() throws Exception
    {
        connector.createCollection(MAIN_COLLECTION, false, 100, 1000);
    }

    /**
     * Tests that a collection can be dropped, having side effects on
     * {@link MongoCloudConnector#existsCollection(String)} and
     * {@link MongoClient#listCollections()}
     */
    @Test
    public void dropCollection() throws Exception
    {
        int originalSize = connector.listCollections().size();

        connector.dropCollection(MAIN_COLLECTION);

        assertFalse(connector.existsCollection(MAIN_COLLECTION));
        assertFalse(connector.listCollections().contains(MAIN_COLLECTION));

        assertEquals(originalSize - 1, connector.listCollections().size());
    }

    /**
     * Tests that a collection can be dropped even if it does not exists
     */
    @Test
    public void dropCollectionInexistent() throws Exception
    {
        connector.dropCollection(ANOTHER_COLLECTION);
    }

    /**
     * Tests that a collection can be dropped, even if it has elements, having side
     * effects on {@link MongoCloudConnector#existsCollection(String)} and
     * {@link MongoClient#listCollections()}
     */
    @Test
    public void dropCollectionWithElements() throws Exception
    {
    	Document o = new Document();
        connector.insertObject(MAIN_COLLECTION, o);
        connector.dropCollection(MAIN_COLLECTION);
        assertFalse(connector.existsCollection(MAIN_COLLECTION));
    }

    /**
     * Tests that an object can be created, impacting in the number of objects in the
     * database
     */
    @Test
    public void createObject() throws Exception
    {
        connector.insertObject(MAIN_COLLECTION, acmeEmployee());

        assertEquals(1, connector.countObjects(MAIN_COLLECTION, acmeQuery()));
        Document employee = connector.findOneObject(MAIN_COLLECTION, acmeQuery(),
            Arrays.asList("name"),false);
        assertNotNull(employee);
        assertEquals("John", employee.get("name"));
        assertNull(employee.get("company"));
    }

    /**
     * Tests that an exception is thrown if no object that matches a query is found
     */
    @Test(expected = MongoException.class)
    public void findOneObjectNotExists() throws Exception
    {
        connector.findOneObject(MAIN_COLLECTION, acmeQuery(), null,true);
    }

    /**
     * Tests that an object can be removed, impacting in the number of objects in the
     * database
     */
    @Test
    public void removeObject() throws Exception
    {
        connector.insertObject(MAIN_COLLECTION, acmeEmployee());

        Document query = acmeQuery();
        connector.removeObjects(MAIN_COLLECTION, query, WriteConcern.DATABASE_DEFAULT);
        assertEquals(0, connector.countObjects(MAIN_COLLECTION, query));
    }

    /**
     * Tests that objects in a collection can be properly counted with or without
     * filters
     */
    @Test
    public void countObjects() throws Exception
    {
        insertInTestDb(new Document("x", 59));
        insertInTestDb(new Document("x", 60));
        insertInTestDb(new Document("x", 60));
        insertInTestDb(new Document("x", 70));
        assertEquals(4, connector.countObjects(MAIN_COLLECTION, null));
        assertEquals(2, connector.countObjects(MAIN_COLLECTION, new Document("x", 60)));
        assertEquals(0, connector.countObjects(MAIN_COLLECTION, new Document("x", 36)));
    }

    private void insertInTestDb(Document o)
    {
        connector.insertObject(MAIN_COLLECTION, o);
    }

    /**
     * Some output collection names
     */
    @DataPoint
    public static final String OUTPUT_COLLECTION_NAME = "anOutputCollection";
    @DataPoint
    public static final String INLINE_COLLECTION_NAME = "null";

    /**
     * Tests that objects can be map-reduced either in memory or in a persistent way.
     * In this test, a collection of elections results is grouped by candidate name
     * and reduced by votes
     */
    @Theory
    @SuppressWarnings("serial")
    public void mapReduce(String outputCollection) throws Exception
    {
        insertInTestDb(new Document()
        {
            {
                put("city", "City1");
                put("candidate", "John");
                put("votes", 100);
            }
        });
        insertInTestDb(new Document()
        {
            {
                put("city", "City2");
                put("candidate", "John");
                put("votes", 20);
            }
        });
        insertInTestDb(new Document()
        {
            {
                put("city", "City3");
                put("candidate", "Mary");
                put("votes", 150);
            }
        });
        insertInTestDb(new Document()
        {
            {
                put("city", "City2");
                put("candidate", "Mary");
                put("votes", 60);
            }
        });
        Iterable<Document> results = connector.mapReduceObjects(MAIN_COLLECTION,
            "function() { emit(this.candidate, this.votes) }",
            "function(key, values) { return values.reduce(function(a, e){ return a + e });  } ",
            outputCollection);
        assertNotNull(results);
        Iterator<Document> iter = results.iterator();
        assertEquals(new Document()
        {
            {
                put("_id", "John");
                put("value", 120);
            }
        }, iter.next());
        assertEquals(new Document()
        {
            {
                put("_id", "Mary");
                put("value", 210);
            }
        }, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void findWithSkipAndLimit()
    {
        insertInTestDb(new Document("x", 1));
        insertInTestDb(new Document("x", 2));
        insertInTestDb(new Document("x", 3));
        insertInTestDb(new Document("x", 4));
        insertInTestDb(new Document("x", 5));

        Iterator<Document> iter = connector.findObjects(MAIN_COLLECTION, null, null, 2, 2, null).iterator();

        assertEquals(3, iter.next().get("x"));
        assertEquals(4, iter.next().get("x"));
        assertFalse(iter.hasNext());
    }

    @Test
    public void updateMulti() throws Exception
    {
        insertInTestDb(new Document("x", 50));
        insertInTestDb(new Document("x", 60));
        insertInTestDb(new Document("x", 60));
        insertInTestDb(new Document("x", 70));
        connector.updateObjects(MAIN_COLLECTION,
            new Document("x", new Document("$gt", 55)), new Document("$inc",
                new Document("x", 2)), true);

        Iterator<Document> iter = connector.findObjects(MAIN_COLLECTION, null, null, null, null, null).iterator();
        assertEquals(50, iter.next().get("x"));
        assertEquals(62, iter.next().get("x"));
        assertEquals(62, iter.next().get("x"));
        assertEquals(72, iter.next().get("x"));
    }

    @Test
    public void updateSingle() throws Exception
    {
        insertInTestDb(new Document("x", 50));
        insertInTestDb(new Document("x", 60));
        insertInTestDb(new Document("x", 60));
        connector.updateObjects(MAIN_COLLECTION,
            new Document("x", new Document("$gt", 55)), new Document("$inc",
                new Document("x", 2)), false);

        Iterator<Document> iter = connector.findObjects(MAIN_COLLECTION, null, null, null, null, null).iterator();
        assertEquals(50, iter.next().get("x"));
        assertEquals(62, iter.next().get("x"));
        assertEquals(60, iter.next().get("x"));
    }

    @Test
    public void createIndex() throws Exception
    {
        assertEquals(1, connector.listIndices(MAIN_COLLECTION).size());
        connector.createIndex(MAIN_COLLECTION, "aField", IndexOrder.DESC);
        assertEquals(2, connector.listIndices(MAIN_COLLECTION).size());
    }

    @Test
    public void testCreateAndGetFile() throws Exception
    {
        DBObject file = connector.createFileFromPayload(
            new ByteArrayInputStream("hello world".getBytes()), "testFile.txt", "text/plain",
            new BasicDBObject("foo", "bar"));
        try
        {
            assertEquals("testFile.txt", file.get("filename"));
            assertEquals("text/plain", file.get("contentType"));
            assertEquals("bar", ((DBObject) file.get("metadata")).get("foo"));

            try (InputStream in = connector.getFileContent(filenameQuery("testFile.txt"))) {
            	assertEquals("hello world", new Scanner(in).nextLine());
            }
        }
        finally
        {
            connector.removeFiles(filenameQuery("testFile.txt"));
        }
    }

    @Test
    public void testCreateAndListFile() throws Exception
    {
        connector.createFileFromPayload("hello world".getBytes(), "testFile.txt", null, null);
        try
        {
            Iterator<DBObject> iter = connector.listFiles(filenameQuery("testFile.txt")).iterator();
            assertTrue(iter.hasNext());
            iter.next();
            assertFalse(iter.hasNext());

            iter = connector.findFiles(filenameQuery("testFile.txt")).iterator();
            assertTrue(iter.hasNext());
            iter.next();
            assertFalse(iter.hasNext());
        }
        finally
        {
            connector.removeFiles(filenameQuery("testFile.txt"));
        }
    }

    private DBObject filenameQuery(String filename)
    {
        return new BasicDBObject("filename", filename);
    }

    private Document acmeQuery()
    {
        Document query = new Document();
        query.put("company", "ACME");
        return query;
    }

    private Document acmeEmployee()
    {
    	Document employee = new Document();
        employee.put("name", "John");
        employee.put("surname", "Doe");
        employee.put("company", "ACME");
        return employee;
    }
}
