/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoMarianoTestParent;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CountObjectsUsingQueryMapTestCases extends MongoMarianoTestParent {

	private Integer numObjects = 5;

	@Before
	public void setUp() throws Exception {
		// Create collection
		getConnector().createCollection("Arenas", false, numObjects, numObjects);

	}

	@After
	public void tearDown() throws Exception {
		// Delete collection
		getConnector().dropCollection("Arenas");

	}

	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_without_map() {
		insertObjects(getEmptyDBObjects(numObjects),"Arenas");

		assertEquals((long) numObjects, getConnector().countObjects("Arenas", new BasicDBObject()));

	}

	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_with_map() {
		List<DBObject> list = getEmptyDBObjects(2);
		Map<String, Object> data = new HashMap<String, Object>();

		String queryAttribKey = "foo";
		String queryAttribVal = "bar";

		DBObject dbObj = new BasicDBObject();
		dbObj.put(queryAttribKey, queryAttribVal);
		list.add(dbObj);
		data.put(queryAttribKey, queryAttribVal);

		insertObjects(list,"Arenas");

		assertEquals(new Long(1), (Long) getConnector().countObjectsUsingQueryMap("Arenas", data));

	}

}
