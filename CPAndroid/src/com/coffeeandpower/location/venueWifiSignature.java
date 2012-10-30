package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;

public class venueWifiSignature {
    public int venueId;
    public ArrayList<String> connectedWifiSSIDs;
    public ArrayList<MyScanResult> wifiSignature;

    public venueWifiSignature() {
        this.venueId = 0;
        this.connectedWifiSSIDs = new ArrayList<String>();
        this.wifiSignature = new ArrayList<MyScanResult>();
    }

    public venueWifiSignature(int venueId) {
        this.venueId = venueId;
        this.connectedWifiSSIDs = new ArrayList<String>();
        this.wifiSignature = new ArrayList<MyScanResult>();
    }

    public void addConnectedSSID(String SSID) {
        //Check to see if this is an SSID we already have
        //If not add it to the arrayList
        if (this.connectedWifiSSIDs.contains(SSID)==false) {
            this.connectedWifiSSIDs.add(SSID);
        }
    }

    public void addWifiNetworkToSignature(MyScanResult network) {
        if (this.wifiSignature.contains(network)==false) {
            this.wifiSignature.add(network);
        }
    }

    public void addWifiNetworkToSignature(ArrayList<MyScanResult> networks) {
        for (MyScanResult currMyScanResult: networks) {
            this.addWifiNetworkToSignature(currMyScanResult);
        }
    }

    public void addWifiNetworkToSignature(List<MyScanResult> networks) {
        for (MyScanResult currMyScanResult: networks) {
            this.addWifiNetworkToSignature(currMyScanResult);
        }
    }

    public void addWifiNetworkToSignature(ScanResult scanResult) {
        this.addWifiNetworkToSignature(new MyScanResult(scanResult));
    }

    //Do compare based on venueId
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof venueWifiSignature) {
            return venueId == ((venueWifiSignature) obj).venueId;
        } else {
            return false;
        }
    }
}
