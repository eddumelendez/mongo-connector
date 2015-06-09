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
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FindObjectsTestCases extends AbstractMongoTest {

    private List<String> objectIDs = new ArrayList<String>();

    @Override
    public void setUp() {
        // create collection
        getConnector().createCollection("Arenas", false, 5, 5);
        int numberOfObjects = 25;

        for (int i = 0; i < numberOfObjects; i++) {
            BasicDBObject dbObject = new BasicDBObject();
            String payload = getConnector().insertObject("Arenas", dbObject, WriteConcern.SAFE);
            objectIDs.add(payload);
        }
    }

    @Category({ SmokeTests.class, RegressionTests.class })
    @Test
    public void testFindObjects() {
        int size = 0;
        Iterable<DBObject> payload = getConnector().findObjects("Arenas", null, null, null, null, null);

        for (DBObject obj : payload) {
            String dbObjectID = obj.get("_id").toString();
            assertTrue(objectIDs.contains(dbObjectID));
            size++;
        }
        assertTrue(objectIDs.size() == size);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }
}
