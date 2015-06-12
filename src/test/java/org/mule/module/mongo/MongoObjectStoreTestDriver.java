/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.mule.api.store.ObjectDoesNotExistException;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.PartitionableExpirableObjectStore;
import org.mule.tck.junit4.FunctionalTestCase;

public class MongoObjectStoreTestDriver extends FunctionalTestCase {

    private PartitionableExpirableObjectStore<Serializable> objectStore;

    @Override
    protected String getConfigResources() {
        return "mongo-objectstore-tests-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception {
        super.doSetUp();

        objectStore = muleContext.getRegistry().lookupObject(FakeObjectStoreUser.class).getObjectStore();
    }

    public void testListableObjectStoreOperations() throws ObjectStoreException {
        // open and close are noops
        objectStore.open();
        objectStore.close();

        assertTrue(objectStore.isPersistent());

        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        assertFalse(objectStore.contains(testKey));
        assertFalse(objectStore.allKeys().contains(testKey));

        try {
            objectStore.retrieve(testKey);
            fail("should have got an ObjectDoesNotExistException");
        } catch (final ObjectDoesNotExistException odnee) {
            // NOOP
        }

        objectStore.store(testKey, testValue);
        assertTrue(objectStore.contains(testKey));
        assertTrue(objectStore.allKeys().contains(testKey));

        // Mongo doesn't throw ObjectAlreadyExistsException on multiple stores
        objectStore.store(testKey, testValue);

        assertEquals(testValue, objectStore.retrieve(testKey));

        assertEquals(testValue, objectStore.remove(testKey));
        assertFalse(objectStore.contains(testKey));
        assertFalse(objectStore.allKeys().contains(testKey));

        try {
            objectStore.remove(testKey);
            fail("should have got an ObjectDoesNotExistException");
        } catch (final ObjectDoesNotExistException odnee) {
            // NOOP
        }

        objectStore.store(testKey, testValue);
        assertTrue(objectStore.contains(testKey));
        // using a negative TTL expires everything!
        objectStore.expire(-1000000, Integer.MAX_VALUE);
        assertFalse(objectStore.contains(testKey));
    }

    public void testPartitionableObjectStoreOperations() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);

        // open and close are noops
        objectStore.open(testPartition);
        objectStore.close(testPartition);

        assertTrue(objectStore.isPersistent());

        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        assertFalse(objectStore.contains(testKey, testPartition));
        assertFalse(objectStore.allKeys(testPartition).contains(testKey));

        try {
            objectStore.retrieve(testKey, testPartition);
            fail("should have got an ObjectDoesNotExistException");
        } catch (final ObjectDoesNotExistException odnee) {
            // NOOP
        }

        objectStore.store(testKey, testValue, testPartition);
        assertTrue(objectStore.contains(testKey, testPartition));
        assertTrue(objectStore.allKeys(testPartition).contains(testKey));

        // Mongo doesn't throw ObjectAlreadyExistsException on multiple stores
        objectStore.store(testKey, testValue, testPartition);

        assertEquals(testValue, objectStore.retrieve(testKey, testPartition));
        assertTrue(objectStore.allPartitions().contains(testPartition));

        assertEquals(testValue, objectStore.remove(testKey, testPartition));
        assertFalse(objectStore.contains(testKey, testPartition));
        assertFalse(objectStore.allKeys(testPartition).contains(testKey));

        try {
            objectStore.remove(testKey, testPartition);
            fail("should have got an ObjectDoesNotExistException");
        } catch (final ObjectDoesNotExistException odnee) {
            // NOOP
        }

        objectStore.store(testKey, testValue, testPartition);
        objectStore.disposePartition(testPartition);
        assertFalse(objectStore.contains(testKey, testPartition));
        assertFalse(objectStore.allPartitions().contains(testPartition));

        objectStore.store(testKey, testValue, testPartition);
        assertTrue(objectStore.contains(testKey, testPartition));
        // using a negative TTL expires everything!
        objectStore.expire(-1000000, Integer.MAX_VALUE, testPartition);
        assertFalse(objectStore.contains(testKey, testPartition));
    }
}
