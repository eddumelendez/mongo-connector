/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class InsertObjectFromMapTestCases extends AbstractMongoTest {

    private Map<String, Object> testData = new HashMap<String, Object>();
    private List<String> list = new LinkedList<String>();

    @Before
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
        testData.put("key", "objectKey");
        testData.put("value", "objectValue");
        list.add("key");
        list.add("value");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testInsertObjectFromMap() {
        Iterable<Document> iterable;
        String objectID = getConnector().insertObjectFromMap("Arenas", testData, WriteConcern.SAFE);

        assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));

        iterable = getConnector().findObjectsUsingQueryMap("Arenas", testData, list, null, null, null);
        for (Document dbObj : iterable) {
            assertTrue(dbObj.containsKey("_id"));
            assertTrue(dbObj.containsKey("key"));
            ObjectId id = (ObjectId) dbObj.get("_id");
            assertTrue(id.toString().equals(objectID));
        }
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }
}
