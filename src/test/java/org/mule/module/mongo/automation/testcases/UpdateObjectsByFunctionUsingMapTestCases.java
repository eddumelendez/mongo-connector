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
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class UpdateObjectsByFunctionUsingMapTestCases extends AbstractMongoTest {

    private String queryKey = "key";
    private int numberOfObjects = 10;
    Map<String, Object> oldMap = new HashMap<String, Object>();

    @Override
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);
        String queryValue = "oldValue";
        oldMap.put(queryKey, queryValue);
        // Create the objects with the key-value pair
        List<Document> objects = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            objects.add(new Document(queryKey, queryValue));
        }

        // Insert the objects
        insertObjects(objects, "Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjectsByFunctionUsingMap() {
        String elementValue = "newValue";
        Map<String, Object> newMap = new HashMap<String, Object>();
        newMap.put(queryKey, elementValue);

        // Update objects
        getConnector().updateObjectsByFunctionUsingMap("Arenas", "$set", oldMap, newMap, false, true, WriteConcern.SAFE);

        // Get all objects
        Iterable<Document> objects = getConnector().findObjects("Arenas", null, null, null, null, null);
        for (Document obj : objects) {
            assertTrue(obj.containsKey(queryKey));
            assertTrue(obj.get(queryKey).equals(elementValue));
        }
        assertTrue(MongoHelper.getIterableSize(objects) == numberOfObjects);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

}
