package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.VenueSmart;

import android.content.Context;
import android.location.Location;

public class LocationFence {

	private static Context myContext;
	
	private static LocationFence instance = new LocationFence();
	
	private MyVenuesObserver myVenuesObserver = new MyVenuesObserver();
	
	private static Location pendingLocation;
	private static int[] autoCheckinArray;
	
	public static void init(Context context) {
		
		 myContext = context;
	}
	
	
	
	public static boolean isLocationWithinFence(Location location) {
		
		pendingLocation = location;
		
		CacheMgrService.startObservingAPICall("venuesWithCheckins", instance.myVenuesObserver);
		
		return false;
		
	}
	
	private static void checkFence() {
		autoCheckinArray = AppCAP.getVenuesWithAutoCheckins();
		
		// iterate through list of venues with autocheckin set
		// check if we are within radius
		
		boolean inRadius = false;
		if (inRadius) {
			
		}
	}
	
	private class MyVenuesObserver implements Observer {
        	@Override
        	public void update(Observable arg0, Object arg1) {

        		// TODO: pull venues from Parcelable object
        		CacheMgrService.stopObservingAPICall("venuesWithCheckins", this);
        		checkFence();
        		
        	}
        	
	}
	
	
}
