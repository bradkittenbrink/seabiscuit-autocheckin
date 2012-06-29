package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiScanBroadcastReceiver extends BroadcastReceiver{
	private static final int posMatchThreshold = 4;
	private WifiManager wifiManager;
	private boolean registeredForScans = false;
	private ArrayList<VenueSmart> venuesBeingVerified;
	
	public WifiScanBroadcastReceiver(Context context){
	  	wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}	
	
	public void checkVenueSignature(Context context, ArrayList<VenueSmart> venuesBeingVerified){
		    this.venuesBeingVerified = venuesBeingVerified;
	  	    //Forcing the scan requires a permission and I don't think we need it because the scan happens
	  	    //frequently enough
	  	    wifiManager.startScan();
		    if(registeredForScans)
		    {
			    //Particularly on disconnect we can get multiple calls
			    Log.d("WifiScanBroadcast","Already registered for scan broadcast");
		    }
		    else
		    {
			    Log.d("WifiScanBroadcast","Registering for scan broadcast");
			    context.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			    registeredForScans = true;
		    }
	}
	
	public void unregisterForWifiScans(Context context) {
		if(registeredForScans)
		{
			context.unregisterReceiver(this);
			registeredForScans = false;
		}
		else
		{
			Log.d("WifiScanBroadcast","Not registered for Wifi scans, skipping unregister...");
			
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action))
		{
			//We have scan results so we can stop listening for additional scan results
			this.unregisterForWifiScans(context);
			//Grab results from fresh scan of wifi networks
			List<ScanResult> visibleWifiNetworks = wifiManager.getScanResults();
			boolean modeCollection = false;
			if(modeCollection)
			{
				List<MyScanResult> venueWifiSig = this.collectWifiSignature(8, visibleWifiNetworks);
			}
			boolean modeVerification = true;
			if(modeVerification)
			{
				venueWifiSignature matchingVenue = signatureVerification(context, visibleWifiNetworks);
				this.reportMatch(matchingVenue);
			}
		}
	    }
	
	private void reportMatch(venueWifiSignature matchingVenue)
	{
		VenueSmart outputVenue = new VenueSmart();
		if(matchingVenue != null)
		{
        		boolean foundMatch = false;
        		for(VenueSmart currVenue: this.venuesBeingVerified)
        		{
        			if(matchingVenue.venueId == currVenue.getVenueId())
        			{
        				Log.d("WifiScanBroadcast","Wifi Match found:" + currVenue.getName());
        				outputVenue = currVenue;
        				foundMatch = true;
        				break;
        			}
        		}
        		if(foundMatch == false)
        		{
        			
        			Log.d("WifiScanBroadcast","Wifi Signature match, but venue lookup failed!!");
        			outputVenue = null;
        		}
		}
		else
		{
			Log.d("WifiScanBroadcast","Wifi Signature did not match");
			outputVenue = null;
		}
		LocationDetectionStateMachine.checkWifiSignatureCOMPLETE(outputVenue);

		
	}
	
	private List<MyScanResult> collectWifiSignature(int maxBssidsSig, List<ScanResult> visibleWifiNetworks){
        	Log.d("WifiScanBroadcast","Forming wifi signature");

		ArrayList<MyScanResult> wifiSignature = new ArrayList<MyScanResult>();
		//Object[] visibleWifiNetworksArray = visibleWifiNetworks.toArray();
		
		Collections.sort(visibleWifiNetworks, new Comparator<ScanResult>() {
			@Override
			public int compare(ScanResult m1, ScanResult m2) {
				if (m1.level > m2.level) {
					return -1;
				}
				return 1;
			}
		});
		for(ScanResult currNet : visibleWifiNetworks)
		{
                	//Need to find if wifiSignature 
                	MyScanResult myCurrNet = new MyScanResult(currNet);
                	boolean tmpBool = wifiSignature.contains(myCurrNet);
                	if(wifiSignature.contains(myCurrNet)==false)
                	{
                		wifiSignature.add(myCurrNet);
                		//DEBUG
                		//This is useful, but very verbose
                        	//Log.d("WifiScanBroadcast","SSID: " + myCurrNet.SSID + " PowerLevel " + myCurrNet.level);
                		if(wifiSignature.size()>=maxBssidsSig)
                		{
                                	Log.d("WifiScanBroadcast","MaxBssid reached. " + String.valueOf(maxBssidsSig) + " Bssid's in signature");
                			return wifiSignature;
                		}
                	}
		}		
		return wifiSignature;
		
		
	}
	private venueWifiSignature signatureVerification(Context context, List<ScanResult> visibleWifiNetworks){
		//FIXME
		//This is a fake test list for C&P and andrews
		ArrayList<venueWifiSignature> testVenuesBeingVerified = AppCAP.getAutoCheckinWifiSignatures();
		//TODO
		//Compare list of visible wifi networks, and see how that 
		//compares to our list of key networks at that venue
		venueWifiSignature matchingVenue = this.checkForMatch(visibleWifiNetworks, testVenuesBeingVerified);
		return matchingVenue;
	}
	    
    private venueWifiSignature checkForMatch(List<ScanResult> visibleWifiNetworks, ArrayList<venueWifiSignature> venuesBeingVerified) {
	    //This is the match threshold, once hit we know we have a match
    for(venueWifiSignature currVenueSig : venuesBeingVerified)
    {
    	int matches = 0;
        for(ScanResult currNet:visibleWifiNetworks)
        {
        	for(MyScanResult currVenueNet:currVenueSig.wifiSignature)
        	{
        		String testArg1 = currNet.BSSID;
        		String TestArg2 = currVenueNet.BSSID;
        		//The below was returning false positives, why is not clear
        		//if(currNet.BSSID.equalsIgnoreCase(currVenueNet.BSSID));
        		if(testArg1.equalsIgnoreCase(TestArg2))
        		{
        			matches++;
        			if(matches>=posMatchThreshold)
        				return currVenueSig;
        		}
        	}
        	//Log.d("WifiBroadcast","SSID: " + currNet.SSID + " BSSID: " + currNet.BSSID);
                }
	    }
	    return null;
    }
}
