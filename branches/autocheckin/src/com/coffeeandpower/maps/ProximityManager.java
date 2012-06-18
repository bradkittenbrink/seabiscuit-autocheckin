package com.coffeeandpower.maps;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CounterData;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityManager implements Observer {

	private static Context myContext;

	private static LocationManager locationManager;
	
	private static final float PROX_ALERT_RADIUS = 20;
	private static final long PROX_ALERT_EXPIRY = 2880000; // 2 days in ms

	private static ProximityManager instance;
	
	private static ArrayList<Integer> venuesWithProxAlertsAdded = new ArrayList<Integer>();
	
	
        public static void venueCheckin(VenueSmart checkinVenue)
        {
        	// Create a prox alert if this is a new venue for this user
        	if (AppCAP.didCheckIntoVenue(checkinVenue.getVenueId())) {
        		createProxAlert(checkinVenue.getVenueId(),checkinVenue.getLat(),checkinVenue.getLng());
        	}
        	
        }
        
        
        public static ProximityManager getInstance() {
        	return instance;
        }
        
        
        public static void onStart(Context context) {

        	instance = new ProximityManager();
        	
        	myContext = context;
        	AppCAP.getCounter().getCachedDataForAPICall("venuesWithCheckins", getInstance());
        	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        	
        }
        
        
        private static boolean venueHasProxAlertAdded(int venueId) {
        	return venuesWithProxAlertsAdded.contains(venueId);
        }
        
        
        private static void createProxAlert(int venueId, double venueLat, double venueLon) {
        	String intentString = "proxIntent_" + venueId;
		Intent intent = new Intent(intentString);
		PendingIntent proxIntent = PendingIntent.getBroadcast(myContext,0,intent,0);
		
		locationManager.addProximityAlert(venueLat, venueLon, PROX_ALERT_RADIUS, PROX_ALERT_EXPIRY, proxIntent);
		
		IntentFilter filter = new IntentFilter(intentString);  
		myContext.registerReceiver(new ProximityReceiver(), filter);
        }



	@Override
	public void update(Observable observable, Object data) {

		Log.d("ProxMgr","update()");
		
		// Get list of venues with user checkins
        	int[] venueList = AppCAP.getVenuesWithUserCheckins();
        	
        	CounterData counterdata = (CounterData) data;
		DataHolder venuesWithCheckins = counterdata.getData();
					
		Object[] obj = (Object[]) venuesWithCheckins.getObject();
		@SuppressWarnings("unchecked")
		ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
        	
        	
        	
		for (VenueSmart receivedVenue: arrayVenues) {
			
			for (int myVenueId:venueList) {
				if (myVenueId == receivedVenue.getVenueId()) {
					if (!venueHasProxAlertAdded(myVenueId)) {
						
						Log.d("ProxMgr","Creating prox alert for venue: " + myVenueId);
						// Create prox alert
						createProxAlert(myVenueId,receivedVenue.getLat(),receivedVenue.getLng());
					}
				}
			}
		}
		
	}

}
