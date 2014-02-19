
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * A <code>MongoCloudConnectorProcessAdapter</code> is a wrapper around {@link MongoCloudConnector } that enables custom processing strategies.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-19T02:52:57-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class MongoCloudConnectorProcessAdapter
    extends MongoCloudConnectorLifecycleAdapter
    implements ProcessAdapter<MongoCloudConnectorCapabilitiesAdapter>
{


    public<P >ProcessTemplate<P, MongoCloudConnectorCapabilitiesAdapter> getProcessTemplate() {
        final MongoCloudConnectorCapabilitiesAdapter object = this;
        return new ProcessTemplate<P,MongoCloudConnectorCapabilitiesAdapter>() {


            @Override
            public P execute(ProcessCallback<P, MongoCloudConnectorCapabilitiesAdapter> processCallback, MessageProcessor messageProcessor, MuleEvent event)
                throws Exception
            {
                return processCallback.process(object);
            }

            @Override
            public P execute(ProcessCallback<P, MongoCloudConnectorCapabilitiesAdapter> processCallback, Filter filter, MuleMessage message)
                throws Exception
            {
                return processCallback.process(object);
            }

        }
        ;
    }

}
