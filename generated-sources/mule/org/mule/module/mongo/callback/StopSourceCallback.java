
package org.mule.module.mongo.callback;

import javax.annotation.Generated;


/**
 * Callback returned by methods that are annotated with @Source
 * <p/>
 * It will be executed when the MessageSource is being stopped.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-08-22T09:27:07-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface StopSourceCallback {

    void stop() throws Exception;
}
