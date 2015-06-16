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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.google.common.collect.Iterables;
import com.mongodb.DBObject;

public class FindFilesUsingQueryMapTestCases extends AbstractMongoTest {

    Map<String, Object> queryAttributes = new HashMap<String, Object>();

    @Before
    public void setUp() {
        createFileFromPayload("file1");
        createFileFromPayload("file2");
        queryAttributes.put("filename", "file2");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindFilesUsingQueryMap() {
        // queryAttribKey and queryAttribVal in testObjects are used in
        // findFilesUsingQueryMapFlow to query for a file with filename of
        // 'file2'
        // One such file should be found

        Iterable<DBObject> iterable = getConnector().findFilesUsingQueryMap(queryAttributes);
        int filesFoundUsingQueryMap = Iterables.size(iterable);

        assertEquals("There should be 1 file with the name file2", 1, filesFoundUsingQueryMap);
        assertEquals("There should be 2 files in total", 2, findFiles(null));
    }
}
