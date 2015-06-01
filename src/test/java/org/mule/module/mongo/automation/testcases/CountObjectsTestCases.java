/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoMarianoTestParent;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;

public class CountObjectsTestCases extends MongoMarianoTestParent {

	private Integer numObjects = 5;

	@Before
	public void setUp() throws Exception {
		// Create collection
		getConnector().dropCollection("Arenas");
	}

	@After
	public void tearDown() throws Exception {
		// Delete collection
		getConnector().dropCollection("Arenas");

	}

	@Category({ RegressionTests.class })
	@Test
	public void testCountObjects() {

		insertObjects(getEmptyDBObjects(numObjects),"Arenas");

		assertEquals((long) numObjects, getConnector().countObjects("Arenas", new BasicDBObject()));

	}

}
