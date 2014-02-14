
package org.mule.module.mongo.process;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-08-22T09:27:07-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface ProcessAdapter<O >{

    <T> ProcessTemplate<T, O> getProcessTemplate();
}
