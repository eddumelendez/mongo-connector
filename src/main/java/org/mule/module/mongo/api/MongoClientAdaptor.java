/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy that adds logging to MongoClients
 */
public final class MongoClientAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(MongoClientAdaptor.class);

    private MongoClientAdaptor() {
    }

    public static MongoClient adapt(final MongoClient receptor) {
        return (MongoClient) Proxy.newProxyInstance(MongoClient.class.getClassLoader(), new Class[] { MongoClient.class }, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    logger.debug("Entering {} with args {}", method.getName(), args);
                    Object ret = method.invoke(receptor, args);
                    logger.debug("Returning from {} with value {}", method.getName(), ret);
                    return ret;
                } catch (InvocationTargetException e) {
                    logger.warn("An exception was thrown while invoking {}: {}", method.getName(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
    }
}
