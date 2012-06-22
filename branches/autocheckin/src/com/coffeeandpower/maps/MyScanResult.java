package com.coffeeandpower.maps;

import android.net.wifi.ScanResult;

public class MyScanResult {
	public String BSSID;
	public String SSID;
	public int frequency;
	public int level;
	
	public MyScanResult(ScanResult scanResult){
		this.BSSID = scanResult.BSSID;
		this.SSID = scanResult.SSID;
		this.frequency = scanResult.frequency;
		this.level = scanResult.level;
	}
	
	//FIXME this is a temporary for tests.
	public MyScanResult(String BSSID){
		this.BSSID = BSSID;
	}
	
	
	//TODO Will need to make this parceable I suspect


}