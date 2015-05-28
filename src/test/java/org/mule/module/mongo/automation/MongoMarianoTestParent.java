package org.mule.module.mongo.automation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.tools.devkit.ctf.mockup.ConnectorDispatcher;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class MongoMarianoTestParent {

	private MongoCloudConnector connector;
	private ConnectorDispatcher<MongoCloudConnector> dispatcher;

	@Rule
    public Timeout globalTimeout = new Timeout(600000);

	protected MongoCloudConnector getConnector() {
		return connector;
	}

	protected List<DBObject> getEmptyDBObjects(int num) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (int i = 0; i < num; i++) {
			list.add(new BasicDBObject());
		}
		return list;
	}

	@SuppressWarnings("static-access")
	protected void insertObjects(List<DBObject> objs, String collection) {
		WriteConcern concern = null;
		for (DBObject obj : objs) {
			connector.insertObject(collection, obj, concern.DATABASE_DEFAULT);
		}

	}

	// Returns all number of all files in database as per find-files operation
	protected int findFiles(DBObject query) {
		int size = 0;
		if(query == null)
		{
			Iterable<DBObject> iterable = null;

			iterable = connector.findFiles(query);

			for(DBObject dbObj : iterable) {
				if(dbObj.containsField("filename")) {
					size++;
				}
			}
			return size;
		}
		return 0;


	}

	@Before
	public void init() throws Exception {

		//Current context instance
	    ConnectorTestContext<MongoCloudConnector> context = ConnectorTestContext.getInstance(MongoCloudConnector.class);

		//Connector dispatcher
		dispatcher = context.getConnectorDispatcher();

		connector = dispatcher.createMockup();

	}

}
