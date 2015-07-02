/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api.automation;

import java.util.List;

import org.bson.Document;
import org.mule.module.mongo.api.IndexOrder;

public class MongoHelper {

    public static String getIndexName(String indexKey, IndexOrder order) {
        String indexName = indexKey + "_" + order.getValue();
        return indexName;
    }

    public static boolean indexExistsInList(List<Document> objects, String indexName) {
        for (Document obj : objects) {
            if (obj.get("name").equals(indexName)) {
                return true;
            }
        }
        return false;
    }

}
