
package org.mule.module.mongo.processors;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionManager;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * CreateIndexMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#createIndex(java.lang.String, java.lang.String, org.mule.module.mongo.api.IndexOrder)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-05T02:17:19-05:00", comments = "Build master.1926.b0106b2")
public class CreateIndexMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object collection;
    protected String _collectionType;
    protected Object field;
    protected String _fieldType;
    protected Object order;
    protected IndexOrder _orderType;

    public CreateIndexMessageProcessor(String operationName) {
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
     * Sets field
     * 
     * @param value Value to set
     */
    public void setField(Object value) {
        this.field = value;
    }

    /**
     * Sets order
     * 
     * @param value Value to set
     */
    public void setOrder(Object value) {
        this.order = value;
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
            final String _transformedCollection = ((String) evaluateAndTransform(getMuleContext(), event, CreateIndexMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), null, collection));
            final String _transformedField = ((String) evaluateAndTransform(getMuleContext(), event, CreateIndexMessageProcessor.class.getDeclaredField("_fieldType").getGenericType(), null, field));
            final IndexOrder _transformedOrder = ((IndexOrder) evaluateAndTransform(getMuleContext(), event, CreateIndexMessageProcessor.class.getDeclaredField("_orderType").getGenericType(), null, order));
            final ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
            processTemplate.execute(new ProcessCallback<Object,Object>() {


                public List<Class<? extends Exception>> getManagedExceptions() {
                    return Arrays.asList(((Class<? extends Exception> []) new Class[] {IllegalStateException.class }));
                }

                public boolean isProtected() {
                    return false;
                }

                public Object process(Object object)
                    throws Exception
                {
                    ((MongoCloudConnector) object).createIndex(_transformedCollection, _transformedField, _transformedOrder);
                    return null;
                }

            }
            , this, event);
            return event;
        } catch (Exception e) {
            throw e;
        }
    }

}
