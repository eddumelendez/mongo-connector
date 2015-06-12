/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class DropCollectionTestCases extends AbstractMongoTest {

    @Override
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
    }

    @Category({ RegressionTests.class })
    @Test
    public void testDropCollection() {
        getConnector().dropCollection("Arenas");
        assertFalse(getConnector().existsCollection("Arenas"));
    }
}
