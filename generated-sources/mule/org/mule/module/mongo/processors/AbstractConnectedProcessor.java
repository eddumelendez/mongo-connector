
package org.mule.module.mongo.processors;

import java.lang.reflect.Type;
import javax.annotation.Generated;
import org.mule.devkit.processor.DevkitBasedMessageProcessor;

@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T04:40:43-05:00", comments = "Build master.1926.b0106b2")
public abstract class AbstractConnectedProcessor
    extends DevkitBasedMessageProcessor
    implements ConnectivityProcessor
{

    protected Object username;
    protected String _usernameType;
    protected Object password;
    protected String _passwordType;
    protected Object database;
    protected String _databaseType;

    public AbstractConnectedProcessor(String operationName) {
        super(operationName);
    }

    /**
     * Sets username
     * 
     * @param value Value to set
     */
    public void setUsername(Object value) {
        this.username = value;
    }

    /**
     * Retrieves username
     * 
     */
    @Override
    public Object getUsername() {
        return this.username;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(Object value) {
        this.password = value;
    }

    /**
     * Retrieves password
     * 
     */
    @Override
    public Object getPassword() {
        return this.password;
    }

    /**
     * Sets database
     * 
     * @param value Value to set
     */
    public void setDatabase(Object value) {
        this.database = value;
    }

    /**
     * Retrieves database
     * 
     */
    @Override
    public Object getDatabase() {
        return this.database;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public Type typeFor(String fieldName)
        throws NoSuchFieldException
    {
        return AbstractConnectedProcessor.class.getDeclaredField(fieldName).getGenericType();
    }

}
