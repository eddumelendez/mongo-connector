/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jersey.repackaged.com.google.common.collect.Iterables;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoCollection extends AbstractCollection<Document> {

    private static final Logger logger = LoggerFactory.getLogger(MongoCollection.class);
    private Iterable<? extends Document> o;

    public MongoCollection(Iterable<? extends Document> o) {
        this.o = o;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Document> iterator() {
        return (Iterator<Document>) o.iterator();
    }

    @Override
    public Object[] toArray() {
        warnEagerMessage("toArray");
        List<Object> l = new LinkedList<Object>();
        for (Object o : this) {
            l.add(o);
        }
        return l.toArray();
    }

    @Override
    public int size() {
        warnEagerMessage("size");
        return Iterables.size(o);
    }

    /**
     * Same impl that those found in Object, in order to avoid eager elements consumption
     */
    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Warns that sending the given message implied processing all the elements, which is not efficient at all, and most times is a bad idea, as lazy iterables should be traversed
     * only once and in a lazy manner.
     * 
     * @param message
     */
    private void warnEagerMessage(String message) {
        if (logger.isWarnEnabled()) {
            logger.warn("Method {} needs to consume all the element. It is inefficient and thus should be used with care", message);
        }
    }

}
