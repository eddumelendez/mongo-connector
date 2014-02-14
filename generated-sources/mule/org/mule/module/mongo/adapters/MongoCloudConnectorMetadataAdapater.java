
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.api.MetadataAware;
import org.mule.module.mongo.MongoCloudConnector;


/**
 * A <code>MongoCloudConnectorMetadataAdapater</code> is a wrapper around {@link MongoCloudConnector } that adds support for querying metadata about the extension.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-14T12:14:34-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class MongoCloudConnectorMetadataAdapater
    extends MongoCloudConnectorCapabilitiesAdapter
    implements MetadataAware
{

    private final static String MODULE_NAME = "Mongo DB";
    private final static String MODULE_VERSION = "3.4.3-SNAPSHOT";
    private final static String DEVKIT_VERSION = "3.5.0-SNAPSHOT";
    private final static String DEVKIT_BUILD = "UNKNOWN_BUILDNUMBER";
    private final static String MIN_MULE_VERSION = "3.5";

    public String getModuleName() {
        return MODULE_NAME;
    }

    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    public String getDevkitVersion() {
        return DEVKIT_VERSION;
    }

    public String getDevkitBuild() {
        return DEVKIT_BUILD;
    }

    public String getMinMuleVersion() {
        return MIN_MULE_VERSION;
    }

}
