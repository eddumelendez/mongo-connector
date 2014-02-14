
package org.mule.module.mongo.config;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mule.api.adapter.SessionManagerAdapter;
import org.mule.api.lifecycle.Initialisable;
import org.mule.config.PoolingProfile;
import org.mule.module.mongo.MongoCloudConnector;


/**
 * A {@code MongoCloudConnectorSessionManagerAdapter} is a wrapper around {@link MongoCloudConnector } that adds session management to the pojo.
 * 
 */
public class MongoCloudConnectorSessionManagerAdapter
    extends MongoCloudConnectorLifecycleAdapter
    implements SessionManagerAdapter<MongoCloudConnectorSessionManagerAdapter.SessionKey, org.mule.module.mongo.MongoSession> , Initialisable
{

    private String username;
    private String password;
    /**
     * Session Pool
     * 
     */
    private GenericKeyedObjectPool sessionPool;
    protected PoolingProfile sessionPoolingProfile;

    /**
     * Sets sessionPoolingProfile
     * 
     * @param value Value to set
     */
    public void setSessionPoolingProfile(PoolingProfile value) {
        this.sessionPoolingProfile = value;
    }

    /**
     * Retrieves sessionPoolingProfile
     * 
     */
    public PoolingProfile getSessionPoolingProfile() {
        return this.sessionPoolingProfile;
    }

    /**
     * Sets username
     * 
     * @param value Value to set
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Retrieves username
     * 
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Retrieves password
     * 
     */
    public String getPassword() {
        return this.password;
    }

    public void initialise() {
        super.initialise();
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (sessionPoolingProfile!= null) {
            config.maxIdle = sessionPoolingProfile.getMaxIdle();
            config.maxActive = sessionPoolingProfile.getMaxActive();
            config.maxWait = sessionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = ((byte) sessionPoolingProfile.getExhaustedAction());
        }
        sessionPool = new GenericKeyedObjectPool(new MongoCloudConnectorSessionManagerAdapter.SessionFactory(this), config);
    }

    public org.mule.module.mongo.MongoSession borrowSession(MongoCloudConnectorSessionManagerAdapter.SessionKey key)
        throws Exception
    {
        return ((org.mule.module.mongo.MongoSession) sessionPool.borrowObject(key));
    }

    public void returnSession(MongoCloudConnectorSessionManagerAdapter.SessionKey key, org.mule.module.mongo.MongoSession session)
        throws Exception
    {
        sessionPool.returnObject(key, session);
    }

    public void destroySession(MongoCloudConnectorSessionManagerAdapter.SessionKey key, org.mule.module.mongo.MongoSession session)
        throws Exception
    {
        sessionPool.invalidateObject(key, session);
    }

    private static class SessionFactory
        implements KeyedPoolableObjectFactory
    {

        /**
         * Session Adapter
         * 
         */
        private MongoCloudConnectorSessionManagerAdapter sessionAdapter;

        public SessionFactory(MongoCloudConnectorSessionManagerAdapter sessionAdapter) {
            this.sessionAdapter = sessionAdapter;
        }

        public Object makeObject(Object key)
            throws Exception
        {
            if (!(key instanceof MongoCloudConnectorSessionManagerAdapter.SessionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            return sessionAdapter.createSession(((MongoCloudConnectorSessionManagerAdapter.SessionKey) key).getUsername(), ((MongoCloudConnectorSessionManagerAdapter.SessionKey) key).getPassword());
        }

        public void destroyObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof MongoCloudConnectorSessionManagerAdapter.SessionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof org.mule.module.mongo.MongoSession)) {
                throw new RuntimeException("Invalid session type");
            }
            sessionAdapter.destroySession(((org.mule.module.mongo.MongoSession) obj));
        }

        public boolean validateObject(Object key, Object obj) {
            return true;
        }

        public void activateObject(Object key, Object obj)
            throws Exception
        {
        }

        public void passivateObject(Object key, Object obj)
            throws Exception
        {
        }

    }

    public static class SessionKey {

        private String username;
        private String password;

        public SessionKey(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Sets username
         * 
         * @param value Value to set
         */
        public void setUsername(String value) {
            this.username = value;
        }

        /**
         * Retrieves username
         * 
         */
        public String getUsername() {
            return this.username;
        }

        /**
         * Sets password
         * 
         * @param value Value to set
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Retrieves password
         * 
         */
        public String getPassword() {
            return this.password;
        }

        public int hashCode() {
            int hash = 1;
            hash = ((hash* 31)+ this.username.hashCode());
            return hash;
        }

        public boolean equals(Object obj) {
            return ((obj instanceof MongoCloudConnectorSessionManagerAdapter.SessionKey)&&(this.username == ((MongoCloudConnectorSessionManagerAdapter.SessionKey) obj).username));
        }

    }

}
