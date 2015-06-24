/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Conversions between Json {@link String}s and {@link Map}s into {@link Document}s and {@link DBObject}s
 */
public final class DBObjects {

    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("ObjectId\\((.+)\\)");

    private DBObjects() {
    }

    /**
     * Performs a shallow conversion of a map into a Document: values of type Map will not be converted
     */
    public static Document fromMap(Map<String, Object> map) {
        return new Document(map);
    }

    @SuppressWarnings("unchecked")
    public static Document from(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Document) {
            return (Document) o;
        }
        if (o instanceof Map<?, ?>) {
            return fromMap((Map<String, Object>) o);
        }
        throw new IllegalArgumentException("Unsupported object type " + o);
    }


    public static Document fromFunction(String function, Document document) {
        return new Document(function, document);
    }

    @SuppressWarnings("unchecked")
    public static Object adapt(Object o) {
        Object obj = o;
        if (obj instanceof Document) {
            adaptObjectId((Document) obj);
            adaptAttributes((Document) obj);
        } else if (obj instanceof Map<?, ?>) {
            obj = adapt(fromMap((Map<String, Object>) o));
        } else if (obj instanceof List<?>) {
            adaptElements(obj);
        }
        return obj;
    }

    public static Document adapt(Map<String, Object> o) {
        return new Document(o);
    }

    public static DBObject adaptToDbObject(Map<String, Object> o) {
        return new BasicDBObject(o);
    }

    @SuppressWarnings("unchecked")
    private static void adaptElements(Object o) {
        for (ListIterator<Object> iter = ((List<Object>) o).listIterator(); iter.hasNext();) {
            iter.set(adapt(iter.next()));
        }
    }

    private static void adaptAttributes(Document o) {
        for (String key : o.keySet()) {
            o.put(key, adapt(o.get(key)));
        }
    }

    private static void adaptObjectId(Document o) {
        Object id = o.get("_id");

        if (id != null && id instanceof String) {
            Matcher m = objectIdMatcher((String) id);

            if (m.matches()) {
                o.put("_id", new ObjectId(m.group(1)));
            }
        }
    }

    private static Matcher objectIdMatcher(String id) {
        return OBJECT_ID_PATTERN.matcher(id);
    }

    public static DBObject documentToDbObject(Document doc) {
        return new BasicDBObject(doc);
    }
}
