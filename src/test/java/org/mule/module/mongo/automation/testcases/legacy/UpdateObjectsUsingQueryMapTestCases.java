/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases.legacy;

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
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsUsingQueryMapTestCases extends MongoTestParent {
	

	@Before
	public void setUp() throws Exception {
			// Create the collection
			initializeTestRunMessage("updateObjectsUsingQueryMap");
			runFlowAndGetPayload("create-collection");

			String queryKey = (String) getTestRunMessageValue("queryKey");
			String queryValue = (String) getTestRunMessageValue("queryValue");
			int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");
			
			// Create the objects with the key-value pair
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				DBObject object = new BasicDBObject(queryKey, queryValue);
				objects.add(object);
			}
			
			// Insert the objects
			insertObjects(objects);
			

	}

	@Category({RegressionTests.class})
	@Test
	public void testUpdateObjectsUsingQueryMap() {
		try {
			DBObject dbObj = (DBObject) getTestRunMessageValue("dbObject");
			DBObject elementDbObj = (DBObject) dbObj.get("$set");
			String queryKey = (String) getTestRunMessageValue("queryKey");
			int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");
			
			// Update objects
			runFlowAndGetPayload("update-objects-using-query-map");
			
			// Get all objects	
			MongoCollection objects = runFlowAndGetPayload("find-objects");
			for (Document obj : objects) {
				assertTrue(obj.containsKey(queryKey));
				assertTrue(obj.get(queryKey).equals(elementDbObj.get(queryKey)));
			}
			assertTrue(objects.size() == numberOfObjects);
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }	

	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");


	}

	
}
