package com.coffeeandpower.urbanairship;

import java.util.Map;

import android.app.Notification;

import com.coffeeandpower.AppCAP;
import com.urbanairship.push.BasicPushNotificationBuilder;

public class CapPushNotificationBuilder extends BasicPushNotificationBuilder {

    private static final String TAG = CapPushNotificationBuilder.class.getSimpleName();
    /* (non-Javadoc)
     * @see com.urbanairship.push.BasicPushNotificationBuilder#buildNotification(java.lang.String, java.util.Map)
     */
    @Override
    public Notification buildNotification(String arg0, Map<String, String> arg1) {
        // if app is active and we're logged in - we don't want the system 
        // notification, otherwise we do
        boolean active = AppCAP.isActive();
        boolean loggedin = AppCAP.isLoggedIn();

        if(active && loggedin) {
            // the app is already running with UI, so the notification will
            // filter through to the internal RootActivity's - no system notification
            return null;
        } else {
            // we need to use the system notification - but the app will have
            // to start/login before we hand handle anything
            arg1.put(IntentReceiver.EXTRA_FORCE_START, "true");
            return super.buildNotification(arg0, arg1);
        }
    }

}
