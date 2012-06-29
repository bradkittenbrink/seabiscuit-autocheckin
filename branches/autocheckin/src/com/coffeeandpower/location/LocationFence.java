package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.List;
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
	
	private static final float highAssuranceThreshold = 33;

	
	
	public static void init(Context context) {
		
		 myContext = context;
	}
		
	public static boolean isLocationHighAssurance(Location location) {
		
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
		return highAssurance;
	}
	
	public static void isLocationWithinFence(Location location)
	{
		isLocationHighAssurance(location);
		CacheMgrService.startObservingAPICall("venuesWithCheckins", instance.myVenuesObserver);	
		
	}
	
	private class MyVenuesObserver implements Observer {
        	@Override
        	public void update(Observable observable, Object data) {

        		CacheMgrService.stopObservingAPICall("venuesWithCheckins", this);        		
        		/*
        		 * verify that the data is really of type CounterData, and log the
        		 * details
        		 */
        		ArrayList<VenueSmart> venuesWithFenceBreaks =  new ArrayList<VenueSmart>();
        		if (data instanceof CachedDataContainer) {
        			CachedDataContainer counterdata = (CachedDataContainer) data;
        			DataHolder venuesWithCheckins = counterdata.getData();
        						
        			Object[] obj = (Object[]) venuesWithCheckins.getObject();
        			@SuppressWarnings("unchecked")
        			List<VenueSmart> arrayVenues = (List<VenueSmart>) obj[0];
        			ArrayList<VenueSmart> venuesWithAutoCheckins = new ArrayList<VenueSmart>();

        			int[] venueIdsWithAuto = AppCAP.getVenuesWithAutoCheckins();
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
        			venuesWithFenceBreaks =  this.checkFence(venuesWithAutoCheckins);
        			if(venuesWithFenceBreaks.size() > 0)
        			{
        				if (Constants.debugLog)
        					Log.d("LocationFence",String.valueOf(venuesWithFenceBreaks.size()) + " Fence breaks found");
        			}
        			else
        			{
        				if (Constants.debugLog)
        					Log.d("LocationFence","Fence detection completed, no breaks");
        			}
        		}
        		else
        		{
        			if (Constants.debugLog)
        			{
        				Log.d("LocationFence","Error: Received unexpected data type: " + data.getClass().toString());
        			}
        			venuesWithFenceBreaks = null;
        		}
			Log.d("LocationFence","LocationFence returning to state machine");
			LocationDetectionStateMachine.positionListenersCOMPLETE(highAssurance, venuesWithFenceBreaks);
        		
        	}
        	
        	
        	private ArrayList<VenueSmart> checkFence(ArrayList<VenueSmart> venuesWithAutoCheckins) {
        		
        		ArrayList<VenueSmart> venuesWithFenceBreaks = new ArrayList<VenueSmart>();
        		//FIXME
        		//Add a max signma at which you don't do anything
        		
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
        				Log.d("LocationFence","User is checked in");
                			if(distance > fenceCheckoutRadiusMeters)
                			{
                				Log.d("LocationFence","Fence break, user has left the venue");
                				venuesWithFenceBreaks.add(currVenue);
                			}
        			}
        			else
        			{
        				Log.d("LocationFence","User is checked out");
                			if(distance < fenceCheckinRadiusMeters)
                			{
                				Log.d("LocationFence","Fence break, has entered the venue");
                				venuesWithFenceBreaks.add(currVenue);
                			}
        			}
        		}
        		return venuesWithFenceBreaks;
        	}
        	
	}
	
	
}
