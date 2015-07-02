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

public class RemoveObjectsTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        // initializeTestRunMessage("removeObjects");
        getConnector().createCollection("Arenas", false, 5, 5);
        getConnector().insertObject("Arenas", new Document());
        // runFlowAndGetPayload("insert-object");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveObjects() {

        // runFlowAndGetPayload("remove-objects");
        getConnector().removeObjects("Arenas", new Document());
        // MongoCollection payload = runFlowAndGetPayload("find-objects");
        Iterable<Document> resultCollection = getConnector().findObjects("Arenas", new Document(), null, 0, 0, null);
        assertFalse(resultCollection.iterator().hasNext());

    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

}
