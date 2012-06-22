package com.coffeeandpower.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.coffeeandpower.location.WifiScanBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver{
	
	private static WifiManager wifiManager;
	private static WifiScanBroadcastReceiver scanReceiver;

	public WifiBroadcastReceiver(){
		
	}
	
	
        public void registerForConnectionState(Context currContext)
        {
        	IntentFilter intentFilter = new IntentFilter();
        	//WIFI_STATE_CHANGED_ACTION never triggers, and it isnt' clear why
        	//intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        	intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        	intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        	currContext.registerReceiver(this, intentFilter);
        }
	
    @Override
    public void onReceive(Context context, Intent intent) {
	final String action = intent.getAction();
	if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
	        if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
	          	    Log.d("WifiBroadcast","Wifi connect");
	            } else {
	              	    Log.d("WifiBroadcast","Wifi disconnected");
	            }
	}
	if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
          Log.d("WifiBroadcast","Network state change");
    	  NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
          if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
          {
      	    Log.d("WifiBroadcast","Wifi connected to:" + intent.getStringExtra(WifiManager.EXTRA_BSSID));
  	    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      	    String ssid = wifiInfo.getSSID();
  	    Log.d("WifiBroadcast","Wifi connected ssid:" + ssid);
  	    
  	    //TODO
  	    //Check connected Wifi and see if it is one we recognize
  	    
  	    //We are connected to wifi we recognize, verify wifi signature
  	    scanReceiver = new WifiScanBroadcastReceiver(context);
  	    scanReceiver.registerForWifiScans(context);

          }
        }
    }

    
}

