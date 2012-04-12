package com.coffeeandpower;

import com.coffeeandpower.utils.HttpUtil;

import android.app.Application;
import android.content.SharedPreferences;

public class AppCAP extends Application{
	
	public static final String TAG = "CoffeeAndPower";
	
	private static final String TAG_USER_EMAIL = "tag_user_email";
	private static final String TAG_USER_EMAIL_PASSWORD = "tag_user_email_password";
    private static final String TAG_USER_LINKEDIN_TOKEN = "tag_user_linkedin_token";
	private static final String TAG_USER_LINKEDIN_TOKEN_SECRET = "tag_user_linkedin_token_secret";	
	private static final String TAG_USER_LINKEDIN_ID = "tag_user_linkedin_id";	
	private static final String TAG_USER_PHOT_URL = "tag_user_photo_url";
	private static final String TAG_USER_PHOT_LARGE_URL = "tag_user_photo_large_url";
	
	public static final String URL_WEB_SERVICE = "https://coffeeandpower.com/"; // production
	//public static final String URL_WEB_SERVICE = "http://staging.coffeeandpower.com/"; // staging	
	public static final String URL_FOURSQUARE = "https://api.foursquare.com/v2/venues/search?oauth_token=BCG410DXRKXSBRWUNM1PPQFSLEFQ5ND4HOUTTTWYUB1PXYC4&v=20120302";
	public static final String URL_LOGIN = "login.php";
	public static final String URL_LOGOUT= "logout.php";
	public static final String URL_SIGNUP= "signup.php";
	public static final String URL_API = "api.php";
	
	
	// Activity codes
	public static final int ACT_CHECK_IN = 1888;
	public static final int ACT_QUIT = 1333;
	
	
	// Http return codes
	public static final int HTTP_ERROR = 1403;
	public static final int HTTP_REQUEST_SUCCEEDED = 1404;
	public static final int ERROR_SUCCEEDED_SHOW_MESS = 1407;
	
	private static AppCAP instance;
	
	private HttpUtil http;
	
	public AppCAP(){
		instance = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		this.http = new HttpUtil();
	}
	
	public static HttpUtil getConnection(){
		return instance.http;
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
	
	public static String cleanResponseString(String data){
	
		return data.replaceAll("\\+", " ").replaceAll("%28", "\"").replaceAll("%29", "\"").replaceAll("%C4%8D", "c")
				.replaceAll("%C4%87", "c");
	}
	
	public static void setLocalUserPhotoURL(String url){
		getSharedPreferences().edit().putString(TAG_USER_PHOT_URL, url).commit();
	}

	public static String getLocalUserPhotoURL (){
		return getSharedPreferences().getString(TAG_USER_PHOT_URL, "");
	}
	
	public static void setLocalUserPhotoLargeURL (String url){
		getSharedPreferences().edit().putString(TAG_USER_PHOT_LARGE_URL, url).commit(); 
	}
	
	public static String getLocalUserPhotoLargeURL (){
		return getSharedPreferences().getString(TAG_USER_PHOT_LARGE_URL, "");
	}
	
        public static String setUserLinkedInToken (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN, "");
	}
	
	public static String setUserLinkedInTokenSecret (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN_SECRET, "");
	}
	
	public static String setUserLinkedInID (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_ID, "");
	}
	
	public static void setUserLinkedInDetails (String token, String tokenSecret, String id){
		getSharedPreferences().edit().putString(TAG_USER_LINKEDIN_ID, id).commit();
		getSharedPreferences().edit().putString(TAG_USER_LINKEDIN_TOKEN, token).commit();
		getSharedPreferences().edit().putString(TAG_USER_LINKEDIN_TOKEN_SECRET, tokenSecret).commit();
	}
	
	public static String getUserLinkedInID (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_ID, "");		
	}
	
	public static String getUserLinkedInToken (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN, "");		
	}
	
	public static String getUserLinkedInTokenSecret (){
		return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN_SECRET, "");		
	}	
				
}
