package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleMessage;

public class InsertObjectTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("insertObject");
			flow = lookupFlowConstruct("create-collection");
			flow.process(getTestEvent(testObjects));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testInsertObject() {
		try {
			flow = lookupFlowConstruct("insert-object");
			MuleMessage message = flow.process(getTestEvent(testObjects)).getMessage();
			String objectID = message.getPayload().toString();
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));						
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			flow = lookupFlowConstruct("remove-objects");
			flow.process(getTestEvent(testObjects));
			flow = lookupFlowConstruct("drop-collection");
			flow.process(getTestEvent(testObjects));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

}
