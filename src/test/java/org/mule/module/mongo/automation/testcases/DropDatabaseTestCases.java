/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class DropDatabaseTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        Document dbObject = new Document();
        dbObject.put("key", "mykey");

        getConnector().createCollection("Arenas", false, 5, 5);
        getConnector().saveObject("Arenas", dbObject);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testDropDatabase() {
        getConnector().dropDatabase();
        assertFalse("After dropping the database, the collection Arenas" + " should not exist", getConnector().existsCollection("Arenas"));
    }
}
