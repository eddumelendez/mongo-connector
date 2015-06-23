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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.mule.module.mongo.api.MongoClient;

import com.mongodb.client.MongoCollection;

public class MongoRestoreDirectory implements Callable<Void> {

    private MongoClient mongoClient;
    private boolean drop;
    private boolean oplogReplay;
    private String inputPath;
    private String database;

    @Override
    public Void call() throws Exception {
        restore();
        return null;
    }

    private void restore() throws IOException {
        Validate.notNull(inputPath);
        List<RestoreFile> restoreFiles = getRestoreFiles(inputPath);
        List<RestoreFile> oplogRestores = new ArrayList<>();
        for (RestoreFile restoreFile : restoreFiles) {
            String collection = restoreFile.getCollection();
            if (!isOplog(collection)) {
                if (drop && !BackupUtils.isSystemCollection(collection)) {
                    mongoClient.dropCollection(collection);
                }

                MongoCollection<Document> dbCollection = mongoClient.getCollection(collection);
                List<Document> dbObjects = restoreFile.getCollectionObjects();

                if (BackupUtils.isUserCollection(collection)) {
                    for (Document currentDocument : dbCollection.find()) {
                        if (!dbObjects.contains(currentDocument)) {
                            dbCollection.findOneAndDelete(currentDocument);
                        }
                    }
                }

                dbCollection.insertMany(dbObjects);
            } else {
                oplogRestores.add(restoreFile);
            }
        }
        if (oplogReplay && !oplogRestores.isEmpty()) {
            for (RestoreFile oplogRestore : oplogRestores) {
                mongoClient.executeCommand(new Document("applyOps", filterOplogForDatabase(oplogRestore).toArray()));
            }
        }
    }

    private List<Document> filterOplogForDatabase(RestoreFile oplogFile) throws IOException {
        List<Document> oplogEntries = oplogFile.getCollectionObjects();
        List<Document> dbOplogEntries = new ArrayList<>();

        for (Document oplogEntry : oplogEntries) {
            if (((String) oplogEntry.get(BackupConstants.NAMESPACE_FIELD)).startsWith(database + ".")) {
                dbOplogEntries.add(oplogEntry);
            }
        }

        return dbOplogEntries;
    }

    private void processRestoreFiles(File input, List<RestoreFile> restoreFiles) throws IOException {
        File unzippedFolder;
        if (ZipUtils.isZipFile(input)) {
            unzippedFolder = new File(BackupUtils.removeExtension(input.getPath()));
            org.mule.util.FileUtils.unzip(input, unzippedFolder);
        } else {
            unzippedFolder = input;
        }

        if (unzippedFolder.isDirectory()) {
            for (File file : unzippedFolder.listFiles()) {
                processRestoreFiles(file, restoreFiles);
            }
        } else if (BackupUtils.isBsonFile(unzippedFolder)) {
            restoreFiles.add(new RestoreFile(unzippedFolder));
        }
    }

    private List<RestoreFile> getRestoreFiles(String inputPath) throws IOException {
        List<RestoreFile> restoreFiles = new ArrayList<RestoreFile>();
        processRestoreFiles(new File(inputPath), restoreFiles);
        Collections.sort(restoreFiles);
        return restoreFiles;
    }

    private boolean isOplog(String collection) {
        return collection.startsWith(BackupConstants.OPLOG);
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public void setOplogReplay(boolean oplogReplay) {
        this.oplogReplay = oplogReplay;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

}
