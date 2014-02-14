
package org.mule.module.mongo.process;

import javax.annotation.Generated;


/**
 * Callback with logic to execute within a controlled environment provided by {@link ProcessTemplate}
 *  @param <T> type of the return value of the processing execution
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-08-22T09:27:07-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface ProcessCallback<T,O >{

    T process(O object) throws Exception;

    java.util.List<Class> getManagedExceptions();

    boolean isProtected();

}
