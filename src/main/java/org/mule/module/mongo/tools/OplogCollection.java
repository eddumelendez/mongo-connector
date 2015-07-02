/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class OplogCollection {

    private static final String MASTER_OPLOG = "$main";
    private static final String REPLICA_OPLOG = "rs";
    private static final String IS_MASTER_FIELD = "ismaster";

    private MongoDatabase admin;
    private MongoDatabase local;

    public OplogCollection(MongoDatabase admin, MongoDatabase local) {
        Validate.notNull(admin);
        Validate.notNull(local);

        this.admin = admin;
        this.local = local;
    }

    public MongoCollection<Document> getOplogCollection() throws IOException {
        String oplogCollectionName = BackupConstants.OPLOG + ".";
        oplogCollectionName += isMaster() ? MASTER_OPLOG : REPLICA_OPLOG;

        return local.getCollection(oplogCollectionName);
    }

    private boolean isMaster() throws IOException {
        // Validate we are on master or replica
        Document commandResult = admin.runCommand(new Document(IS_MASTER_FIELD, 1));
        boolean isMaster = commandResult.getBoolean(IS_MASTER_FIELD, false);

        // Replica set member
        if (commandResult.containsKey("hosts")) {
            return false;
        } else {
            if (!isMaster) {
                throw new IOException("oplog mode is only supported on master or replica set member");
            }
            return true;
        }

    }

}
