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

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsUsingMapTestCases extends AbstractMongoTest {

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
        List<DBObject> objects = new ArrayList<DBObject>();
        for (int i = 0; i < numberOfObjects; i++) {
            DBObject object = new BasicDBObject(queryKey, queryValue);
            objects.add(object);
        }

        // Insert the objects
        insertObjects(objects, "Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjectsUsingMap() {
        int size = 0;
        String elementValue = "newValue";
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(queryKey, elementValue);
        newMap.put("$set", data);

        // Update objects
        getConnector().updateObjectsUsingMap("Arenas", oldMap, newMap, false, true, WriteConcern.SAFE);

        // Get all objects
        Iterable<DBObject> objects = getConnector().findObjects("Arenas", null, null, null, null, null);
        for (DBObject obj : objects) {
            assertTrue(obj.containsField(queryKey));
            assertTrue(obj.get(queryKey).equals(elementValue));
            size++;
        }
        assertTrue(size == numberOfObjects);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");

    }

}
