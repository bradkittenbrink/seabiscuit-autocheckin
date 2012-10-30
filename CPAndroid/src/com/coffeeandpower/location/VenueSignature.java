package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;

public class VenueSignature {
    public int venueId;
    public ArrayList<String> connectedWifiSSIDs;
    public ArrayList<MyScanResult> wifiSignature;

    public VenueSignature() {
        venueId = 0;
        connectedWifiSSIDs = new ArrayList<String>();
        wifiSignature = new ArrayList<MyScanResult>();
    }

    public VenueSignature(int venueId) {
        venueId = venueId;
        connectedWifiSSIDs = new ArrayList<String>();
        wifiSignature = new ArrayList<MyScanResult>();
    }

    public void addConnectedSSID(String SSID) {
        //Check to see if this is an SSID we already have
        //If not add it to the arrayList
        if (!connectedWifiSSIDs.contains(SSID)) {
            connectedWifiSSIDs.add(SSID);
        }
    }

    public void addWifiNetworkToSignature(MyScanResult network) {
        if (!wifiSignature.contains(network)) {
            wifiSignature.add(network);
        }
    }

    public void addWifiNetworkToSignature(ArrayList<MyScanResult> networks) {
        for (MyScanResult currMyScanResult: networks) {
            addWifiNetworkToSignature(currMyScanResult);
        }
    }

    public void addWifiNetworkToSignature(List<MyScanResult> networks) {
        for (MyScanResult currMyScanResult: networks) {
            addWifiNetworkToSignature(currMyScanResult);
        }
    }

    public void addWifiNetworkToSignature(ScanResult scanResult) {
        addWifiNetworkToSignature(new MyScanResult(scanResult));
    }

    //Do compare based on venueId
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VenueSignature) {
            return venueId == ((VenueSignature) obj).venueId;
        } else {
            return false;
        }
    }
}
