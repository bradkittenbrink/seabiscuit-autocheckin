package com.coffeeandpower.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class ActiveLocationListener implements LocationListener{

	public ActiveLocationListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLocationChanged(Location location) {
		
        	
        	// send location to state machine
		LocationFence.isLocationWithinFence(location);
		
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
