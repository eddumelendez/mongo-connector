/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class DropIndexTestCases extends AbstractMongoTest {

    private String indexKey = "myField";
    private IndexOrder indexOrder;

    @Override
    public void setUp() {
        // Create the collection
        indexOrder = IndexOrder.ASC;
        getConnector().createCollection("Arenas", false, 5, 5);

        // Create the index
        getConnector().createIndex("Arenas", indexKey, indexOrder);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testDropIndexByName() {
        String indexName = indexKey + "_" + indexOrder.getValue();

        getConnector().dropIndex("Arenas", indexName);

        List<Document> payload = (List<Document>) getConnector().listIndices("Arenas");
        assertFalse(MongoHelper.indexExistsInList(payload, indexName));
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }
}
