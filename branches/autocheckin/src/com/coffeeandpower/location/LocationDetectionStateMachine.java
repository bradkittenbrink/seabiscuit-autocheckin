package com.coffeeandpower.location;

import java.util.Observable;
import java.util.Observer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class LocationDetectionStateMachine {
	
	private static final long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	private static final int MAX_DISTANCE = 0;
	
	private static Context myContext;
	
	private static LocationManager locationManager;
	private static ActiveLocationListener activeLocationListener;
	
	private static PendingIntent pendingPassiveReceiverIntent;
	private static WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	
	// This function must be called before the state machine will work
	public static void init(Context context) {
		
		myContext = context;
		
		activeLocationListener = new ActiveLocationListener();
		locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
		
	}
	
	
	
	//=============================================================
	// PUBLIC METHODS
	//=============================================================	
	
	public static void start() {
		startListenersForCheckedInState();
		
	}
	
	public static void stop() {
		
		stopListenersForCheckedInState();
		
	}
	
	public static void checkedInListenerDidTrigger(boolean isHighConfidence) {
		
		if (isHighConfidence) {
			checkWifiSignature();
		}
		else {
			commandGPS();
		}
	}
	
	public static void activeLocationListenerTrigger() {
		
	}
	
	
	
	
	
	//=============================================================
	// CHECKED IN STATES
	//=============================================================
	
	private static void startListenersForCheckedInState() {
		
		startPassiveLocationListener();
		startWifiStateListener();
	}
	
	private static void stopListenersForCheckedInState() {
		
		stopPassiveLocationListener();
		stopWifiStateListener();
	}
	
	
	private static void startPassiveLocationListener() {
		// Create pending intent for passive location listener
		Intent receiverIntent = new Intent(myContext,PassiveLocationUpdateReceiver.class);
		pendingPassiveReceiverIntent = PendingIntent.getBroadcast(myContext,
				0,
				receiverIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Register the passive listener for updates
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 
				MAX_TIME, 
				MAX_DISTANCE, 
				pendingPassiveReceiverIntent);
	}
	
	private static void startWifiStateListener() {
		
		wifiStateBroadcastReceiver.registerForConnectionState(myContext); 
		
	}
	
	private static void stopPassiveLocationListener() {
		
		locationManager.removeUpdates(pendingPassiveReceiverIntent);
		
	}
	
	private static void stopWifiStateListener() {
		
		wifiStateBroadcastReceiver.unregisterForConnectionState(myContext);
		
	}
	
	private static void checkWifiSignature() {
		
	}
	
	private static void commandGPS() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				MAX_TIME, 
				MAX_DISTANCE, 
				activeLocationListener);
	}



	
	
	
	//=============================================================
	// CHECKED OUT STATES
	//=============================================================

}
