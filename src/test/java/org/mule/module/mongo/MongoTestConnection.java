/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;

public class MongoTestConnection
{
    private MongoCloudConnector connector;
    
    @Before
    public void setup() throws Exception
    {
        connector = new MongoCloudConnector();
        connector.getStrategy().setHost("127.0.0.1");
    }
    
    @After
	public void tearDown() throws Exception {
			connector.getStrategy().disconnect();
    }
    
    @Test
    public void connectionIncorrectPort() throws ConnectionException
    {
    	connector.getStrategy().setPort(32589);
        assertTrue(!isConnected("admin","","test"));
    }
    
    @Test
    public void connectionIncorrectCredentials()
    {
    	connector.getStrategy().setPort(27017);
        assertTrue(!isConnected("admin","zdrgdr","test"));
    }
    
    @Test
    public void validConnection()
    {
        connector.getStrategy().setPort(27017);
        assertTrue(isConnected("admin","","test"));
    }
    
    private boolean isConnected(String user,String pass,String db){
    	try {
			connector.getStrategy().connect(user, pass, db);
		} catch (ConnectionException e) {
			return false;
		}
		return true;
    }
}
