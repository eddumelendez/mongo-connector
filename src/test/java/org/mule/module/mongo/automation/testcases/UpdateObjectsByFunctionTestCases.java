/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

import com.google.common.collect.Iterables;

public class UpdateObjectsByFunctionTestCases extends AbstractMongoTest {

    private int numberOfObjects = 10;
    private Document queryDBObj = new Document();

    @Before
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);

        queryDBObj.put("key", "oldvalue");

        // Create the objects with the key-value pair
        List<Document> objects = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            objects.add(new Document(queryDBObj));
        }

        // Insert the objects
        insertObjects(objects, "Arenas");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testUpdateObjectsByFunction() {
        String queryKey = "key";
        Document elementDbObj = new Document("key", "newValue");

        // Update objects
        getConnector().updateObjectsByFunction("Arenas", "$set", queryDBObj, elementDbObj, false, true);

        // Get all objects
        Iterable<Document> objects = getConnector().findObjects("Arenas", new Document(), null, null, null, null);
        for (Document obj : objects) {
            assertTrue(obj.containsKey(queryKey));
            assertTrue(obj.get(queryKey).equals(elementDbObj.get(queryKey)));
        }
        assertTrue(Iterables.size(objects) == numberOfObjects);
    }

    @After
    public void tearDown() throws Exception {
        getConnector().dropCollection("Arenas");
    }

}
