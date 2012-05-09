package com.coffeeandpower.urbanairship;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coffeeandpower.activity.ActivityLoginPage;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class IntentReceiver extends BroadcastReceiver {

    private static final String TAG = "LOG";

    @Override
    public void onReceive(Context context, Intent intent) {

	Log.i(TAG, "Received intent: " + intent.toString());

	String action = intent.getAction();

	if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
	    int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

	    Log.i(TAG, "Received push notification. Alert: " + intent.getStringExtra(PushManager.EXTRA_ALERT) + ". Payload: "
		    + intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA) + ". NotificationID=" + id);

	    String alert = intent.getStringExtra(PushManager.EXTRA_ALERT);
	    String extra = intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA);

	    logPushExtras(intent);

	    // PushNotificationPlugin plugin =
	    // PushNotificationPlugin.getInstance();
	    // plugin.sendResultBack(alert, extra);

	} else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
	    Log.i(TAG, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT) + ". Payload: "
		    + intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA));

	    Intent launch = new Intent(Intent.ACTION_MAIN);
	    launch.setClass(UAirship.shared().getApplicationContext(), ActivityLoginPage.class);
	    launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	    UAirship.shared().getApplicationContext().startActivity(launch);

	} else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
	    Log.i(TAG,
		    "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID) + ". Valid: "
			    + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
	}
    }

    /**
     * Log the values sent in the payload's "extra" dictionary.
     * 
     * @param intent
     *            A PushManager.ACTION_NOTIFICATION_OPENED or
     *            ACTION_PUSH_RECEIVED intent.
     */
    private void logPushExtras(Intent intent) {
	Set<String> keys = intent.getExtras().keySet();
	for (String key : keys) {

	    // ignore standard C2DM extra keys
	    List<String> ignoredKeys = (List<String>) Arrays.asList("collapse_key",// c2dm
										   // collapse
										   // key
		    "from",// c2dm sender
		    PushManager.EXTRA_NOTIFICATION_ID,// int id of
						      // generated
						      // notification
						      // (ACTION_PUSH_RECEIVED
						      // only)
		    PushManager.EXTRA_PUSH_ID,// internal UA push id
		    PushManager.EXTRA_ALERT);// ignore alert
	    if (ignoredKeys.contains(key)) {
		continue;
	    }
	    Log.i("LOG", "Push Notification Extra: [" + key + " : " + intent.getStringExtra(key) + "]");
	}
    }
}
