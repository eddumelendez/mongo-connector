/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import java.util.List;

import jersey.repackaged.com.google.common.base.Function;
import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectStrategy;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientAdaptor;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWaitQueueFullException;
import com.mongodb.ServerAddress;

@ConnectionManagement(friendlyName = "ConnectionManagement", configElementName = "config")
public class ConnectionManagementStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManagementStrategy.class);

    /**
     * The host of the Mongo server, it can also be a list of comma separated hosts for replicas
     */
    @Configurable
    @Default("localhost")
    private String host;

    /**
     * The port of the Mongo server
     */
    @Configurable
    @Optional
    @Default("27017")
    private int port;

    /**
     * The number of connections allowed per host (the pool size, per host)
     */
    @Configurable
    @Optional
    public Integer connectionsPerHost;

    /**
     * Multiplier for connectionsPerHost for # of threads that can block
     */
    @Configurable
    @Optional
    public Integer threadsAllowedToBlockForConnectionMultiplier;

    /**
     * The max wait time for a blocking thread for a connection from the pool in ms.
     */
    @Configurable
    @Optional
    public Integer maxWaitTime;

    /**
     * The connection timeout in milliseconds; this is for establishing the socket connections (open). 0 is default and infinite.
     */
    @Configurable
    @Optional
    @Default("30000")
    private Integer connectTimeout;

    /**
     * The socket timeout. 0 is default and infinite.
     */
    @Configurable
    @Optional
    private Integer socketTimeout;

    private String database;

    private com.mongodb.MongoClient mongo;

    private MongoClient client;

    public MongoClient getClient() {
        return client;
    }

    /**
     * Method invoked when a Mongo session needs to be created.
     * 
     * @param username
     *            the username to use for authentication.
     * @param password
     *            the password to use for authentication. If the password is null or whitespaces only, the connector won't use authentication and username must be empty too.
     * @param database
     *            Name of the database
     * @throws org.mule.api.ConnectionException
     */
    @Connect(strategy = ConnectStrategy.SINGLE_INSTANCE)
    @TestConnectivity
    public void connect(@ConnectionKey final String username, @Optional @Password final String password, @ConnectionKey final String database) throws ConnectionException {
        try {
            final List<ServerAddress> addresses = Lists.transform(Lists.newArrayList(host.split(",\\s?")), new Function<String, ServerAddress>() {

                @Override
                public ServerAddress apply(String input) {
                    return new ServerAddress(input, getPort());
                }
            });

            MongoClientOptions mongoOptions = getMongoOptions(database);
            if (StringUtils.isNotBlank(password)) {
                Validate.notNull(username, "Username must not be null if password is set");
                logger.info("Connecting to MongoDB, authenticating as user '{}'", username);

                MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
                mongo = new com.mongodb.MongoClient(addresses, Lists.newArrayList(credential), mongoOptions);
            } else {
                logger.info("Connecting to MongoDB, not using authentication");
                mongo = new com.mongodb.MongoClient(addresses, mongoOptions);
            }

            client = new MongoClientImpl(mongo, database);

            // We perform a dummy, cheap operation to valid user has access to the DB
            if (! client.isAlive())
                throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "N/A", "Could not connect to MongoDB");

        } catch (final IllegalArgumentException e) {
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, e.getLocalizedMessage(), e.getMessage(), e.getCause());
        } catch (MongoSecurityException e) {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, String.valueOf(e.getCode()), "Authentication failed", e);
        } catch (MongoTimeoutException e) {
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, String.valueOf(e.getCode()), "Timeout waiting for server or a connection to become available", e);
        } catch (MongoWaitQueueFullException e) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, String.valueOf(e.getCode()), "Wait Queue is full", e);
        }
    }

    private MongoClientOptions getMongoOptions(String database) {
        final MongoClientOptions.Builder options = MongoClientOptions.builder();

        if (connectionsPerHost != null) {
            options.connectionsPerHost(connectionsPerHost);
        }
        if (threadsAllowedToBlockForConnectionMultiplier != null) {
            options.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);
        }
        if (maxWaitTime != null) {
            options.maxWaitTime(maxWaitTime);
        }
        if (connectTimeout != null) {
            options.connectTimeout(connectTimeout);
        }
        if (socketTimeout != null) {
            options.socketTimeout(socketTimeout);
        }
        if (database != null) {
            this.database = database;
        }
        return options.build();
    }

    /**
     * Method invoked when the Mongo session is to be destroyed.
     */
    @Disconnect
    public void disconnect() {
        IOUtils.closeQuietly(client);
        client = null;
    }

    @ValidateConnection
    public boolean isConnected() {
        return client != null && client.isAlive(); // && mongo.getConnector().isOpen();
    }

    @ConnectionIdentifier
    public String connectionId() {
        return mongo == null ? "n/a" : mongo.toString();
    }

    protected MongoClient adaptClient(final MongoClient client) {
        return MongoClientAdaptor.adapt(client);
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }

    public Integer getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(final Integer connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public Integer getThreadsAllowedToBlockForConnectionMultiplier() {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(final Integer threadsAllowedToBlockForConnectionMultiplier) {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(final Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(final Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

}
