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
import com.mongodb.DBObject;

public class RemoveFilesTestCases extends AbstractMongoTest {

    private DBObject query = new BasicDBObject();

    @Override
    public void setUp() {
        createFileFromPayload("filename1");
        createFileFromPayload("filename1");
        createFileFromPayload("filename2");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveFiles_emptyQuery() {
        getConnector().removeFiles(query);
        assertEquals("There should be 0 files found after remove-files with an empty query", 0, findFiles(null));
    }

    // For some reason, when running all test cases together, this test fails sometimes (not always). When only the RemoveFilesTestCases is executed, both tests pass
    @Category({ RegressionTests.class })
    @Test
    public void testRemoveFiles_nonemptyQuery() {
        query.put("filename", "filename1");
        getConnector().removeFiles(query);

        assertEquals("There should be 1 files found after remove-files with a non-empty query, which deletes all files of name " + query.keySet().toString(), 1, findFiles(null));
    }
}
