
package org.mule.module.mongo.adapters;

import javax.annotation.Generated;
import org.mule.api.MetadataAware;
import org.mule.module.mongo.MongoCloudConnector;


/**
 * A <code>MongoCloudConnectorMetadataAdapater</code> is a wrapper around {@link MongoCloudConnector } that adds support for querying metadata about the extension.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-05T02:17:19-05:00", comments = "Build master.1926.b0106b2")
public class MongoCloudConnectorMetadataAdapater
    extends MongoCloudConnectorCapabilitiesAdapter
    implements MetadataAware
{

    private final static String MODULE_NAME = "Mongo DB";
    private final static String MODULE_VERSION = "3.4.5-SNAPSHOT";
    private final static String DEVKIT_VERSION = "3.5.0-RC1";
    private final static String DEVKIT_BUILD = "master.1926.b0106b2";
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
