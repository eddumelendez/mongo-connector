/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SaveObjectTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testSaveObject() {

        DBObject element = new BasicDBObject();
        element.put("someKey", "someValue");
        getConnector().saveObject("Arenas", element, WriteConcern.SAFE);

        // Check that object was inserted
        Iterable<DBObject> dbObjects = getObjects("Arenas", element);

        for (DBObject obj : dbObjects) {
            assertEquals(obj, element);
        }

        // Get key and value from payload (defined in bean)
        String key = "someKey";
        String value = "differentValue";

        // Modify object and save
        element.put(key, value);
        getConnector().saveObject("Arenas", element, WriteConcern.SAFE);

        // Check that object was changed in MongoDB
        dbObjects = getObjects("Arenas", element);

        for (DBObject obj : dbObjects) {
            assertEquals(obj, element);
        }
    }

    @After
    public void tearDown() {
        getConnector().dropCollection("Arenas");

    }

}
