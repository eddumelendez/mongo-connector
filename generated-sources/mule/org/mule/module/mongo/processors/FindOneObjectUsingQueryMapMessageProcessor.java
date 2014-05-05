
package org.mule.module.mongo.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
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
 * FindOneObjectUsingQueryMapMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#findOneObjectUsingQueryMap(java.lang.String, java.util.Map, java.util.List, java.lang.Boolean)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-05T02:17:19-05:00", comments = "Build master.1926.b0106b2")
public class FindOneObjectUsingQueryMapMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object collection;
    protected String _collectionType;
    protected Object queryAttributes;
    protected Map<String, Object> _queryAttributesType;
    protected Object fields;
    protected List<String> _fieldsType;
    protected Object failOnNotFound;
    protected Boolean _failOnNotFoundType;

    public FindOneObjectUsingQueryMapMessageProcessor(String operationName) {
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
     * Sets queryAttributes
     * 
     * @param value Value to set
     */
    public void setQueryAttributes(Object value) {
        this.queryAttributes = value;
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
            final String _transformedCollection = ((String) evaluateAndTransform(getMuleContext(), event, FindOneObjectUsingQueryMapMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), null, collection));
            final Map<String, Object> _transformedQueryAttributes = ((Map<String, Object> ) evaluateAndTransform(getMuleContext(), event, FindOneObjectUsingQueryMapMessageProcessor.class.getDeclaredField("_queryAttributesType").getGenericType(), null, queryAttributes));
            final List<String> _transformedFields = ((List<String> ) evaluateAndTransform(getMuleContext(), event, FindOneObjectUsingQueryMapMessageProcessor.class.getDeclaredField("_fieldsType").getGenericType(), null, fields));
            final Boolean _transformedFailOnNotFound = ((Boolean) evaluateAndTransform(getMuleContext(), event, FindOneObjectUsingQueryMapMessageProcessor.class.getDeclaredField("_failOnNotFoundType").getGenericType(), null, failOnNotFound));
            Object resultPayload;
            final ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
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
                    return ((MongoCloudConnector) object).findOneObjectUsingQueryMap(_transformedCollection, _transformedQueryAttributes, _transformedFields, _transformedFailOnNotFound);
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
