
package org.mule.module.mongo.adapters;

import org.mule.api.Capabilities;
import org.mule.api.Capability;


/**
 * A <code>MongoObjectStoreCapabilitiesAdapter</code> is a wrapper around {@link org.mule.module.mongo.MongoObjectStore } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
public class MongoObjectStoreCapabilitiesAdapter
    extends org.mule.module.mongo.MongoObjectStore
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
        return false;
    }

}
