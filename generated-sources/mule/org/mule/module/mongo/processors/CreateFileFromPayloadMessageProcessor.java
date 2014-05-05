
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
 * CreateFileFromPayloadMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#createFileFromPayload(java.lang.Object, java.lang.String, java.lang.String, com.mongodb.DBObject)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-05T02:17:19-05:00", comments = "Build master.1926.b0106b2")
public class CreateFileFromPayloadMessageProcessor
    extends AbstractConnectedProcessor
    implements MessageProcessor
{

    protected Object payload;
    protected Object _payloadType;
    protected Object filename;
    protected String _filenameType;
    protected Object contentType;
    protected String _contentTypeType;
    protected Object metadata;
    protected DBObject _metadataType;

    public CreateFileFromPayloadMessageProcessor(String operationName) {
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
     * Sets filename
     * 
     * @param value Value to set
     */
    public void setFilename(Object value) {
        this.filename = value;
    }

    /**
     * Sets payload
     * 
     * @param value Value to set
     */
    public void setPayload(Object value) {
        this.payload = value;
    }

    /**
     * Sets contentType
     * 
     * @param value Value to set
     */
    public void setContentType(Object value) {
        this.contentType = value;
    }

    /**
     * Sets metadata
     * 
     * @param value Value to set
     */
    public void setMetadata(Object value) {
        this.metadata = value;
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
            final Object _transformedPayload = ((Object) evaluateAndTransform(getMuleContext(), event, CreateFileFromPayloadMessageProcessor.class.getDeclaredField("_payloadType").getGenericType(), null, "#[payload]"));
            final String _transformedFilename = ((String) evaluateAndTransform(getMuleContext(), event, CreateFileFromPayloadMessageProcessor.class.getDeclaredField("_filenameType").getGenericType(), null, filename));
            final String _transformedContentType = ((String) evaluateAndTransform(getMuleContext(), event, CreateFileFromPayloadMessageProcessor.class.getDeclaredField("_contentTypeType").getGenericType(), null, contentType));
            final DBObject _transformedMetadata = ((DBObject) evaluateAndTransform(getMuleContext(), event, CreateFileFromPayloadMessageProcessor.class.getDeclaredField("_metadataType").getGenericType(), null, metadata));
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
                    return ((MongoCloudConnector) object).createFileFromPayload(_transformedPayload, _transformedFilename, _transformedContentType, _transformedMetadata);
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
