/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RemoveObjectsTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // initializeTestRunMessage("removeObjects");
        getConnector().createCollection("Arenas", false, 5, 5);
        getConnector().insertObject("Arenas", new BasicDBObject(), WriteConcern.SAFE);
        // runFlowAndGetPayload("insert-object");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveObjects() {

        // runFlowAndGetPayload("remove-objects");
        getConnector().removeObjects("Arenas", new BasicDBObject(), WriteConcern.SAFE);
        // MongoCollection payload = runFlowAndGetPayload("find-objects");
        Iterable<DBObject> resultCollection = getConnector().findObjects("Arenas", null, null, 0, 0, null);
        assertFalse(resultCollection.iterator().hasNext());

    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");

    }

}
