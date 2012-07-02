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
	
	private Context locationDetectionServiceContext;
	
	//private static LocationDetectionStateMachine sm = new LocationDetectionStateMachine(this);
	
	
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
		
		locationDetectionServiceContext = this;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				LocationDetectionStateMachine.init(locationDetectionServiceContext);
				
			}
		});
		thread.setDaemon(true);
		thread.start();		
	}
	
	@Override
	public void onDestroy() {
		
		Log.d(TAG,"onDestroy()");
		
		LocationDetectionStateMachine.stop();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG,"onStartCommand()");
		
		  
		
		
		
		
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
