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
import com.coffeeandpower.adapters.MyNotificationsListAdapter;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyVenueNotificationAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CachedDataContainer;
import com.coffeeandpower.tab.activities.ActivityPeopleAndPlaces;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.urbanairship.UAirship;

public class ActivityNotifications extends RootActivity {
	
	private String NOTIFICATIONS_SCREEN_TITLE = "Notifications";
	
	private ListView notificationList = null;
	private MyNotificationsListAdapter myNotificationsListAdapter = null;
	
	private ArrayList<VenueSmart> arrayVenues;
	
	private ProgressDialog progress;
	
	private ArrayList<String> listItems = new ArrayList<String>();
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		this.notificationList = (ListView) findViewById(R.id.notification_list);
		this.notificationList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				
				Log.d("Notifications","User clicked on row.");
				
				String selection = listItems.get(position);
				
				if (selection.equalsIgnoreCase("Auto Checkin")) {
					Intent intent = new Intent(ActivityNotifications.this, ActivityAutoCheckinList.class);
					startActivity(intent);
				}
				

			}
		});
		
		
		// Create list of strings for list items in sub menu
		this.listItems.add("Auto Checkin");
		
		myNotificationsListAdapter = new MyNotificationsListAdapter(ActivityNotifications.this,listItems);
		
		Log.d("Notification","this.notificationList: " + this.notificationList.toString());
		Log.d("Notification","myVenueNotificationAdapter: " + myNotificationsListAdapter.toString());
		
		this.notificationList.setAdapter(myNotificationsListAdapter);
		
		
		

		
		// prevent keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		
		
		// Views
		
		((CustomFontView) findViewById(R.id.textview_location_name)).setText(NOTIFICATIONS_SCREEN_TITLE);
	}
	
	@Override
	protected void onStart() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStart()");
		super.onStart();
		
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
			Log.d("Notifications","ActivityNotifications.onStop()");
		if (AppCAP.isLoggedIn())
		{
			UAirship.shared().getAnalytics().activityStopped(this);
		}
		super.onStop();
	}
	
	
	
	

}
