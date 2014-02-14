
package org.mule.module.mongo.config;

import org.mule.api.Capabilities;
import org.mule.api.Capability;


/**
 * A <code>MongoCloudConnectorCapabilitiesAdapter</code> is a wrapper around {@link org.mule.module.mongo.MongoCloudConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
public class MongoCloudConnectorCapabilitiesAdapter
    extends org.mule.module.mongo.MongoCloudConnector
    implements Capabilities
{


    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE) {
            return true;
        }
        return false;
    }

}
