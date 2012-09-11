package com.coffeeandpower.linkedin;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.*;

public class LinkedInApiWithEmail extends DefaultApi10a {
    private static final String AUTHORIZE_URL = "https://api.linkedin.com/uas/oauth/authorize?oauth_token=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.linkedin.com/uas/oauth/accessToken";
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "https://api.linkedin.com/uas/oauth/requestToken?scope=r_basicprofile+r_emailaddress+r_network+w_messages";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

}