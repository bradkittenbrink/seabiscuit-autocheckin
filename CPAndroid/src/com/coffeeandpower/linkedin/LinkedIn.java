/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coffeeandpower.linkedin;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.net.Uri;
import android.util.Log;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.linkedin.LinkedInApiWithEmail;

/**
 * 
 * @author jrojas
 */
public class LinkedIn {
    Token requestToken = null;
    OAuthService service = null;
    UserSmart currUser;
    Token accessToken = null;

    String apiKey;
    String apiSec;
    protected String errorMessage = "";

    // // Person currUser;
    // // Connections connections;

    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
    public static final String OAUTH_CALLBACK_HOST = "callback";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
            + "://" + OAUTH_CALLBACK_HOST;
    private static final String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~:(email-address,id,first-name,last-name,industry)?format=json";
    private static final String CONNECTIONS_URL = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,picture-url)?format=json";
    private static final String SEND_MESSAGE_TO_CONNECTION = "http://api.linkedin.com/v1/people/~/mailbox";

    public String getServiceNameSignUp() {
        return "linkedin";
    }

    public String getServiceNameLogin() {
        return "Linkedin";
    }

    public boolean isConnected() {
        return service != null && accessToken != null;
    }

    public void initialize(String apiKey_, String apiSec_) {
        apiKey = apiKey_;
        apiSec = apiSec_;
        currUser = new UserSmart(0, 0, "", "", "", "", "", "", "", 0, 0, 0, "",
                "", 0, "", false);

        service = new ServiceBuilder().provider(LinkedInApiWithEmail.class)
                .apiKey(apiKey).apiSecret(apiSec).callback(OAUTH_CALLBACK_URL)
                .build();

        requestToken = service.getRequestToken();

        String url = service.getAuthorizationUrl(requestToken);
    }

    public String inviteString(List<String> arraySelectedUsersIds,
            String title, String messageContent) {
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version='1.0' encoding='UTF-8'?>");
        xml.append("<mailbox-item>");
        xml.append("<recipients>");
        for (String strId : arraySelectedUsersIds) {
            xml.append("<recipient>");
            xml.append("<person path=" + '"' + "/people/" + strId + '"' + " />");
            xml.append("</recipient>");
        }
        xml.append("</recipients>");
        xml.append("<subject>" + title + "</subject>");
        xml.append("<body>" + messageContent + "</body>");
        xml.append("</mailbox-item>");

        return xml.toString();
    }
    public static String escapeXMLChars(String s) {
        return s.replaceAll("&",  "&amp;")
             .replaceAll("'",  "&apos;")
             .replaceAll("\"", "&quot;")
             .replaceAll("<",  "&lt;")
             .replaceAll(">",  "&gt;");
    }
    
    public DataHolder sendInvite(String token, String tokenSecret,
            List<String> arraySelectedUsersIds, String title,
            String messageContent) {
        DataHolder result = new DataHolder(0, "", null);
        if (accessToken == null) {
            accessToken = new Token(AppCAP.getUserLinkedInToken(),
                    AppCAP.getUserLinkedInTokenSecret());

        }
        
        if (!isConnected()) {
            result.setHandlerCode(1); 
            result.setResponseMessage("Cannot connect to linkedin!");
            return result;
        }
        title = escapeXMLChars(title);
        messageContent = escapeXMLChars(messageContent);
        String payload = inviteString(arraySelectedUsersIds, title,
                messageContent);
        OAuthRequest post = new OAuthRequest(Verb.POST,
                SEND_MESSAGE_TO_CONNECTION);

        post.addPayload(payload);
        post.addHeader("Content-Type", "text/xml;charset=UTF-8");

        service.signRequest(accessToken, post);
        try {
            Response response = post.send();
            if (response.getCode() != 201){
                result.setHandlerCode(1); 
                result.setResponseMessage("Error posting the invite to linkedin server:" + response.getCode() + 
                        " - " + response.getBody());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setHandlerCode(1); 
            result.setResponseMessage("Error posting the invite to linkedin server!");
            return result;
        }
        return result;
    }

    public String getAccessToken() {
        return accessToken.getToken();
    }

    public String getAccessTokenSecret() {
        return accessToken.getSecret();
    }

    public String getUserId() {
        if (currUser == null)
            return null;
        return currUser.getLinkedinId();
    }

    public String getUserName() {
        return currUser.getLinkedinEmail();
    }

    public String getUserNickName() {
        return currUser.getNickName();
    }

    public String getUserPassword() {
        return getAccessTokenSecret();
    }

    public void saveSettings() {
        if (service == null || accessToken == null)
            return;
        AppCAP.setUserLinkedInDetails(getAccessToken(), getAccessTokenSecret(),
                getUserId());
    }

    public void clearSettings() {
        AppCAP.setUserLinkedInDetails("", "", "");
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(requestToken);
    }

    public boolean callbackReceived(String url) {
        if (url.startsWith(OAUTH_CALLBACK_SCHEME)) {
            Uri uri = Uri.parse(url);
            String verifier = uri.getQueryParameter("oauth_verifier");
            Verifier v = new Verifier(verifier);
            accessToken = service.getAccessToken(requestToken, v);
            return connectUsingAccessToken();
        }
        return false;
    }

    public boolean reconnectUsingAccessToken(String token, String tokenSecret) {
        accessToken = new Token(token, tokenSecret);
        return connectUsingAccessToken();
    }

    public boolean connectUsingAccessToken() {
        try {
            OAuthRequest req = new OAuthRequest(Verb.GET,
                    PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, req);
            Response response = req.send();
            String responseString = response.getBody();
            if (responseString != null) {
                JSONObject json = new JSONObject(responseString);
                int errorCode = json.optInt("errorCode");
                int status = json.optInt("status");
                errorMessage = json.optString("message");
                String emailAddress = json.optString("emailAddress");
                String firstName = json.optString("firstName");
                String lastName = json.optString("lastName");
                String linkedinId = json.optString("id");
                currUser.setLinkedinEmail(emailAddress);
                currUser.setLinkedinId(linkedinId);
                currUser.setNickName(firstName + " " + lastName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public ArrayList<UserSmart> getConnections() {
        ArrayList<UserSmart> arrayUsers = new ArrayList<UserSmart>();
        if (accessToken == null) {
            accessToken = new Token(AppCAP.getUserLinkedInToken(),
                    AppCAP.getUserLinkedInTokenSecret());

        }
        try {
            OAuthRequest req = new OAuthRequest(Verb.GET, CONNECTIONS_URL);
            service.signRequest(accessToken, req);
            Response response = req.send();
            String responseString = response.getBody();
            if (responseString != null) {
                JSONObject json = new JSONObject(responseString);
                JSONArray userArray = json.optJSONArray("values");
                if (userArray != null) {
                    for (int x = 0; x < userArray.length(); x++) {

                        JSONObject objUser = userArray.optJSONObject(x);
                        if (objUser != null) {
                            String nickName = objUser.optString("firstName")
                                    + " " + json.optString("lastName");
                            String linkedinId = objUser.optString("id");
                            String photo = objUser.optString("pictureUrl");
                            UserSmart newUser = new UserSmart(nickName,
                                    linkedinId, photo);
                            arrayUsers.add(newUser);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return arrayUsers;
        }

        return arrayUsers;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
