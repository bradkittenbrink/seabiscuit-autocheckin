package com.coffeeandpower.datatiming;

import java.util.Observer;

import android.os.Handler;
import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.google.android.maps.GeoPoint;

public class Counter {
	
	private Integer tick = 0;
	//private DataHolder venuesWithCheckinsResponse;
	//private DataHolder nearbyVenuesResponse;
	
	private CachedNetworkData venuesWithCheckinsCache = new CachedNetworkData("venuesWithCheckins");
	private CachedNetworkData nearbyVenuesCache = new CachedNetworkData("nearbyVenues");
	private CachedNetworkData contactsListCache = new CachedNetworkData("contactsList");
	
	private boolean isRunning = false;
	//private boolean delayHttp = false;	// Flag used to delay HTTP call if there is cached data
	private boolean isFirstRun = true;
	
	private boolean allowCachedDataThisRun = false;
	
	private int numberOfCalls = 0;
	
	private boolean apisCalledThisUpdate = false;
	
	// Scheduler
	protected Handler taskHandler = new Handler();
	
	public Counter(Integer tick, Integer trigger) {
		super();
		this.tick = tick;
		this.isFirstRun = true;
	}

	public void getCachedDataForAPICall(String apicall, Observer context) {
		
		if (apicall.equals("venuesWithCheckins")) {
			Log.d("Counter","Enabling venuesWithCheckins.");
			venuesWithCheckinsCache.isActive = true;
			venuesWithCheckinsCache.addObserver(context);
		}
		else if (apicall.equals("nearbyVenues")) {
			Log.d("Counter","Enabling nearbyVenues.");
			nearbyVenuesCache.isActive = true;
			nearbyVenuesCache.addObserver(context);
		}
		else if (apicall.equals("contactsList")) {
			Log.d("Counter","Enabling contactsList.");
			contactsListCache.isActive = true;
			contactsListCache.addObserver(context);
		}
		else
		{
			Log.d("Counter","INVALID OPTION FOR OBSERVER REGISTRATION");
		}
		
		//The user is moving around the activities lets keep the data fresh
		this.numberOfCalls = 0;
		//Restart the timer
		allowCachedDataThisRun = true;
		Log.d("Counter","getCachedDataForAPICall is calling stop/start");
		stop();
		start();
		
	}
	
	public void getCachedDataForAPICalls(String apicall1, String apicall2, Observer context) {
		
		if (apicall1.equals("venuesWithCheckins") || apicall2.equals("venuesWithCheckins")) {
			Log.d("Counter","Enabling venuesWithCheckins.");
			venuesWithCheckinsCache.isActive = true;
			venuesWithCheckinsCache.addObserver(context);
		}
		if (apicall1.equals("nearbyVenues") || apicall2.equals("nearbyVenues")) {
			Log.d("Counter","Enabling nearbyVenues.");
			nearbyVenuesCache.isActive = true;
			nearbyVenuesCache.addObserver(context);
		}
		if (apicall1.equals("contactsList") || apicall2.equals("contactsList")) {
			Log.d("Counter","Enabling contactsList.");
			contactsListCache.isActive = true;
			nearbyVenuesCache.addObserver(context);
		}
		
		//The user is moving around the activities lets keep the data fresh
		this.numberOfCalls = 0;
		//Restart the timer
		allowCachedDataThisRun = true;
		Log.d("Counter","getCachedDataForAPICall is calling stop/start");
		stop();
		start();
		
	}
	
	public void stoppedObservingAPICall(String apicall, Observer context) {
		if (apicall.equals("venuesWithCheckins")) {
			Log.d("Counter","Disabling venuesWithCheckins for " + context.toString() + ".");
			venuesWithCheckinsCache.isActive = false;
			venuesWithCheckinsCache.deleteObserver(context);
		}
		else if (apicall.equals("nearbyVenues")) {
			Log.d("Counter","Disabling nearbyVenues for " + context.toString() + ".");
			nearbyVenuesCache.isActive = false;
			nearbyVenuesCache.deleteObserver(context);
		}
		else if (apicall.equals("contactsList")) {
			Log.d("Counter","Disabling contactsList for " + context.toString() + ".");
			contactsListCache.isActive = false;
			contactsListCache.deleteObserver(context);
		}
	}
	
	

	public void stop() {
		
		if (isRunning == true) {
			Log.d("Counter","Counter.stop()");
			this.isRunning = false;
			taskHandler.removeCallbacks(runTimer);
		}
		else
		{
			Log.d("Counter","Warning: Tried to stop Counter but it was not started...");
		}
	}

	public void start() {
		
		if (isRunning == false) {
			Log.d("Counter","Counter.start()");
			this.isRunning = true;
			taskHandler.removeCallbacks(runTimer);
			taskHandler.post(runTimer);
		}
		else
		{
			Log.d("Counter","Warning: Tried to start Counter when it was already running...");
		}
		
	}
	
	public void manualTrigger() {
		Log.d("Counter","manualTrigger()");
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
        				    Log.d("Counter","User position is currently 0-0, skipping API calls until a position is received.");
        			    } else {                                            
                                	    
                        		    Log.d("Counter","API calls: Using coordinates: " + AppCAP.getUserLatLon()[0] + ", " + AppCAP.getUserLatLon()[1] + ", isFirstRun: " + isFirstRun);
                        		    Log.d("Counter","Current data cache: venuesWithCheckins: " + venuesWithCheckinsCache.hasData() + 
                        				    " nearbyVenues: " + nearbyVenuesCache.hasData() + 
                        				    " contactsList: " + contactsListCache.hasData());
                        		    
                        		    
                        		    apisCalledThisUpdate = false;
                        		    
                        		    
                        		    if (venuesWithCheckinsCache.isActive || isFirstRun) {
                        			    
                        			    if (allowCachedDataThisRun && venuesWithCheckinsCache.hasData()) {
                        				    Log.d("Counter","Sending cached data for venuesWithCheckins");
                        				    venuesWithCheckinsCache.sendCachedData();
                        			    } else {
                        				    Log.d("Counter","Refreshing venuesWithCheckinsCache and sending updated data...");
                                			    venuesWithCheckinsCache.setNewData(AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(AppCAP.getUserLatLon()));
                                			    Log.d("Counter","Called VenuesWithCheckins, Received: " + venuesWithCheckinsCache.getData().getResponseMessage());
                                			    apisCalledThisUpdate = true;
                        			    }
                        			    
                        			    
                        		    }
                        		    
                        		    if (nearbyVenuesCache.isActive || isFirstRun) {
                        			    
                        			    if (allowCachedDataThisRun && nearbyVenuesCache.hasData()) {
                        				    Log.d("Counter","Sending cached data for nearbyVenues");
                        				    nearbyVenuesCache.sendCachedData();
                        			    } else {
                        				    Log.d("Counter","Refreshing nearbyVenuesCache and sending updated data...");
                                        		    final GeoPoint gp = new GeoPoint((int)(AppCAP.getUserLatLon()[0]*1E6), (int)(AppCAP.getUserLatLon()[1]*1E6));
                                        		    nearbyVenuesCache.setNewData(AppCAP.getConnection().getVenuesCloseToLocation(gp,20));
                                        		    Log.d("Counter","Called VenuesWithCheckins, Received: " + nearbyVenuesCache.getData().getResponseMessage());
                                        		    apisCalledThisUpdate = true;
                        			    }
                        		    }
                        		    
                        		    if (contactsListCache.isActive || isFirstRun) {
                        			    if (allowCachedDataThisRun && contactsListCache.hasData()) {
                        				    Log.d("Counter","Sending cached data for contactsList");
                        				    contactsListCache.sendCachedData();
                        			    } else {
                                			    Log.d("Counter","Refreshing contactsListCache and sending updated data...");
                                			 
                                			    contactsListCache.setNewData(AppCAP.getConnection().getContactsList());
                                			    Log.d("Counter","Called getContactsList, Received: " + contactsListCache.getData().getResponseMessage());
                                        		    apisCalledThisUpdate = true;
                        			    }
                        			    
                        			    
                        			    
                        		    }
                        		    
                        		    if (!apisCalledThisUpdate) {
                        			    Log.d("Counter","No data consumers active.  No API calls made.");
                        		    }
                                	    
                                	    
                        	    }  
                        	    
        			    //We are going stop the timer if the user hasn't moved views in a while
        			    numberOfCalls++;
        			    //Currently 10 second interval with 20 calls so 3.3bar minutes
        			    if(numberOfCalls < 20)
        			    {
        				    Log.d("Counter","Posting runnable delayed for 10 seconds...");
        				    taskHandler.postDelayed(runTimer, tick * 1000);
        			    }
        			    else
        			    {
                                	    Log.d("Counter","Turning off counter until user activity");
                                	    //TODO We should also turn off the GPS at this point as well
        			    }
        			    if (isFirstRun)
        				    	isFirstRun = false;
        			    allowCachedDataThisRun = false;
        		    }
        		    
        	    }).start();
        	    
        	    
            }
        };
        
        
	
}
