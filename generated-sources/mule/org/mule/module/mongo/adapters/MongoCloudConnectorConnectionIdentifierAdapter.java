
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.connection.Connection;


/**
 * A <code>MongoCloudConnectorConnectionIdentifierAdapter</code> is a wrapper around {@link MongoCloudConnector } that implements {@link org.mule.devkit.dynamic.api.helper.Connection} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-04-15T03:23:24-05:00", comments = "Build master.1915.dd1962d")
public class MongoCloudConnectorConnectionIdentifierAdapter
    extends MongoCloudConnectorProcessAdapter
    implements Connection
{


    public String getConnectionIdentifier() {
        return super.connectionId();
    }

}
