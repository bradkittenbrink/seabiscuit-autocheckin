package com.coffeeandpower;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.app.R;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class RootActivity extends FragmentActivity {

    public static final int DIALOG_MUST_BE_A_MEMBER = 30;

    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        if (Constants.debugLog)
            Log.d("RootActivity", "RootActivity.onCreate()");
    }

    @Override
    protected void onDestroy() {
        if (Constants.debugLog)
            Log.d("RootActivity", "RootActivity.onDestroy()");
        
        super.onDestroy();
    }
    
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        if (savedInstanceState.getBoolean("shouldchecklogin", false)) {
            int uid = AppCAP.getLoggedInUserId();
            if (uid != 0) {
                AppCAP.setLoggedInUserId(uid);
                AppCAP.setLoggedIn(true);            
            } else {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ActivityLoginPage.class);
                startActivity(i);
                this.finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("shouldchecklogin", true);
    }

    @Override
    protected void onPause() {
        if (Constants.debugLog)
            Log.d("RootActivity", "RootActivity.onPause()");

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (Constants.debugLog)
            Log.d("RootActivity", "RootActivity.onStop()");

        super.onStop();
    }

    /**
     * Easy log
     * 
     * @param msg
     */
    public static void log(String msg) {
        if (Constants.debugLog)
            Log.d(AppCAP.TAG, msg);
    }

    /**
     * Get distance between points
     * 
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     * @return String 100m or 5.4km
     */
    public static String getDistanceBetween(double startLat, double startLng,
            double endLat, double endLng, boolean addFarAway) {
        float[] results = new float[1];
        Location.distanceBetween(startLat, startLng, endLat, endLng, results);

        if (Float.isNaN(results[0])
                || (addFarAway && (results[0] / 1000) > 500)) {
            return AppCAP.getAppContext().getString(R.string.map_distance_far); 
        }
        return formatToMetricsOrImperial(results[0]);
    }

    public static String formatToMetricsOrImperial(float distance) {
        DecimalFormat oneDForm = new DecimalFormat("#.#");
        String distanceS = "";

        if (AppCAP.isMetrics()) {
            if (distance < 100) {
                float d = Float.valueOf(oneDForm.format(distance));
                distanceS = d + "m";
            } else {
                float d = Float.valueOf(oneDForm.format(distance / 1000));
                distanceS = d + "km";
            }
        } else {
            distance = distance * 3.28f; // feets
            if (distance < 1000) {
                float d = Float.valueOf(oneDForm.format(distance));
                distanceS = d + "ft";
            } else {
                float d = Float.valueOf(oneDForm.format(distance / 5280));
                distanceS = d + "mi";
            }
        }
        return distanceS;
    }

    protected DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RootActivity.this);

        switch (id) {

        case DIALOG_MUST_BE_A_MEMBER:
            builder.setMessage("You must be a member to use this feature.")
                    .setCancelable(false)
                    .setPositiveButton("LOGIN",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    Activity a = alert.getOwnerActivity();
                                    if (a != null) {
                                        Intent i = new Intent();
                                        i.setClass(a, ActivityLoginPage.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        a.startActivity(i);
                                        a.finish();
                                    }
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });
            alert = builder.create();
            break;

        default:
            alert = null;
            break;
        }

        return alert;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public String getResStr(int id) {
        return getResources().getString(id);
    }
    
    public boolean startSmartActivity(Intent intent, String activityName) {
        if (activityName == "ActivityMap") {
            intent.putExtra("fragment", R.id.tab_fragment_area_map);
            intent.setClass(RootActivity.this, ActivityVenueFeeds.class);
            startActivity(intent);
            return true;
        } else if (activityName == "ActivityPeopleAndPlaces") {
            intent.putExtra("fragment", R.id.tab_fragment_area_places);
            intent.setClass(RootActivity.this, ActivityVenueFeeds.class);
            startActivity(intent);
            return true;
        } else if (activityName == "ActivityContacts") {
            intent.putExtra("fragment", R.id.tab_fragment_area_contacts);
            intent.setClass(RootActivity.this, ActivityVenueFeeds.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

}
