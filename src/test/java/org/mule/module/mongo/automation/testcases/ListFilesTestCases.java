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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ListFilesTestCases extends AbstractMongoTest {

    private DBObject query = new BasicDBObject();

    @Before
    public void setUp() {
        createFileFromPayload("filename1");
        createFileFromPayload("filename2");
        createFileFromPayload("filename1");
    }

    @After
    public void tearDown() {
        deleteFilesCreatedByCreateFileFromPayload();
    }

    @Category({ RegressionTests.class })
    @Test
    public void testListFiles_emptyQuery() {
        Iterable<DBObject> response = getConnector().listFiles(query);

        assertNotNull(response);
        assertTrue(response instanceof Iterable);
        assertEquals("An empty DBObject for the query should list all the files", 3, Iterables.size(response));
    }

    @Category({ RegressionTests.class })
    @Test
    public void testListFiles_nonemptyQuery() {
        query.put("filename", "filename1");

        Iterable<DBObject> response = getConnector().listFiles(query);

        assertNotNull(response);
        assertTrue(response instanceof Iterable);
        assertEquals("Listing files with a query with key " + query.keySet().toString() + " and value " + query.get("filename1") + " should give 2 results", 2,
                Iterables.size(response));
    }
}
