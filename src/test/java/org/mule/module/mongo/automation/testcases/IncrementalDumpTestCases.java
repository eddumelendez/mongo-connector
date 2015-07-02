/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class IncrementalDumpTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
        insertObjects(getEmptyDocuments(10), "Arenas");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("./dump"));
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testIncrementalDump() throws IOException {

        getConnector().dump("./dump", "test", false, false, 5);
        getConnector().incrementalDump("./dump", "footime");
        File dumpOutputDir = new File("./dump");
        assertTrue("dump directory should exist after test runs", dumpOutputDir.exists());
    }
}
