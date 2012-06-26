package com.coffeeandpower.location;

import java.util.ArrayList;
import android.net.wifi.ScanResult;

public class venueWifiSignature {
	public int venueId;
	public ArrayList<String> connectedWifiSSIDs;
	public ArrayList<MyScanResult> wifiSignature;
	
	public venueWifiSignature(){
		venueId = 0;
		this.connectedWifiSSIDs = new ArrayList<String>();
		this.wifiSignature = new ArrayList<MyScanResult>();
	}

	public void addConnectedSSID(String SSID){
		//Check to see if this is an SSID we already have
		//If not add it to the arrayList
		if(this.connectedWifiSSIDs.contains(SSID)==false)
		{
			this.connectedWifiSSIDs.add(SSID);
		}
	}
	
	public void addWifiNetworkToSignature(MyScanResult network)
	{
		if(this.wifiSignature.contains(network)==false)
		{
			this.wifiSignature.add(network);
		}
	}
	public void addWifiNetworkToSignature(ScanResult scanResult)
	{
		this.addWifiNetworkToSignature(new MyScanResult(scanResult));
	}
}
