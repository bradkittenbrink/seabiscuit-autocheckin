package com.coffeeandpower.location;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

class PassiveLocationUpdateReceiver extends BroadcastReceiver {
    protected static String TAG = "PassiveLocationUpdateReceiver";

    /**
     * When a new location is received, extract it from the Intent
     * 
     * This is the Passive receiver, used to receive Location updates from 
     * third party apps when the Activity is not visible. 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_LOCATION_CHANGED;
        Location location = null;

        if (intent.hasExtra(key)) {
            //Once we get an update we need to stop listening so that we avoid concurrent changes

            LocationDetectionStateMachine.passiveListenerDidReceiveLocation();
            
            // This update came from Passive provider, so we can extract the location
            // directly.
            location = (Location)intent.getExtras().get(key);
            Log.d(TAG,"Received Updated Location: " + location.getLatitude() + ", " + location.getLongitude());
                
            LocationFence.isLocationWithinFence(location);
        }
        else {
            Log.d(TAG,"Location has not changed.");
        }
    }   
}
