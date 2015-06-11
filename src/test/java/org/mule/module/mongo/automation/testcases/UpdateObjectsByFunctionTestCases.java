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
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsByFunctionTestCases extends AbstractMongoTest {

    private int numberOfObjects = 10;
    private DBObject queryDBObj = new BasicDBObject();

    @Override
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);

        queryDBObj.put("key", "oldvalue");

        // Create the objects with the key-value pair
        List<DBObject> objects = new ArrayList<DBObject>();
        for (int i = 0; i < numberOfObjects; i++) {
            objects.add(new BasicDBObject(queryDBObj.toMap()));
        }

        // Insert the objects
        insertObjects(objects, "Arenas");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjectsByFunction() {
        String queryKey = "key";
        DBObject elementDbObj = new BasicDBObject("key", "newValue");

        // Update objects
        getConnector().updateObjectsByFunction("Arenas", "$set", queryDBObj, elementDbObj, false, true, WriteConcern.DATABASE_DEFAULT);

        // Get all objects
        Iterable<DBObject> objects = getConnector().findObjects("Arenas", null, null, null, null, null);
        for (DBObject obj : objects) {
            assertTrue(obj.containsField(queryKey));
            assertTrue(obj.get(queryKey).equals(elementDbObj.get(queryKey)));
        }
        assertTrue(MongoHelper.getIterableSize(objects) == numberOfObjects);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");

    }

}
