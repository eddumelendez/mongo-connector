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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FindOneFileTestCases extends AbstractMongoTest {

    private DBObject query = new BasicDBObject("filename", "file1");

    @Before
    public void setUp() {
        createFileFromPayload("file1");
        createFileFromPayload("file2");
        createFileFromPayload("file3");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindOneFile() {
        DBObject dbObj = getConnector().findOneFile(query);

        assertEquals("The file found should have the name file1", query.get("filename"), dbObj.get("filename"));
        assertEquals("There should be 3 files in total", 3, findFiles(null));
    }
}
