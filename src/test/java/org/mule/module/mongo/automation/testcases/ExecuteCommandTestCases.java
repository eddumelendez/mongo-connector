/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.CommandResult;

public class ExecuteCommandTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        // Create a collection
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testExecuteCommand() {
        // Drop the collection using command
        CommandResult cmdResult = (CommandResult) getConnector().executeCommand("drop", "Arenas");
        assertTrue(cmdResult.ok());

        Boolean exists = getConnector().existsCollection("Arenas");
        assertFalse(exists);
    }
}
