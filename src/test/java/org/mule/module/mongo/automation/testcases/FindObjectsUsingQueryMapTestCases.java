/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class FindObjectsUsingQueryMapTestCases extends AbstractMongoTest {

    private int numberOfObjects;
    private int limit;
    private String queryKey;
    private String queryValue;
    private int extraObjects;
    private Map<String, Object> queryAttributes = new HashMap<String, Object>();

    @Override
    public void setUp() {
        // Create collection
        getConnector().createCollection("Arenas", false, 5, 5);

        extraObjects = 10;
        numberOfObjects = 25;
        limit = 10;
        queryKey = "myKey";
        queryValue = "myValue";
        queryAttributes.put(queryKey, queryValue);

        // Create a number of objects
        List<Document> objects = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            Document obj = new Document(queryKey, queryValue);
            objects.add(obj);
        }

        // Add extra objects which do not have the key-value pair defined in testObjects
        // These should not be retrieved
        objects.addAll(getEmptyDocuments(extraObjects));

        insertObjects(objects, "Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindObjectsUsingQueryMap_WithQuery() {
        Iterable<Document> collection = getConnector().findObjectsUsingQueryMap("Arenas", queryAttributes, null, null, null, null);

        for (Document obj : collection) {
            assertTrue(obj.containsKey(queryKey));
            assertTrue(obj.get(queryKey).equals(queryValue));
        }
        assertTrue(MongoHelper.getIterableSize(collection) == numberOfObjects);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindObjectsUsingQueryMap_WithoutQuery() {
        Iterable<Document> collection = getConnector().findObjectsUsingQueryMap("Arenas", null, null, null, null, null);

        // Assert that everything was retrieved (empty objects + key-value pair objects)
        assertTrue(numberOfObjects + extraObjects == MongoHelper.getIterableSize(collection));
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindObjectsUsingQueryMap_WithLimit() {
        Iterable<Document> collection = getConnector().findObjectsUsingQueryMap("Arenas", queryAttributes, null, null, limit, null);

        // Assert that only "limit" objects were retrieved
        assertTrue(limit == MongoHelper.getIterableSize(collection));
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }
}
