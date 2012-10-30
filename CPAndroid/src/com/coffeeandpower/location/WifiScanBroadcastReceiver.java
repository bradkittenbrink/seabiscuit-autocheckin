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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

class WifiScanBroadcastReceiver extends BroadcastReceiver {
    private static final int POS_MATCH_THRESHOLD = 4;
    private WifiManager wifiManager;
    private boolean modeCollection = false;
    private boolean modeVerification = false;
    private ArrayList<VenueSmart> venuesBeingVerified;
    private VenueSignature venueForSignature;
    
    public WifiScanBroadcastReceiver(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        modeCollection = false;
        modeVerification = false;
    }   
    
    public void checkVenueSignature(Context context, ArrayList<VenueSmart> venues) {
        venuesBeingVerified = venues;
        wifiManager.startScan();
        modeVerification = true;
    }
    
    public void grabVenueSignature(Context context, int venueId) {
        Log.d("WifiScanBroadcast","grabVenueSignature");

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        venueForSignature = new VenueSignature(venueId);
        venueForSignature.addConnectedSSID(wifiInfo.getSSID());

        //Force the scan to get the ssid's as soon as possible
        wifiManager.startScan();
        modeCollection = true;
    }
    
    public void unregisterForWifiScans(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            //We have scan results so we can stop listening for additional scan results
            LocationDetectionStateMachine.wifiScanListenerDidReceiveScan();
            //Grab results from fresh scan of wifi networks
            List<ScanResult> visibleWifiNetworks = wifiManager.getScanResults();

            if (modeCollection) {
                List<MyScanResult> venueWifiSig = collectWifiSignature(8, visibleWifiNetworks);
                venueForSignature.addWifiNetworkToSignature(venueWifiSig);
                reportWifiSignature(venueForSignature);
            }

            if (modeVerification) {
                VenueSignature matchingVenue = signatureVerification(context, visibleWifiNetworks);
                reportMatch(matchingVenue);
            }
        }
    }
    
    private void reportMatch(VenueSignature matchingVenue) {
        VenueSmart outputVenue = null;
        if(matchingVenue != null) {
            for(VenueSmart currVenue : venuesBeingVerified) {
                if(matchingVenue.venueId == currVenue.getVenueId()) {
                    Log.d("WifiScanBroadcast","Wifi Match found:" + currVenue.getName());
                    outputVenue = currVenue;
                    break;
                }
            }

            if(outputVenue == null) {
                Log.d("WifiScanBroadcast","Wifi Signature match, but venue lookup failed!!");
            }
        } else {
            Log.d("WifiScanBroadcast","Wifi Signature did not match");
        }
        LocationDetectionStateMachine.checkWifiSignatureCOMPLETE(outputVenue);
    }
    
    private List<MyScanResult> collectWifiSignature(int maxBssidsSig, List<ScanResult> visibleWifiNetworks){
        Log.d("WifiScanBroadcast","Forming wifi signature");

        ArrayList<MyScanResult> wifiSignature = new ArrayList<MyScanResult>();
        
        Collections.sort(visibleWifiNetworks, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult m1, ScanResult m2) {
                if (m1.level > m2.level) {
                    return -1;
                }
                return 1;
            }
        });
        for (ScanResult currNet : visibleWifiNetworks) {
            //Need to find if wifiSignature 
            MyScanResult myCurrNet = new MyScanResult(currNet);
            if (!wifiSignature.contains(myCurrNet)) {
                wifiSignature.add(myCurrNet);
                if (wifiSignature.size() >= maxBssidsSig) {
                    Log.d("WifiScanBroadcast","MaxBssid reached. " + String.valueOf(maxBssidsSig) + " Bssid's in signature");
                    return wifiSignature;
                }
            }
        }       
        return wifiSignature;
    }

    private VenueSignature signatureVerification(Context context, List<ScanResult> visibleWifiNetworks) {
        ArrayList<VenueSignature> venuesBeingVerified = AppCAP.getAutoCheckinVenueSignatures();
        return checkForMatch(visibleWifiNetworks, venuesBeingVerified);
    }

    private VenueSignature checkForMatch(List<ScanResult> visibleWifiNetworks, ArrayList<VenueSignature> venuesBeingVerified) {
        //If there isn't many wifi results knock down the threshold
        double tmpThreshold = 0.40 * (double) visibleWifiNetworks.size();
        int tmpRoundedThreshold = (int) Math.floor(tmpThreshold);

        // clamp the threshold to between 2 and POS_MATCH_THRESHOLD
        int threshold = Math.max(2, Math.min(tmpRoundedThreshold, POS_MATCH_THRESHOLD));
        
        //This is the match threshold, once hit we know we have a match
        for (VenueSignature currVenueSig : venuesBeingVerified) {
            int matches = 0;
            for (ScanResult currNet : visibleWifiNetworks) {
                String netName = currNet.BSSID;
                for (MyScanResult savedNet : currVenueSig.wifiSignature) {
                    String savedName = savedNet.BSSID;
                    if (netName.equalsIgnoreCase(savedName)) {
                        matches++;
                        if(matches >= threshold)
                            return currVenueSig;
                        break;
                    }
                }
            }
        }
        return null;
    }

    private void reportWifiSignature(VenueSignature signatureForCurrVenue) {
        LocationDetectionStateMachine.collectionCOMPLETE(signatureForCurrVenue);
    }
}
