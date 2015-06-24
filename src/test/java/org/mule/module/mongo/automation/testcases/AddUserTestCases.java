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

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class AddUserTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
    }

    @Category({ RegressionTests.class })
    @Test
    public void testAddUser() {
        Document result = getConnector().addUser("newUsername", "newPassword");

        assertNotNull(result);
        assertEquals(result.get("ok"), 1.0);
    }

    @After
    public void tearDown() {
        getConnector().executeCommand("dropUser", "newUsername");
    }
}
