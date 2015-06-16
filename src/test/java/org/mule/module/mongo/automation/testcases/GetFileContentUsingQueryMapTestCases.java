/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class GetFileContentUsingQueryMapTestCases extends AbstractMongoTest {

    private Map<String, Object> queryAttributes = new HashMap<String, Object>();

    @Before
    public void setUp() {
        createFileFromPayload("filename1");
        createFileFromPayload("filename2");
        queryAttributes.put("filename", "filename1");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testGetFileContentUsingQueryMap() {
        Object response = getConnector().getFileContentUsingQueryMap(queryAttributes);
        assertNotNull(response);
        assertTrue(response instanceof InputStream);
    }
}
