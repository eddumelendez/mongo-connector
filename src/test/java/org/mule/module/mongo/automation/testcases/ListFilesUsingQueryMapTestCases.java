/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.DBObject;

public class ListFilesUsingQueryMapTestCases extends AbstractMongoTest {

    private Map<String, Object> queryAttributes = new HashMap<String, Object>();

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
    public void testListFilesUsingQueryMap_emptyQuery() {
        Iterable<DBObject> response = getConnector().listFilesUsingQueryMap(queryAttributes);

        assertNotNull(response);
        assertTrue(response instanceof Iterable);

        assertEquals("An empty query map for the query should list all the files", 3, MongoHelper.getIterableSize(response));
    }

    @Category({ RegressionTests.class })
    @Test
    public void testListFilesUsingQueryMap_nonemptyQuery() {
        queryAttributes.put("filename", "filename1");
        Iterable<DBObject> response = getConnector().listFilesUsingQueryMap(queryAttributes);

        assertNotNull(response);
        assertTrue(response instanceof Iterable);
        assertEquals("Listing files with a query with key " + queryAttributes.keySet().toString() + " and value " + queryAttributes.get("filename") + " should give 2 results", 2,
                MongoHelper.getIterableSize(response));
    }
}
