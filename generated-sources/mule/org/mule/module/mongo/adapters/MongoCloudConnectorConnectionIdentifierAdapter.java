
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.connection.Connection;


/**
 * A <code>MongoCloudConnectorConnectionIdentifierAdapter</code> is a wrapper around {@link MongoCloudConnector } that implements {@link org.mule.devkit.dynamic.api.helper.Connection} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-M4", date = "2014-03-07T01:34:18-06:00", comments = "Build M4.1875.17b58a3")
public class MongoCloudConnectorConnectionIdentifierAdapter
    extends MongoCloudConnectorProcessAdapter
    implements Connection
{


    public String getConnectionIdentifier() {
        return super.connectionId();
    }

}
