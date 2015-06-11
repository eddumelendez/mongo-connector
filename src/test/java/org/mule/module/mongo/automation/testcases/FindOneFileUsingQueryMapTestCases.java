/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.DBObject;

public class FindOneFileUsingQueryMapTestCases extends AbstractMongoTest {

    private Map<String, Object> queryAttributes = new HashMap<String, Object>();

    @Override
    public void setUp() {
        createFileFromPayload("file1");
        createFileFromPayload("file2");
        createFileFromPayload("file3");
        queryAttributes.put("filename", "file1");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindOneFileUsingQueryMap() {
        DBObject dbObj = getConnector().findOneFileUsingQueryMap(queryAttributes);

        assertEquals("The file found should have the name file1", queryAttributes.get("filename"), dbObj.get("filename"));
        assertEquals("There should be 3 files in total", 3, findFiles(null));
    }
}
