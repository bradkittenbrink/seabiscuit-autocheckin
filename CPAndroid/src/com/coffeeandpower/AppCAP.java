package com.coffeeandpower;

import android.app.Application;
import android.content.SharedPreferences;

public class AppCAP extends Application{
	
	public static final String TAG = "CoffeeAndPower";
	
	private static final String TAG_USER_EMAIL = "tag_user_email";
	private static final String TAG_USER_EMAIL_PASSWORD = "tag_user_email_password";
	
	public static final String URL_WEB_SERVICE = "https://coffeeandpower.com/"; // production
	public static final String URL_FOURSQUARE = "https://api.foursquare.com/v2/venues/search?oauth_token=BCG410DXRKXSBRWUNM1PPQFSLEFQ5ND4HOUTTTWYUB1PXYC4&v=20120302";
	public static final String URL_LOGIN = "login.php";
	public static final String URL_LOGOUT= "logout.php";
	public static final String URL_SIGNUP= "signup.php";
	public static final String URL_API = "api.php";
	
	// Http return codes
	public static final int HTTP_ERROR = 1403;
	public static final int HTTP_REQUEST_SUCCEEDED = 1404;
	public static final int ERROR_SUCCEEDED_SHOW_MESS = 1407;
	
	private static AppCAP instance;
	
	public AppCAP(){
		instance = this;
	}

	private static SharedPreferences getSharedPreferences() {
		return instance.getSharedPreferences(AppCAP.TAG, MODE_PRIVATE);
	}
	
	public static void setUserEmail (String email){
		getSharedPreferences().edit().putString(TAG_USER_EMAIL, email).commit();
	}
	
	public static String getUserEmail (){
		return getSharedPreferences().getString(TAG_USER_EMAIL, "");
	}
	
	public static void setUserEmailPassword (String pass){
		getSharedPreferences().edit().putString(TAG_USER_EMAIL_PASSWORD, pass).commit();
	}
	
	public static String getUserEmailPassword (){
		return getSharedPreferences().getString(TAG_USER_EMAIL_PASSWORD, "");
	}
}
