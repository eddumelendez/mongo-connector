/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.mongo;

import java.io.Serializable;

import org.mule.api.store.PartitionableObjectStore;
import org.springframework.beans.factory.annotation.Required;

public class FakeObjectStoreUser
{

    private PartitionableObjectStore<Serializable> objectStore;

    public PartitionableObjectStore<Serializable> getObjectStore()
    {
        return objectStore;
    }

    @Required
    public void setObjectStore(final PartitionableObjectStore<Serializable> objectStore)
    {
        this.objectStore = objectStore;
    }
}
