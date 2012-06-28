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
	
	private static int currentState = 0;
	
	private static final long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	private static final int MAX_DISTANCE = 0;
	
	private static Context myContext;
	
	private static LocationManager locationManager;
	private static ActiveLocationListener activeLocationListener;
	
	private static PendingIntent pendingPassiveReceiverIntent;
	private static WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	private static WifiScanBroadcastReceiver wifiScanBroadcastReceiver;
	
	//Data caches for data passing between states
	private static ArrayList<VenueSmart> triggeringVenuesCACHE;
	private static VenueSmart currVenueCACHE;
	
	
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
	//All state transitions are dataless, all data flows through
	//member variables
	private static void passiveListeningSTATE(){
		currentState = 0;
		startPassiveListenersINIT();
	}
	private static void locationBasedVerificationSTATE(){
		currentState = 1;
		commandGPSINIT();
	}
	private static void wifiBasedVerificationSTATE(){
		currentState = 2;
		checkWifiSignatureINIT();
	}
	private static void venueStateTransitionSTATE(){
		currentState = 3;
		transitionVenueCheckinINIT();
	}
	
	//=============================================================
	// INIT: Private State transition initiators
	//=============================================================
	//All calls should be to either private helper functions
	//or external classes and methods.  None should end in STATE()
	
	private static void startPassiveListenersINIT() {
		startPassiveLocationListener();
		startWifiStateListener();
	}
	
	private static void commandGPSINIT() {
		//FIXME
		//This needs to get fed in here
		//triggeringVenuesCACHE
		//FIXME
		//This belongs in a helper function
		activeLocationListener.init();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				MAX_TIME, 
				MAX_DISTANCE, 
				activeLocationListener);
	}
	
	private static void checkWifiSignatureINIT() {
		wifiScanBroadcastReceiver.checkVenueSignature(myContext, triggeringVenuesCACHE);
	}
	
	//private static void venueStateTransition(VenueSmart currentVenue)
	private static void transitionVenueCheckinINIT()
	{
		if(AppCAP.isUserCheckedIn())
		{
			//Checkout the user
			//currVenueCACHE

		}
		else
		{
			//Checkin the user
			//currVenueCACHE
		}
	}
	
	//=============================================================
	// COMPLETE: PUBLIC State transition completers
	//=============================================================
	//All calls should end in STATE(), transitioning the state to
	//something new
	public static void start() {
		passiveListeningSTATE();	
	}
	//Closer for startPassiveListeners(), commandGPSINIT()
	public static void positionListenersCOMPLETE(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
		triggeringVenuesCACHE = triggeringVenues;
		//If we have a fence break respond
		if(triggeringVenues != null)
		{
        		//PassiveListenersINIT returning
        		if(currentState == 0)
        		{
        			stopPassiveListeners();
                		if (isHighConfidence) {
                			wifiBasedVerificationSTATE();
                		}
                		else {
                			locationBasedVerificationSTATE();
                		}
        		}
        		else{
        			//commandGPSINIT
                		if (isHighConfidence) {
                			wifiBasedVerificationSTATE();
                		}
                		else {
                			//If we can't get a high assurance position
                			//Return to passive listening
                			passiveListeningSTATE();
                		}			
        		}
		}
		else
		{
			//No fence breaks return to passive listening
			passiveListeningSTATE();
		}
	}
	
	public static void checkWifiSignatureCOMPLETE(VenueSmart currVenue)
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
				currVenueCACHE = currVenue;
				//If we did get a match we are still at the venue
				//Therefore we want to go back to our passive listeners
				//TODO we need to increase the trigger threshold here
				//to avoid it looping back too quickly
				//Currently the threshold is hardcoded in LocationFence
				passiveListeningSTATE();
			}
		}
		else
		{
			//If we get a match then we are at the venue
			//and we want to checkin
			if(currVenue != null)
			{
				currVenueCACHE = currVenue;
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
	// Private Helper functions
	//=============================================================
	
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
	
	//=============================================================
	// PUBLIC METHODS
	//=============================================================	
	
	public static void stop() {
		//FIXME
		//We really need to stop all listeners here
		stopPassiveListeners();
		
	}

	


}
