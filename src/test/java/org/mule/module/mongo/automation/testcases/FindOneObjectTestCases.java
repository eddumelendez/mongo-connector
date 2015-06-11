/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class FindOneObjectTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);
        // Insert object
        getConnector().insertObject("Arenas", new Document());
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindOneObject() {
        // Get the retrieved DBObject
        Document payload = getConnector().findOneObject("Arenas", new Document(), null, false);
        assertNotNull(payload);
        assertTrue(payload.keySet().size() == 1);
    }

    @After
    public void tearDown() throws Exception {
        // drop the collection
        getConnector().dropCollection("Arenas");
    }
}
