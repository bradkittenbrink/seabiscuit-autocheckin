package com.coffeeandpower.location;

import android.app.AlarmManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ActiveLocationListener implements LocationListener{

    private final String TAG = "ActiveLocationListener";
    private boolean hasReceivedHighAssuranceLocation;
    private Context myContext;
    
    public ActiveLocationListener(Context context) {
        hasReceivedHighAssuranceLocation = false;
        
        myContext = context;
    }
    
    // only want to send a single high assurance position
    // state machine will call init when re-registering the active listener
    public void init() {
        hasReceivedHighAssuranceLocation = false;
    }
    
    public void startListener() {
        Log.d(TAG,"Starting Active Listener...");
        LocationManager locationManager = (LocationManager)myContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            400,
            0,
            this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ActiveLocationListener","Received location");
        if (!hasReceivedHighAssuranceLocation) {
            // send location to LocationFence for fence check if assurance is high enough
            if (LocationFence.isLocationHighAssurance(location)) {
                hasReceivedHighAssuranceLocation = true;
                LocationFence.isLocationWithinFence(location);
            }
            // else just wait for the next location to be received
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
        
    }

}
