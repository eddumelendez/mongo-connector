/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.types.BSONTimestamp;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class IncrementalOplogDump implements Callable<Void> {

    private static final String INCREMENTAL_LAST_TIMESTAMP = "incremental_last_timestamp.txt";

    private Map<String, MongoDatabase> dbs = new HashMap<>();
    private String incrementalTimestampFile;
    private String outputDirectory;
    private String database;

    @Override
    public Void call() throws Exception {
        dump(outputDirectory, database);
        return null;
    }

    private void dump(String outputDirectory, String database) throws IOException {
        Validate.notNull(outputDirectory);
        Validate.notNull(database);

        String incrementalFilePath = incrementalTimestampFile != null ? incrementalTimestampFile : outputDirectory + File.separator + INCREMENTAL_LAST_TIMESTAMP;
        BSONTimestamp lastTimestamp = getLastTimestamp(incrementalFilePath);

        MongoCollection<Document> oplogCollection = new OplogCollection(dbs.get(BackupConstants.ADMIN_DB), dbs.get(BackupConstants.LOCAL_DB)).getOplogCollection();
        FindIterable<Document> oplogCursor;
        if (lastTimestamp != null) {
            Document query = new Document();
            query.put(BackupConstants.TIMESTAMP_FIELD, new BasicDBObject("$gt", lastTimestamp));
            // Filter only oplogs for given database
            query.put(BackupConstants.NAMESPACE_FIELD, BackupUtils.getNamespacePattern(database));

            oplogCursor = oplogCollection.find(query);
            oplogCursor.oplogReplay(true);
        } else {
            oplogCursor = oplogCollection.find();
        }

        DumpWriter dumpWriter = new BsonDumpWriter(outputDirectory);
        String oplogCollectionTimestamp = BackupConstants.OPLOG + appendTimestamp();

        try {
            MongoCursor<Document> iterator = oplogCursor.iterator();
            while (iterator.hasNext()) {
                Document oplogEntry = iterator.next();
                lastTimestamp = (BSONTimestamp) oplogEntry.get("ts");
                dumpWriter.writeObject(oplogCollectionTimestamp, oplogEntry);
            }
        } finally {
            writeLastTimestamp(incrementalFilePath, lastTimestamp);
        }
    }

    private BSONTimestamp getLastTimestamp(String incrementalFilePath) throws IOException {
        File incrementalFile = new File(incrementalFilePath);
        if (!incrementalFile.exists()) {
            return null;
        }
        try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(incrementalFile), "UTF-8"))) {
            String line = input.readLine();
            String[] parts = line.split("\\|");
            return new BSONTimestamp(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

        } catch (NullPointerException ne) {
            throw new RuntimeException(ne.getMessage(), ne);
        }
    }

    private void writeLastTimestamp(String incrementalFilePath, BSONTimestamp lastTimestamp) throws IOException {
        if (lastTimestamp != null) {
            Writer writer = null;
            try {
                OutputStream outputStream = new FileOutputStream(new File(incrementalFilePath));
                writer = new OutputStreamWriter(outputStream, "UTF-8");
                writer.write(lastTimestamp.getTime() + "|" + lastTimestamp.getInc());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }

        }
    }

    private String appendTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MongoDump.TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    public void setDBs(Map<String, MongoDatabase> dbs) {
        this.dbs.putAll(dbs);
    }

    public void setIncrementalTimestampFile(String incrementalTimestampFile) {
        this.incrementalTimestampFile = incrementalTimestampFile;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
