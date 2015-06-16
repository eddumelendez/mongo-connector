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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.automation.AbstractMongoTest;
import org.mule.module.mongo.automation.RegressionTests;

public class MapReduceObjectsTestCases extends AbstractMongoTest {

    @Before
    public void setUp() {
        // Create the collection
        getConnector().createCollection("Arenas", false, 5, 5);

        // Create sample objects with which we can map reduce
        List<Document> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Document obj = new Document("item", "apple");
            objects.add(obj);
        }
        for (int i = 0; i < 5; i++) {
            Document obj = new Document("item", "orange");
            objects.add(obj);
        }

        // Insert the objects into the collection
        insertObjects(objects, "Arenas");
    }

    @Category({ RegressionTests.class })
    @Test
    public void testMapReduceObjects() {

        MongoCollection resultCollection = (MongoCollection) getConnector().mapReduceObjects("Arenas", "function() { emit(this.item, 1); }",
                "function(key, values) { var result = 0;    values.forEach(function(value) { result += 1 }); return {count: result}; }", "resultCollection");
        assertTrue(resultCollection != null);
        assertTrue(resultCollection.size() == 2); // We only have apples and oranges

        for (Document obj : resultCollection) {
            Document valueObject = (Document) obj.get("value");
            assertNotNull(valueObject);
            if (obj.get("_id").equals("apple")) {
                assertTrue(valueObject.containsKey("count"));
                assertTrue((Double) valueObject.get("count") == 10); // map reduce returns doubles, typecast to Double and compare
            } else {
                if (obj.get("_id").equals("orange")) {
                    assertTrue(valueObject.containsKey("count"));
                    assertTrue((Double) valueObject.get("count") == 5); // map reduce returns doubles, typecast to Double and compare
                } else {
                    fail();
                }
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        // drop the collection
        getConnector().dropCollection("Arenas");

        // drop the output collection
        getConnector().dropCollection("resultCollection");
    }
}
