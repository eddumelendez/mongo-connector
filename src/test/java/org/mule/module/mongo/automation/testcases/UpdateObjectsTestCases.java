/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class UpdateObjectsTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);
        // Insert object
        getConnector().insertObject("Arenas", new Document());
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
        Document newDBObject = new Document(key, value);

        // Update the object
        getConnector().updateObjects("Arenas", new Document(), newDBObject, false);

        // Attempt to find the object
        Document obj = getConnector().findOneObject("Arenas", newDBObject, null, true);

        // Assert that the object retrieved from MongoDB contains the key-value pairs
        assertTrue(obj.containsKey(key));
        assertTrue(obj.get(key).equals(value));
    }
}
