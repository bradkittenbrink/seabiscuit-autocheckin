package com.coffeeandpower.datatiming;

import java.util.ArrayList;
import java.util.Observer;

import android.os.Handler;
import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.google.android.maps.GeoPoint;

public class Counter {
	
	private Integer tick = 0;
	
	private final double defaultLat = 37.7717121657157;
	private final double defaultLon = -122.4239288438208;
	
	private CachedNetworkData venuesWithCheckinsCache = new CachedNetworkData("venuesWithCheckins");
	private CachedNetworkData nearbyVenuesCache = new CachedNetworkData("nearbyVenues");
	private CachedNetworkData contactsListCache = new CachedNetworkData("contactsList");
	
	private boolean isRunning = false;
	
	private boolean allowCachedDataThisRun = false;
	private boolean refreshAllDataThisRun = false;
	private boolean skipAPICallsThisRun = false;
	
	private double latForAPI;
	private double lonForAPI;
	private double[] llArray = new double[2];
	
	private int numberOfCalls = 0;
	
	private boolean apisCalledThisUpdate = false;
	private boolean cachedDataSentThisUpdate = false;
	
	// Scheduler
	protected Handler taskHandler = new Handler();
	
	public Counter(Integer tick, Integer trigger) {
		super();
		this.tick = tick;
	}

	public void getCachedDataForAPICall(String apicall, Observer context) {
		
		if (apicall.equals("venuesWithCheckins")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling venuesWithCheckins API for " + context.toString());
			venuesWithCheckinsCache.activate();
			venuesWithCheckinsCache.addObserver(context);
		}
		else if (apicall.equals("nearbyVenues")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling nearbyVenues API for " + context.toString());
			nearbyVenuesCache.activate();
			nearbyVenuesCache.addObserver(context);
		}
		else if (apicall.equals("contactsList")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling contactsList API for " + context.toString());
			contactsListCache.activate();
			contactsListCache.addObserver(context);
		}
		else
		{
			if (Constants.debugLog)
				Log.d("Counter","INVALID OPTION FOR OBSERVER REGISTRATION");
		}
		
		//The user is moving around the activities lets keep the data fresh
		this.numberOfCalls = 0;
		//Restart the timer
		allowCachedDataThisRun = true;
		if (Constants.debugLog)
			Log.d("Counter","getCachedDataForAPICall is calling stop/start");
		stop();
		start();
		
	}
	
	public void getCachedDataForAPICalls(String apicall1, String apicall2, Observer context) {
		
		if (apicall1.equals("venuesWithCheckins") || apicall2.equals("venuesWithCheckins")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling venuesWithCheckins API for " + context.toString());
			venuesWithCheckinsCache.activate();
			venuesWithCheckinsCache.addObserver(context);
		}
		if (apicall1.equals("nearbyVenues") || apicall2.equals("nearbyVenues")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling nearbyVenues API for " + context.toString());
			nearbyVenuesCache.activate();
			nearbyVenuesCache.addObserver(context);
		}
		if (apicall1.equals("contactsList") || apicall2.equals("contactsList")) {
			if (Constants.debugLog)
				Log.d("Counter","Enabling contactsList API for " + context.toString());
			contactsListCache.activate();
			nearbyVenuesCache.addObserver(context);
		}
		
		//The user is moving around the activities lets keep the data fresh
		this.numberOfCalls = 0;
		//Restart the timer
		allowCachedDataThisRun = true;
		if (Constants.debugLog)
			Log.d("Counter","getCachedDataForAPICall is calling stop/start");
		stop();
		start();
		
	}
	
	public void stoppedObservingAPICall(String apicall, Observer context) {
		if (apicall.equals("venuesWithCheckins")) {
			if (Constants.debugLog)
				Log.d("Counter","Removing venuesWithCheckins observer for " + context.toString() + ".");
			venuesWithCheckinsCache.deleteObserver(context);
			if (venuesWithCheckinsCache.countObservers() == 0) {
				venuesWithCheckinsCache.deactivate();
				if (Constants.debugLog)
					Log.d("Counter","Removed last observer from venuesWithCheckins, deactivating.");
			}
			
		}
		else if (apicall.equals("nearbyVenues")) {
			if (Constants.debugLog)
				Log.d("Counter","Removing nearbyVenues observer for " + context.toString() + ".");
			nearbyVenuesCache.deleteObserver(context);
			if (nearbyVenuesCache.countObservers() == 0) {
				nearbyVenuesCache.deactivate();
				if (Constants.debugLog)
					Log.d("Counter","Removed last observer from nearbyVenues, deactivating.");
			}
		}
		else if (apicall.equals("contactsList")) {
			if (Constants.debugLog)
				Log.d("Counter","Removing contactsList observer for " + context.toString() + ".");
			contactsListCache.deleteObserver(context);
			if (contactsListCache.countObservers() == 0) {
				contactsListCache.deactivate();
				if (Constants.debugLog)
					Log.d("Counter","Removed last observer from contactsList, deactivating.");
			}
		}
	}
	
	

	public void stop() {
		
		if (isRunning == true) {
			if (Constants.debugLog)
				Log.d("Counter","Counter.stop()");
			this.isRunning = false;
			taskHandler.removeCallbacks(runTimer);
		}
		else
		{
			if (Constants.debugLog)
				Log.d("Counter","Warning: Tried to stop Counter but it was not started...");
		}
	}

	public void start() {
		
		if (isRunning == false) {
			if (Constants.debugLog)
				Log.d("Counter","Counter.start()");
			this.isRunning = true;
			taskHandler.removeCallbacks(runTimer);
			taskHandler.post(runTimer);
		}
		else
		{
			if (Constants.debugLog)
				Log.d("Counter","Warning: Tried to start Counter when it was already running...");
		}
		
	}
	
	public void manualTrigger() {
		if (Constants.debugLog)
			Log.d("Counter","manualTrigger()");
		stop();
		start();
	}
	
	public void refreshAllData() {
		if (Constants.debugLog)
			Log.d("Counter","refreshAllData()");
		refreshAllDataThisRun = true;
		stop();
		start();
	}

	private Runnable runTimer = new Runnable()
        {
            @Override
            public void run()
            {
        	    // We are now on the main thread, so kick off the API call in a worker thread
        	    new Thread(new Runnable() {
        		    public void run() {
        			    
        			    //isFirstRun = true;
        			    
        			    if (AppCAP.getUserLatLon()[0] == 0 && AppCAP.getUserLatLon()[1] == 0) {
        				    if (Constants.debugLog)
        						Log.d("Counter","User position is currently 0-0, using default position for API calls.");
        				    //skipAPICallsThisRun = true;
        				    latForAPI = defaultLat;
        				    lonForAPI = defaultLon;
        			    } else {
        				    
        				    
        				    latForAPI = AppCAP.getUserLatLon()[0];
        				    lonForAPI = AppCAP.getUserLatLon()[1];
        			    }
        			    
        			    llArray[0] = latForAPI;
				    llArray[1] = lonForAPI;
        			    
        			    if (!skipAPICallsThisRun) {                                            
                                	    
        				    if (Constants.debugLog)
        						Log.d("Counter","API calls: Using coordinates: " + latForAPI + ", " + lonForAPI);
        				    if (Constants.debugLog)
        						Log.d("Counter","Cache Status: venuesWithCheckins: " + venuesWithCheckinsCache.hasData() +
                        				    " nearbyVenues: " + nearbyVenuesCache.hasData() + 
                        				    " contactsList: " + contactsListCache.hasData());
        				    if (Constants.debugLog)
        						Log.d("Counter"," APIs Active: venuesWithCheckins: " + venuesWithCheckinsCache.isActive() +
                        				    " nearbyVenues: " + nearbyVenuesCache.isActive() + 
                        				    " contactsList: " + contactsListCache.isActive());
                        		    
                        		    
                        		    apisCalledThisUpdate = false;
                        		    cachedDataSentThisUpdate = false;
                        		    
                        		    // Determine if venuesWithCheckins should run
                        		    if (venuesWithCheckinsCache.isActive() || 
                        				    !venuesWithCheckinsCache.hasData() ||
                        				    refreshAllDataThisRun) {
                        			    
                        			    if (allowCachedDataThisRun && venuesWithCheckinsCache.hasData() && !refreshAllDataThisRun) {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Sending cached data for venuesWithCheckins");
                        				    venuesWithCheckinsCache.sendCachedData();
                        				    cachedDataSentThisUpdate = true;
                        			    } else {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Refreshing venuesWithCheckinsCache...");
                                			    venuesWithCheckinsCache.setNewData(AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(llArray));
                                			    if (Constants.debugLog)
                                					Log.d("Counter","Called VenuesWithCheckins, Received: " + venuesWithCheckinsCache.getData().getResponseMessage());
                                			    apisCalledThisUpdate = true;
                        			    } 
                        		    }
                        		    
                        		    // Determine if venuesWithCheckins should run
                        		    if (nearbyVenuesCache.isActive() || 
                        				    !nearbyVenuesCache.hasData() ||
                        				    refreshAllDataThisRun) {
                        			    
                        			    if (allowCachedDataThisRun && nearbyVenuesCache.hasData() && !refreshAllDataThisRun) {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Sending cached data for nearbyVenues");
                        				    nearbyVenuesCache.sendCachedData();
                        				    cachedDataSentThisUpdate = true;
                        			    } else {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Refreshing nearbyVenuesCache...");
                                        		    final GeoPoint gp = new GeoPoint((int)(latForAPI*1E6), (int)(lonForAPI*1E6));
                                        		    nearbyVenuesCache.setNewData(AppCAP.getConnection().getVenuesCloseToLocation(gp,20));
                                        		    if (Constants.debugLog)
                                        				Log.d("Counter","Called VenuesWithCheckins, Received: " + nearbyVenuesCache.getData().getResponseMessage());
                                        		    apisCalledThisUpdate = true;
                        			    }
                        		    }
                        		    
                        		 // Determine if contactsList should run
                        		    if (contactsListCache.isActive() || 
                        				    !contactsListCache.hasData() ||
                        				    refreshAllDataThisRun) {
                        			    
                        			    if (allowCachedDataThisRun && contactsListCache.hasData() && !refreshAllDataThisRun) {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Sending cached data for contactsList");
                        				    contactsListCache.sendCachedData();
                        				    cachedDataSentThisUpdate = true;
                        			    } else {
                        				    if (Constants.debugLog)
                        						Log.d("Counter","Refreshing contactsListCache...");
                                			    contactsListCache.setNewData(AppCAP.getConnection().getContactsList());
                                			    if (Constants.debugLog)
                                					Log.d("Counter","Called getContactsList, Received: " + contactsListCache.getData().getResponseMessage());
                                        		    apisCalledThisUpdate = true;
                        			    }
                        		    }
                        		    
                        		    if (cachedDataSentThisUpdate) {
                        			    if (Constants.debugLog)
                        					Log.d("Counter","Cached Data sent this update.");
                        		    }
                        		    if (apisCalledThisUpdate) {
                        			    if (Constants.debugLog)
                        					Log.d("Counter","New API calls this made this update.");
                        		    }
                        		    if (!cachedDataSentThisUpdate && !apisCalledThisUpdate)
                        			    if (Constants.debugLog)
                        					Log.d("Counter","No data consumers active.");
                                	    
                        		    
                        		    // Clear any flags that control behavior for a single update
                			    allowCachedDataThisRun = false;
                			    refreshAllDataThisRun = false;
                			    skipAPICallsThisRun = false;
                                	    
                        	    }  
                        	    
        			    //We are going stop the timer if the user hasn't moved views in a while
        			    numberOfCalls++;
        			    //Currently 10 second interval with 20 calls so 3.3bar minutes
        			    if(numberOfCalls < 20)
        			    {
        				    if (Constants.debugLog)
        						Log.d("Counter","Posting runnable delayed for 10 seconds...");
        				    taskHandler.postDelayed(runTimer, tick * 1000);
        			    }
        			    else
        			    {
        				    if (Constants.debugLog)
        						Log.d("Counter","Turning off counter until user activity");
                                	    //TODO We should also turn off the GPS at this point as well
        			    }
        			    
        			    
        		    }
        		    
        	    }).start();
        	    
        	    
            }
        };
        
	public void checkInTrigger(VenueSmart checkedInVenue) {

		this.stop();
		//Stow the venue Id for the checkout later
		//FIXME
		//Test uninitialized case first
		//AppCAP.setUserLastCheckinVenueId(checkedInVenue.getVenueId());
		
		//Venue Related
		//We need to look at the list of venues with checkins and see if they checked into one of those venues
		//If they checked into a venue without checkins, we need to add it to the venuesWithCheckinsCache
		DataHolder venuesWithCheckins = venuesWithCheckinsCache.getData();
		Object[] obj = (Object[]) venuesWithCheckins.getObject();
		@SuppressWarnings("unchecked")
		ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
		boolean venueFound = false;
		VenueSmart tmpVenue = null;
		for(VenueSmart currVenue : arrayVenues)
		{
			if(currVenue.getVenueId() == checkedInVenue.getVenueId())
			{
				venueFound = true;
				tmpVenue = currVenue;
				break;
			}
		}
		if(venueFound==false)
		{
			arrayVenues.add(checkedInVenue);
			tmpVenue = checkedInVenue;
		}
		//Once we have the correct venue we need to add our user to the list of checkins and increment the total venue checkins
		CheckinData newCheckinData = new CheckinData(AppCAP.getLoggedInUserId(), 0, 1);
		//Check to see if we are in the checkins array first erroneously and then add us and increment
                //TODO
                //Implement check
		tmpVenue.getArrayCheckins().add(newCheckinData);
		tmpVenue.setCheckins(tmpVenue.getCheckins()+1);
		
		//People list Related
		//Find the current logged in user in the people list and update their status to checkedin
		@SuppressWarnings("unchecked")
		ArrayList<UserSmart> arrayUsers = (ArrayList<UserSmart>) obj[1];
		boolean userFound = false;
		for(UserSmart currUser : arrayUsers)
		{
			if(currUser.getUserId() == AppCAP.getLoggedInUserId())
			{
				currUser.setCheckedIn(1);
				userFound =  true;
				break;
			}
		}
		if(userFound == false && Constants.debugLog)
		{
			Log.d("Counter","Logged In User not found in People list!!!!!");
		}
		//After local cache is updated kickoff a refresh of all data via http
		this.refreshAllData();
	}
	public void checkOutTrigger(){
		
		this.stop();
		boolean waitForServerData = false;
		//Stow the venue Id for the checkout later
		int lastVenueId = AppCAP.getUserLastCheckinVenueId();
		//Check here for case when LastCheckin is not valid, multiple devices will defy this and create brief data discounts
		//if(yada-yada)
		//Venue Related
                //We need to look at the list of venues with checkins and see if they checked into one of those venues
                DataHolder venuesWithCheckins = venuesWithCheckinsCache.getData();
                Object[] obj = (Object[]) venuesWithCheckins.getObject();
                @SuppressWarnings("unchecked")
                ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
                boolean venueFound = false;
                VenueSmart tmpVenue = null;
                for(VenueSmart currVenue : arrayVenues)
                {
                	if(currVenue.getVenueId() == lastVenueId)
                	{
                		venueFound = true;
                		tmpVenue = currVenue;
                		int numCheckins = tmpVenue.getCheckins();
                		//Decrement number of users checkedin, should never go below 0
                		if(numCheckins>0)
                		{
                			tmpVenue.setCheckins(numCheckins - 1);
                		}
                		break;
                	}
                }
                if(venueFound==false)
                {
                	//This shouldn't really happen, but if it does we just need to wait for server data
                	//The list of venues with checkins should include the venue the user is checking out of
                	waitForServerData = true;
                }
                else
                {
                        //Once we have the correct venue we need to remove our user to the list of checkins
                        boolean usersCheckinFound = false;
                        for(CheckinData currCheckIn : tmpVenue.getArrayCheckins() )
                        {
                        	if(currCheckIn.getUserId() == AppCAP.getLoggedInUserId())
                        	{
                        		//Remove the current user from the checkin list
                        		tmpVenue.getArrayCheckins().remove(currCheckIn);
                        		usersCheckinFound =  true;
                        		break;
                        	}
                        }
                        if(usersCheckinFound == false)
                        {
                        	//We can't find our user in the cached venue, we will need to wait for the server data
                        	waitForServerData = true;
                        }
                }
                
                //People list Related
                //Find the current logged in user in the people list and update their status to checkedOut
                @SuppressWarnings("unchecked")
                ArrayList<UserSmart> arrayUsers = (ArrayList<UserSmart>) obj[1];
                boolean userFound = false;
                for(UserSmart currUser : arrayUsers)
                {
                	if(currUser.getUserId() == AppCAP.getLoggedInUserId())
                	{
                		currUser.setCheckedIn(0);
                		userFound =  true;
                		break;
                	}
                }
                if(userFound == false && Constants.debugLog)
                {
			if (Constants.debugLog)
				Log.d("Counter","Logged In User not found in People list!!!!!");
                }
                if(waitForServerData)
                {
                	if (Constants.debugLog)
				Log.d("Counter","NEED TO WAIT FOR SERVER DATA CHECKOUT LOGIC FAILED!!!!!");
                }
                //After local cache is updated kickoff a refresh of all data via http
                this.refreshAllData();
		
	}
        
        
	
}
