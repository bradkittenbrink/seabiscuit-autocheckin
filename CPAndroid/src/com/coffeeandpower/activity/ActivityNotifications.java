package com.coffeeandpower.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TimePicker;
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
	
    private static final Runnable notificationsSettingsPusher = new Runnable() {
        @Override
        public synchronized void run() {
            // Note - the synchronization is probably unnecessary, but it makes it explicit
            //  that we want to wait until one UI handler's change gets committed before
            //  posting another change to the api.
            AppCAP.getConnection().setNotificationSettings(
                AppCAP.getPushDistance(),
                AppCAP.getNotificationToggle(),
                AppCAP.getQuietTimeToggle(),
                AppCAP.getQuietFrom(),
                AppCAP.getQuietTo(),
                AppCAP.getContactsOnlyChatToggle());
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
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				AppCAP.setNotificationToggle(isChecked);

				pushNotificationSettings();
		   }
		});

		final ToggleButton toggleQuiet = (ToggleButton) findViewById(R.id.toggleButtonQuietTime);
		toggleQuiet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				AppCAP.setQuietTimeToggle(isChecked);

				View v = findViewById(R.id.quiet_time_extras);
				v.setVisibility(isChecked ? View.VISIBLE : View.GONE);

				pushNotificationSettings();
		   }
		});

		class MyTimePicker extends DialogFragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
			private final Button button;
			
			MyTimePicker(Button b) {
				button = b;
			}

			@Override
			public void onClick(View v) {
				show(getSupportFragmentManager(), "tag_quiet_time_limits");
			}
	
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				String[] parts = null;
				switch(button.getId()) {
					case R.id.btn_quiet_from:
						parts = AppCAP.getQuietFrom().split(":");
						break;
					case R.id.btn_quiet_to:
						parts = AppCAP.getQuietTo().split(":");
						break;
				}
				return new TimePickerDialog(ActivityNotifications.this, this, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), false);
			}
	
			@Override
			public void onTimeSet(TimePicker picker, int hour, int min) {
				Time t = new Time();
				t.set(0, min, hour, 0, 0, 0);
	
				String timeformat = t.format("%H:%M:00");
				String timedisplay = t.format("%l:%M %P");

				button.setText(timedisplay);
				switch(button.getId()) {
					case R.id.btn_quiet_from:
						AppCAP.setQuietFrom(timeformat);
						break;
					case R.id.btn_quiet_to:
						AppCAP.setQuietTo(timeformat);
						break;
				}
				pushNotificationSettings();
			}
		};

		final Button buttonQuietFrom = (Button) findViewById(R.id.btn_quiet_from);
		buttonQuietFrom.setOnClickListener(new MyTimePicker(buttonQuietFrom));
		final Button buttonQuietTo = (Button) findViewById(R.id.btn_quiet_to);
		buttonQuietTo.setOnClickListener(new MyTimePicker(buttonQuietTo));

		final ToggleButton toggleAnyoneChat = (ToggleButton) findViewById(R.id.toggleButtonOneToOneChat);
		toggleAnyoneChat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				AppCAP.setContactsOnlyChatToggle(!isChecked);

				pushNotificationSettings();
		   }
		});

		final CharSequence [] data = { "City", "Venue", "Contacts" };

		final Button btnFrom = (Button) findViewById(R.id.btn_from);
		// Get Notification settings from shared prefs
		btnFrom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String push_distance = AppCAP.getPushDistance();
				new AlertDialog.Builder(ActivityNotifications.this)
					.setTitle("Show me new check-ins from:")
					.setSingleChoiceItems(
							data,
							push_distance.equals("city") ? 0
									: (push_distance.equals("venue") ? 1 : 2),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									switch(whichButton) {
										case 0:
											AppCAP.setPushDistance("city");
											break;
										case 1:
											AppCAP.setPushDistance("venue");
											break;
										case 2:
											AppCAP.setPushDistance("contacts");
											break;
										default:
											// do nothing.
											break;
									}
									resetButtonFromText();
									if (whichButton != -1) {
										pushNotificationSettings();
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

		AppCAP.refreshNotificationSettings();

		// Get Notification settings from shared prefs
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButtonOnlyWhenCheckedIn);
		toggle.setChecked(AppCAP.getNotificationToggle());

		resetButtonFromText();

		final ToggleButton toggleQuiet = (ToggleButton) findViewById(R.id.toggleButtonQuietTime);
		toggleQuiet.setChecked(AppCAP.getQuietTimeToggle());
		final Button buttonQuietFrom = (Button) findViewById(R.id.btn_quiet_from);
		buttonQuietFrom.setText(displayTime(AppCAP.getQuietFrom()));
		final Button buttonQuietTo = (Button) findViewById(R.id.btn_quiet_to);
		buttonQuietTo.setText(displayTime(AppCAP.getQuietTo()));

		final ToggleButton toggleAnyoneChat = (ToggleButton) findViewById(R.id.toggleButtonOneToOneChat);
		toggleAnyoneChat.setChecked(!AppCAP.getContactsOnlyChatToggle());

		super.onResume();
	}
	
	public void onClickBack(View v) {
	    onBackPressed();
	}

	private static void pushNotificationSettings() {
		new Thread(notificationsSettingsPusher, "ActivityNotifications.notificationsSettingsPusher").start();
	}

	private static String displayTime(String time) {
		String[] parts = time.split(":");
		Time t = new Time();
		t.set(0, Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), 0, 0, 0);
		return t.format("%l:%M %P");
	}

	/**
	 * helper function to look up the "notifications from" button's text in the
	 * string resources based upon the current push_distance.
	 */
	private void resetButtonFromText() {
		final Button btnFrom = (Button) findViewById(R.id.btn_from);

		// e.g. user_menu_in_city / user_menu_in_venue / etc...
		final String resource_name = "user_menu_in_" + AppCAP.getPushDistance();

		Resources res = getResources();

		int id = res.getIdentifier(resource_name, "string", "com.coffeeandpower");
		if (0 == id) {
			throw new Resources.NotFoundException(resource_name);
		}
		// at this point id should be one of R.string.{user_menu_in_city,user_menu_in_venue,user_menu_in_contacts}

		btnFrom.setText(res.getString(id));
	}
}
