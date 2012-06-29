package com.coffeeandpower.location;

import java.util.ArrayList;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LocationDetectionStateMachine {
	
	private static final String TAG = "LocationDetectionStateMachine";
	
	private static int currentState = 0;
	
	private static final long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	private static final int MAX_DISTANCE = 0;
	
	private static Context myContext;
	private static Handler locationThreadTaskHandler;
	
	private static LocationManager locationManager;
	private static ActiveLocationListener activeLocationListener;
	
	private static PendingIntent pendingPassiveReceiverIntent;
	private static WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	private static WifiScanBroadcastReceiver wifiScanBroadcastReceiver;
	
	//Data caches for data passing between states
	private static ArrayList<VenueSmart> triggeringVenuesCACHE;
	private static VenueSmart currVenueCACHE;
	
	private static boolean stateMachineActive = false;
	private static boolean passiveLocationReceiverActive = false;
	private static boolean activeLocationListenerActive = false;
	private static boolean wifiStateBroadcastReceiverActive = false;
	private static boolean wifiScanBroadcastReceiverActive = false;
	
	// This function must be called before the state machine will work
	public static void init(Context context) {
		
		Log.d(TAG,"LocationDetectionStateMachine.init()");
		
		myContext = context;
		
		Looper.prepare();
		
		//Looper.myLooper().prepare();
		
		locationThreadTaskHandler = new Handler(Looper.myLooper()) {

			// handleMessage - on the main thread
			@Override
			public void handleMessage(Message msg) {
				
				String messageType = msg.getData().getString("type");
				Log.d(TAG,"locationThreadTaskHandler.handleMessage: " + messageType);
				
				if (messageType.equalsIgnoreCase("start")) {
					startCallback();
				}
				else if (messageType.equalsIgnoreCase("stop")) {
					stopCallback();
				}
				else if (messageType.equalsIgnoreCase("positionListenersCOMPLETE")) {
					boolean isHighAssurance = msg.getData().getBoolean("isHighAssurance");
					ArrayList<VenueSmart> triggeringVenues = msg.getData().getParcelableArrayList("triggeringVenues");
					positionListenersCallback(isHighAssurance,triggeringVenues);
				}
				else if (messageType.equalsIgnoreCase("checkWifiSignatureCOMPLETE")) {
					VenueSmart currVenue = msg.getData().getParcelable("currVenue");
					checkWifiSignatureCallback(currVenue);
				}
				else if (messageType.equalsIgnoreCase("passiveListenerDidReceiveLocation")) {
					passiveListenerDidReceiveLocationCallback();
				}
				else if (messageType.equalsIgnoreCase("wifiScanListenerDidReceiveScan")) {
					wifiScanListenerDidReceiveScanCallback();
				}
				else
				{
					Log.d(TAG, "TaskHandler message is unhandled!!!");
				}
				
				super.handleMessage(msg);
			}
		};
		
		activeLocationListener = new ActiveLocationListener();
		locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
		wifiScanBroadcastReceiver = new WifiScanBroadcastReceiver(myContext);
		
		startCallback();
		
		
	}
	
	//=============================================================
	// STATES
	//=============================================================
	//All state transitions are dataless, all data flows through
	//member variables
	private static void passiveListeningSTATE(){
		Log.d(TAG, "passiveListeningSTATE");
		currentState = 0;
		startPassiveListenersINIT();
	}
	private static void locationBasedVerificationSTATE(){
		Log.d(TAG, "locationBasedVerificationSTATE");
		currentState = 1;
		commandGPSINIT();
	}
	private static void wifiBasedVerificationSTATE(){
		Log.d(TAG, "wifiBasedVerificationSTATE");
		currentState = 2;
		checkWifiSignatureINIT();
	}
	private static void venueStateTransitionSTATE(){
		Log.d(TAG, "venueStateTransitionSTATE");
		currentState = 3;
		transitionVenueCheckinINIT();
	}
	
	//=============================================================
	// INIT: Private State transition initiators
	//=============================================================
	//All calls should be to either private helper functions
	//or external classes and methods.  None should end in STATE()
	
	private static void startPassiveListenersINIT() {
		//DEBUG
		//Listening to only wifi to start
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
		startActiveLocationListener();
	}
	
	private static void checkWifiSignatureINIT() {
		startWifiScanListener();
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
	// These are in public/private pairs where the public method
	// can be called from any thread, and the corresponding
	// private method will be called on the "location" thread.
	//
	//All calls should end in STATE(), transitioning the state to
	//something new
	//=============================================================
	public static void start() {
		
		Log.d(TAG,"LocationDetectionStateMachine.start()");
		
		if (!stateMachineActive) {
			stateMachineActive = true;
			
        		Message message = new Message();
        		Bundle bundle = new Bundle();
        		bundle.putCharSequence("type", "start");
        		message.setData(bundle);
        		
        		Log.d(TAG,"Sending message...");
        		locationThreadTaskHandler.dispatchMessage(message);
        		
		} else {
			Log.d(TAG,"Warning: Tried to start state machine while already active...");
		}
		
		
	}
	private static void startCallback() {
		passiveListeningSTATE();
	}
	
	public static void stop() {
		
		Log.d(TAG,"Stopping...");
		if (stateMachineActive) {
			stateMachineActive = false;
			
        		Message message = new Message();
        		Bundle bundle = new Bundle();
        		bundle.putCharSequence("type", "stop");
        		message.setData(bundle);
        		
        		locationThreadTaskHandler.dispatchMessage(message);
		}
	}
	private static void stopCallback() {
		
		stopPassiveListeners();
		stopActiveLocationListener();
		stopWifiScanListener();
	}
	
	//Closer for startPassiveListeners(), commandGPSINIT()
	//FIXME
	//This needs a handler of some kind since it can be called from multiple listeners
	public static void positionListenersCOMPLETE(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
		Log.d(TAG,"positionListenersCOMPLETE");
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putCharSequence("type", "positionListenersCOMPLETE");
		bundle.putBoolean("isHighConfidence", isHighConfidence);
		bundle.putParcelableArrayList("triggeringVenues", triggeringVenues);
		message.setData(bundle);
		
		locationThreadTaskHandler.dispatchMessage(message);
	}
	private static void positionListenersCallback(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
		if(currentState == 0 || (currentState <= 1 && isHighConfidence))
		{
        		triggeringVenuesCACHE = triggeringVenues;
        		//If we have a fence break respond
        		if(triggeringVenues != null )
        		{
        			if(triggeringVenues.size() == 0)
        			{
                			try {
        					Thread.sleep(2000);
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
                			passiveListeningSTATE();			}
        			else
        			{
                        		//PassiveListenersINIT returning
                        		if(currentState == 0)
                        		{
                        			stopPassiveListeners();
                                		if (isHighConfidence) {
                                			wifiBasedVerificationSTATE();
                                		}
                                		else {
                                			//FIXME
                                			//Skipping active GPS right now
                                			wifiBasedVerificationSTATE();
                                			//locationBasedVerificationSTATE();
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
                                			try {
        							Thread.sleep(2000);
        						} catch (InterruptedException e) {
        							// TODO Auto-generated catch block
        							e.printStackTrace();
        						}
                                			passiveListeningSTATE();
                                		}			
                        		}
        			}
        		}
        		else
        		{
        			//No fence breaks return to passive listening
        			try {
        				Thread.sleep(2000);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        			passiveListeningSTATE();		
        		}
		}
		else{
			Log.d(TAG,"Redundant late call to: positionListenersCallback");

		}
	}
	
	public static void checkWifiSignatureCOMPLETE(VenueSmart currVenue) {
		Log.d(TAG,"checkWifiSignatureCOMPLETE");
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putCharSequence("type", "checkWifiSignatureCOMPLETE");
		bundle.putParcelable("currVenue", currVenue);
		message.setData(bundle);
		
		locationThreadTaskHandler.dispatchMessage(message);
	}
	private static void checkWifiSignatureCallback(VenueSmart currVenue)
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
	
	
	public static void passiveListenerDidReceiveLocation() {
		Log.d(TAG,"passiveListenerDidReceiveLocation");
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putCharSequence("type", "passiveListenerDidReceiveLocation");
		message.setData(bundle);
		
		locationThreadTaskHandler.dispatchMessage(message);
	}
	public static void passiveListenerDidReceiveLocationCallback() {
		stopPassiveLocationListener();
	}
	
	public static void wifiScanListenerDidReceiveScan() {
		Log.d(TAG,"wifiScanListenerDidReceiveScan");
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putCharSequence("type", "wifiScanListenerDidReceiveScan");
		message.setData(bundle);
		
		locationThreadTaskHandler.dispatchMessage(message);
	}
	
	private static void wifiScanListenerDidReceiveScanCallback() {
		stopWifiScanListener();
	}
	
	
	
	//=============================================================
	// Private Helper functions
	//=============================================================
	
	
	
	private static void startPassiveLocationListener() {
		
		if (!passiveLocationReceiverActive) {
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
        		
        		passiveLocationReceiverActive = true;
		}
		else 
			Log.d(TAG,"Warning: Tried to start passive location listener when it was already active.");
	}
	private static void startActiveLocationListener() {
		//Skip this state for now
		/*
		if (!activeLocationListenerActive) {
			
        		activeLocationListenerActive = true;
		}
		else 
			Log.d(TAG,"Warning: Tried to start active location listener when it was already active.");
			*/
	}
	
	private static void startWifiStateListener() {
		
		if (!wifiStateBroadcastReceiverActive) {
        		wifiStateBroadcastReceiver.registerForConnectionState(myContext);
        		wifiStateBroadcastReceiverActive = true;
		}
		else 
			Log.d(TAG,"Warning: Tried to start wifi state listener when it was already active.");
		
	}
	
	private static void startWifiScanListener() {
		
		if (!wifiScanBroadcastReceiverActive) {
        		myContext.registerReceiver(wifiScanBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        		wifiScanBroadcastReceiverActive = true;
		}
		else 
			Log.d(TAG,"Warning: Tried to start wifi scan listener when it was already active.");
		
	}
	
	
	private static void stopPassiveListeners() {
		//DEBUG
		stopPassiveLocationListener();
		stopWifiStateListener();
	}
	
	private static void stopPassiveLocationListener() {
		
		if (passiveLocationReceiverActive) {
			locationManager.removeUpdates(pendingPassiveReceiverIntent);
			passiveLocationReceiverActive = false;
		}
		else 
			Log.d(TAG,"Warning: Tried to stop passive location listener when it wasn't active.");
		
	}
	
	private static void stopActiveLocationListener() {
		
		if (activeLocationListenerActive) {
			locationManager.removeUpdates(activeLocationListener);
			activeLocationListenerActive = false;
		}
		else 
			Log.d(TAG,"Warning: Tried to stop active location listener when it wasn't active.");
		
	}
	
	private static void stopWifiStateListener() {
		
		if (wifiStateBroadcastReceiverActive) {
			try{
        		wifiStateBroadcastReceiver.unregisterForConnectionState(myContext);
			}
			catch(Exception e){
				Log.d(TAG,"Error: Tried to stop wifiStateBroadcastReceiver when it wasn't active.  Boolean protection failed!");
				Log.d(TAG,"Error:" + e.getMessage());
			}
			wifiStateBroadcastReceiverActive = false;
		}
		else 
			Log.d(TAG,"Warning: Tried to stop wifiStateBroadcastReceiver when it wasn't active.");
		
	}
	
	private static void stopWifiScanListener() {
		
		if (wifiScanBroadcastReceiverActive) {
        		wifiScanBroadcastReceiver.unregisterForWifiScans(myContext);
        		wifiScanBroadcastReceiverActive = false;
		}
		else 
			Log.d(TAG,"Warning: Tried to stop wifiScanBroadcastReceiver when it wasn't active.");
		
	}
	
	
	
	//=============================================================
	// PUBLIC METHODS
	//=============================================================	
	
	

	


}
