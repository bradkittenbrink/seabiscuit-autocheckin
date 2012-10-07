package com.coffeeandpower;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.coffeeandpower.app.R;
import com.coffeeandpower.app.R.string;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.location.LocationDetectionService;
import com.coffeeandpower.location.venueWifiSignature;
import com.coffeeandpower.urbanairship.CapPushNotificationBuilder;
import com.coffeeandpower.urbanairship.IntentReceiver;
import com.coffeeandpower.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urbanairship.UAirship;
import com.urbanairship.push.BasicPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

public class AppCAP extends Application {

    // How to generate a map key for debug
    // 1. Use keytool to get MD5 for your debug app:
    // - e.g. c:\Program Files\Java\jre6\bin\keytool -list -alias
    // androiddebugkey -keystore c:\Users\<username>\.android\debug.keystore
    // - debug keystore password is: android
    // 2. Go to this URL:
    // https://developers.google.com/maps/documentation/android/maps-api-signup?hl=en-US
    // 3. Enter the MD5 from keytool
    // 4. Once you have your key, replace the property android:apiKey in
    // res/layout/tab_activity_map.xml

    // Google maps api key for debug Kep:
    // 0PV0Dp_6Dj6PkG_8xJqiTbSPxXwq2XEiEqXkO_Q
    // Google maps api key for debug Tengai home:
    // 0PV0Dp_6Dj6M_WBuUrThj9-fW3btGy9kxl83wgQ
    // Map key for andrewa debug:
    // 08WpTLaphEjlVeOsrM0kfBODmF3ieB49C4lEHJA (home)
    // 08WpTLaphEjlZ7KvkG-v0IgrMqC8ASbgH39t5hg (laptop)
    // Google maps key for debug Andres:
    // 0N2B-_20GlM_H0LiHavOsRcF1VIqEQmyxijXZ3w

    public static final String TAG = "CoffeeAndPower";

    private static final String TAG_USER_EMAIL = "tag_user_email";
    private static final String TAG_USER_EMAIL_PASSWORD = "tag_user_email_password";
    private static final String TAG_USER_ENTERED_INVITE_CODE = "entered_invite_code";
    private static final String TAG_USER_LAST_VENUE_CHECKIN_ID = "tag_user_last_venue_checkin_id";
    private static final String TAG_USER_LINKEDIN_TOKEN = "tag_user_linkedin_token";
    private static final String TAG_USER_LINKEDIN_TOKEN_SECRET = "tag_user_linkedin_token_secret";
    private static final String TAG_USER_LINKEDIN_ID = "tag_user_linkedin_id";
    private static final String TAG_USER_PHOT_URL = "tag_user_photo_url";
    private static final String TAG_USER_PHOT_LARGE_URL = "tag_user_photo_large_url";
    private static final String TAG_LOGGED_IN_USER_ID = "tag_logged_in_user_id";
    private static final String TAG_LOGGED_IN_USER_NICKNAME = "tag_logged_in_user_nickname";
    private static final String TAG_USER_COORDINATES = "tag_user_coordinates";
    private static final String TAG_IS_USER_CHECKED_IN = "tag_is_user_checked_in";
    private static final String TAG_SHOULD_FINISH_ACTIVITY_MAP = "tag_sgould_finish_activity_map";
    private static final String TAG_SHOULD_START_LOG_IN = "tag_sgould_start_log_in";
    private static final String TAG_COOKIE_STRING = "tag_cookie_string";
    private static final String TAG_METRIC_SYSTEM = "tag_metric_system";
    private static final String TAG_START_LOGIN_PAGE_FROM_CONTACTS = "tag_start_login_page_from_contacts";
    private static final String TAG_IS_LOGGED_IN = "tag_is_logged_in";
    private static final String TAG_SCREEN_WIDTH = "tag_screen_width";
    private static final String TAG_FIRST_START = "tag_first_start";
    private static final String TAG_INFO_DIALOG = "tag_info_dialog";

    private static final String TAG_VENUES_WITH_USER_CHECKINS = "venuesWithUserCheckins";
    private static final String TAG_VENUES_WITH_AUTO_CHECKINS = "venuesWithAutoCheckins";
    private static final String TAG_VENUE_WIFI_SIGNATURES = "venueWifiSignatures";
    private static final String TAG_LAST_CHECKEDIN_VENUES = "listLastCheckedinVenues";

    // Notification settings
    private static final String TAG_PUSH_DISTANCE = "tag_push_distance";
    private static final String TAG_NOTIFICATION_TOGGLE = "tag_notification_toggle";
    private static final String TAG_QUIET_FROM = "tag_quiet_from";
    private static final String TAG_QUIET_TO = "tag_quiet_to";
    private static final String TAG_QUIET_TOGGLE = "tag_quiet_toggle";
    private static final String TAG_CONTACTS_ONLY_CHAT_TOGGLE = "tag_contacts_only_chat_toggle";
    private static final String DEFAULT_QUIET_FROM = "20:00:00";
    private static final String DEFAULT_QUIET_TO = "07:00:00";

    //public static final String URL_WEB_SERVICE = "https://www.candp.me/"; //
    public static final String URL_FEEDBACK = "http://coffeeandpower.uservoice.com";

    //public static final String URL_WEB_SERVICE = "https://www.candp.me/"; //
    // production
    public static final String URL_WEB_SERVICE = "https://staging.candp.me/";
    // staging

    //public static final String URL_WEB_SERVICE =
    // "http://dev.worklist.net/~andres/candpweb2/web/"; // staging
    //public static final String URL_WEB_SERVICE =
    //"http://dev.worklist.net/~vincent/candpweb2_18320/web/"; // staging

    public static final String URL_FOURSQUARE = "https://api.foursquare.com/v2/venues/search?oauth_token=BCG410DXRKXSBRWUNM1PPQFSLEFQ5ND4HOUTTTWYUB1PXYC4&v=20120302";
    public static final String FOURSQUARE_OAUTH = "BCG410DXRKXSBRWUNM1PPQFSLEFQ5ND4HOUTTTWYUB1PXYC4";
    public static final String URL_FUNDS = "http://www.coffeeandpower.com/m/?ios#addFundsiPhone";
    public static final String URL_LOGIN = "login.php";
    public static final String URL_LOGOUT = "logout.php";
    public static final String URL_SIGNUP = "signup.php";
    public static final String URL_API = "api.php";
    public static final String URL_TOS = "terms.php#termsTabContent";

    // Activity codes
    public static final int ACT_CHECK_IN = 1888;
    public static final int ACT_QUIT = 1333;

    // Http return codes
    public static final int HTTP_ERROR = 1403;
    public static final int HTTP_REQUEST_SUCCEEDED = 1404;
    public static final int ERROR_SUCCEEDED_SHOW_MESS = 1407;

    public static final int VIEW_HOLDER = 1;
    public static final int VENUE_NAME_AND_FEEDS = 2;
    
    private static boolean releaseApp = true;

    private static Gson gsonConverter = new Gson();

    private static Context mapContext;
    private static boolean bActive = false;

    // App wide observables

    private static AppCAP instance;
    private static Context context;
    private static int mapCenterLng;
    private static int mapCenterLat;

    private HttpUtil http;

    private static ArrayList<VenueNameAndFeeds> listLastCheckedinVenues;

    // Service management
    private static boolean locationDetectionServiceRunning = false;

    // private Counter timingCounter;

    public AppCAP() {
        instance = this;
    }

    /**
     * 
     * @category viewLifeCycle
     */
    @Override
    public void onCreate() {

        // You may or may not see the onCreate messages in LogCat...
        super.onCreate();

        setReleaseApp(!isDebuggable(this));
        // Start Urban Airship
        UAirship.takeOff(this);

        // Detect whether we are on the main thread. If so, do app init stuff
        // onCreate will get triggered multiple times due to the UA process
        // getting started
        // so we only want to call the app init stuff on the main thread/process

        if (getAppName().equalsIgnoreCase("com.coffeeandpower.app")) {

            Log.d("Coffee", "Main process loading (onCreate)...");

            // getSharedPreferences().edit().putString(TAG_VENUES_WITH_AUTO_CHECKINS,
            // null).commit();
            // getSharedPreferences().edit().putString(TAG_VENUES_WITH_USER_CHECKINS,
            // null).commit();
            AppCAP.context = getApplicationContext();

            this.http = new HttpUtil(getString(string.message_internet_connection_error));

            PushPreferences prefs = PushManager.shared().getPreferences();
            prefs.setSoundEnabled(true);
            prefs.setVibrateEnabled(true);

            // Setup push notifications (will use app icon
            // and push payload 'alert' string for message)
            CapPushNotificationBuilder pnb = new CapPushNotificationBuilder();
            PushManager.shared().setNotificationBuilder(pnb);

            PushManager.enablePush();
            PushManager.shared().setIntentReceiver(IntentReceiver.class);

            getUserLastCheckinVenue();
            if (Constants.debugLog)
                Log.d("LOG", "Found APID: " + prefs.getPushId());

            // Get country code for metrics/imperial units
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (Constants.debugLog)
                Log.d("LOG", "Locale: " + tm.getSimCountryIso());
            if (tm.getSimCountryIso() != null
                    && !tm.getSimCountryIso().equals("")) {
                if (tm.getSimCountryIso().contains("US")
                        || tm.getSimCountryIso().contains("us")
                        || tm.getSimCountryIso().contains("usa")
                        || tm.getSimCountryIso().contains("um")) {
                    setMetricsSys(false);
                } else {
                    setMetricsSys(true);
                }
            } else {
                setMetricsSys(false);
            }
        } else {
            Log.d(TAG, "Starting process " + getAppName());
        }
    }

    // called from onCreate of main activity (FragmentMap)
    public static void mainActivityDidStart(Context context) {

        mapContext = context;

        // context.startService(new Intent(context, CacheMgrService.class));

        enableAutoCheckin(context);

    }

    public static void setActive(boolean active) {
        bActive = active;
    }

    public static boolean isActive() {
        return bActive;
    }

    // called from main activity on app exit
    public static void applicationWillExit(Context context) {

        Log.d("AppCAP", "Running app cleanup...");

        // ProximityManager.onStop(this);
        Log.d("AppCAP", "Disabling cache service...");
        // context.stopService(new Intent(context,CacheMgrService.class));
        CacheMgrService.stop();

        Log.d("AppCAP", "Disabling auto checkin...");
        disableAutoCheckin(context);

        try {
            UAirship.land();
        } catch (Exception e) {
            Log.d("AppCAP",
                    "ERROR: UAirship crash landed: " + e + ", "
                            + e.getStackTrace());
        }

    }

    public static boolean autoCheckinEnabled() {
        return locationDetectionServiceRunning;
    }

    public static void enableAutoCheckin(Context context) {
        int[] currentAutoCheckinVenues = getVenuesWithAutoCheckins();
        // If the list of autocheckins is non-zero in length then proceed.
        // Otherwise we can keep the whole locationDetectionStateMachine off
        if (currentAutoCheckinVenues.length > 0) {
            // If the service is already running don't start it.
            // There should only be one
            if (!locationDetectionServiceRunning) {

                context.startService(new Intent(context,
                        LocationDetectionService.class));
                locationDetectionServiceRunning = true;
            }
        }
    }

    public static void disableAutoCheckin(Context context) {
        if (locationDetectionServiceRunning) {

            context.stopService(new Intent(context,
                    LocationDetectionService.class));
            locationDetectionServiceRunning = false;
        }
    }

    public static void showToast(String msg) {

        Toast.makeText(mapContext, msg, Toast.LENGTH_LONG).show();

    }

    /**
     * 
     * @category sharedResource
     */
    public static HttpUtil getConnection() {
        return instance.http;
    }

    /**
     * 
     * @category sharedResource
     */
    /*
     * public static Counter getCounter () { return instance.timingCounter; }
     */

    /**
     * 
     * @category counter
     */
    /*
     * public static void startCounter(Context context) {
     * 
     * Log.d("AppCAP","startCounter()");
     * 
     * instance.timingCounter.start();
     * 
     * // Start passive location listener service //context.startService(new
     * Intent(context, LocationUpdateService.class)); }
     */

    /**
     * 
     * @category localUserData
     */
    private static SharedPreferences getSharedPreferences() {
        return instance.getSharedPreferences(AppCAP.TAG, MODE_PRIVATE);
    }

    /*
     * Add a Venue to the User Checkin List
     * 
     * @category localUserData
     */
    public static boolean addVenueToUserCheckinList(int venueId) {

        int[] currentVenues = getVenuesWithUserCheckins();
        int venueIdx = 0;

        // If the venue is already present, return false
        while (venueIdx < currentVenues.length) {
            if (currentVenues[venueIdx] == venueId)
                return false;
            venueIdx += 1;
        }

        ArrayList<Integer> venueArray = new ArrayList<Integer>();

        venueIdx = 0;

        // Concat all venues together
        while (venueIdx < currentVenues.length) {
            venueArray.add(currentVenues[venueIdx]);
            venueIdx += 1;
        }

        // Save comma-separated string to preferences
        venueArray.add(venueId);

        String newPrefValue = gsonConverter.toJson(venueArray);

        Log.d("GSON", "Writing string to user checkins: " + newPrefValue);

        getSharedPreferences().edit()
                .putString(TAG_VENUES_WITH_USER_CHECKINS, newPrefValue)
                .commit();
        return true;

    }

    /*
     * Return list of venue IDs for which the user has selected Auto Checkin
     */
    public static int[] getVenuesWithUserCheckins() {

        // return
        // getSharedPreferences().getStringSet("venuesWithUserCheckins",null);
        String venueStrings = getSharedPreferences().getString(
                TAG_VENUES_WITH_USER_CHECKINS, null);
        Log.d("GSON", "Queried user checkins value: " + venueStrings);

        int[] returnArray = gsonConverter.fromJson(venueStrings, int[].class);

        if (returnArray != null)
            return returnArray;
        else
            return new int[0];
    }

    /*
     * Add a Venue to the Auto Checkin List
     * 
     * @category localUserData
     */
    public static boolean enableAutoCheckinForVenue(int venueId) {

        int[] currentAutoCheckinVenues = getVenuesWithAutoCheckins();
        int venueIdx = 0;

        // If the venue is already present, return - this should not happen and
        // would be considered a bug
        while (venueIdx < currentAutoCheckinVenues.length) {
            if (currentAutoCheckinVenues[venueIdx] == venueId) {
                Log.d(TAG,
                        "WARNING: Tried to enable autocheckin for a venue already on the list...");
                return false;
            }
            venueIdx += 1;
        }

        ArrayList<Integer> venueArray = new ArrayList<Integer>();

        venueIdx = 0;

        // Concat all venues together
        while (venueIdx < currentAutoCheckinVenues.length) {
            venueArray.add(currentAutoCheckinVenues[venueIdx]);
            venueIdx += 1;
        }

        // Save comma-separated string to preferences
        venueArray.add(venueId);
        String newPrefValue = gsonConverter.toJson(venueArray);

        Log.d("GSON", "Updating auto checkins value: " + newPrefValue);

        getSharedPreferences().edit()
                .putString(TAG_VENUES_WITH_AUTO_CHECKINS, newPrefValue)
                .commit();

        return true;

    }

    public static boolean disableAutoCheckinForVenue(int venueId) {
        int[] currentAutoCheckinVenues = getVenuesWithAutoCheckins();

        ArrayList<Integer> venueArray = new ArrayList<Integer>();

        int venueIdx = 0;

        boolean venueFound = false;

        // Concat all venues together except target venue
        while (venueIdx < currentAutoCheckinVenues.length) {
            if (currentAutoCheckinVenues[venueIdx] != venueId) {
                venueArray.add(currentAutoCheckinVenues[venueIdx]);
            } else {
                venueFound = true;
            }

            venueIdx += 1;
        }

        if (!venueFound) {
            Log.d(TAG,
                    "WARNING: Tried to disable autocheckin for a venue not on the list...");
        }

        // Save json-encoded string
        String newPrefValue = gsonConverter.toJson(venueArray);

        Log.d("GSON", "Saving auto checkins value: " + newPrefValue);

        getSharedPreferences().edit()
                .putString(TAG_VENUES_WITH_AUTO_CHECKINS, newPrefValue)
                .commit();
        return true;
    }

    /*
     * Return list of venue IDs for which the user has selected Auto Checkin
     */
    public static int[] getVenuesWithAutoCheckins() {

        String venueStrings = getSharedPreferences().getString(
                TAG_VENUES_WITH_AUTO_CHECKINS, null);
        Log.d("GSON", "Queried auto checkins value: " + venueStrings);

        int[] returnArray = gsonConverter.fromJson(venueStrings, int[].class);

        if (returnArray != null)
            return returnArray;
        else
            return new int[0];

    }

    public static boolean isVenueAutoCheckinEnabled(int venueId) {
        int[] currentAutoCheckinVenues = getVenuesWithAutoCheckins();
        int venueIdx = 0;

        // Concat all venues together except target venue
        while (venueIdx < currentAutoCheckinVenues.length) {
            if (currentAutoCheckinVenues[venueIdx] == venueId)
                return true;
            venueIdx += 1;
        }

        return false;
    }

    /**
     * 
     * @category localUserData
     */
    public static boolean isFirstStart() {
        return getSharedPreferences().getBoolean(TAG_FIRST_START, true);
    }

    /**
     * 
     * @category setter
     */
    public static void setNotFirstStart() {
        getSharedPreferences().edit().putBoolean(TAG_FIRST_START, false)
                .commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static boolean getEnteredInviteCode() {
        return getSharedPreferences().getBoolean(TAG_USER_ENTERED_INVITE_CODE,
                false);
    }

    /**
     * 
     * @category setter
     */
    public static void setEnteredInviteCode() {
        getSharedPreferences().edit()
                .putBoolean(TAG_USER_ENTERED_INVITE_CODE, true).commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static boolean shouldShowInfoDialog() {
        return getSharedPreferences().getBoolean(TAG_INFO_DIALOG, true);
    }

    public static void dontShowInfoDialog() {
        getSharedPreferences().edit().putBoolean(TAG_INFO_DIALOG, false)
                .commit();
    }

    /**
     * 
     * @category globalSetting
     */
    public static boolean isMetrics() {
        return getSharedPreferences().getBoolean(TAG_METRIC_SYSTEM, false);
    }

    /**
     * 
     * @category setter
     */
    private void setMetricsSys(boolean set) {
        getSharedPreferences().edit().putBoolean(TAG_METRIC_SYSTEM, set)
                .commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static String getUserEmail() {
        return getSharedPreferences().getString(TAG_USER_EMAIL, "");
    }

    /**
     * 
     * @category setter
     */
    public static void setUserEmail(String email) {
        getSharedPreferences().edit().putString(TAG_USER_EMAIL, email).commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static String getUserEmailPassword() {
        return getSharedPreferences().getString(TAG_USER_EMAIL_PASSWORD, "");
    }

    /**
     * 
     * @category setter
     */
    public static void setUserEmailPassword(String pass) {
        getSharedPreferences().edit().putString(TAG_USER_EMAIL_PASSWORD, pass)
                .commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static int getUserLastCheckinVenueId() {
        return getSharedPreferences().getInt(TAG_USER_LAST_VENUE_CHECKIN_ID, 0);
    }

    /**
     * 
     * @category setter
     */
    public static void setUserLastCheckinVenueId(int venueId) {
        getSharedPreferences().edit()
                .putInt(TAG_USER_LAST_VENUE_CHECKIN_ID, venueId).commit();
    }

    /**
     * 
     * @category unknown
     */
    public static String cleanResponseString(String data) {
        String retS = data;
        data = Html.fromHtml(data).toString();

        try {
            retS = URLDecoder.decode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return retS;
    }

    /**
     * 
     * @category localUserData
     */
    public static String getLocalUserPhotoURL() {
        return getSharedPreferences().getString(TAG_USER_PHOT_URL, "");
    }

    /**
     * 
     * @category setter
     */
    public static void setLocalUserPhotoURL(String url) {
        getSharedPreferences().edit().putString(TAG_USER_PHOT_URL, url)
                .commit();
    }

    /**
     * 
     * @category setter
     */
    public static void setLocalUserPhotoLargeURL(String url) {
        getSharedPreferences().edit().putString(TAG_USER_PHOT_LARGE_URL, url)
                .commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static String getLocalUserPhotoLargeURL() {
        return getSharedPreferences().getString(TAG_USER_PHOT_LARGE_URL, "");
    }

    /**
     * 
     * @category setter
     */
    public static String setUserLinkedInToken() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN, "");
    }

    /**
     * 
     * @category setter
     */
    public static String setUserLinkedInTokenSecret() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN_SECRET,
                "");
    }

    /**
     * 
     * @category setter
     */
    public static String setUserLinkedInID() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_ID, "");
    }

    /**
     * 
     * @category setter
     */
    public static void setUserLinkedInDetails(String token, String tokenSecret,
            String id) {
        getSharedPreferences().edit().putString(TAG_USER_LINKEDIN_ID, id)
                .commit();
        getSharedPreferences().edit().putString(TAG_USER_LINKEDIN_TOKEN, token)
                .commit();
        getSharedPreferences().edit()
                .putString(TAG_USER_LINKEDIN_TOKEN_SECRET, tokenSecret)
                .commit();
    }

    /**
     * 
     * @category localUserData
     */
    public static String getUserLinkedInID() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_ID, "");
    }

    /**
     * 
     * @category localUserData
     */
    public static String getUserLinkedInToken() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN, "");
    }

    /**
     * 
     * @category localUserData
     */
    public static String getUserLinkedInTokenSecret() {
        return getSharedPreferences().getString(TAG_USER_LINKEDIN_TOKEN_SECRET,
                "");
    }

    /**
     * 
     * @category localUserData
     */
    public static void setLoggedInUserId(int userId) {

        // code will send user ID of zero on logout
        // if nonzero (login), update the Urban Airship alias and enable push
        // TODO: add user preferences to control whether to enable push
        if (userId != 0) {
            // Register userID as UAirship alias for server-side pushes
            PushPreferences prefs = PushManager.shared().getPreferences();
            prefs.setAlias(String.valueOf(userId));

            PushManager.shared().setIntentReceiver(IntentReceiver.class);
            PushManager.enablePush();
        }

        // Save logged in user ID
        getSharedPreferences().edit().putInt(TAG_LOGGED_IN_USER_ID, userId)
                .commit();

    }

    /**
     * 
     * @category localUserData
     */
    public static int getLoggedInUserId() {
        return getSharedPreferences().getInt(TAG_LOGGED_IN_USER_ID, 0);
    }

    /**
     * 
     * @category setter
     */
    public static String getLoggedInUserNickname() {
        return getSharedPreferences()
                .getString(TAG_LOGGED_IN_USER_NICKNAME, "");
    }

    /**
     * 
     * @category localUserData
     */
    public static void setLoggedInUserNickname(String nickname) {
        getSharedPreferences().edit()
                .putString(TAG_LOGGED_IN_USER_NICKNAME, nickname).commit();
    }
    
    public static int getLoggedInUserEmail() {
        return getSharedPreferences().getInt(TAG_USER_EMAIL, 0);
    }
    
    public static void setLoggedInUserEmail(String email) {
        getSharedPreferences().edit().putString(TAG_USER_EMAIL, email).commit();
    }

    /**
     * 
     * @category setter
     */
    public static void setUserCoordinates(double[] data) {
        if (data[4] != 0 && data[5] != 0) {
            getSharedPreferences().edit()
                    .putFloat(TAG_USER_COORDINATES + "sw_lat", (float) data[0])
                    .commit();
            getSharedPreferences().edit()
                    .putFloat(TAG_USER_COORDINATES + "sw_lng", (float) data[1])
                    .commit();
            getSharedPreferences().edit()
                    .putFloat(TAG_USER_COORDINATES + "ne_lat", (float) data[2])
                    .commit();
            getSharedPreferences().edit()
                    .putFloat(TAG_USER_COORDINATES + "ne_lng", (float) data[3])
                    .commit();
            getSharedPreferences()
                    .edit()
                    .putFloat(TAG_USER_COORDINATES + "user_lat",
                            (float) data[4]).commit();
            getSharedPreferences()
                    .edit()
                    .putFloat(TAG_USER_COORDINATES + "user_lng",
                            (float) data[5]).commit();
        }
    }

    /**
     * data[0] = sw_lat; data[1] = sw_lng; data[2] = ne_lat; data[3] = ne_lng;
     * data[4] = user_lat; data[5] = user_lng;
     * 
     * @category localUserData
     */
    public static double[] getUserCoordinates() {
        double[] data = new double[6];
        data[0] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "sw_lat", 0);
        data[1] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "sw_lng", 0);
        data[2] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "ne_lat", 0);
        data[3] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "ne_lng", 0);
        data[4] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "user_lat", 0);
        data[5] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "user_lng", 0);
        return data;
    }

    /**
     * data[0] = user_lat; data[1]) = user_lng;
     * 
     * @category localUserData
     */
    public static double[] getUserLatLon() {
        double[] data = new double[2];
        data[0] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "user_lat", 0);
        data[1] = (double) getSharedPreferences().getFloat(
                TAG_USER_COORDINATES + "user_lng", 0);
        return data;
    }

    public static void setUserLatLon(float lat, float lng) {
        getSharedPreferences()
            .edit()
            .putFloat(TAG_USER_COORDINATES + "user_lat",
                lat).commit();
        getSharedPreferences()
            .edit()
            .putFloat(TAG_USER_COORDINATES + "user_lng",
                lng).commit();
    }
    
    /**
     * 
     * @category localUserData
     */
    public static boolean isUserCheckedIn() {
        return getSharedPreferences().getBoolean(TAG_IS_USER_CHECKED_IN, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setUserCheckedIn(boolean set) {
        getSharedPreferences().edit().putBoolean(TAG_IS_USER_CHECKED_IN, set)
                .commit();
    }

    /**
     * 
     * @category unknown
     */
    public static boolean shouldFinishActivities() {
        return getSharedPreferences().getBoolean(
                TAG_SHOULD_FINISH_ACTIVITY_MAP, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setShouldFinishActivities(boolean set) {
        getSharedPreferences().edit()
                .putBoolean(TAG_SHOULD_FINISH_ACTIVITY_MAP, set).commit();
    }

    /**
     * 
     * @category unknown
     */
    public static boolean shouldStartLogIn() {
        return getSharedPreferences()
                .getBoolean(TAG_SHOULD_START_LOG_IN, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setShouldStartLogIn(boolean set) {
        getSharedPreferences().edit().putBoolean(TAG_SHOULD_START_LOG_IN, set)
                .commit();
    }

    /**
     * 
     * @category unknown
     */
    public static boolean isStartingLoginPageFromContacts() {
        return getSharedPreferences().getBoolean(
                TAG_START_LOGIN_PAGE_FROM_CONTACTS, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setStartLoginPageFromContacts(boolean set) {
        getSharedPreferences().edit()
                .putBoolean(TAG_START_LOGIN_PAGE_FROM_CONTACTS, set).commit();
    }

    /**
     * 
     * @category unknown
     */
    public static String getCookieString() {
        return getSharedPreferences().getString(TAG_COOKIE_STRING, "");
    }

    /**
     * 
     * @category setter
     */
    public static void setCookieString(String cookie) {
        getSharedPreferences().edit().putString(TAG_COOKIE_STRING, cookie)
                .commit();
    }

    /**
     * 
     * @category setter
     */
    public static void setLoggedIn(boolean set) {
        getSharedPreferences().edit().putBoolean(TAG_IS_LOGGED_IN, set)
                .commit();
    }

    /**
     * 
     * @category localUserState
     */
    public static boolean isLoggedIn() {
        return getSharedPreferences().getBoolean(TAG_IS_LOGGED_IN, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setPushDistance(String dist) {
        getSharedPreferences().edit().putString(TAG_PUSH_DISTANCE, dist)
                .commit();
    }

    /**
     * 
     * @category utility
     */
    public static String getPushDistance() {
        return getSharedPreferences().getString(TAG_PUSH_DISTANCE, "city");
    }

    /**
     * 
     * @category unknown
     */
    public static void refreshNotificationSettings() {
        DataHolder data = getConnection().getNotificationSettings();
        JSONObject settings = (JSONObject) data.getObject();

        // null probably means there's no network connectivity
        if (settings != null) {
            String push_distance = settings.optString("push_distance", "city");
            boolean checked_in_only = settings.optInt("checked_in_only", 0) == 0 ?
                false : true;
            boolean quiet_time_enabled = settings.optInt("quiet_time", 0) == 0 ?
                false : true;
            String quiet_from = settings.optString("quiet_time_from", "");
            String quiet_to = settings.optString("quiet_time_to", "");
            boolean contacts_only_chat = settings.optInt("contacts_only_chat", 0) == 0 ?
                false : true;

            setPushDistance(push_distance);
            setNotificationToggle(checked_in_only);
            setQuietTimeToggle(quiet_time_enabled);
            setQuietFrom(quiet_from);
            setQuietTo(quiet_to);
            setContactsOnlyChatToggle(contacts_only_chat);
        }
    }

    /**
     * 
     * @category setter
     */
    public static void setNotificationToggle(boolean res) {
        getSharedPreferences().edit().putBoolean(TAG_NOTIFICATION_TOGGLE, res)
                .commit();
    }


    /**
     * 
     * @category unknown
     */
    public static boolean getNotificationToggle() {
        return getSharedPreferences()
                .getBoolean(TAG_NOTIFICATION_TOGGLE, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setQuietFrom(String from) {
        getSharedPreferences().edit().putString(TAG_QUIET_FROM, from)
                .commit();
    }

    /**
     * 
     * @category setter
     */
    public static void setQuietTo(String to) {
        getSharedPreferences().edit().putString(TAG_QUIET_TO, to)
                .commit();
    }

    /**
     * 
     * @category setter
     */
    public static void setQuietTimeToggle(boolean quietEnabled) {
        getSharedPreferences().edit().putBoolean(TAG_QUIET_TOGGLE, quietEnabled)
                .commit();
    }

    /**
     * 
     * @category unknown
     */
    public static String getQuietFrom() {
        return getSharedPreferences().getString(TAG_QUIET_FROM,
                DEFAULT_QUIET_FROM);
    }

    /**
     * 
     * @category unknown
     */
    public static String getQuietTo() {
        return getSharedPreferences().getString(TAG_QUIET_TO,
                DEFAULT_QUIET_TO);
    }

    /**
     * 
     * @category unknown
     */
    public static boolean getQuietTimeToggle() {
        return getSharedPreferences()
                .getBoolean(TAG_QUIET_TOGGLE, false);
    }

    /**
     * 
     * @category setter
     */
    public static void setContactsOnlyChatToggle(boolean contactsOnly) {
        getSharedPreferences().edit().putBoolean(TAG_CONTACTS_ONLY_CHAT_TOGGLE, contactsOnly)
                .commit();
    }

    /**
     * 
     * @category unknown
     */
    public static boolean getContactsOnlyChatToggle() {
        return getSharedPreferences()
                .getBoolean(TAG_CONTACTS_ONLY_CHAT_TOGGLE, false);
    }

    /**
     * 
     * @category utility
     */
    public static int getScreenWidth() {
        return getSharedPreferences().getInt(TAG_SCREEN_WIDTH, 480);
    }

    /**
     * 
     * @category utility
     */
    public static void saveScreenWidth(int screenWidth) {
        getSharedPreferences().edit().putInt(TAG_SCREEN_WIDTH, screenWidth)
                .commit();
    }

    /**
     * 
     * @category unknown
     */
    public static void logInFile(String data) {
        try {
            FileOutputStream fOut = instance.openFileOutput("big_log.txt",
                    MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @category setter
     */
    public static void addAutoCheckinWifiSignature(venueWifiSignature currentSig) {
        Gson gsonConverter = new Gson();
        Type listOfVenueWifiSigs = new TypeToken<ArrayList<venueWifiSignature>>() {
        }.getType();

        String jsonWifiSigs = getSharedPreferences().getString(
                TAG_VENUE_WIFI_SIGNATURES, "");
        ArrayList<venueWifiSignature> ArrayOfSignatures = new ArrayList<venueWifiSignature>();
        if (jsonWifiSigs.equals("")) {
            // No existing venues in autocheckin list
        } else {
            ArrayOfSignatures = gsonConverter.fromJson(jsonWifiSigs,
                    listOfVenueWifiSigs);
        }
    }

    public static void setMapCenterCoordinates(int lngSpan, int latSpan) {
        mapCenterLng = lngSpan;
        mapCenterLat = latSpan;
    }

    public static double[] getMapCenterLatLon() {
        double[] data = new double[2];
        data[0] = (double) mapCenterLat / 1000000;
        data[1] = (double) mapCenterLng / 1000000;
        return data;
    }

    public static Context getAppContext() {
        return AppCAP.context;
    }

    /**
     * 
     * @category setter
     */
    public static void removeAutoCheckinWifiSignature(int venueId) {
        venueWifiSignature currentSig = new venueWifiSignature();
        currentSig.venueId = venueId;
        Gson gsonConverter = new Gson();
        Type listOfVenueWifiSigs = new TypeToken<ArrayList<venueWifiSignature>>() {
        }.getType();

        String jsonWifiSigs = getSharedPreferences().getString(
                TAG_VENUE_WIFI_SIGNATURES, "");

        ArrayList<venueWifiSignature> ArrayOfSignatures = gsonConverter
                .fromJson(jsonWifiSigs, listOfVenueWifiSigs);
        if (ArrayOfSignatures.contains(currentSig)) {
            ArrayOfSignatures.remove(currentSig);
        }
        String outputString = gsonConverter.toJson(ArrayOfSignatures,
                listOfVenueWifiSigs);
        getSharedPreferences().edit()
                .putString(TAG_VENUE_WIFI_SIGNATURES, outputString).commit();

    }

    /**
     * 
     * @category localUserData
     */
    public static ArrayList<venueWifiSignature> getAutoCheckinWifiSignatures() {
        Gson gsonConverter = new Gson();
        Type listOfVenueWifiSigs = new TypeToken<ArrayList<venueWifiSignature>>() {
        }.getType();

        String jsonWifiSigs = getSharedPreferences().getString(
                TAG_VENUE_WIFI_SIGNATURES, "");

        if (jsonWifiSigs == "") {
            return new ArrayList<venueWifiSignature>();
        }
        return gsonConverter.fromJson(jsonWifiSigs, listOfVenueWifiSigs);
    }

    /**
     * 
     * @category tempTestData
     */
    /*
     * public static ArrayList<venueWifiSignature>
     * getAutoCheckinWifiSignatures() { ArrayList<venueWifiSignature>
     * arrayOfVenuesSigs = new ArrayList<venueWifiSignature>(); //Data for C&P
     * List<String> testBssids = Arrays.asList("98:fc:11:8f:8f:b0",
     * "00:1c:b3:ff:8d:53", "f4:6d:04:6d:33:2e", "e0:91:f5:87:71:2b",
     * "74:91:1a:50:eb:98","c4:3d:c7:8d:6b:f8"); ArrayList<MyScanResult>
     * venueWifiNetworks = new ArrayList<MyScanResult>(); for(String
     * currBssid:testBssids) { venueWifiNetworks.add(new
     * MyScanResult(currBssid)); }
     * 
     * venueWifiSignature testSignature = new venueWifiSignature();
     * testSignature.venueId = 23;
     * testSignature.addConnectedSSID("coffeeandpower");
     * testSignature.addWifiNetworkToSignature(venueWifiNetworks);
     * arrayOfVenuesSigs.add(testSignature);
     * 
     * //This is a fake test list for Andrew's testBssids =
     * Arrays.asList("00:24:36:a4:f5:2d", "e4:83:99:07:c8:e0",
     * "20:4e:7f:44:cd:dc", "1c:14:48:09:30:40", "c8:60:00:94:33:12",
     * "30:46:9a:1c:63:5c"); ArrayList<MyScanResult> andrewWifiNetworks = new
     * ArrayList<MyScanResult>(); for(String currBssid:testBssids) {
     * andrewWifiNetworks.add(new MyScanResult(currBssid)); }
     * 
     * venueWifiSignature andrewTestSignature = new venueWifiSignature();
     * andrewTestSignature.addConnectedSSID("veronica");
     * andrewTestSignature.addWifiNetworkToSignature(andrewWifiNetworks);
     * 
     * arrayOfVenuesSigs.add(andrewTestSignature); return arrayOfVenuesSigs; }
     */
    private String getAppName() {
        int pID = android.os.Process.myPid();
        String processName = "";
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        // PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
                    .next());
            try {
                if (info.pid == pID) {
                    // CharSequence c =
                    // pm.getApplicationLabel(pm.getApplicationInfo(info.processName,
                    // PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    public static void removeUserLastCheckinVenue(int venueId) {
        for (VenueNameAndFeeds currVenue : listLastCheckedinVenues) {
            if (currVenue.getVenueId() == venueId) {
                listLastCheckedinVenues.remove(currVenue);
                break;
            }
        }
        setUserLastCheckinVenue();
    }

    public static void updateUserLastCheckinVenue(
            VenueNameAndFeeds checkedInVenue, boolean topPosition) {
        for (VenueNameAndFeeds currVenue : listLastCheckedinVenues) {
            if (currVenue.getVenueId() == checkedInVenue.getVenueId()) {
                if (topPosition) {
                    listLastCheckedinVenues.remove(currVenue);
                    break;
                } else {
                    return;
                }
            }
        }
        if (topPosition) {
            ArrayList<VenueNameAndFeeds> newList = new ArrayList<VenueNameAndFeeds>();
            newList.add(checkedInVenue);
            newList.addAll(listLastCheckedinVenues);
            listLastCheckedinVenues = newList;
        } else {
            listLastCheckedinVenues.add(checkedInVenue);
        }
        setUserLastCheckinVenue();
    }

    public static String getUserLastCheckinVenueIds() {
        String listIds = "";
        for (VenueNameAndFeeds currVenue : listLastCheckedinVenues) {
            if (listIds.contentEquals("") == false) {
                listIds = listIds + ",";
            }
            listIds = listIds + String.valueOf(currVenue.getVenueId());
        }
        return listIds;
    }

    private static void setUserLastCheckinVenue() {
        JSONArray mJSONArray = new JSONArray();
        for (VenueNameAndFeeds currVenue : listLastCheckedinVenues) {
            JSONObject jsonVenue;
            jsonVenue = new JSONObject();
            try {
                jsonVenue.put("id", currVenue.getVenueId());
                jsonVenue.put("name", currVenue.getName());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mJSONArray.put(jsonVenue);
        }
        getSharedPreferences().edit()
                .putString(TAG_LAST_CHECKEDIN_VENUES, mJSONArray.toString())
                .commit();
    }


    public static void queueLocalNotificationForVenue(Context context, VenueSmart venue, int checkInDuration){
    // Fire a notification 5 minutes before checkout time
    int minutesBefore = 5;   
    long currentTime = System.currentTimeMillis();
    long checkOutTime = currentTime + 
            (checkInDuration * 60 * 60 * 1000) - 
            (minutesBefore * 60 * 1000);
    Intent intent = new Intent(context, CheckOutIntentReceiver.class);
    intent.putExtra("alarm_message", "You will be checked out of " + 
            venue.getName() + " in " + minutesBefore + " min." );
    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    am.set(AlarmManager.RTC_WAKEUP, checkOutTime, sender);   
}

    
    public static ArrayList<VenueNameAndFeeds> getListLastCheckedinVenues() {
        return listLastCheckedinVenues;
    }

    public static void setListLastCheckedinVenues(
            ArrayList<VenueNameAndFeeds> listLastCheckedinVenues) {
        AppCAP.listLastCheckedinVenues = listLastCheckedinVenues;
    }
    
    public boolean isDebuggable(Context ctx)
    {
        boolean debuggable = false;
     
        PackageManager pm = ctx.getPackageManager();
        try
        {
            ApplicationInfo appinfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        }
        catch(NameNotFoundException e)
        {
            /*debuggable variable will remain false*/
        }
         
        return debuggable;
    }

    public static boolean isReleaseApp() {
        return releaseApp;
    }

    public static void setReleaseApp(boolean releaseApp) {
        AppCAP.releaseApp = releaseApp;
    }

    private static void getUserLastCheckinVenue() {
        String sListLastCheckedinVenues = getSharedPreferences().getString(
                TAG_LAST_CHECKEDIN_VENUES, "");
        JSONArray venues = null;
        try {
            venues = new JSONArray(sListLastCheckedinVenues);
        } catch (JSONException e) {
        }
        if (listLastCheckedinVenues != null) {
            listLastCheckedinVenues.clear();
        } else {
            listLastCheckedinVenues = new ArrayList<VenueNameAndFeeds>();
        }
        if (venues != null) {
            for (int m = 0; m < venues.length(); m++) {

                JSONObject venue = venues.optJSONObject(m);
                if (venue != null) {
                    try {
                        listLastCheckedinVenues.add(new VenueNameAndFeeds(venue
                                .getInt("id"), venue.getString("name")));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
