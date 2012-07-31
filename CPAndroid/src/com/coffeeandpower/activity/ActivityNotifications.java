package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import android.widget.Button;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyNotificationsListAdapter;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.views.CustomFontView;
import com.urbanairship.UAirship;

public class ActivityNotifications extends RootActivity {
	
	private String NOTIFICATIONS_SCREEN_TITLE = "Notifications";
	
	private ListView notificationList = null;
	private MyNotificationsListAdapter myNotificationsListAdapter = null;
	
	private ArrayList<VenueSmart> arrayVenues;
	
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
        
		final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButtonOnlyWhenCheckedIn);
		// Get Notification settings from shared prefs
		toggle.setChecked(AppCAP.getNotificationToggle());
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				AppCAP.setNotificationToggle(isChecked);

				Runnable r = new Runnable() {
					@Override
					public void run() {
						AppCAP.getConnection().setNotificationSettings(AppCAP.getPushDistance(), isChecked);
					}
				};
				new Thread(r,"ActivityNotifications.SettingsListener.setOnCheckedChangeListener").start();
		   }
		});

		final CharSequence [] data = { "City", "Venue", "Contacts" };

		final Button btnFrom = (Button) findViewById(R.id.btn_from);
		// Get Notification settings from shared prefs
		btnFrom.setText(AppCAP.getNotificationFrom());
		btnFrom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ActivityNotifications.this)
					.setTitle("Show me new check-ins from:")
					.setSingleChoiceItems(
							data,
							AppCAP.getNotificationFrom().equals("in city") ? 0
									: (AppCAP.getNotificationFrom().equals("in venue") ? 1 : 2),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									switch(whichButton) {
										case 0:
											AppCAP.setPushDistance("city");
											AppCAP.setNotificationFrom("in city");
											btnFrom.setText("in city");
											break;
										case 1:
											AppCAP.setPushDistance("venue");
											AppCAP.setNotificationFrom("in venue");
											btnFrom.setText("in venue");
											break;
										case 2:
											AppCAP.setPushDistance("contacts");
											AppCAP.setNotificationFrom("contacts");
											btnFrom.setText("contacts");
											break;
										default:
											// do nothing.
											break;
									}
									if (whichButton != -1) {
										Runnable r = new Runnable() {
											@Override
											public void run() {
												AppCAP.getConnection().setNotificationSettings(AppCAP.getPushDistance(), toggle.isChecked());
											}
										};
										new Thread(r, "ActivityNotifications.SettingsListener.Alert").start();
									}
									dialog.dismiss();
								}
							})
					.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						}).show();
			}
		});
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

	@Override
	public void onResume() {
		if (Constants.debugLog) {
			Log.d("Notifications", "ActivityNotificatins displayed, refreshing prefs from AppCAP");
		}

		// Get Notification settings from shared prefs
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButtonOnlyWhenCheckedIn);
		toggle.setChecked(AppCAP.getNotificationToggle());

		Button btnFrom = (Button) findViewById(R.id.btn_from);
		btnFrom.setText(AppCAP.getNotificationFrom());

		super.onResume();
	}

}
