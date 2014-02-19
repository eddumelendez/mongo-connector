
package org.mule.module.mongo.processors;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import com.mongodb.DBObject;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionManager;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * FindOneObjectMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#findOneObject(java.lang.String, com.mongodb.DBObject, java.util.List, java.lang.Boolean)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-19T02:52:57-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class FindOneObjectMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object collection;
    protected String _collectionType;
    protected Object query;
    protected DBObject _queryType;
    protected Object fields;
    protected List<String> _fieldsType;
    protected Object failOnNotFound;
    protected Boolean _failOnNotFoundType;

    public FindOneObjectMessageProcessor(String operationName) {
        super(operationName);
    }

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object  has not been set already it will search the Mule registry for a default one.
     * 
     * @throws InitialisationException
     */
    public void initialise()
        throws InitialisationException
    {
    }

    @Override
    public void start()
        throws MuleException
    {
        super.start();
    }

    @Override
    public void stop()
        throws MuleException
    {
        super.stop();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Sets query
     * 
     * @param value Value to set
     */
    public void setQuery(Object value) {
        this.query = value;
    }

    /**
     * Sets collection
     * 
     * @param value Value to set
     */
    public void setCollection(Object value) {
        this.collection = value;
    }

    /**
     * Sets failOnNotFound
     * 
     * @param value Value to set
     */
    public void setFailOnNotFound(Object value) {
        this.failOnNotFound = value;
    }

    /**
     * Sets fields
     * 
     * @param value Value to set
     */
    public void setFields(Object value) {
        this.fields = value;
    }

    /**
     * Invokes the MessageProcessor.
     * 
     * @param event MuleEvent to be processed
     * @throws Exception
     */
    public MuleEvent doProcess(final MuleEvent event)
        throws Exception
    {
        Object moduleObject = null;
        try {
            moduleObject = findOrCreate(MongoCloudConnectorConnectionManager.class, true, event);
            final String _transformedCollection = ((String) evaluateAndTransform(getMuleContext(), event, FindOneObjectMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), null, collection));
            final DBObject _transformedQuery = ((DBObject) evaluateAndTransform(getMuleContext(), event, FindOneObjectMessageProcessor.class.getDeclaredField("_queryType").getGenericType(), null, query));
            final List<String> _transformedFields = ((List<String> ) evaluateAndTransform(getMuleContext(), event, FindOneObjectMessageProcessor.class.getDeclaredField("_fieldsType").getGenericType(), null, fields));
            final Boolean _transformedFailOnNotFound = ((Boolean) evaluateAndTransform(getMuleContext(), event, FindOneObjectMessageProcessor.class.getDeclaredField("_failOnNotFoundType").getGenericType(), null, failOnNotFound));
            Object resultPayload;
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
            resultPayload = processTemplate.execute(new ProcessCallback<Object,Object>() {


                public List<Class<? extends Exception>> getManagedExceptions() {
                    return Arrays.asList(((Class<? extends Exception> []) new Class[] {IllegalStateException.class }));
                }

                public boolean isProtected() {
                    return false;
                }

                public Object process(Object object)
                    throws Exception
                {
                    return ((MongoCloudConnector) object).findOneObject(_transformedCollection, _transformedQuery, _transformedFields, _transformedFailOnNotFound);
                }

            }
            , this, event);
            event.getMessage().setPayload(resultPayload);
            return event;
        } catch (Exception e) {
            throw e;
        }
    }

}
