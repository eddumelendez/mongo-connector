
package org.mule.module.mongo.connectivity;

import javax.annotation.Generated;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.module.mongo.adapters.MongoCloudConnectorConnectionIdentifierAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-14T12:14:34-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class MongoCloudConnectorConnectionFactory implements KeyedPoolableObjectFactory
{

    private static Logger logger = LoggerFactory.getLogger(MongoCloudConnectorConnectionFactory.class);
    private MongoCloudConnectorConnectionManager connectionManager;

    public MongoCloudConnectorConnectionFactory(MongoCloudConnectorConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Object makeObject(Object key)
        throws Exception
    {
        if (!(key instanceof MongoCloudConnectorConnectionKey)) {
            if (key == null) {
                logger.warn("Connection key is null");
            } else {
                logger.warn("Cannot cast key of type ".concat(key.getClass().getName().concat(" to ").concat("org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionKey")));
            }
            throw new RuntimeException("Invalid key type ".concat(key.getClass().getName()));
        }
        MongoCloudConnectorConnectionIdentifierAdapter connector = new MongoCloudConnectorConnectionIdentifierAdapter();
        connector.setHost(connectionManager.getHost());
        connector.setPort(connectionManager.getPort());
        connector.setConnectionsPerHost(connectionManager.getConnectionsPerHost());
        connector.setThreadsAllowedToBlockForConnectionMultiplier(connectionManager.getThreadsAllowedToBlockForConnectionMultiplier());
        connector.setMaxWaitTime(connectionManager.getMaxWaitTime());
        connector.setConnectTimeout(connectionManager.getConnectTimeout());
        connector.setSocketTimeout(connectionManager.getSocketTimeout());
        connector.setAutoConnectRetry(connectionManager.getAutoConnectRetry());
        if (connector instanceof MuleContextAware) {
            ((MuleContextAware) connector).setMuleContext(connectionManager.getMuleContext());
        }
        if (connector instanceof Initialisable) {
            ((Initialisable) connector).initialise();
        }
        if (connector instanceof Startable) {
            ((Startable) connector).start();
        }
        if (!connector.isConnected()) {
            connector.connect(((MongoCloudConnectorConnectionKey) key).getUsername(), ((MongoCloudConnectorConnectionKey) key).getPassword(), ((MongoCloudConnectorConnectionKey) key).getDatabase());
        }
        return connector;
    }

    public void destroyObject(Object key, Object obj)
        throws Exception
    {
        if (!(key instanceof MongoCloudConnectorConnectionKey)) {
            if (key == null) {
                logger.warn("Connection key is null");
            } else {
                logger.warn("Cannot cast key of type ".concat(key.getClass().getName().concat(" to ").concat("org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionKey")));
            }
            throw new RuntimeException("Invalid key type ".concat(key.getClass().getName()));
        }
        if (!(obj instanceof MongoCloudConnectorConnectionIdentifierAdapter)) {
            if (obj == null) {
                logger.warn("Connector is null");
            } else {
                logger.warn("Cannot cast connector of type ".concat(obj.getClass().getName().concat(" to ").concat("org.mule.module.mongo.adapters.MongoCloudConnectorConnectionIdentifierAdapter")));
            }
            throw new RuntimeException("Invalid connector type ".concat(obj.getClass().getName()));
        }
        try {
            ((MongoCloudConnectorConnectionIdentifierAdapter) obj).disconnect();
        } catch (Exception e) {
            throw e;
        } finally {
            if (((MongoCloudConnectorConnectionIdentifierAdapter) obj) instanceof Stoppable) {
                ((Stoppable) obj).stop();
            }
            if (((MongoCloudConnectorConnectionIdentifierAdapter) obj) instanceof Disposable) {
                ((Disposable) obj).dispose();
            }
        }
    }

    public boolean validateObject(Object key, Object obj) {
        if (!(obj instanceof MongoCloudConnectorConnectionIdentifierAdapter)) {
            if (obj == null) {
                logger.warn("Connector is null");
            } else {
                logger.warn("Cannot cast connector of type ".concat(obj.getClass().getName().concat(" to ").concat("org.mule.module.mongo.adapters.MongoCloudConnectorConnectionIdentifierAdapter")));
            }
            throw new RuntimeException("Invalid connector type ".concat(obj.getClass().getName()));
        }
        try {
            return ((MongoCloudConnectorConnectionIdentifierAdapter) obj).isConnected();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public void activateObject(Object key, Object obj)
        throws Exception
    {
        if (!(key instanceof MongoCloudConnectorConnectionKey)) {
            throw new RuntimeException("Invalid key type");
        }
        if (!(obj instanceof MongoCloudConnectorConnectionIdentifierAdapter)) {
            throw new RuntimeException("Invalid connector type");
        }
        try {
            if (!((MongoCloudConnectorConnectionIdentifierAdapter) obj).isConnected()) {
                ((MongoCloudConnectorConnectionIdentifierAdapter) obj).connect(((MongoCloudConnectorConnectionKey) key).getUsername(), ((MongoCloudConnectorConnectionKey) key).getPassword(), ((MongoCloudConnectorConnectionKey) key).getDatabase());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void passivateObject(Object key, Object obj)
        throws Exception
    {
    }

}
