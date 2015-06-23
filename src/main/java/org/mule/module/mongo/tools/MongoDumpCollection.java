/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class MongoDumpCollection implements Callable<Void> {

    private final MongoCollection<Document> collection;
    private DumpWriter dumpWriter;
    private Document query;
    private String name;

    public MongoDumpCollection(final MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public Void call() throws Exception {
        final FindIterable<Document> cursor = query != null ? collection.find(query) : collection.find();
        cursor.sort(new Document("_id", 1));
        cursor.oplogReplay(true);
        Iterator<Document> iterator = cursor.iterator();
        while (iterator.hasNext()) {
            final Document document = iterator.next();
            dumpWriter.writeObject(name != null ? name : collection.getNamespace().getCollectionName(), document);
        }
        return null;
    }

    public void setDumpWriter(final DumpWriter dumpWriter) {
        this.dumpWriter = dumpWriter;
    }

    public void setQuery(final Document query) {
        this.query = query;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
