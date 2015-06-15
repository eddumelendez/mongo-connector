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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.store.ObjectDoesNotExistException;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.PartitionableExpirableObjectStore;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.tck.junit4.FunctionalTestCase;

public class MongoObjectStoreTestCase extends FunctionalTestCase {

    private PartitionableExpirableObjectStore<Serializable> objectStore;

    @Override
    @BeforeClass
    public String getConfigResources() {
        return "mongo-objectstore-tests-config.xml";
    }

    @Override
    @Before
    public void doSetUp() throws Exception {
        super.doSetUp();

        objectStore = muleContext.getRegistry().lookupObject(FakeObjectStoreUser.class).getObjectStore();

        // open and close are noops
        objectStore.open();
        objectStore.close();
    }

    @Test
    @Category({ RegressionTests.class })
    public void Persistent() {
        assertTrue(objectStore.isPersistent());
    }

    @Test
    @Category({ RegressionTests.class })
    public void validContains() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);

        assertFalse(objectStore.contains(testKey));
        assertFalse(objectStore.allKeys().contains(testKey));
    }

    @Test(expected = ObjectStoreException.class)
    @Category({ RegressionTests.class })
    public void invalidContains() throws ObjectStoreException {
        final String testKey = null;

        assertFalse(objectStore.contains(testKey));
    }

    @Test(expected = ObjectStoreException.class)
    @Category({ RegressionTests.class })
    public void invalidAllKeysContains() throws ObjectStoreException {
        final String testKey = null;

        assertFalse(objectStore.allKeys().contains(testKey));
    }

    @Test(expected = ObjectStoreException.class)
    @Category({ RegressionTests.class })
    public void invalidRetrieve() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);

        objectStore.retrieve(testKey);
        fail("should have got an ObjectDoesNotExistException");
    }

    @Test
    @Category({ RegressionTests.class })
    public void validRetrieve() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        assertEquals(testValue, objectStore.retrieve(testKey));
    }

    @Test(expected = ObjectStoreException.class)
    @Category({ RegressionTests.class })
    public void invalidStore() throws ObjectStoreException {
        final String testKey = null;
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        objectStore.store(testKey, testValue);
    }

    @Test
    @Category({ RegressionTests.class })
    public void validStore() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        objectStore.store(testKey, testValue);
        assertTrue(objectStore.contains(testKey));
        assertTrue(objectStore.allKeys().contains(testKey));
    }

    @Test(expected = ObjectStoreException.class)
    @Category({ RegressionTests.class })
    public void invalidRemove() throws ObjectStoreException {
        final String testKey = null;
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        assertEquals(testValue, objectStore.remove(testKey));
    }

    @Test(expected = ObjectDoesNotExistException.class)
    @Category({ RegressionTests.class })
    public void invalidRemoveNotExist() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        assertEquals(testValue, objectStore.remove(testKey));
    }

    @Test
    @Category({ RegressionTests.class })
    public void validRemove() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());
        objectStore.store(testKey, testValue);

        assertEquals(testValue, objectStore.remove(testKey));
    }

    @Test
    @Category({ RegressionTests.class })
    public void expires() throws ObjectStoreException {
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        objectStore.store(testKey, testValue);
        assertTrue(objectStore.contains(testKey));
        assertTrue(objectStore.allKeys().contains(testKey));

        // using a negative TTL expires everything!
        objectStore.expire(-1000000, Integer.MAX_VALUE);
        assertFalse(objectStore.contains(testKey));
    }

    @Test
    @Category({ RegressionTests.class })
    public void partitionableContains() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);
        final String testKey = RandomStringUtils.randomAlphanumeric(20);

        assertFalse(objectStore.contains(testKey, testPartition));
        assertFalse(objectStore.allKeys(testPartition).contains(testKey));
    }

    @Test
    @Category({ RegressionTests.class })
    public void partitionableRetrieve() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);
        final String testKey = RandomStringUtils.randomAlphanumeric(20);

        try {
            objectStore.retrieve(testKey, testPartition);
            fail("should have got an ObjectDoesNotExistException");
        } catch (final ObjectDoesNotExistException odnee) {
            // NOOP
        }
    }

    @Test
    @Category({ RegressionTests.class })
    public void partitionableStoreandRemove() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

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
    }

    @Test
    @Category({ RegressionTests.class })
    public void partitionableDispose() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        objectStore.store(testKey, testValue, testPartition);
        objectStore.disposePartition(testPartition);
        assertFalse(objectStore.contains(testKey, testPartition));
        assertFalse(objectStore.allPartitions().contains(testPartition));
    }

    @Test
    @Category({ RegressionTests.class })
    public void partitionableExpire() throws ObjectStoreException {
        final String testPartition = RandomStringUtils.randomAlphanumeric(20);
        final String testKey = RandomStringUtils.randomAlphanumeric(20);
        final Serializable testValue = BigInteger.valueOf(RandomUtils.nextLong());

        objectStore.store(testKey, testValue, testPartition);
        assertTrue(objectStore.contains(testKey, testPartition));
        // using a negative TTL expires everything!
        objectStore.expire(-1000000, Integer.MAX_VALUE, testPartition);
        assertFalse(objectStore.contains(testKey, testPartition));
    }
}
