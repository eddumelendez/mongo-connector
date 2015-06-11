/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public final class FieldsSet {

    private FieldsSet() {
    }

    public static Bson from(List<String> fieldsList) {
        if (fieldsList == null) {
            return null;
        }

        Document o = new Document();
        for (String s : fieldsList) {
            o.put(s, 1);
        }
        return o;
    }

}
