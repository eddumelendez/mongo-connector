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
 * Conversions between JSon Strings and Maps into DBObjects
 */
public final class DBObjects
{
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("ObjectId\\((.+)\\)");

    private DBObjects()
    {
    }

    /**
     * Performs a shallow conversion of a map into a DBObject: values of type Map
     * will not be converted
     */
    public static DBObject fromMap(Map<String, Object> map)
    {
        return new BasicDBObject(map);
    }

    @SuppressWarnings("unchecked")
    public static DBObject from(Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof DBObject)
        {
            return (DBObject) o;
        }
        if (o instanceof Map<?, ?>)
        {
            return fromMap((Map<String, Object>) o);
        }
        throw new IllegalArgumentException("Unsupported object type " + o);
    }
    
    public static DBObject fromFunction(String function, DBObject dbObject)
    {
        return new BasicDBObject(function, dbObject);
    }
    
    public static DBObject fromCommand(String commandName, String commandValue)
    {
        DBObject dbObject;
        if (commandValue == null)
    	{
            dbObject = new BasicDBObject(commandName, 1);
    	}
    	else
    	{
            dbObject = new BasicDBObject(commandName, commandValue);
    	}
    	
        return dbObject;
    }

    @SuppressWarnings("unchecked")
    public static Object adapt(Object o)
    {
        Object obj = o;
        if (obj instanceof DBObject)
        {
            adaptObjectId((DBObject) obj);
            adaptAttributes((DBObject) obj);
        }
        else if (obj instanceof Map<?, ?>)
        {
            obj = adapt(fromMap((Map<String, Object>) o));
        }
        else if (obj instanceof List<?>)
        {
            adaptElements(obj);
        }
        return obj;
    }

    public static Document adapt(Map<String, Object> o)
    {
    	return new Document(o);
    }
    
    @SuppressWarnings("unchecked")
    private static void adaptElements(Object o)
    {
        for (ListIterator<Object> iter = ((List<Object>) o).listIterator(); iter.hasNext();)
        {
            iter.set(adapt(iter.next()));
        }
    }

    private static void adaptAttributes(DBObject o)
    {
        for (String key : o.keySet())
        {
            o.put(key, adapt(o.get(key)));
        }
    }

    private static void adaptObjectId(DBObject o)
    {
        Object id = o.get("_id");

        if (id != null && id instanceof String)
        {
            Matcher m = objectIdMatcher((String) id);
        	
            if (m.matches())
        	{
                o.put("_id", new ObjectId(m.group(1)));
        	}
        }
    }

    private static Matcher objectIdMatcher(String id)
    {
        return OBJECT_ID_PATTERN.matcher(id);
    }
}
