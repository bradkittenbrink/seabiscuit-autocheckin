package com.coffeeandpower.location;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CacheMgrService;
import com.coffeeandpower.datatiming.CachedDataContainer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityManager implements Observer {

	private static Context myContext;

	private static LocationManager locationManager;
	
	private static final float PROX_ALERT_RADIUS = 100;
	private static final long PROX_ALERT_EXPIRY = 2880000; // 2 days in ms

	private static ProximityManager instance;
	
	private static ArrayList<Integer> venuesWithProxAlertsAdded = new ArrayList<Integer>();
	
	private static WifiBroadcastReceiver wifiBroadcastReceiver;
	
	
        public static void addVenueToAutoCheckinList(VenueSmart checkinVenue)
        {
        	// Create a prox alert if this is a new venue for this user
        	if (AppCAP.addVenueToAutoCheckinList(checkinVenue.getVenueId())) {
        		createProxAlert(checkinVenue);
        	}
        }
        
        
        public static ProximityManager getInstance() {
        	return instance;
        }
        
        
        public static void onStart(Context context) {

        	instance = new ProximityManager();
        	wifiBroadcastReceiver = new WifiBroadcastReceiver();
        	
        	myContext = context;
        	CacheMgrService.startObservingAPICall("venuesWithCheckins", getInstance());
        	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        	//We want to register for wifi connection broadcasts
        	wifiBroadcastReceiver.registerForConnectionState(myContext);        	
        }
        
        public static void onStop(Context context) {
        	wifiBroadcastReceiver.unregisterForConnectionState(context);
        }
        
        
        private static boolean venueHasProxAlertAdded(int venueId) {
        	return venuesWithProxAlertsAdded.contains(venueId);
        }
        
        
        private static void createProxAlert(VenueSmart currVenue) {
        	//Record that this venue has a prox alert
        	venuesWithProxAlertsAdded.add(currVenue.getVenueId());
        	
        	String intentString = "proxIntent_" + currVenue.getVenueId();
		Intent intent = new Intent(intentString);
		intent.putExtra("venue", currVenue);
		PendingIntent proxIntent = PendingIntent.getBroadcast(myContext,0,intent,0);
		
		locationManager.addProximityAlert(currVenue.getLat(), currVenue.getLng(), PROX_ALERT_RADIUS, PROX_ALERT_EXPIRY, proxIntent);
		
		IntentFilter filter = new IntentFilter(intentString);  
		myContext.registerReceiver(new ProximityReceiver(), filter);
        }



	@Override
	public void update(Observable observable, Object data) {

		Log.d("ProxMgr","update()");
		
		// Get list of venues with user checkins
        	int[] venueList = AppCAP.getVenuesWithAutoCheckins();
        	
        	CachedDataContainer counterdata = (CachedDataContainer) data;
		DataHolder venuesWithCheckins = counterdata.getData();
					
		Object[] obj = (Object[]) venuesWithCheckins.getObject();
		@SuppressWarnings("unchecked")
		ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
		
		//If have created a prox for all the venues, we can stop listening to the observable
		if(venuesWithProxAlertsAdded.size() == venueList.length)
		{
			Log.d("ProxMgr","Shutting down observer, all venues accounted for");
			CacheMgrService.stopObservingAPICall("venuesWithCheckins", getInstance());
		}
        	
		for (VenueSmart receivedVenue: arrayVenues) {
			
			for (int myVenueId:venueList) {
				if (myVenueId == receivedVenue.getVenueId()) {
					if (!venueHasProxAlertAdded(myVenueId)) {
						
						Log.d("ProxMgr","Creating prox alert for venue: " + myVenueId);
						// Create prox alert
						createProxAlert(receivedVenue);
					}
				}
			}
		}
		
	}

}
