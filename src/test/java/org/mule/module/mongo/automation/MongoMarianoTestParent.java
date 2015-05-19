package org.mule.module.mongo.automation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.tools.devkit.ctf.mockup.ConnectorDispatcher;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

public abstract class MongoMarianoTestParent {

	private MongoCloudConnector connector;
	private ConnectorDispatcher<MongoCloudConnector> dispatcher;

	@Rule
    public Timeout globalTimeout = new Timeout(600000);

	protected MongoCloudConnector getConnector() {
		return connector;
	}

	@Before
	public void init() throws Exception {

		//Current context instance
	    ConnectorTestContext<MongoCloudConnector> context = ConnectorTestContext.getInstance(MongoCloudConnector.class);

		//Connector dispatcher
		dispatcher = context.getConnectorDispatcher();

		connector = dispatcher.createMockup();

		setUp();

	}

	protected void setUp() throws Exception {
	}
}
