/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.RegressionTests;

public class MongoCollectionUnitTest {

    private Iterable<? extends Document> o;
    MongoCollection<Document> mo;

    @Category({ RegressionTests.class })
    @Test
    public void collectionToString() {
        mo = new MongoCollection<>(o);
        String className = mo.toString();
        className = className.substring(0, className.indexOf("@"));
        assertEquals("This should be class name", "org.mule.module.mongo.api.MongoCollection", className);
    }

    @Category({ RegressionTests.class })
    @Test
    public void toArray() {
        List<Document> obj = new ArrayList<>();
        Document o = new Document();
        // Fill with sample data
        o.put("foo", "bar");
        obj.add(o);
        // Create a new instance of MongoCollection
        mo = new MongoCollection<Document>(obj);
        Object test = mo.toArray();

        assertTrue(test instanceof Object[]);
        assertTrue("Array length should be 1", mo.size() == 1);
    }
}
