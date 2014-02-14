
package org.mule.module.mongo.oauth;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-08-22T09:27:07-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface OAuth1Adapter extends OAuthAdapter
{

        public String authorize(java.util.Map<String, String> extraParameters, String requestTokenUrl, String accessTokenUrl, String authorizationUrl, String redirectUri)
        throws UnableToAcquireRequestTokenException
    ;

    public void fetchAccessToken(String requestTokenUrl, String accessTokenUrl, String authorizationUrl, String redirectUri)
        throws UnableToAcquireAccessTokenException
    ;

    public String getAccessTokenSecret();

    public void setAccessTokenSecret(String value);
}
