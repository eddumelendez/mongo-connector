/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */


package org.mule.module.mongo.tools;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DefaultDBDecoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

public class RestoreFile implements Comparable<RestoreFile>
{
    private String collection;
    private File file;

    public RestoreFile(File file)
    {
        this.file = file;
        this.collection = BackupUtils.getCollectionName(file.getName());
    }

    public List<DBObject> getCollectionObjects() throws IOException
    {
        BSONDecoder bsonDecoder = new DefaultDBDecoder();
        BufferedInputStream inputStream = null;
        List<DBObject> dbObjects = new ArrayList<DBObject>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            while(inputStream.available() != 0)
            {
                BSONObject bsonObject = bsonDecoder.readObject(inputStream);
                if(bsonObject != null)
                {
                    dbObjects.add(new BasicDBObject((BasicBSONObject) bsonObject));
                }
            }
            return dbObjects;
        }
        finally
        {
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
    }

    public String getCollection()
    {
        return collection;
    }
    
    public int compareTo(RestoreFile restoreFile)
    {
        return collection.compareTo(restoreFile.getCollection());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj )
        {  
            return true;
        }
        if ( !(obj instanceof RestoreFile) ) 
        {
            return false;
        }
        RestoreFile that = (RestoreFile)obj;
        return
        areEqual(this.collection, that.collection) &&
        areEqual(this.file, that.file);
    }
      
    private boolean areEqual(Object oThis, Object oThat)
    {
        return oThis == null ? oThat == null : oThis.equals(oThat);
    }
    
    @Override
    public int hashCode()
    {
        return collection.hashCode();
    }
}
