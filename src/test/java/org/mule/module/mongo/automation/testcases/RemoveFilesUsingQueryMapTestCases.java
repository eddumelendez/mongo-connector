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

public class RemoveFilesUsingQueryMapTestCases extends AbstractMongoTest {

    private Map<String, Object> queryAttributes = new HashMap<String, Object>();

    @Before
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
    public void testRemoveFilesUsingQueryMap_emptyQuery() {
        getConnector().removeFilesUsingQueryMap(queryAttributes);
        assertEquals("There should be 0 files found after remove-files-using-query-map with an empty query", 0, findFiles(null));
    }

    @Category({ RegressionTests.class })
    @Test
    public void testRemoveFilesUsingQueryMap_nonemptyQuery() {
        queryAttributes.put("filename", "filename1");
        getConnector().removeFilesUsingQueryMap(queryAttributes);
        assertEquals("There should be 1 files found after removing files with a filename of " + queryAttributes.get("filename"), 1, findFiles(null));
    }
}
