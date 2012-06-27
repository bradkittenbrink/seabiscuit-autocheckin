package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class LocationFence {

	private static Context myContext;
	
	private static LocationFence instance = new LocationFence();
	
	private MyVenuesObserver myVenuesObserver = new MyVenuesObserver();
	
	private static Location pendingLocation;
	private static int[] autoCheckinArray;
	private static boolean highAssurance = false;
	
	//Constants for threshold calculation
	private static final float initialCheckinFenceDist = 40;
	private static final float initialCheckoutFenceDist = 60;
	
	private static final float highAssuranceThreshold = 30;

	
	
	public static void init(Context context) {
		
		 myContext = context;
	}
		
	public static boolean isLocationWithinFence(Location location) {
		
		pendingLocation = location;
		if(pendingLocation.hasAccuracy())
		{
			if(pendingLocation.getAccuracy() < highAssuranceThreshold)
			{
				highAssurance = true;
			}
			else
			{
				highAssurance = false;
			}
		}
		else
			highAssurance = false;
		
		CacheMgrService.startObservingAPICall("venuesWithCheckins", instance.myVenuesObserver);
		
		return false;
		
	}
	
	private class MyVenuesObserver implements Observer {
        	@Override
        	public void update(Observable observable, Object data) {

        		CacheMgrService.stopObservingAPICall("venuesWithCheckins", this);        		
        		/*
        		 * verify that the data is really of type CounterData, and log the
        		 * details
        		 */
        		if (data instanceof CachedDataContainer) {
        			CachedDataContainer counterdata = (CachedDataContainer) data;
        			DataHolder venuesWithCheckins = counterdata.getData();
        						
        			Object[] obj = (Object[]) venuesWithCheckins.getObject();
        			@SuppressWarnings("unchecked")
        			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
        			
        			int[] venueIdsWithAuto = AppCAP.getVenuesWithAutoCheckins();
        			ArrayList<VenueSmart> venuesWithAutoCheckins = new ArrayList<VenueSmart>();
        			//FIXME
        			//This has the same cache miss issue we have elsewhere, if their autocheckin venues
        			//aren't all covered by the venue list from nearbyvenueswithcheckins
        			for(int venueId : venueIdsWithAuto)
        			{
                			for(VenueSmart currentVenue:arrayVenues)
                			{
                				if(currentVenue.getVenueId() == venueId)
                				{
                					venuesWithAutoCheckins.add(currentVenue);
                					//Lets make the search array smaller everytime we find
                					//a hit
                					arrayVenues.remove(currentVenue);
                					break;
                				}
                			}
        				
        				
        			}
        			//We have the venue positions now lets check for fence breaks
        			ArrayList<VenueSmart> venuesWithFenceBreaks =  this.checkFence(venuesWithAutoCheckins);
        			//FIXME
        			//This is a helper class and should not return straight back to LocationDetectionStateMachine
        			//It needs to go back to whatever location provider is doing the test
				LocationDetectionStateMachine.passiveListenersCOMPLETE(highAssurance, venuesWithAutoCheckins);
        		}
        		else
        			if (Constants.debugLog)
        				Log.d("LocationFence","Error: Received unexpected data type: " + data.getClass().toString());
        		
        		
        	}
        	
        	
        	private ArrayList<VenueSmart> checkFence(ArrayList<VenueSmart> venuesWithAutoCheckins) {
        		
        		ArrayList<VenueSmart> venuesWithFenceBreaks = new ArrayList<VenueSmart>();
        		//FIXME
        		//This number the below calculation needs some more thought put into it
        		//Add in the accuracy (assumed to be 2 sigma measurement)
        		float fenceCheckinRadiusMeters = initialCheckinFenceDist + pendingLocation.getAccuracy();
        		float fenceCheckoutRadiusMeters = initialCheckoutFenceDist + pendingLocation.getAccuracy();
        		// iterate through list of venues with autocheckin set
        		for(VenueSmart currVenue:venuesWithAutoCheckins)
        		{
        			Location tmpLocation = new Location("");
        			tmpLocation.setLatitude(currVenue.getLat());
        			tmpLocation.setLongitude(currVenue.getLng());
        			//Calculate the distance between the venue and the pendingLocation
        			float distance = pendingLocation.distanceTo(tmpLocation);
        			//Check that status the checkin
        			if(AppCAP.isUserCheckedIn())
        			{
                			if(distance < fenceCheckinRadiusMeters)
                			{
                				venuesWithFenceBreaks.add(currVenue);
                			}
        			}
        			else
        			{
                			if(distance > fenceCheckoutRadiusMeters)
                			{
                				venuesWithFenceBreaks.add(currVenue);
                			}
        			}
        		}
        		return venuesWithFenceBreaks;
        	}
        	
	}
	
	
}
