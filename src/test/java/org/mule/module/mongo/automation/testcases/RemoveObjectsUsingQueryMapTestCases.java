/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class RemoveObjectsUsingQueryMapTestCases extends AbstractMongoTest {

    private int numberOfObjects = 25;
    private int extraObjects = 10;
    private String key = "someKey";
    private String value = "someValue";

    @Override
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);

        // Create list of objects, some with key-value pair, some without
        List<Document> objects = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            Document dbObj = new Document(key, value);
            objects.add(dbObj);
        }

        objects.addAll(getEmptyDocuments(extraObjects));

        // Insert objects into collection
        insertObjects(objects, "Arenas");
    }

    @After
    public void tearDown() throws Exception {
        // Drop the collection
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveUsingQueryMap_WithQueryMap() {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(key, value);

        // Remove all records matching key-value pair
        getConnector().removeObjectsUsingQueryMap("Arenas", query, WriteConcern.SAFE);

        // Get all objects
        // Only objects which should be returned are those without the key value pairs
        Iterable<Document> objects = getConnector().findObjects("Arenas", null, null, null, null, null);

        // Check that each returned object does not contain the defined key-value pair
        for (Document dbo : objects) {
            assertTrue(!dbo.containsKey(key));
        }
        assertTrue(MongoHelper.getIterableSize(objects) == extraObjects);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveUsingQueryMap_WithoutQueryMap() {

        Map<String, Object> query = new HashMap<String, Object>();
        // Remove all records
        getConnector().removeObjectsUsingQueryMap("Arenas", query, WriteConcern.SAFE);

        // Get all objects
        Iterable<Document> objects = getConnector().findObjects("Arenas", null, null, null, null, null);
        assertFalse(objects.iterator().hasNext());

    }
}
