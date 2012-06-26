package com.coffeeandpower.location;

import java.util.ArrayList;

import com.coffeeandpower.AppCAP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateBroadcastReceiver extends BroadcastReceiver{
	
	private static WifiManager wifiManager;
	private static WifiScanBroadcastReceiver scanReceiver;
	
	private static IntentFilter intentFilter = new IntentFilter();

	public WifiStateBroadcastReceiver(){
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        	intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
	}
	
	//Let external entities grab the current ssid
	public String returnCurrentSSID(Context context)
	{
		return this.grabCurrentSSID(context);
	}
	
	//Internal helper function to grab ssid string
	private String grabCurrentSSID(Context context)
	{
	  	    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	      	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	      	    String ssid = wifiInfo.getSSID();
	      	    return ssid;
	}
	
	
        public void registerForConnectionState(Context context)
        {
        	//WIFI_STATE_CHANGED_ACTION never triggers, and it isnt' clear why
        	//intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        	context.registerReceiver(this, intentFilter);
      	    	scanReceiver = new WifiScanBroadcastReceiver(context);        	
        }
        
        public void unregisterForConnectionState(Context currContext) {
        	currContext.unregisterReceiver(this);
        	
        	// Also send message to scan receiver to unregister
        	scanReceiver.unregisterForWifiScans(currContext);
        }
	
    @Override
    public void onReceive(Context context, Intent intent) {
	final String action = intent.getAction();
	Log.d("WifiBroadcast","Received Broadcast with action:" + action);
	if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {

	        if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
	          	    Log.d("WifiBroadcast","Wifi connect");
	            } else {
	              	    Log.d("WifiBroadcast","Wifi disconnected");
	            }
	}
	/*
	if(action.equals(android.net.wifi.NETWORK_STATE_CHANGED_ACTION))
	{
		Log.d("WifiBroadcast","Wifi State Changed:" + String.valueOf(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)));

	}
	*/
	if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
          Log.d("WifiBroadcast","Network state change");
    	  NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
          if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
          {
      	    Log.d("WifiBroadcast","Wifi connected to:" + intent.getStringExtra(WifiManager.EXTRA_BSSID));
      	    String ssid = this.grabCurrentSSID(context);
  	    Log.d("WifiBroadcast","Wifi connected ssid:" + ssid);
  	    
  	    //Check connected Wifi and see if it is one we recognize
  	    boolean knownSSID = false;
  	    ArrayList<String> knownSSIDs = new ArrayList<String>();
  	    //FIXME
  	    //Test SSIDs
  	    //This needs to come from AppCAP or some other global store
  	    knownSSIDs.add("veronica");
  	    knownSSIDs.add("coffeeandpower");
  	    for(String testWifiSSID:knownSSIDs)
  	    {
          	    if(ssid.equalsIgnoreCase(testWifiSSID))
          	    {
          		    Log.d("WifiBroadcast","Connected to" + testWifiSSID +", double check wifiSignature"); 
          		  knownSSID = true;
          		    break;
          	    }
  	    }
  	    if(knownSSID == false)
  	    {
  		    Log.d("WifiBroadcast","Wifi SSID is unrecognized"); 
  	    }
  	    if(knownSSID)
  	    {
  		    //We don't have the venues here, and probably never will
  		    LocationDetectionStateMachine.checkedInListenerDidTrigger(true, triggeringVenues);
  	    }


          }
          else if(networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED))
          {
        	  if(AppCAP.isUserCheckedIn())
        	  {
		    Log.d("WifiBroadcast","Wifi Disconnected, verifying that wifi signature no longer matches");
		    
  		    LocationDetectionStateMachine.checkedInListenerDidTrigger(true, triggeringVenues);
        	  }

          }
          else if(networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
          {
		    Log.d("WifiBroadcast","Wifi Disconnecting");
          }

        }
    }

    
}

