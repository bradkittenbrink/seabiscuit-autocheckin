package com.coffeeandpower.location;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class LocationDetectionService extends Service {

	protected static String TAG = "LocationDetectionService";
	
	
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
		
		
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG,"onDestroy()");
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG,"onStartCommand()");
		
		
		return START_STICKY;
		
	}

}
