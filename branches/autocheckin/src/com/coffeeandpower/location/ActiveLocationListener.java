package com.coffeeandpower.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class ActiveLocationListener implements LocationListener{

	private boolean hasReceivedHighAssuranceLocation;
	
	public ActiveLocationListener() {
		hasReceivedHighAssuranceLocation = false;
	}
	
	// only want to send a single high assurance position
	// state machine will call init when re-registering the active listener
	public void init() {
		hasReceivedHighAssuranceLocation = false;
	}

	@Override
	public void onLocationChanged(Location location) {
		
        	if (!hasReceivedHighAssuranceLocation) {
                	// send location to LocationFence for fence check if assurance is high enough
        		if (LocationFence.isLocationHighAssurance(location)) {
        			hasReceivedHighAssuranceLocation = true;
        			LocationFence.isLocationWithinFence(location);
        	        }
        		// else just wait for the next location to be received
        	}
		
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
		
		
	}

}
