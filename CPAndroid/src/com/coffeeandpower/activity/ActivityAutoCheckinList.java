package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyNotificationsListAdapter;
import com.coffeeandpower.adapters.MyVenueNotificationAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.utils.Utils;
import com.urbanairship.UAirship;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;

public class ActivityAutoCheckinList extends RootActivity implements Observer {
	
	private ListView autoCheckinList = null;		// List with one row for each venue user has checked into before
	//private ListView masterAutoCheckinToggleList = null;    // List with only one row for master toggle switch
	
	private ToggleButton masterToggleSwitch = null;
	
	private MyVenueNotificationAdapter myVenueNotificationAdapter = null;   // adapter for venue list
	//private MyNotificationsListAdapter myNotificationsListAdapter = null;   // adapter for master toggle switch list
	
	//private ArrayList<VenueSmart> arrayVenues;
	
	private ProgressDialog progress;
	
	private boolean initialLoad = true;
	
	private Context contextForServiceManagement;
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		// handleMessage - on the main thread
		@Override
		public void handleMessage(Message msg) {

			ArrayList<VenueSmart> arrayVenues = msg.getData().getParcelableArrayList("venues");
			
			Log.d("Notifications","arrayVenues: " + arrayVenues);

			setListData(arrayVenues);
			
			progress.dismiss();
			
			super.handleMessage(msg);
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_checkin_list);
		
		this.masterToggleSwitch = (ToggleButton) findViewById(R.id.toggleButtonMasterAutoCheckinToggle);
		
		this.autoCheckinList = (ListView) findViewById(R.id.venue_auto_checkin_list);
		
		//this.masterAutoCheckinToggleList = (ListView) findViewById(R.id.master_auto_checkin_toggle);
		/*
		this.masterToggleSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		*/
		
		this.masterToggleSwitch.setChecked(AppCAP.autoCheckinEnabled());
		
		contextForServiceManagement = this;
		
		this.masterToggleSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
				//intent.putExtra("venueSmart", (VenueSmart) adapterPlaces.getItem(position));
				//We are sending the whole place object so we won't need the 4sqId separately
				//intent.putExtra("foursquare_id", arrayVenues.get(position).getFoursquareId());
				//I don't know what data is, but I don't think we will need
				//intent.putExtra("coords", data);
				//startActivity(intent);
				
				
				
				Log.d("AutoCheckin","User clicked toggle button.");
				
				
				if (!masterToggleSwitch.isChecked()) {
					Log.d("AutoCheckin","Disabling auto-checkin...");
					AppCAP.disableAutoCheckin(contextForServiceManagement);
				}
				else {
					Log.d("AutoCheckin","Enabling auto-checkin...");
					AppCAP.enableAutoCheckin(contextForServiceManagement);
				}
				
				
				

			}
		});
		
		
		
		
		// prevent keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();
	}
	
	@Override
	protected void onStart() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStart()");
		super.onStart();
		
		//If the user isn't logged in then we will displaying the login screen not the list of contacts.
		if (AppCAP.isLoggedIn())
		{
			CacheMgrService.startObservingAPICall("venuesWithCheckins",this);
		}
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStop()");
		if (AppCAP.isLoggedIn())
		{
			UAirship.shared().getAnalytics().activityStopped(this);
			CacheMgrService.stopObservingAPICall("venuesWithCheckins",this);
		}
		super.onStop();
	}
	
	
	private void setListData(ArrayList<VenueSmart> arrayVenues) {
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
        	int[] venueList = AppCAP.getVenuesWithUserCheckins();
        	
        	CachedDataContainer counterdata = (CachedDataContainer) data;
		DataHolder venuesWithCheckins = counterdata.getData();
					
		Object[] obj = (Object[]) venuesWithCheckins.getObject();
		@SuppressWarnings("unchecked")
		List<VenueSmart> listVenues = (List<VenueSmart>) obj[0];
		ArrayList<VenueSmart> arrayVenues = new ArrayList<VenueSmart>(listVenues);
        	
		//List of Venues we are going to send to list adapter
		ArrayList<VenueSmart> deliveredVenues = new ArrayList<VenueSmart>();

		//Add VenueSmarts to deliveredVenues if the venueID matches a venue where user has checked in before
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
			CacheMgrService.stopObservingAPICall("venuesWithCheckins", this);
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
	
	public void onClickBack(View v) {
	    onBackPressed();
	}

}