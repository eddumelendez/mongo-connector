/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsUsingMapTestCases extends MongoTestParent {

    @Before
    public void setUp() throws Exception {
        // Create the collection
        initializeTestRunMessage("updateObjectsUsingMap");
        runFlowAndGetPayload("create-collection");

        String queryKey = getTestRunMessageValue("queryKey").toString();
        String queryValue = getTestRunMessageValue("queryValue").toString();
        int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");

        // Create the objects with the key-value pair
        List<DBObject> objects = new ArrayList<DBObject>();
        for (int i = 0; i < numberOfObjects; i++) {
            DBObject object = new BasicDBObject(queryKey, queryValue);
            objects.add(object);
        }

        // Insert the objects
        insertObjects(objects);

    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjectsUsingMap() {
        try {
            String elementKey = getTestRunMessageValue("elementKey").toString();
            String elementValue = getTestRunMessageValue("elementValue").toString();
            int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");

            // Update objects
            runFlowAndGetPayload("update-objects-using-map");

            // Get all objects
            MongoCollection objects = runFlowAndGetPayload("find-objects");
            for (DBObject obj : objects) {
                assertTrue(obj.containsField(elementKey));
                assertTrue(obj.get(elementKey).equals(elementValue));
            }
            assertTrue(objects.size() == numberOfObjects);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("drop-collection");

    }

}
