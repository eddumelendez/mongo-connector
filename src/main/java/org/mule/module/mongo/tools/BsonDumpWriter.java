/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;

public class BsonDumpWriter extends DumpWriter {

    private static final String BSON_EXTENSION = "bson";
    private static final Logger logger = LoggerFactory.getLogger(BsonDumpWriter.class);

    public BsonDumpWriter(String outputDirectory, String database) {
        super(outputDirectory, database);
    }

    public BsonDumpWriter(String outputDirectory) {
        super(outputDirectory);
    }

    @Override
    public String getExtension() {
        return BSON_EXTENSION;
    }

    @Override
    public void writeObject(String collection, Document document) throws IOException {
        ;
        File outputFile = new File(getFilePath(collection));
        if (!outputFile.getParentFile().mkdirs()) {
            logger.info("Couldn't create dir: " + outputFile.getParentFile());
        }
        try (FileOutputStream outputStream = new FileOutputStream(outputFile, true)) {
            BSONObject doc = new BasicDBObject(document);
            outputStream.write(BSON.encode(doc));
        }
    }
}
