/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.module.mongo.automation.testdata.TestDataBuilder;

import com.mongodb.DBObject;

public class CreateIndexTestCases extends AbstractMongoTest {

    private Map<String, Object> testData;
    private String indexKey = "myField";
    private IndexOrder indexOrder;

    @Override
    public void setUp() {
        testData = TestDataBuilder.createIndex();
        indexOrder = (IndexOrder) testData.get("order");
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @Category({ SmokeTests.class, RegressionTests.class })
    @Test
    public void testCreateIndex() {
        String indexName = indexKey + "_" + indexOrder.getValue();

        getConnector().createIndex("Arenas", indexKey, indexOrder);

        List<DBObject> payload = (List<DBObject>) getConnector().listIndices("Arenas");
        assertTrue(MongoHelper.indexExistsInList(payload, indexName));
    }

    @After
    public void tearDown() throws Exception {
        // Drop the created index
        String indexName = indexKey + "_" + indexOrder.getValue();
        getConnector().dropIndex("Arenas", indexName);

        // Drop the collection
        getConnector().dropCollection("Arenas");
    }
}
