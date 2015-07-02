/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.types.BSONTimestamp;
import org.mule.module.mongo.api.MongoClient;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDump extends AbstractMongoUtility {

    public static final String TIMESTAMP_FORMAT = "'.'yyyy-MM-dd-HH-mm";

    private final MongoClient mongoClient;
    private boolean zip;
    private boolean oplog;
    private final Map<String, MongoDatabase> dbs = new HashMap<>();
    private MongoCollection<Document> oplogCollection;
    private BSONTimestamp oplogStart;

    public MongoDump(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void dump(final String outputDirectory, final String database, String outputName, final int threads) throws IOException {
        Validate.notNull(outputDirectory);
        Validate.notNull(outputName);
        Validate.notNull(database);

        String opName = outputName;
        opName += appendTimestamp();

        initOplog(database);

        final MongoIterable<String> collections = mongoClient.listCollections();
        if (collections != null) {
            final ExecutorService executor = Executors.newFixedThreadPool(threads);
            final DumpWriter dumpWriter = new BsonDumpWriter(outputDirectory, opName);
            for (final String collectionName : collections) {
                final MongoCollection<Document> dbCollection = mongoClient.getCollection(collectionName);
                final MongoDumpCollection dumpCollection = new MongoDumpCollection(dbCollection);
                dumpCollection.setDumpWriter(dumpWriter);

                final Future<Void> future = executor.submit(dumpCollection);
                propagateException(future);
            }

            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }

                if (oplog) {
                    final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
                    final MongoDumpCollection dumpCollection = new MongoDumpCollection(oplogCollection);
                    dumpCollection.setName(BackupConstants.OPLOG);
                    dumpCollection.setOplogReplay(true);
                    final Document query = new Document();
                    query.put(BackupConstants.TIMESTAMP_FIELD, new BasicDBObject("$gt", oplogStart));
                    // Filter only oplogs for given database
                    query.put(BackupConstants.NAMESPACE_FIELD, BackupUtils.getNamespacePattern(database));
                    dumpCollection.setQuery(query);
                    dumpCollection.setDumpWriter(dumpWriter);
                    final Future<Void> future = singleExecutor.submit(dumpCollection);
                    propagateException(future);
                }

                if (zip) {
                    final String dbDumpPath = outputDirectory + File.separator + opName;
                    ZipUtils.zipDirectory(dbDumpPath);
                    FileUtils.deleteDirectory(new File(dbDumpPath));
                }
            } catch (final InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void initOplog(final String database) throws IOException {
        if (oplog) {
            oplogCollection = new OplogCollection(dbs.get(BackupConstants.ADMIN_DB), dbs.get(BackupConstants.LOCAL_DB)).getOplogCollection();
            // Filter for oplogs for the given database
            final Document query = new Document(BackupConstants.NAMESPACE_FIELD, BackupUtils.getNamespacePattern(database));
            final FindIterable<Document> oplogCursor = oplogCollection.find(query);
            oplogCursor.sort(new Document("$natural", -1));
            MongoCursor<Document> iterator = oplogCursor.iterator();
            if (iterator.hasNext()) {
                oplogStart = ((BSONTimestamp) iterator.next().get("ts"));
            }
        }
    }

    private String appendTimestamp() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    public void setZip(final boolean zip) {
        this.zip = zip;
    }

    public void setOplog(final boolean oplog) {
        this.oplog = oplog;
    }

    public void addDB(final MongoDatabase db) {
        dbs.put(db.getName(), db);
    }
}
