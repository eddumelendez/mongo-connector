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
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;

public class CountObjectsTestCases extends AbstractMongoTest {

    private Integer numObjects = 5;

    @Override
    public void setUp() {
        // Create collection
        getConnector().createCollection("Arenas", false, numObjects, numObjects);
    }

    @After
    public void tearDown() throws Exception {
        // Delete collection
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testCountObjects() {
        insertObjects(getEmptyDBObjects(numObjects), "Arenas");

        assertEquals((long) numObjects, getConnector().countObjects("Arenas", new BasicDBObject()));
    }
}
