package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.activity.ActivityCheckIn;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

// package private class, all external interactions should go through
// LocationDetectionService
public class LocationDetectionStateMachine {
    
    private static final String TAG = "LocationDetectionStateMachine";
    
    private static volatile int currentState = 0;
    
    private static final long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private static final int MAX_DISTANCE = 0;
    
    private static LocationManager locationManager;
    
    private static PendingIntent pendingPassiveReceiverIntent;
    private static WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
    private static WifiScanBroadcastReceiver wifiScanBroadcastReceiver;
    
    //Data caches for data passing between states
    private static ArrayList<VenueSmart> triggeringVenuesCACHE;
    private static VenueSmart currVenueCACHE;
    
    public static boolean stateMachineActive = false;
    
    private static LocationDetectionService myService;
    
    private static Executor exe;

    // TODO(brad) - redesign this class to make this private again
    public static Observable myAutoCheckinObservable = new Observable();
    
    // This function must be called before the state machine will work
    public static void init(LocationDetectionService service) {
        Log.d(TAG,"LocationDetectionStateMachine.init()");
        
        myService = service;
        
        exe = new Executor(myService);
        exe.setExecutorListener(new ExecutorInterface() {
            @Override
            public void onErrorReceived() {
                errorReceived();
            }

            @Override
            public void onActionFinished(int action) {
                actionFinished(action);             
            }
        });
        
        stateMachineActive = false;
        
        locationManager = (LocationManager) myService.getSystemService(Context.LOCATION_SERVICE);
        wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
        wifiScanBroadcastReceiver = new WifiScanBroadcastReceiver(myService);
        
        start();
    }
    
    public static void manualCheckin(final VenueSmart venue) {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                //This is after the handler so it should be ok to call stop, we might want to send all 
                //stop calls through the locationThreadTaskHandler
                stopCallback();
                wifiScanBroadcastReceiver.grabVenueSignature(myService, venue.getVenueId());
                startWifiScanListener();
                signatureCollectionSTATE();
                return null;
            }
        }.execute();
    }
    
    
    //=============================================================
    // STATES
    //=============================================================
    //All state transitions are dataless, all data flows through
    //member variables
    private static void signatureCollectionSTATE(){
        //AppCAP.showToast("signatureCollectionSTATE");
        Log.d(TAG,"signatureCollectionSTATE");
        currentState = -1;
        //This state is unique because the initial kickoff comes
        //comes from another thread with data so we get in this state
        //and then call asdfasdfaf in startCollectionCallback(venue)
    }
    
    private static void passiveListeningSTATE(){
        //AppCAP.showToast("passiveListeningSTATE");
        Log.d(TAG, "passiveListeningSTATE");
        currentState = 0;
        startPassiveListenersINIT();
    }

    private static void locationBasedVerificationSTATE(){
        //AppCAP.showToast("locationBasedVerificationSTATE");
        Log.d(TAG, "locationBasedVerificationSTATE");
        currentState = 1;
        commandGPSINIT();
    }

    private static void wifiBasedVerificationSTATE(){
        //AppCAP.showToast("wifiBasedVerificationSTATE");
        Log.d(TAG, "wifiBasedVerificationSTATE");
        currentState = 2;
        checkWifiSignatureINIT();
    }

    private static void venueStateTransitionSTATE(){
        //AppCAP.showToast("venueStateTransitionSTATE");
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
        //startPassiveLocationListener();
        //FIXME
        //Start with wifi only based checkins
        startWifiStateListener();
    }
    
    private static void commandGPSINIT() {
        startActiveLocationListener();
    }
    
    private static void checkWifiSignatureINIT() {
        startWifiScanListener();
        wifiScanBroadcastReceiver.checkVenueSignature(myService, triggeringVenuesCACHE);
    }
    
    //private static void venueStateTransition(VenueSmart currentVenue)
    private static void transitionVenueCheckinINIT() {
        if(AppCAP.isUserCheckedIn()) {
            //Checkout the user
            // TODO(brad) - replace this with exe.checkOut();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataHolder result = AppCAP.getConnection().checkOut();
                    
                    Log.d("AutoCheckin","Checking user out...");
                    CacheMgrService.checkOutTrigger();
                    
                    myAutoCheckinObservable.notifyObservers(null);
                    //userState.onCheckOut();
                    LocationDetectionStateMachine.checkinCheckoutCOMPLETE();
                }
            },"LocationDectionStateMachine.transitionVenueCheckinINIT").start();
        } else {
            //Checkin the user
            Log.d("AutoCheckin","Auto-checking in the user...");
            final int checkInTime = (int) (System.currentTimeMillis() / 1000);
            final int checkOutTime = checkInTime + 24 * 3600;
            
            exe.checkIn(currVenueCACHE, checkInTime, checkOutTime, "", false, true, myService);
            
            myAutoCheckinObservable.notifyObservers(null);
            //currVenueCACHE
        }
    }
    
    //=============================================================
    // PUBLIC State transition completers
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
            
            startCallback();
        } else {
            Log.d(TAG,"Warning: Tried to start state machine while already active...");
        }
    }
    
    private static void startCallback() {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                passiveListeningSTATE();
                return null;
            }
        }.execute();
    }
    
    public static void stop() {
        Log.d(TAG,"Stopping...");
        
        //FIXME
        //Why don't we need to doInBackground here?
        stopCallback();
    }

    private static void stopCallback() {
        Log.d(TAG,"Stop callback...");
        stopPassiveListeners();
        stopActiveLocationListener();
        stopWifiScanListener();
    }

    public static void collectionCOMPLETE(VenueSignature signatureForCurrVenue){
        //We should be on the main thread which is where we should call AppCAP
        //I believe AppCAP isn't very thread safe already
        AppCAP.addAutoCheckinVenueSignature(signatureForCurrVenue);
        
        //Let the state machine run again once we have the WifiSignature
        startCallback();
    }
    
    //Closer for startPassiveListeners(), commandGPSINIT()
    public static void positionListenersCOMPLETE(final boolean isHighConfidence, final ArrayList<VenueSmart> triggeringVenues, String senderId) {
        Log.d(TAG,"positionListenersCOMPLETE");
        
        if (stateMachineActive) {
            new AsyncTask<Void,Void,Void>() {
                @Override protected Void doInBackground(Void... args) {
                    positionListenersCallback(isHighConfidence, triggeringVenues);
                    return null;
                }
            }.execute();
        } else {
            Log.w(TAG,"positionListenersCOMPLETE came back before state machine was initialized...");
        }
    }

    private static void positionListenersCallback(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
        if (currentState == 0 || (currentState > 0 && currentState <= 1 && isHighConfidence)) {
            triggeringVenuesCACHE = triggeringVenues;
            //If we have a fence break respond
            if (triggeringVenues != null ) {
                if (triggeringVenues.size() == 0) {
                    waitFor(2000);
                    passiveListeningSTATE();            
                } else {
                    //PassiveListenersINIT returning
                    if (currentState == 0) {
                        //We are going to move this until we have verified the Wifi
                        //Since we get a false state change everytime we register for the 
                        //WifiState listener
                        //stopPassiveListeners();
                        if (isHighConfidence) {
                            wifiBasedVerificationSTATE();
                        } else {
                            //FIXME
                            //Skipping active GPS right now
                            wifiBasedVerificationSTATE();
                            //locationBasedVerificationSTATE();
                        }
                    } else {
                        //commandGPSINIT
                        if (isHighConfidence) {
                            wifiBasedVerificationSTATE();
                        } else {
                            //If we can't get a high assurance position
                            //Return to passive listening
                            waitFor(2000);
                            passiveListeningSTATE();
                        }           
                    }
                }
            } else {
                if (AppCAP.isUserCheckedIn()) {
                    venueStateTransitionSTATE();
                } else {
                    //No fence breaks return to passive listening
                    waitFor(2000);
                    passiveListeningSTATE();
                }
            }
        } else {
            Log.d(TAG,"Redundant late call to: positionListenersCallback");
        }
    }
    
    private static void waitFor(long time_ms) {
        try {
            Thread.sleep(time_ms);
        } catch (InterruptedException e) {
            // ignore interruptions
        }
    }

    public static void checkWifiSignatureCOMPLETE(final VenueSmart currVenue) {
        if(Constants.debugLocationToast) {
            AppCAP.showToast("checkWifiSignatureComplete");
        }
        Log.d(TAG,"checkWifiSignatureCOMPLETE");

        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                checkWifiSignatureCallback(currVenue);
                return null;
            }
        }.execute();
    }

    private static void checkWifiSignatureCallback(VenueSmart currVenue) {
        if(Constants.debugLocationToast) {
            AppCAP.showToast("checkWifiSignatureCallback");
        }

        if(AppCAP.isUserCheckedIn()) {
            //If we get a null the venue did not match
            //When we are checked in that means we have left the venue
            if(currVenue == null) {
                venueStateTransitionSTATE();
            } else {
                currVenueCACHE = currVenue;
                //If we did get a match we are still at the venue
                //Therefore we want to go back to our passive listeners
                //TODO we need to increase the trigger threshold here
                //to avoid it looping back too quickly
                //Currently the threshold is hardcoded in LocationFence
                passiveListeningSTATE();
            }
        } else {
            //If we get a match then we are at the venue
            //and we want to checkin
            if(currVenue != null) {
                currVenueCACHE = currVenue;
                venueStateTransitionSTATE();
            } else {
                //If we didn't get a match we aren't at the venue
                //so lets turn the passive listeners back on 
                passiveListeningSTATE();
            }
        }
    }
    
    public static void checkinCheckoutCOMPLETE() {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                if(currentState > 0) {
                    passiveListeningSTATE();
                } else {
                    Log.d(TAG, "Overriding passiveListeningSTATE waiting for signature to be collected");
                }
                return null;
            }
        }.execute();
    }

    public static void passiveListenerDidReceiveLocation() {
        Log.d(TAG,"passiveListenerDidReceiveLocation");

        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                stopPassiveLocationListener();
                return null;
            }
        }.execute();
    }

    public static void wifiScanListenerDidReceiveScan() {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... args) {
                wifiScanListenerDidReceiveScanCallback();
                return null;
            }
        }.execute();
    }
    
    private static void wifiScanListenerDidReceiveScanCallback() {
        stopWifiScanListener();
    }

    
    //=============================================================
    // Private Helper functions
    //=============================================================
    private static void startPassiveLocationListener() {
        Intent receiverIntent = new Intent(myService,PassiveLocationUpdateReceiver.class);
        pendingPassiveReceiverIntent = PendingIntent.getBroadcast(myService,
                0,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Register the passive listener for updates
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 
                MAX_TIME, 
                MAX_DISTANCE, 
                pendingPassiveReceiverIntent);
                
    }

    private static void startActiveLocationListener() {
        myService.runOnMainThread(new Runnable() {
            @Override public void run() {
                myService.startActiveListener();
            }
        });
    }
    
    private static void startWifiStateListener() {
        try {
            wifiStateBroadcastReceiver.registerForConnectionState(myService);
        } catch(Exception e) {
            Log.e(TAG, "Tried to register wifiStateBroadcastReceiver and failed:", e);
        }
        
    }
    
    private static void startWifiScanListener() {
        try {
            myService.registerReceiver(wifiScanBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        } catch(Exception e) {
            Log.e(TAG, "Tried to register wifiScanBroadcastReceiver and failed:", e);
        }   
    }
    
    
    private static void stopPassiveListeners() {
        Log.d(TAG,"Calling Stop Passive State Listener...");
        stopPassiveLocationListener();
        
        Log.d(TAG,"Calling Stop Wifi State Listener...");
        stopWifiStateListener();
    }
    
    private static void stopPassiveLocationListener() {
        try {
            locationManager.removeUpdates(pendingPassiveReceiverIntent);
            Log.d(TAG,"Pending Passive Receiver Intent removed.");
        } catch(IllegalArgumentException e){
            Log.e(TAG, "Tried to unregister pendingPassiveReceiverIntent that wasn't registered:", e);
        }
    }
    
    private static void stopActiveLocationListener() {
        myService.runOnMainThread(new Runnable() {
            @Override public void run() {
                myService.stopActiveListener();
            }
        });
    }
    
    private static void stopWifiStateListener() {
        Log.d(TAG,"Stopping Wifi State Listener...");
        
        try{
            wifiStateBroadcastReceiver.unregisterForConnectionState(myService);
        } catch(Exception e) {
            Log.e(TAG, "Tried to unregister wifiStateBroadcastReceiver that wasn't registered:", e);
        }
    }
    
    private static void stopWifiScanListener() {
        try {
            wifiScanBroadcastReceiver.unregisterForWifiScans(myService);
            //wifiScanBroadcastReceiverActive = false;
        } catch(Exception e) {
            Log.e(TAG, "Tried to unregister wifiScanBroadcastReceiver that wasn't registered:", e);
        }
    }


    //=============================================================
    // EXE METHODS
    //============================================================= 

    private static void errorReceived() {

    }

    private static void actionFinished(int action) {
        DataHolder result = exe.getResult();

        switch (action) {

        case Executor.HANDLE_CHECK_IN:
            Log.d(TAG,"Handling post-checkin triggers...");
            CacheMgrService.checkInTrigger(currVenueCACHE);
            AppCAP.setUserCheckedIn(true);

        }
    }

    
    //=============================================================
    // Non-Transitioning Public Methods
    //============================================================= 
    
    public static void startObservingAutoCheckinTrigger(Observer obs) {
        myAutoCheckinObservable.addObserver(obs);
        
    }
    
    public static void stopObservingAutoCheckinTrigger(Observer obs) {
        myAutoCheckinObservable.deleteObserver(obs);
        
    }
}
