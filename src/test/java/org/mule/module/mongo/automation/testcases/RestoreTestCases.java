/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class RestoreTestCases extends AbstractMongoTest {

    private String indexKey = "myField";
    private IndexOrder indexOrder = IndexOrder.ASC;

    @Override
    public void setUp() {

        getConnector().createCollection("Arenas", false, 5, 5);

        String indexName = MongoHelper.getIndexName(indexKey, indexOrder);

        getConnector().createIndex("Arenas", indexKey, indexOrder);
        try {
            getConnector().dump("dump", "Test", false, false, 5);
        } catch (IOException io) {
            throw new RuntimeException(io.getMessage(), io);
        }

        // drop index
        getConnector().dropIndex("Arenas", indexName);

    }

    @After
    public void tearDown() throws Exception {
        File dumpOutputDir = new File("./dump");
        FileUtils.deleteDirectory(dumpOutputDir);
        String indexName = MongoHelper.getIndexName(indexKey, indexOrder);

        // drop index
        getConnector().dropIndex("Arenas", indexName);

        // Need to drop the collection becuase creating the index creates the collection
        getConnector().dropCollection("Arenas");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testRestore() throws IOException {

        getConnector().restore("dump", false, false);

        String indexName = MongoHelper.getIndexName(indexKey, indexOrder);

        List<Document> payload = (List<Document>) getConnector().listIndices("Arenas");

        assertTrue("After restoring the database, the index with index name = " + indexName + " should exist", MongoHelper.indexExistsInList(payload, indexName));

    }

}
