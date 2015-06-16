/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class FindOneObjectUsingQueryMapTestCases extends AbstractMongoTest {

    private Map<String, Object> elementAttributes = new HashMap<String, Object>();

    @Before
    public void setUp() {
        getConnector().createCollection("Arenas", false, 5, 5);
        elementAttributes.put("myKey", "myValue");
        getConnector().saveObjectFromMap("Arenas", elementAttributes, WriteConcern.SAFE);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testFindOneObjectUsingQueryMap() {
        Document document = getConnector().findOneObjectUsingQueryMap("Arenas", elementAttributes, null, false);
        assertTrue(document.get("myKey").equals("myValue"));
    }
}
