package com.coffeeandpower.location;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class LocationDetectionService extends Service {

	protected static String TAG = "LocationDetectionService";
	
	
	private static WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	
	//=====================================================
	// Service Lifecycle
	//=====================================================
	
	@Override
	public IBinder onBind(Intent arg0) {
		// This service is not intended to be bound so return null
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG,"onCreate()");
		
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
		
	}
	
	@Override
	public void onDestroy() {
		
		Log.d(TAG,"onDestroy()");
		
		wifiStateBroadcastReceiver.unregisterForConnectionState(this);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG,"onStartCommand()");
		
		wifiStateBroadcastReceiver.registerForConnectionState(this);   
		
		return START_STICKY;
		
	}
	
	
	
	
	
	
	
	
	
	public static void addVenueToAutoCheckinList(VenueSmart checkinVenue)
        {
        	// Create a prox alert if this is a new venue for this user
        	if (AppCAP.addVenueToAutoCheckinList(checkinVenue.getVenueId())) {
        		//createProxAlert(checkinVenue);
        	}
        }

}
