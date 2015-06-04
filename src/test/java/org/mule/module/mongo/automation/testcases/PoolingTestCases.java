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
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PoolingTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // Create collection
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @After
    public void tearDown() throws Exception {
        // Delete collection
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testPoolSizeDoesNotExceedConfiguration() throws Exception {

        int numObjects = 5;

        insertObjects(getEmptyDBObjects(numObjects), "Arenas");
        Integer startingConnections = getConnections();

        for (int i = 0; i < 32; i++) {
            getConnector().countObjects("Arenas", new BasicDBObject());
        }

        int newConnections = getConnections() - startingConnections;
        assertTrue("Too many new connections (" + newConnections + ", ", newConnections <= 2);
    }

    private int getConnections() {
        DBObject dbObj = getConnector().executeCommand("serverStatus", "");
        dbObj = (DBObject) dbObj.get("connections");
        return (Integer) dbObj.get("current");
    }
}
