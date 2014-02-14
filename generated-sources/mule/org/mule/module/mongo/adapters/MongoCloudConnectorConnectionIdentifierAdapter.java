
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.connection.Connection;


/**
 * A <code>MongoCloudConnectorConnectionIdentifierAdapter</code> is a wrapper around {@link MongoCloudConnector } that implements {@link org.mule.devkit.dynamic.api.helper.Connection} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-14T12:14:34-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class MongoCloudConnectorConnectionIdentifierAdapter
    extends MongoCloudConnectorProcessAdapter
    implements Connection
{


    public String getConnectionIdentifier() {
        return super.connectionId();
    }

}
