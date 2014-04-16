
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
import org.mule.module.mongo.connectivity.MongoCloudConnectorConnectionManager;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * MapReduceObjectsMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#mapReduceObjects(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-04-16T09:55:15-05:00", comments = "Build master.1915.dd1962d")
public class MapReduceObjectsMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object collection;
    protected String _collectionType;
    protected Object mapFunction;
    protected String _mapFunctionType;
    protected Object reduceFunction;
    protected String _reduceFunctionType;
    protected Object outputCollection;
    protected String _outputCollectionType;

    public MapReduceObjectsMessageProcessor(String operationName) {
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
     * Sets mapFunction
     * 
     * @param value Value to set
     */
    public void setMapFunction(Object value) {
        this.mapFunction = value;
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
     * Sets reduceFunction
     * 
     * @param value Value to set
     */
    public void setReduceFunction(Object value) {
        this.reduceFunction = value;
    }

    /**
     * Sets outputCollection
     * 
     * @param value Value to set
     */
    public void setOutputCollection(Object value) {
        this.outputCollection = value;
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
            final String _transformedCollection = ((String) evaluateAndTransform(getMuleContext(), event, MapReduceObjectsMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), null, collection));
            final String _transformedMapFunction = ((String) evaluateAndTransform(getMuleContext(), event, MapReduceObjectsMessageProcessor.class.getDeclaredField("_mapFunctionType").getGenericType(), null, mapFunction));
            final String _transformedReduceFunction = ((String) evaluateAndTransform(getMuleContext(), event, MapReduceObjectsMessageProcessor.class.getDeclaredField("_reduceFunctionType").getGenericType(), null, reduceFunction));
            final String _transformedOutputCollection = ((String) evaluateAndTransform(getMuleContext(), event, MapReduceObjectsMessageProcessor.class.getDeclaredField("_outputCollectionType").getGenericType(), null, outputCollection));
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
                    return ((MongoCloudConnector) object).mapReduceObjects(_transformedCollection, _transformedMapFunction, _transformedReduceFunction, _transformedOutputCollection);
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
