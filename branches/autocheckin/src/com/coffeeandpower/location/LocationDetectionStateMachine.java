package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;

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
	private static WifiScanBroadcastReceiver wifiScanBroadcastReceiver;
	
	// This function must be called before the state machine will work
	public static void init(Context context) {
		
		myContext = context;
		
		activeLocationListener = new ActiveLocationListener();
		locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
	}
	
	//=============================================================
	// STATES
	//=============================================================
	
	private static void passiveListeningSTATE(){
		startPassiveListeners();
		
	}
	private static void locationBasedVerificationSTATE(){
		stopPassiveListeners();
		commandGPS(triggeringVenues);
	}
	private static void wifiBasedVerificationSTATE(){
		stopPassiveListeners();
		checkWifiSignature(triggeringVenues);
	}
	private static void venueStateTransitionSTATE(){
		transitionVenueCheckin();
	}
	
	
	
	//=============================================================
	// PUBLIC METHODS
	//=============================================================	
	
	public static void start() {
		startPassiveListeners();
		
	}
	
	public static void stop() {
		//We really need to stop all listeners here
		stopPassiveListeners();
		
	}
	
	public static void passiveListenerDidTrigger(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
		
		if (isHighConfidence) {
			wifiBasedVerificationSTATE();
		}
		else {
			locationBasedVerificationSTATE();
		}
	}
	
	public static void activeLocationListenerTrigger(ArrayList<VenueSmart> triggeringVenues) {
		wifiBasedVerificationSTATE();		
	}
	
	private static void wifiSignatureResults(VenueSmart currVenue)
	{
		if(AppCAP.isUserCheckedIn())
		{
			//If we get a null the venue did not match
			//When we are checked in that means we have left the venue
			if(currVenue == null)
			{
				venueStateTransitionSTATE();
			}
			else
			{
				//If we did get a match we are still at the venue
				//Therefore we want to go back to our passive listeners
				passiveListeningSTATE();
			}
		}
		else
		{
			//If we get a match then we are at the venue
			//and we want to checkin
			if(currVenue != null)
			{
				venueStateTransitionSTATE();
			}
			else
			{
				//If we didn't get a match we aren't at the venue
				//so lets turn the passive listeners back on 
				passiveListeningSTATE();
			}
		}
		
		
	}

	
	//=============================================================
	// Private actions
	//=============================================================
	
	
	private static void startPassiveListeners() {
		
		startPassiveLocationListener();
		startWifiStateListener();
	}
	
	private static void stopPassiveListeners() {
		
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
	
	private static void checkWifiSignature(ArrayList<VenueSmart> triggeringVenues) {
		wifiScanBroadcastReceiver.checkVenueSignature(myContext, triggeringVenues);

	}
	
	
	private static void commandGPS(ArrayList<VenueSmart> triggeringVenues) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				MAX_TIME, 
				MAX_DISTANCE, 
				activeLocationListener);
	}
	
	//private static void venueStateTransition(VenueSmart currentVenue)
	private static void transitionVenueCheckin()
	{
		if(AppCAP.isUserCheckedIn())
		{
			//Checkout the user
		}
		else
		{
			//Checkin the user
		}
	}

}
