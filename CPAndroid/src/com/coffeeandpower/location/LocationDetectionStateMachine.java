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
    private static Handler mainThreadTaskHandler;
    
    private static LocationManager locationManager;
    //private static ActiveLocationListener activeLocationListener;
    
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
    public static class MyAutoCheckinObservable extends Observable {
        
    }
    public static MyAutoCheckinObservable myAutoCheckinObservable = new MyAutoCheckinObservable();
    
    // This function must be called before the state machine will work
    public static void init(Context context, Handler mainThreadHandler) {
        
        Log.d(TAG,"LocationDetectionStateMachine.init()");
        
        myContext = context;
        
        exe = new Executor(myContext);
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
        
        //This is used for the activeLocationListener, which is currently disabled
        mainThreadTaskHandler = mainThreadHandler;
        
        locationThreadTaskHandler = new Handler(Looper.myLooper()) {

            // handleMessage - on the main thread
            @Override
            public void handleMessage(Message msg) {
                
                super.handleMessage(msg);
                
                String messageType = msg.getData().getString("type");
                Log.d(TAG,"locationThreadTaskHandler.handleMessage: " + messageType);
                
                if (messageType.equalsIgnoreCase("start")) {
                    startCallback();
                }
                else if (messageType.equalsIgnoreCase("stop")) {
                    stopCallback();
                }
                else if (messageType.equalsIgnoreCase("startCollection")) {
                    VenueSmart venue = msg.getData().getParcelable("venue");
                    startCollectionCallback(venue);
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
                else if (messageType.equalsIgnoreCase("checkinCheckoutCOMPLETE")) {
                    checkinCheckoutCallback();
                }
                else
                {
                    Log.d(TAG, "Location TaskHandler message is unhandled!!!");
                }
                
                
            }
        };
        
        
        locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
        wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver();
        wifiScanBroadcastReceiver = new WifiScanBroadcastReceiver(myContext);
        
        start();
    }
    
    public static void manualCheckin(Context context, Handler mainThreadHandler, VenueSmart venue) {
        //If the statemachine isn't running we need to activate it
        if(!stateMachineActive)
        {
            init(context, mainThreadHandler);   
        }
        
        stateMachineActive = true;
        
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("type", "startCollection");
        bundle.putParcelable("venue", venue);
        message.setData(bundle);
        
        Log.d(TAG,"Sending message..." + bundle.getString("type"));
        locationThreadTaskHandler.sendMessage(message);

        AppCAP.enableAutoCheckinForVenue(venue.getVenueId());
        AppCAP.enableAutoCheckin(context);
        LocationDetectionService.addVenueToAutoCheckinList(venue);
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
        wifiScanBroadcastReceiver.checkVenueSignature(myContext, triggeringVenuesCACHE);
    }
    
    //private static void venueStateTransition(VenueSmart currentVenue)
    private static void transitionVenueCheckinINIT()
    {
        if(AppCAP.isUserCheckedIn())
        {
            //Checkout the user
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
        }
        else
        {
            //Checkin the user
            Log.d("AutoCheckin","Auto-checking in the user...");
            final int checkInTime = (int) (System.currentTimeMillis() / 1000);
            final int checkOutTime = checkInTime + 24 * 3600;
            
            exe.checkIn(currVenueCACHE, checkInTime, checkOutTime, "",false,true,myContext);
            
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
            
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", "start");
                message.setData(bundle);
                
                Log.d(TAG,"Sending message..." + bundle.getString("type"));
                locationThreadTaskHandler.sendMessage(message);
                
        } else {
            Log.d(TAG,"Warning: Tried to start state machine while already active...");
        }
    }
    
    private static void startCallback() {
        passiveListeningSTATE();
    }
    
    public static void stop() {
        
        Log.d(TAG,"Stopping...");
        
        /*
        if (stateMachineActive) {
            stateMachineActive = false;
            
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", "stop");
                message.setData(bundle);
                
                locationThreadTaskHandler.sendMessage(message);
        }*/
        //FIXME
        //Why don't we need to sendMessage here?
        stopCallback();
    }

    private static void stopCallback() {
        
        Log.d(TAG,"Stop callback...");
        stopPassiveListeners();
        stopActiveLocationListener();
        stopWifiScanListener();
    }

    private static void startCollectionCallback(VenueSmart venue)
    {
        //This is after the handler so it should be ok to call stop, we might want to send all 
        //stop calls through the locationThreadTaskHandler
        stopCallback();
        wifiScanBroadcastReceiver.grabVenueSignature(myContext, venue.getVenueId());
        startWifiScanListener();
        signatureCollectionSTATE();
    }
    
    public static void collectionCOMPLETE(venueWifiSignature signatureForCurrVenue){
        //We should be on the main thread which is where we should call AppCAP
        //I believe AppCAP isn't very thread safe already
        AppCAP.addAutoCheckinWifiSignature(signatureForCurrVenue);
        
        //Let the state machine run again once we have the WifiSignature
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("type", "start");
        message.setData(bundle);
        
        Log.d(TAG,"Sending message..." + bundle.getString("type"));
        locationThreadTaskHandler.sendMessage(message);
    }
    
    //Closer for startPassiveListeners(), commandGPSINIT()
    public static void positionListenersCOMPLETE(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues, String senderId) {
        Log.d(TAG,"positionListenersCOMPLETE");
        
        if (stateMachineActive) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", "positionListenersCOMPLETE");
                bundle.putBoolean("isHighConfidence", isHighConfidence);
                bundle.putParcelableArrayList("triggeringVenues", triggeringVenues);
                message.setData(bundle);
        
        
            locationThreadTaskHandler.sendMessage(message);
        }
        else
            Log.d(TAG,"WARNING: positionListenersCOMPLETE came back before state machine was initialized...");
    }

    private static void positionListenersCallback(boolean isHighConfidence, ArrayList<VenueSmart> triggeringVenues) {
        if (currentState == 0 || (currentState > 0 && currentState <= 1 && isHighConfidence)) {
            triggeringVenuesCACHE = triggeringVenues;
            //If we have a fence break respond
            if (triggeringVenues != null ) {
                if (triggeringVenues.size() == 0) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
            } else {
                if (AppCAP.isUserCheckedIn()) {
                    venueStateTransitionSTATE();
                } else {
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
        } else {
            Log.d(TAG,"Redundant late call to: positionListenersCallback");
        }
    }
    
    public static void checkWifiSignatureCOMPLETE(VenueSmart currVenue) {
        if(Constants.debugLocationToast) {
            AppCAP.showToast("checkWifiSignatureComplete");
        }
        Log.d(TAG,"checkWifiSignatureCOMPLETE");
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("type", "checkWifiSignatureCOMPLETE");
        bundle.putParcelable("currVenue", currVenue);
        message.setData(bundle);
        
        locationThreadTaskHandler.sendMessage(message);
    }

    private static void checkWifiSignatureCallback(VenueSmart currVenue)
    {
        if(Constants.debugLocationToast) {
            AppCAP.showToast("checkWifiSignatureCallback");
        }
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
    
    public static void checkinCheckoutCOMPLETE() {
        Log.d(TAG,"checkinCheckoutCOMPLETE");
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("type", "checkinCheckoutCOMPLETE");
        message.setData(bundle);
        
        if (locationThreadTaskHandler != null)
            locationThreadTaskHandler.sendMessage(message);
        else
            Log.d(TAG,"WARNING: checkinCheckoutCOMPLETE  before State Machine init was called...");
        
    }
    private static void checkinCheckoutCallback() {
        if(currentState > 0)
        {
            passiveListeningSTATE();
        }
        else
        {
            Log.d(TAG,"Overriding passiveListeningSTATE waiting for signature to be collected");
        }
    }
    
    
    public static void passiveListenerDidReceiveLocation() {
        Log.d(TAG,"passiveListenerDidReceiveLocation");
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("type", "passiveListenerDidReceiveLocation");
        message.setData(bundle);
        
        if (locationThreadTaskHandler != null)
            locationThreadTaskHandler.sendMessage(message);
        else
            Log.d(TAG,"WARNING: Passive Listener received location before State Machine init was called...");
        
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
        
        locationThreadTaskHandler.sendMessage(message);
    }
    
    private static void wifiScanListenerDidReceiveScanCallback() {
        stopWifiScanListener();
    }

    
    //=============================================================
    // Private Helper functions
    //=============================================================
    private static void startPassiveLocationListener() {
        
        //if (!passiveLocationReceiverActive) {
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
                        
                
                //passiveLocationReceiverActive = true;
        //}
        //else 
            //Log.d(TAG,"Warning: Tried to start passive location listener when it was already active.");
    }

    private static void startActiveLocationListener() {
        //Skip this state for now
        
        //if (!activeLocationListenerActive) {
            
                //activeLocationListenerActive = true;
                Log.d(TAG,"Active Location Listener requeset...");
                //Looper.prepare();
                
                // create message to send to main thread handler
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", "startActiveListener");
                message.setData(bundle);
                
                mainThreadTaskHandler.sendMessage(message);
                
                
        //}
        //else 
            //Log.d(TAG,"Warning: Tried to start active location listener when it was already active.");
            
    }
    
    private static void startWifiStateListener() {
        
        //if (!wifiStateBroadcastReceiverActive) {
        try {
                wifiStateBroadcastReceiver.registerForConnectionState(myContext);
        }
        catch(Exception e){
            Log.d(TAG,"Warning: Tried to register wifiStateBroadcastReceiver and failed:");
            Log.d(TAG,"Error:" + e.getMessage());
        }
                //wifiStateBroadcastReceiverActive = true;
        //}
        //else 
        //  Log.d(TAG,"Warning: Tried to start wifi state listener when it was already active.");
        
    }
    
    private static void startWifiScanListener() {
        
        //if (!wifiScanBroadcastReceiverActive) {
        try {
                myContext.registerReceiver(wifiScanBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        catch(Exception e){
            Log.d(TAG,"Warning: Tried to register wifiScanBroadcastReceiver and failed:");
            Log.d(TAG,"Error:" + e.getMessage());
        }   
                //wifiScanBroadcastReceiverActive = true;
        //}
        //else 
            //Log.d(TAG,"Warning: Tried to start wifi scan listener when it was already active.");
        
    }
    
    
    private static void stopPassiveListeners() {
        Log.d(TAG,"Calling Stop Passive State Listener...");
        stopPassiveLocationListener();
        
        Log.d(TAG,"Calling Stop Wifi State Listener...");
        stopWifiStateListener();
    }
    
    private static void stopPassiveLocationListener() {
        
        //if (passiveLocationReceiverActive) {
        try {
            locationManager.removeUpdates(pendingPassiveReceiverIntent);
            Log.d(TAG,"Pending Passive Receiver Intent removed.");
            //passiveLocationReceiverActive = false;
        }
        catch(Exception e){
            Log.d(TAG,"Warning: Tried to unregister pendingPassiveReceiverIntent that wasn't registered:");
            Log.d(TAG,"Error:" + e.getMessage());
        }
        //}
        //else 
        //  Log.d(TAG,"Warning: Tried to stop passive location listener when it wasn't active.");
        
    }
    
    private static void stopActiveLocationListener() {
        
        //if (activeLocationListenerActive) {
            
            // create message to send to main thread handler
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", "stopActiveListener");
                message.setData(bundle);
                
                mainThreadTaskHandler.sendMessage(message);
                
            
            //activeLocationListenerActive = false;
        ////}
        //else 
        //  Log.d(TAG,"Warning: Tried to stop active location listener when it wasn't active.");
        
    }
    
    private static void stopWifiStateListener() {
        
        Log.d(TAG,"Stopping Wifi State Listener...");
        
        //if (wifiStateBroadcastReceiverActive) {
            try{
                wifiStateBroadcastReceiver.unregisterForConnectionState(myContext);
            }
            catch(Exception e){
                Log.d(TAG,"Warning: Tried to unregister wifiStateBroadcastReceiver that wasn't registered:");
                Log.d(TAG,"Error:" + e.getMessage());
            }
            //wifiStateBroadcastReceiverActive = false;
        //}
        //else 
        //  Log.d(TAG,"Warning: Tried to stop wifiStateBroadcastReceiver when it wasn't active.");
        
    }
    
    private static void stopWifiScanListener() {
        
        //if (wifiScanBroadcastReceiverActive) {
        try {
                wifiScanBroadcastReceiver.unregisterForWifiScans(myContext);
                //wifiScanBroadcastReceiverActive = false;
        }
        catch(Exception e){
            Log.d(TAG,"Warning: Tried to unregister wifiScanBroadcastReceiver that wasn't registered:");
            Log.d(TAG,"Error:" + e.getMessage());
        }
        //}
        //else 
        //  Log.d(TAG,"Warning: Tried to stop wifiScanBroadcastReceiver when it wasn't active.");
        
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
