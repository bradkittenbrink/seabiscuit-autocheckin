package com.coffeeandpower.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.urbanairship.IntentReceiver;
import com.coffeeandpower.views.CustomDialog;

public class ActivityUtils {

    public static abstract class Action implements Runnable {
        protected DataHolder result;
        protected LinkedIn service;
        protected ActivityUtils.ProgressHandler handler;
        protected Runnable action;

        public DataHolder getResult() {
            return result;
        }
    }

    public static class ProgressHandler extends Handler {
        ProgressDialog progress;
        DataHolder result;

        public void setResult(DataHolder result_) {
            result = result_;
        }
    }

    public static class JoinProgressHandler extends ProgressHandler {

        public JoinProgressHandler(Activity a) {
            progress = new ProgressDialog(a);
            progress.setOwnerActivity(a);
            progress.setMessage("Accessing Account...");
            progress.show();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity a = progress.getOwnerActivity();
            progress.dismiss();

            switch (msg.what) {

            case AppCAP.HTTP_ERROR:
                new CustomDialog(a, "Error", "Internet connection error")
                        .show();
                break;

            case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:
                if (result != null) {
                    new CustomDialog(a, "Error Accessing Account",
                            result.getResponseMessage()).show();
                }
                break;
            }
        }
    };

    public static class LoginProgressHandler extends ProgressHandler {

        HashMap<String, String> forwardedExtras;

        public LoginProgressHandler(Activity a) {

            forwardedExtras = new HashMap<String, String>();

            // if we need to login to handle an intent, cache it here and
            // pass it along accordingly
            if(a instanceof RootActivity) {
                final RootActivity ra = (RootActivity)a;
                Intent i = ra.getIntent();
                // a pending contact action is reason to forward an intent
                if(null != i.getStringExtra(IntentReceiver.EXTRA_CONTACT_ACTION_PENDING)) {
                    forwardedExtras.put(IntentReceiver.EXTRA_CONTACT_ACTION_PENDING, "true");
                    forwardedExtras.put(IntentReceiver.EXTRA_ALERT,
                            i.getStringExtra(IntentReceiver.EXTRA_ALERT));
                    forwardedExtras.put(IntentReceiver.EXTRA_CONTACT_REQUEST_SENDER,
                            i.getStringExtra(IntentReceiver.EXTRA_CONTACT_REQUEST_SENDER));
                }
            }

            progress = new ProgressDialog(a);
            progress.setOwnerActivity(a);
            progress.setMessage("Logging in...");
            progress.show();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity a = progress.getOwnerActivity();
            progress.dismiss();

            switch (msg.what) {

            case AppCAP.HTTP_ERROR:
                new CustomDialog(a, "Error", "Internet connection error")
                        .show();
                break;

            case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:
                new CustomDialog(a, "Error", "Could not login").show();
                break;

            case AppCAP.HTTP_REQUEST_SUCCEEDED:
                AppCAP.setLoggedIn(true);
                    Intent intent = new Intent(a, ActivityVenueFeeds.class);
                    intent.putExtra("fragment", R.id.tab_fragment_area_feed);
                    // check for more extras to forward
                    if(null != forwardedExtras && !forwardedExtras.isEmpty()) {
                        Set<String> keys = forwardedExtras.keySet();
                        Iterator<String> ki = keys.iterator();
                        while(ki.hasNext()) {
                            String key = ki.next();
                            intent.putExtra(key, forwardedExtras.get(key));
                        }
                    }
                    a.startActivity(intent);
                    a.finish();
                break;
            }
        }
    };

    public static class DisplayLinkedinLoginProgressHandler extends ProgressHandler {

        public DisplayLinkedinLoginProgressHandler(Activity a) {
            progress = new ProgressDialog(a);
            progress.setOwnerActivity(a);
            progress.setMessage("Logging in...");
            progress.show();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity a = progress.getOwnerActivity();
            progress.dismiss();

        }
    };

}
