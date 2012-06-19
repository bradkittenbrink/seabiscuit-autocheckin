package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyVenueNotificationAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CounterData;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;

public class ActivityNotifications extends RootActivity implements Observer {
	
	private String NOTIFICATIONS_SCREEN_TITLE = "Notifications";
	
	private ListView notificationList = null;
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
		setContentView(R.layout.activity_notifications);
		
		this.notificationList = (ListView) findViewById(R.id.notification_list);
		this.notificationList.setOnItemClickListener(new OnItemClickListener() {
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
		
		((CustomFontView) findViewById(R.id.textview_location_name)).setText(NOTIFICATIONS_SCREEN_TITLE);
		
		Log.d("Notifications","Getting venue data for notification list...");
		
		// Register for venue data from Counter
		AppCAP.getCounter().getCachedDataForAPICall("venuesWithCheckins",this);	
	}
	
	
	private void setListData() {
		if(initialLoad)
		{
			if (Constants.debugLog)
				Log.d("ActivityNotifications","Place List Initial Load");
			
			
			myVenueNotificationAdapter = new MyVenueNotificationAdapter(ActivityNotifications.this, arrayVenues);
			
			Log.d("Notification","this.notificationList: " + this.notificationList.toString());
			Log.d("Notification","myVenueNotificationAdapter: " + myVenueNotificationAdapter.toString());
			
			this.notificationList.setAdapter(myVenueNotificationAdapter);
			Utils.animateListView(this.notificationList);
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
		// TODO Auto-generated method stub
		if (data instanceof CounterData) {
			CounterData counterdata = (CounterData) data;
			DataHolder venuesWithCheckins = counterdata.getData();
						
			Object[] obj = (Object[]) venuesWithCheckins.getObject();
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
			
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", counterdata.type);
			bundle.putParcelableArrayList("venues", arrayVenues);
			message.setData(bundle);
			
			if (Constants.debugLog)
				Log.d("PeoplePlaces","ActivityNotifications.update: Sending handler message...");
			taskHandler.sendMessage(message);
			
			
		}
	}

}
