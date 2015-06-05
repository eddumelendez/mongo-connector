package org.mule.module.mongo.automation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.tck.testmodels.mule.TestConnector;
import org.mule.tools.devkit.ctf.mockup.ConnectorDispatcher;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;

public abstract class AbstractMongoTest {

    private MongoCloudConnector connector;
    private ConnectorDispatcher<MongoCloudConnector> dispatcher;

    @Rule
    public Timeout globalTimeout = new Timeout(600000);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected MongoCloudConnector getConnector() {
        return connector;
    }

    protected ConnectorDispatcher<MongoCloudConnector> getDispatcher() {
        return dispatcher;
    }

    protected List<DBObject> getEmptyDBObjects(int num) {
        List<DBObject> list = new ArrayList<DBObject>();
        for (int i = 0; i < num; i++) {
            list.add(new BasicDBObject());
        }
        return list;
    }

    protected void insertObjects(List<DBObject> objs, String collection) {
        for (DBObject obj : objs) {
            connector.insertObject(collection, obj, WriteConcern.DATABASE_DEFAULT);
        }
    }

    // Returns all number of all files in database as per find-files operation
    protected int findFiles(DBObject query) {
        int size = 0;
        if (query == null) {
            Iterable<DBObject> iterable = null;

            iterable = connector.findFiles(query);

            for (DBObject dbObj : iterable) {
                if (dbObj.containsField("filename")) {
                    size++;
                }
            }
            return size;
        }
        return 0;
    }

    // TODO
    protected GridFSInputFile createFileFromPayload(DBObject dbObj, String filename) {
        GridFSInputFile res = null;
        try {
            File file = folder.newFile(filename);

            // upsertOnTestRunMessage("filename", filename);
            // upsertOnTestRunMessage("metadataRef", dbObj);
            // upsertOnTestRunMessage("payloadContent", file);

            // res = runFlowAndGetPayload("create-file-from-payload");
            res = (GridFSInputFile) getConnector().createFileFromPayload(file, filename, "foo", dbObj);
        } catch (IOException io) {
            throw new RuntimeException(io.getMessage(), io);
        }

        return res;
    }

    protected GridFSInputFile createFileFromPayload(String filename) {
        return createFileFromPayload(new BasicDBObject(), filename);
    }

    protected GridFSInputFile createFileFromPayload(Object filename) {
        return createFileFromPayload(filename.toString());
    }

    @Before
    public void init() throws Exception {

        // Single-test runs
        ConnectorTestContext.initialize(TestConnector.class, false);

        // Current context instance
        ConnectorTestContext<MongoCloudConnector> context = ConnectorTestContext.getInstance(MongoCloudConnector.class);

        // Connector dispatcher
        connector = context.getConnectorDispatcher().createMockup();

        setUp();
    }

    @After
    public void shutdown() throws Exception {
        ConnectorTestContext.shutDown(false);
    }

    protected abstract void setUp();

}
