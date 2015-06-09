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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;

import com.mongodb.DBObject;

public class SaveObjectFromMapTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // initializeTestRunMessage("saveObjectFromMap");
        getConnector().createCollection("Arenas", false, 5, 5);

    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");

    }

    @Category({ SmokeTests.class, RegressionTests.class })
    @Test
    public void testSaveObjectFromMap() {

        String key = "someKey";
        String value = "someValue";
        Map<String, Object> elementAttributes = new HashMap<String, Object>();
        elementAttributes.put(key, value);

        // Save object to MongoDB
        getConnector().saveObjectFromMap("Arenas", elementAttributes, WriteConcern.SAFE);

        // Check whether it was saved
        DBObject object = getConnector().findOneObjectUsingQueryMap("Arenas", elementAttributes, null, true);
        assertTrue(object.containsField(key));
        assertTrue(object.get(key).equals(value));

        // Modify object and save to MongoDB
        String differentValue = "differentValue";
        elementAttributes.clear();
        elementAttributes.put(key, differentValue);
        getConnector().saveObjectFromMap("Arenas", elementAttributes, WriteConcern.SAFE);

        // Check that modifications were saved
        object = getConnector().findOneObjectUsingQueryMap("Arenas", elementAttributes, null, true);
        assertTrue(object.containsField(key));
        assertFalse(object.get(key).equals(value));
        assertTrue(object.get(key).equals(differentValue));

    }
}
