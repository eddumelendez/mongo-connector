
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
 * ExecuteCommandMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#executeCommand(java.lang.String, java.lang.String)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T04:40:43-05:00", comments = "Build master.1926.b0106b2")
public class ExecuteCommandMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object commandName;
    protected String _commandNameType;
    protected Object commandValue;
    protected String _commandValueType;

    public ExecuteCommandMessageProcessor(String operationName) {
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
     * Sets commandName
     * 
     * @param value Value to set
     */
    public void setCommandName(Object value) {
        this.commandName = value;
    }

    /**
     * Sets commandValue
     * 
     * @param value Value to set
     */
    public void setCommandValue(Object value) {
        this.commandValue = value;
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
            final String _transformedCommandName = ((String) evaluateAndTransform(getMuleContext(), event, ExecuteCommandMessageProcessor.class.getDeclaredField("_commandNameType").getGenericType(), null, commandName));
            final String _transformedCommandValue = ((String) evaluateAndTransform(getMuleContext(), event, ExecuteCommandMessageProcessor.class.getDeclaredField("_commandValueType").getGenericType(), null, commandValue));
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
                    return ((MongoCloudConnector) object).executeCommand(_transformedCommandName, _transformedCommandValue);
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
