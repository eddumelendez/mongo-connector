/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.RegressionTests;

import com.mongodb.BasicDBObject;

public class FieldsSetUnitTest {

    @Category({ RegressionTests.class })
    @Test
    public void fromEmptyList() throws Exception {
        assertEquals(new BasicDBObject(), FieldsSet.from(Arrays.<String> asList()));
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromNonEmpty() throws Exception {
        assertEquals(new BasicDBObject("f1", 1), FieldsSet.from(Arrays.asList("f1")));
    }

    @Category({ RegressionTests.class })
    @Test
    public void fromNull() throws Exception {
        assertNull(FieldsSet.from(null));
    }

}
