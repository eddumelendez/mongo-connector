
package org.mule.module.mongo.oauth;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-08-22T09:27:07-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface SaveAccessTokenCallback {

        /**
     * Save access token and secret
     *
     * @param accessToken       Access token to be saved
     * @param accessTokenSecret Access token secret to be saved
     */
    void saveAccessToken(String accessToken, String accessTokenSecret);
}
