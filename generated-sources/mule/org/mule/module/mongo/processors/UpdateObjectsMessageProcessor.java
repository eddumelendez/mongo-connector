
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
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionManager;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * UpdateObjectsMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#updateObjects(java.lang.String, com.mongodb.DBObject, com.mongodb.DBObject, boolean, boolean, org.mule.module.mongo.api.WriteConcern)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T04:40:43-05:00", comments = "Build master.1926.b0106b2")
public class UpdateObjectsMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object collection;
    protected String _collectionType;
    protected Object query;
    protected DBObject _queryType;
    protected Object element;
    protected DBObject _elementType;
    protected Object upsert;
    protected boolean _upsertType;
    protected Object multi;
    protected boolean _multiType;
    protected Object writeConcern;
    protected WriteConcern _writeConcernType;

    public UpdateObjectsMessageProcessor(String operationName) {
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
     * Sets element
     * 
     * @param value Value to set
     */
    public void setElement(Object value) {
        this.element = value;
    }

    /**
     * Sets writeConcern
     * 
     * @param value Value to set
     */
    public void setWriteConcern(Object value) {
        this.writeConcern = value;
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
     * Sets multi
     * 
     * @param value Value to set
     */
    public void setMulti(Object value) {
        this.multi = value;
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
     * Sets upsert
     * 
     * @param value Value to set
     */
    public void setUpsert(Object value) {
        this.upsert = value;
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
            final String _transformedCollection = ((String) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), null, collection));
            final DBObject _transformedQuery = ((DBObject) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_queryType").getGenericType(), null, query));
            final DBObject _transformedElement = ((DBObject) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_elementType").getGenericType(), null, element));
            final Boolean _transformedUpsert = ((Boolean) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_upsertType").getGenericType(), null, upsert));
            final Boolean _transformedMulti = ((Boolean) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_multiType").getGenericType(), null, multi));
            final WriteConcern _transformedWriteConcern = ((WriteConcern) evaluateAndTransform(getMuleContext(), event, UpdateObjectsMessageProcessor.class.getDeclaredField("_writeConcernType").getGenericType(), null, writeConcern));
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
                    ((MongoCloudConnector) object).updateObjects(_transformedCollection, _transformedQuery, _transformedElement, _transformedUpsert, _transformedMulti, _transformedWriteConcern);
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
