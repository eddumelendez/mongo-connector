/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.DBObject;

public class MongoCollectionUnitTest
{
    private Iterable<? extends DBObject> o;
    MongoCollection mo = new MongoCollection(o);
    
    @Category({RegressionTests.class})
    @Test
    public void collectionToString() 
    {
        String className = mo.toString();
        className = className.substring(0, className.indexOf("@"));
        assertEquals("This should be class name","org.mule.module.mongo.api.MongoCollection",className);
    }
}
