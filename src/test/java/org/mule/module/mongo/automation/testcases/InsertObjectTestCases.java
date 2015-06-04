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
import org.mule.module.mongo.automation.SmokeTests;

import com.mongodb.BasicDBObject;

public class InsertObjectTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @Category({ SmokeTests.class, RegressionTests.class })
    @Test
    public void testInsertObject() {
        String objectID = getConnector().insertObject("Arenas", new BasicDBObject(), WriteConcern.SAFE);
        assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

}
