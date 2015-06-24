/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.RegressionTests;

import com.google.common.collect.ImmutableMap;

public class DBObjectsUnitTest {

    @Category({ RegressionTests.class })
    @Test
    public void fromNull() throws Exception {
        assertNull(DBObjects.from(null));
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromMap() throws Exception {
        Document map = DBObjects.from(ImmutableMap.of("key1",  4, "key2", Collections.singletonMap("key3", 9)));
        assertEquals(4, map.get("key1"));
        assertThat(map.get("key2"), instanceOf(Map.class));
        assertThat(map, instanceOf(Document.class));
    }

    @Category({ RegressionTests.class })
    @Test(expected = IllegalArgumentException.class)
    public void fromMapWithInteger() {
        int map = 43;
        DBObjects.from(map);
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromMapWithId() throws Exception {
        Document o = DBObjects.from(ImmutableMap.of("name",  "John", "surname", "Doe", "age", 35, "_id", 500));
        assertEquals("John", o.get("name"));
        assertEquals(500, o.get("_id"));
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromMapWithObjectId() throws Exception {
        Map<String, Object> map = ImmutableMap.<String, Object>of("name",  "John", "surname", "Doe", "age", 35, "_id", new ObjectId("4df7b8e8663b85b105725d34"));
        Document o = DBObjects.from(map);
        assertEquals("John", o.get("name"));
        assertEquals(new ObjectId("4df7b8e8663b85b105725d34"), o.get("_id"));
        assertEquals(o.keySet(), map.keySet());
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromMapWithNestedObject() throws Exception {
        final Document cat = DBObjects.from(ImmutableMap.of("name", "Garfield"));
        Document o = DBObjects.from(ImmutableMap.of("name", "Jon", "surname", "Arbuckle", "cat", cat));
        assertEquals("Jon", o.get("name"));
        assertEquals("Arbuckle", o.get("surname"));
        assertThat(o.get("cat"), instanceOf(Document.class));
        assertEquals("Garfield", ((Document) o.get("cat")).get("name"));
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromMapWithNestedList() throws Exception {
        final Document garfield = DBObjects.from(ImmutableMap.of("name", "Garfield"));
        final Document oddie = DBObjects.from(ImmutableMap.of("name", "Oddie"));
        Document o = DBObjects.from(ImmutableMap.of("name", "Jon", "surname", "Arbuckle", "pets", Arrays.asList(garfield, oddie)));
        assertEquals("Jon", o.get("name"));
        assertEquals("Arbuckle", o.get("surname"));
        assertThat(o.get("pets"), instanceOf(List.class));
        assertTrue(((List<?>) o.get("pets")).get(0) instanceof Document);
    }
}
