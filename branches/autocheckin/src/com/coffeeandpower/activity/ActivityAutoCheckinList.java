package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyVenueNotificationAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CounterData;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.urbanairship.UAirship;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityAutoCheckinList extends RootActivity implements Observer {
	
	private String AUTOCHECKIN_SCREEN_TITLE = "Auto Checkin";
	
	private ListView autoCheckinList = null;
	private MyVenueNotificationAdapter myVenueNotificationAdapter = null;
	
	private ArrayList<VenueSmart> arrayVenues;
	
	private ProgressDialog progress;
	
	private boolean initialLoad = true;
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		// handleMessage - on the main thread
		@Override
		public void handleMessage(Message msg) {

			arrayVenues = msg.getData().getParcelableArrayList("venues");
			
			Log.d("Notifications","arrayVenues: " + arrayVenues);

			setListData();
			
			progress.dismiss();
			
			super.handleMessage(msg);
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_checkin_list);
		
		this.autoCheckinList = (ListView) findViewById(R.id.venue_auto_checkin_list);
		this.autoCheckinList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				//Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
				//intent.putExtra("venueSmart", (VenueSmart) adapterPlaces.getItem(position));
				//We are sending the whole place object so we won't need the 4sqId separately
				//intent.putExtra("foursquare_id", arrayVenues.get(position).getFoursquareId());
				//I don't know what data is, but I don't think we will need
				//intent.putExtra("coords", data);
				//startActivity(intent);
				Log.d("Notifications","User clicked on row.");
				
				
				

			}
		});

		
		// prevent keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();
		
		// Views
		
		((CustomFontView) findViewById(R.id.textview_location_name)).setText(AUTOCHECKIN_SCREEN_TITLE);
	}
	
	@Override
	protected void onStart() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStart()");
		super.onStart();
		
		//If the user isn't logged in then we will displaying the login screen not the list of contacts.
		if (AppCAP.isLoggedIn())
		{
			AppCAP.getCounter().getCachedDataForAPICall("venuesWithCheckins",this);
		}
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStop()");
		if (AppCAP.isLoggedIn())
		{
			UAirship.shared().getAnalytics().activityStopped(this);
			AppCAP.getCounter().stoppedObservingAPICall("venuesWithCheckins",this);
		}
		super.onStop();
	}
	
	
	private void setListData() {
		if(initialLoad)
		{
			if (Constants.debugLog)
				Log.d("ActivityNotifications","Place List Initial Load");
			
			
			myVenueNotificationAdapter = new MyVenueNotificationAdapter(ActivityAutoCheckinList.this, arrayVenues);
			
			Log.d("Notification","this.autoCheckinList: " + this.autoCheckinList.toString());
			Log.d("Notification","myVenueNotificationAdapter: " + myVenueNotificationAdapter.toString());
			
			this.autoCheckinList.setAdapter(myVenueNotificationAdapter);
			Utils.animateListView(this.autoCheckinList);
			initialLoad = false;
		}
		else
		{
			myVenueNotificationAdapter.setNewData(arrayVenues);
			myVenueNotificationAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void update(Observable observable, Object data) {
		// Get list of venues with user checkins
        	int[] venueList = AppCAP.getVenuesWithAutoCheckins();
        	
        	CounterData counterdata = (CounterData) data;
		DataHolder venuesWithCheckins = counterdata.getData();
					
		Object[] obj = (Object[]) venuesWithCheckins.getObject();
		@SuppressWarnings("unchecked")
		ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
        	
		//List of Venues we are going to send to list adapter
		ArrayList<VenueSmart> deliveredVenues = new ArrayList<VenueSmart>();

		//Grab the Venue data from the cache and see if it matches all the venues in the
		//previously checkedin list
		for (VenueSmart receivedVenue: arrayVenues) {
			for (int myVenueId:venueList) {
				if (myVenueId == receivedVenue.getVenueId()) {
					deliveredVenues.add(receivedVenue);
				}
			}
		}
		if(venueList.length == deliveredVenues.size())
		{
			//We have all the venues so we can stop listening
			if (Constants.debugLog)
				Log.d("Notifications","Shutting down observer, all venues accounted for");
			AppCAP.getCounter().stoppedObservingAPICall("venuesWithCheckins", this);
		}
		else
		{
			//FIXME
			//We need to go get all the venues and update the adapter when we get them
		}
		
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putCharSequence("type", counterdata.type);
		bundle.putParcelableArrayList("venues", deliveredVenues);
		message.setData(bundle);
		
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.update: Sending handler message...");
		taskHandler.sendMessage(message);
	}

}