/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class ListCollectionTestCases extends AbstractMongoTest {

    private List<String> collectionNames = new LinkedList<String>();

    @Before
    public void setUp() {
        collectionNames.add("FirstCollection");
        collectionNames.add("SecondCollection");
        collectionNames.add("ThirdCollection");

        for (String collectionName : collectionNames) {
            getConnector().createCollection(collectionName, false, 1, 1);
        }
    }

    @Category({ RegressionTests.class })
    @Test
    public void testListCollections() {
        Collection<String> payload = getConnector().listCollections();

        for (String collectionName : collectionNames) {
            assertTrue(payload.contains(collectionName));
        }
    }

    @After
    public void tearDown() throws Exception {
        for (String collectionName : collectionNames) {
            getConnector().dropCollection(collectionName);
        }
    }
}
