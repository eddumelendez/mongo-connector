/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);
        // Insert object
        getConnector().insertObject("Arenas", new BasicDBObject(), WriteConcern.DATABASE_DEFAULT);
    }

    @After
    public void tearDown() throws Exception {
        // Drop the collection
        getConnector().dropCollection("Arenas");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjects_OneObject() {

        // Grab the key-value pair
        String key = "myKey";
        String value = "myValue";

        // Create new DBObject based on key-value pair to replace existing DBObject
        DBObject newDBObject = new BasicDBObject(key, value);

        // Update the object
        getConnector().updateObjects("Arenas", new BasicDBObject(), newDBObject, false, false, WriteConcern.SAFE);

        // Attempt to find the object
        DBObject obj = getConnector().findOneObject("Arenas", newDBObject, null, true);

        // Assert that the object retrieved from MongoDB contains the key-value pairs
        assertTrue(obj.containsField(key));
        assertTrue(obj.get(key).equals(value));
    }
}
