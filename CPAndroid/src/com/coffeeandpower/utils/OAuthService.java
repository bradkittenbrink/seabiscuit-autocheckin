package com.coffeeandpower.utils;

import android.app.Activity;
import android.content.Intent;

public interface OAuthService {
	
	public boolean isConnected();
	public String getServiceNameSignUp();
	public String getServiceNameLogin();
	public void initialize(String apiKey, String apiSecret);
	public Intent authorize();
	public boolean verify(String verifier);	
	public boolean reconnect(String token, String tokenSecret);
	public String getAccessToken();
	public String getAccessTokenSecret();
	public String getUserId();
	public String getUserName();
	public String getUserNickName();
	public String getUserPassword();
	public void saveSettings();
	public void clearSettings();
}
