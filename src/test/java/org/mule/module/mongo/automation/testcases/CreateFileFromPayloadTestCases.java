/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;

public class CreateFileFromPayloadTestCases extends AbstractMongoTest {

    private DBObject dbObj;

    @Override
    @Before
    public void setUp() {
        dbObj = new BasicDBObject();
        dbObj.put("filename", "file1");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testCreateFileFromPayload() throws IOException {

        getConnector().dropCollection("Arenas");
        assertEquals("There should be 0 files found before create-file-from-payload", 0, findFiles(new BasicDBObject()));

        GridFSInputFile res = createFileFromPayload(dbObj, "file1");

        assertEquals("The created file should be named file1", "file1", res.getFilename());
        assertEquals("There should be 1 files found after create-file-from-payload", 1, findFiles(dbObj));
    }
}
