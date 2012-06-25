package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.activity.ActivityCheckIn;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyVenuesAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cache.CachedNetworkData;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.Utils;
import com.google.android.maps.GeoPoint;
import com.urbanairship.UAirship;

public class ActivityCheckInList extends ListActivity implements Observer {

	//private DataHolder result;

	private Executor exe;
	private boolean initialLoad = true;

	private MyVenuesAdapter adapter;
	
	private DataHolder venuesWithCheckinsHolderWORKERTHREAD;
	private DataHolder nearbyVenuesHolderWORKERTHREAD;
	
	ArrayList<VenueSmart> venueArray;
	ArrayList<VenueSmart> checkinVenueArray;
	
	private ProgressDialog progress;
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		@SuppressWarnings("unchecked")  // suppress warning on Collections.sort
		@Override
		public void handleMessage(Message msg) {

			// pass message data along to venue update method
			if (venueArray != null)
				venueArray.clear();
			ArrayList<Parcelable> venueArrayReference = msg.getData().getParcelableArrayList("venues");
			if (venueArrayReference != null && venueArrayReference instanceof ArrayList<?>)
			{
        			venueArray = (ArrayList<VenueSmart>)venueArrayReference.clone();
				Collections.sort(venueArray);
				if (Constants.debugLog)
					Log.d("CheckInList","Adding an add_place placeholder to list...");
        			venueArray.add(VenueSmart.createVenuePlaceholder("add_place", "Add New Place..."));
        			
        			//updateVenuesAndCheckinsFromApiResult(venueArray);
        			checkinVenueArray = msg.getData().getParcelableArrayList("venuesWCheckins");
        			
        			if(initialLoad)	{
        				if (Constants.debugLog)
        					Log.d("ActivityPeopleAndPlaces","Place List Initial Load");
        				adapter = new MyVenuesAdapter(ActivityCheckInList.this, venueArray);
        				setListAdapter(adapter);
        				Utils.animateListView(getListView());
        				initialLoad = false;
        			} else {
        				//adapter = new MyVenuesAdapter(ActivityCheckInList.this, venueArray);
        				//setListAdapter(adapter);
        				adapter.setNewData(venueArray);
        				adapter.notifyDataSetChanged();
        			}
			}
			
			progress.dismiss();
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_in_list);

		// Executor
		exe = new Executor(ActivityCheckInList.this);
		exe.setExecutorListener(new ExecutorInterface() {
			@Override
			public void onErrorReceived() {
				errorReceived();
			}

			@Override
			public void onActionFinished(int action) {
				actionFinished(action);
			}
		});
		
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (((VenueSmart) adapter.getItem(position)).getFoursquareId().equals("add_place")) {
			final EditText input = new EditText(this);
			new AlertDialog.Builder(ActivityCheckInList.this).setTitle("Name of New Place").setView(input)
					.setPositiveButton("Add", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							exe.addPlace(input.getText().toString());
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
						}
					}).show();
		} else {
			Intent intent = new Intent(ActivityCheckInList.this, ActivityCheckIn.class);
			//The 4square API doesn't have all the data we need, so we cross reference the 4squareId to our list of
			//venues with checkins and if the venue has or has had checkins we fill that data into the venue class
			VenueSmart selectedVenue = (VenueSmart) adapter.getItem(position);
			for(VenueSmart currVenue:checkinVenueArray)
			{
				//Find the venue by the FoursquareId
				if(currVenue.getFoursquareId().equals(selectedVenue.getFoursquareId()))
				{
					selectedVenue.setVenueId(currVenue.getVenueId());
					selectedVenue.setArrayCheckins(currVenue.getArrayCheckins());
					break;
				}
				
			}
			intent.putExtra("venue", selectedVenue);
			startActivityForResult(intent, AppCAP.ACT_CHECK_IN);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case AppCAP.ACT_CHECK_IN:
			if (resultCode == AppCAP.ACT_QUIT) {
				ActivityCheckInList.this.finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClickCancel(View v) {
		onBackPressed();
	}
	
	@Override
	protected void onStart() {
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		
		if (Constants.debugLog)
			Log.d("CheckIn","ActivityCheckInList.onStart()");
		super.onStart();
		//initialLoad = true;
		UAirship.shared().getAnalytics().activityStarted(this);

		CacheMgrService.startObservingAPICalls("venuesWithCheckins","nearbyVenues",this);	
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
			Log.d("CheckIn","ActivityCheckInList.onStop()");
		super.onStop();
		UAirship.shared().getAnalytics().activityStopped(this);
		CacheMgrService.stopObservingAPICall("venuesWithCheckins",this);	// is this required?
		CacheMgrService.stopObservingAPICall("nearbyVenues",this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void errorReceived() {
	}

	
	private void actionFinished(int action) {
		DataHolder result = exe.getResult();

		switch (action) {
		/*case Executor.HANDLE_VENUES_CLOSE_TO_LOCATION:
			if (result.getObject() != null) {
				((ArrayList<Venue>) result.getObject()).add(new Venue("add_place", 0, "Add Place...", "", "", 0, 0, 0, "", "", "",
						"", "", "", "", 0, 0, 0, 0, "", "", "", ""));
				adapter = new MyVenuesAdapter(ActivityCheckInList.this, (ArrayList<Venue>) result.getObject());
				setListAdapter(adapter);
				Utils.animateListView(getListView());
			}
			break;*/

		case Executor.HANDLE_ADD_PLACE:
			if (result.getObject() != null) {
				Intent intent = new Intent(ActivityCheckInList.this, ActivityCheckIn.class);
				intent.putExtra("venue", (VenueSmart) result.getObject());
				startActivityForResult(intent, AppCAP.ACT_CHECK_IN);
			}
			break;
		}
	}
	
	
	
	@Override
	public void update(Observable observable, Object data) {
		/*
		 * verify that the data is really of type CounterData, and log the
		 * details
		 */
		
		CachedNetworkData cachedData = (CachedNetworkData)observable;
		CachedDataContainer counterdata = (CachedDataContainer) data;
		
		if (cachedData.getType().equals("nearbyVenues")) {
			if (Constants.debugLog)
				Log.d("CheckInList","Received nearbyVenues data.");
			this.nearbyVenuesHolderWORKERTHREAD = counterdata.getData();
			
		} else if (cachedData.getType().equals("venuesWithCheckins")) {
			if (Constants.debugLog)
				Log.d("CheckInList","Received venuesWithCheckins data.");
			this.venuesWithCheckinsHolderWORKERTHREAD = counterdata.getData();
			
		}
		
		
		if (this.nearbyVenuesHolderWORKERTHREAD != null && this.venuesWithCheckinsHolderWORKERTHREAD != null) {
				
			if (Constants.debugLog)
				Log.d("CheckInList","We have data for both APIs...");
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) nearbyVenuesHolderWORKERTHREAD.getObject();
			
			
			Object[] obj = (Object[]) venuesWithCheckinsHolderWORKERTHREAD.getObject();
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenuesWCheckins = (ArrayList<VenueSmart>) obj[0];
						
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", counterdata.type);
			bundle.putParcelableArrayList("venues", arrayVenues);
			bundle.putParcelableArrayList("venuesWCheckins", arrayVenuesWCheckins);
			message.setData(bundle);
			
			if (Constants.debugLog)
				Log.d("CheckInList","ActivityCheckInList.update: Sending handler message with " + arrayVenues.size() + " venues...");
			for (VenueSmart tempVenue:arrayVenues) {
				if (Constants.debugLog)
					Log.d("CheckInList","Venue: " + tempVenue.getName());
			}
			
			this.nearbyVenuesHolderWORKERTHREAD = null;
			this.venuesWithCheckinsHolderWORKERTHREAD = null;
			
			taskHandler.sendMessage(message);			
		}
		
		if (Constants.debugLog)
		{
        		if (this.nearbyVenuesHolderWORKERTHREAD == null)
        			Log.d("CheckInList","nearbyVenuesHolderWORKERTHREAD is null.");
        		else
        			Log.d("CheckInList","nearbyVenuesHolderWORKERTHREAD has data.");
        		
        		if (this.venuesWithCheckinsHolderWORKERTHREAD == null)
        			Log.d("CheckInList","venuesWithCheckinsHolderWORKERTHREAD is null.");
        		else
        			Log.d("CheckInList","venuesWithCheckinsHolderWORKERTHREAD has data.");
		}
	}
}
