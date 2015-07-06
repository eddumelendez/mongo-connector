/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the Connector when Authenticated. This test is only meaningful when server has being started with --auth argument, and a user has being created:
 *
 * <pre>
 * db.addUser(username, password)
 * SSL argument:-Djavax.net.ssl.trustStore
 * </pre>
 *
 */
public class MongoAuthenticationTestDriver {

    private MongoCloudConnector connector;

    /**
     * Setups an authenticated connector
     */
    @Before
    public void setup() throws Exception {
        connector = new MongoCloudConnector();
        final ConnectionManagementStrategy strategy = new ConnectionManagementStrategy();
        connector.setStrategy(strategy);
        connector.getStrategy().setHost("127.0.0.1");
        connector.getStrategy().setPort(27017);
    }

    @Test
    public void createCollectionwithoutSsl() throws Exception {
        connector.getStrategy().setSsl(false);
        connector.getStrategy().connect("admin", "admin", "test");
        assertNotNull(connector.listCollections());
    }

    @Test
    public void createCollectionUsingSsl() throws Exception {
        connector.getStrategy().setSsl(true);
        connector.getStrategy().setSslInvalidHostNameAllowed(true);
        connector.getStrategy().connect("admin", "admin", "test");
        assertNotNull(connector.listCollections());
    }

}
