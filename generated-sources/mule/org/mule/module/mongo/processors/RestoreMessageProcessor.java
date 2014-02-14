
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
 * RestoreMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#restore(java.lang.String, boolean, boolean)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-14T12:17:17-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class RestoreMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object inputPath;
    protected String _inputPathType;
    protected Object drop;
    protected boolean _dropType;
    protected Object oplogReplay;
    protected boolean _oplogReplayType;

    public RestoreMessageProcessor(String operationName) {
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
     * Sets oplogReplay
     * 
     * @param value Value to set
     */
    public void setOplogReplay(Object value) {
        this.oplogReplay = value;
    }

    /**
     * Sets inputPath
     * 
     * @param value Value to set
     */
    public void setInputPath(Object value) {
        this.inputPath = value;
    }

    /**
     * Sets drop
     * 
     * @param value Value to set
     */
    public void setDrop(Object value) {
        this.drop = value;
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
            final String _transformedInputPath = ((String) evaluateAndTransform(getMuleContext(), event, RestoreMessageProcessor.class.getDeclaredField("_inputPathType").getGenericType(), null, inputPath));
            final Boolean _transformedDrop = ((Boolean) evaluateAndTransform(getMuleContext(), event, RestoreMessageProcessor.class.getDeclaredField("_dropType").getGenericType(), null, drop));
            final Boolean _transformedOplogReplay = ((Boolean) evaluateAndTransform(getMuleContext(), event, RestoreMessageProcessor.class.getDeclaredField("_oplogReplayType").getGenericType(), null, oplogReplay));
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
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
                    ((MongoCloudConnector) object).restore(_transformedInputPath, _transformedDrop, _transformedOplogReplay);
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
