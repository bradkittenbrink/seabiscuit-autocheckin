package com.coffeeandpower.urbanairship;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class IntentReceiver extends BroadcastReceiver {

    private static final String TAG = "LOG";

    public static final String ACTION_CONTACT_EXCHANGE_REQUESTED = "com.coffeeandpower.intent.CONTACT_EXCHANGE_REQUEST_ACTION";
    public static final String ACTION_CONTACT_EXCHANGE_ACCEPTED = "com.coffeeandpower.intent.CONTACT_EXCHANGE_ACCEPTED_ACTION";

    public static final String EXTRA_ALERT = PushManager.EXTRA_ALERT;
    // extra delivered with sendContactRequest
    public static final String EXTRA_CONTACT_REQUEST_SENDER = "contact_request";
    // extras delivered with acceptContactRequest
    public static final String EXTRA_CONTACT_REQUEST_ACCEPTED_ID = "contact_accepted";
    public static final String EXTRA_CONTACT_REQUEST_ACCEPTED_NICK = "acceptor";
    // extra to denote that app may require force start to handle intents
    public static final String EXTRA_FORCE_START = "force_start";
    public static final String EXTRA_CONTACT_ACTION_PENDING = "contact_action_pending";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.i(TAG, "Received intent: " + intent.toString());

        String action = intent.getAction();
        String alert = intent.getStringExtra(EXTRA_ALERT);
        // note that the new urbanairship API deprecates the monolithic
        // string extra in favor of name/value pairs
        Set<String> extraKeys = intent.getExtras().keySet();

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
            /*
             * UAirship has received a push notification which may or may not have
             * resulted in a system notification being placed with the Notification
             * Manager - check extras to determine what type of push it is we're 
             * dealing with
             */
            if (extraKeys.contains(EXTRA_CONTACT_REQUEST_SENDER)) {
                // push is requesting contact exchange

                if (null != intent.getExtras() &&
                    null != intent.getExtras().getString(EXTRA_FORCE_START)) {
                    // the app isn't active so there's no point firing our internal
                    // intent - this will be handled when it's opened from the 
                    // notification manager
                    Log.d(TAG, "deferring contact request while app not active.");
                } else {

                    String sender = intent.getStringExtra(EXTRA_CONTACT_REQUEST_SENDER);

                    // intent for RootActivity to pick up if running
                    Intent i = new Intent();
                    i.setAction(ACTION_CONTACT_EXCHANGE_REQUESTED);
                    i.putExtra(EXTRA_ALERT, alert);
                    i.putExtra(EXTRA_CONTACT_REQUEST_SENDER, sender);

                    // fire away
                    context.sendBroadcast(i);
                }

            } else if (extraKeys.contains(EXTRA_CONTACT_REQUEST_ACCEPTED_ID) &&
                    extraKeys.contains(EXTRA_CONTACT_REQUEST_ACCEPTED_NICK)) {
                // push is acknowledging accepted contact exchange request

                if (null != intent.getExtras().getString(EXTRA_FORCE_START)) {
                    // the app isn't active so there's no point firing our internal
                    // intent - this will be handled when it's opened from the 
                    // notification manager
                    Log.d(TAG, "deferring contact acknowledgement while app not active.");
                } else {
                    String cid = intent.getStringExtra(EXTRA_CONTACT_REQUEST_ACCEPTED_ID);
                    String cname = intent.getStringExtra(EXTRA_CONTACT_REQUEST_ACCEPTED_NICK);

                    // intent for RootActivity to pick up if running
                    Intent i = new Intent();
                    i.setAction(ACTION_CONTACT_EXCHANGE_ACCEPTED);
                    i.putExtra(EXTRA_ALERT, alert);
                    i.putExtra(EXTRA_CONTACT_REQUEST_ACCEPTED_ID, cid);
                    i.putExtra(EXTRA_CONTACT_REQUEST_ACCEPTED_NICK, cname);

                    // fire away
                    context.sendBroadcast(i);
                }
            }
            logPushExtras(intent);

        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
            /*
             * UAirship has placed a notification with the notification manager
             * and we've opened it
             */
            Log.i(TAG,
                    "User clicked notification. Message: "
                            + intent.getStringExtra(PushManager.EXTRA_ALERT)
                            + ". Payload: "
                            + " + Extras: " + intent.getExtras().keySet().toString()
                            );

            if (null != intent.getExtras().getString(EXTRA_FORCE_START)) {
                /*
                 *  we need to launch the app first, and then have it pick up
                 *  the request to allow the user to complete the exchange
                 */

                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClass(UAirship.shared().getApplicationContext(),
                        ActivityLoginPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (extraKeys.contains(EXTRA_CONTACT_REQUEST_SENDER)) {
                    i.putExtra(EXTRA_CONTACT_ACTION_PENDING, "true");
                    String sender = intent.getStringExtra(EXTRA_CONTACT_REQUEST_SENDER);
                    i.putExtra(EXTRA_ALERT, alert);
                    i.putExtra(EXTRA_CONTACT_REQUEST_SENDER, sender);
                }

                UAirship.shared().getApplicationContext().startActivity(i);

            }

        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.i(TAG,
                    "Registration complete. APID:"
                            + intent.getStringExtra(PushManager.EXTRA_APID)
                            + ". Valid: "
                            + intent.getBooleanExtra(
                                    PushManager.EXTRA_REGISTRATION_VALID, false));
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
        // note that the new urbanairship API deprecates the monolithic
        // string extra in favor of name/value pairs
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {

            // ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>) Arrays.asList(
                    "collapse_key",// c2dm
                    // collapse
                    // key
                    "from",// c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,// int
                    // id
                    // of
                    // generated
                    // notification
                    // (ACTION_PUSH_RECEIVED
                    // only)
                    PushManager.EXTRA_PUSH_ID// internal UA
                    // push id
                    //PushManager.EXTRA_ALERT
                    );// ignore alert
            if (ignoredKeys.contains(key)) {
                continue;
            }
            Log.i("LOG",
                    "Push Notification Extra: [" + key + " : "
                            + intent.getStringExtra(key) + "]");
        }
    }
}
