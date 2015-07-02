/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class SaveObjectTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testSaveObject() {

        Document element = new Document();
        element.put("someKey", "someValue");
        getConnector().saveObject("Arenas", element);

        // Check that object was inserted
        Iterable<Document> dbObjects = getObjects("Arenas", element);

        for (Document obj : dbObjects) {
            assertEquals(obj, element);
        }

        String key = "someKey";
        String value = "differentValue";

        // Modify object and save
        element.put(key, value);
        getConnector().saveObject("Arenas", element);

        // Check that object was changed in MongoDB
        dbObjects = getObjects("Arenas", element);

        for (Document obj : dbObjects) {
            assertEquals(obj, element);
        }
    }

    @After
    public void tearDown() {
        getConnector().dropCollection("Arenas");
    }

}
