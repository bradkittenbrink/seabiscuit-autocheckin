package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.datatiming.CounterData;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class ActivityPeopleAndPlaces extends RootActivity implements TabMenu, UserMenu, Observer{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;
	
	private static final String PLACES_SCREEN_TITLE = "Venues";
	private static final String PEOPLE_SCREEN_TITLE = "People";

	private MyUsersAdapter adapterUsers;
	private MyPlacesAdapter adapterPlaces;

	private ProgressDialog progress;

	private ListView listView;

	private HorizontalPagerModified pager;

	//private DataHolder result;

	private ArrayList<UserSmart> arrayUsers;
	private ArrayList<VenueSmart> arrayVenues;

	private double userLat;
	private double userLng;
	private double data[];

	private boolean isPeopleList;

	private UserAndTabMenu menu;

	private String type;
	
	private boolean initialLoad = true;
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler taskHandler = new Handler() {

		// handleMessage - on the main thread
		@Override
		public void handleMessage(Message msg) {

			if (type.equals("people")) {
				
				arrayUsers = msg.getData().getParcelableArrayList("users");
				
				
				// Sort users list
				if (arrayUsers != null) {
					Collections.sort(arrayUsers, new Comparator<UserSmart>() {
						@Override
						public int compare(UserSmart m1, UserSmart m2) {
							if (m1.getCheckedIn() > m2.getCheckedIn()) {
								return -1;
							}
							return 1;
						}
					});
				}
				//Populate table view
				setPeopleList();
			}
			else
			{
				// pass message data along to venue update method
				arrayVenues = msg.getData().getParcelableArrayList("venues");
				setPlaceList();	
			}

			super.handleMessage(msg);
		}
	};

	{
		data = new double[6];
		// default view is People List
		isPeopleList = true;
	}

	/**
	 * Check if user is checked in or not
	 */
	private void checkUserState() {
		if (AppCAP.isUserCheckedIn()) {
			((TextView) findViewById(R.id.textview_check_in)).setText("Check Out");
			((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).setAnimation(AnimationUtils.loadAnimation(ActivityPeopleAndPlaces.this,
					R.anim.rotate_indefinitely));
		} else {
			((TextView) findViewById(R.id.textview_check_in)).setText("Check In");
			((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).clearAnimation();
		}
	}

	private void setPeopleList() {
		
		if(initialLoad)
		{
			if (Constants.debugLog)
				Log.d("ActivityPeopleAndPlaces","People List Initial Load");
			adapterUsers = new MyUsersAdapter(ActivityPeopleAndPlaces.this, arrayUsers, userLat, userLng);
			listView.setAdapter(adapterUsers);
			Utils.animateListView(listView);
			initialLoad = false;
		}
		else
		{
			adapterUsers.setNewData(arrayUsers);
			adapterUsers.notifyDataSetChanged();
		}

	}

	private void setPlaceList() {
		isPeopleList = false;
		((CustomFontView) findViewById(R.id.textview_location_name)).setText(PLACES_SCREEN_TITLE);
		
		if(initialLoad)
		{
			if (Constants.debugLog)
				Log.d("ActivityPeopleAndPlaces","Place List Initial Load");
			adapterPlaces = new MyPlacesAdapter(ActivityPeopleAndPlaces.this, arrayVenues, userLat, userLng);
			listView.setAdapter(adapterPlaces);
			Utils.animateListView(listView);
			initialLoad = false;
		}
		else
		{
			adapterPlaces.setNewData(arrayVenues);
			adapterPlaces.notifyDataSetChanged();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Constants.debugLog)
			Log.d("ActivityPeopleAndPlaces","ActivityPeopleAndPlaces.onCreate()");
		setContentView(R.layout.tab_activity_people_and_places);

		((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());

		// Default View
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		initialLoad = true;
		
		
		
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (isPeopleList) {
					if (!AppCAP.isLoggedIn()) {
						showDialog(DIALOG_MUST_BE_A_MEMBER);
					} else {
						Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityUserDetails.class);
						intent.putExtra("mapuserobject", (UserSmart) adapterUsers.getItem(position));
						intent.putExtra("from_act", "list");
						startActivity(intent);
					}
				} else {
					Intent intent = new Intent(ActivityPeopleAndPlaces.this, ActivityPlaceDetails.class);
					intent.putExtra("venueSmart", (VenueSmart) adapterPlaces.getItem(position));
					//We are sending the whole place object so we won't need the 4sqId separately
					//intent.putExtra("foursquare_id", arrayVenues.get(position).getFoursquareId());
					//I don't know what data is, but I don't think we will need
					//intent.putExtra("coords", data);
					startActivity(intent);
				}

			}
		});

		// User and Tab Menu
		checkUserState();
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {
			@Override
			public void onCheckOut() {
				checkUserState();
			}

			@Override
			public void onLogOut() {
			}
		});

		// Get data from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// Check is it People or Places List
			type = extras.getString("type");
			if (type.equals("people")) {
				((RelativeLayout) findViewById(R.id.rel_people)).setBackgroundResource(R.drawable.bg_tabbar_selected);
				((ImageView) findViewById(R.id.imageview_people)).setImageResource(R.drawable.tab_people_pressed);
				((TextView) findViewById(R.id.text_people)).setTextColor(Color.WHITE);
				((CustomFontView) findViewById(R.id.textview_location_name)).setText(PEOPLE_SCREEN_TITLE);
			} else {
				((RelativeLayout) findViewById(R.id.rel_places)).setBackgroundResource(R.drawable.bg_tabbar_selected);
				((ImageView) findViewById(R.id.imageview_places)).setImageResource(R.drawable.tab_places_pressed);
				((TextView) findViewById(R.id.text_places)).setTextColor(Color.WHITE);
				((CustomFontView) findViewById(R.id.textview_location_name)).setText(PLACES_SCREEN_TITLE);
			}

			// Check is it click from Activity or Balloon
			String from = extras.getString("from");
			if (from != null) {
				if (from.equals("from_tab")) {

					data[0] = extras.getDouble("sw_lat");
					data[1] = extras.getDouble("sw_lng");
					data[2] = extras.getDouble("ne_lat");
					data[3] = extras.getDouble("ne_lng");
					data[4] = extras.getDouble("user_lat");
					data[5] = extras.getDouble("user_lng");

					userLat = data[4];
					userLng = data[5];
				} else {

				}
			}
		}
		
		
	}   // end onCreate()
	
	@Override
	protected void onStart() {
		if (Constants.debugLog)
			Log.d("PeoplePlaces","ActivityPeopleAndPlaces.onStart()");
		super.onStart();
		initialLoad = true;
		UAirship.shared().getAnalytics().activityStarted(this);
		AppCAP.getCounter().getCachedDataForAPICall("venuesWithCheckins",this);	
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
			Log.d("PeoplePlaces","ActivityPeopleAndPlaces.onStop()");
		super.onStop();
		UAirship.shared().getAnalytics().activityStopped(this);
		AppCAP.getCounter().stoppedObservingAPICall("venuesWithCheckins",this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (AppCAP.shouldFinishActivities()) {
			onBackPressed();
		} else {
			// Get Notification settings from shared prefs
			((ToggleButton) findViewById(R.id.toggle_checked_in)).setChecked(AppCAP.getNotificationToggle());
			((Button) findViewById(R.id.btn_from)).setText(AppCAP.getNotificationFrom());

			// Check and Set Notification settings
			menu.setOnNotificationSettingsListener((ToggleButton) findViewById(R.id.toggle_checked_in),
					(Button) findViewById(R.id.btn_from), false);
		}
	}

	public void onClickMenu(View v) {
		if (pager.getCurrentScreen() == SCREEN_USER) {
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_USER, true);
		}
	}

	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClickEnterInviteCode(View v) {
		menu.onClickEnterInviteCode(v);
	}

	@Override
	public void onClickWallet(View v) {
		menu.onClickWallet(v);

	}

	@Override
	public void onClickSettings(View v) {
		menu.onClickSettings(v);

	}

	@Override
	public void onClickLogout(View v) {
		menu.onClickLogout(v);
		onBackPressed();
	}

	@Override
	public void onClickMap(View v) {
		menu.onClickMap(v);
		finish();
	}

	@Override
	public void onClickContacts(View v) {
		menu.onClickContacts(v);
		finish();
	}

	public void onClickPlaces(View v) {
		menu.onClickPlaces(v);
		finish();
	}

	public void onClickPeople(View v) {
		menu.onClickPeople(v);
		finish();
	}

	@Override
	public void onClickCheckIn(View v) {
		if (AppCAP.isLoggedIn()) {
			menu.onClickCheckIn(v);
		} else {
			showDialog(DIALOG_MUST_BE_A_MEMBER);
		}
	}
	
	@Override
	public void update(Observable observable, Object data) {
		/*
		 * verify that the data is really of type CounterData, and log the
		 * details
		 */
		if (data instanceof CounterData) {
			CounterData counterdata = (CounterData) data;
			DataHolder venuesWithCheckins = counterdata.getData();
						
			Object[] obj = (Object[]) venuesWithCheckins.getObject();
			@SuppressWarnings("unchecked")
			ArrayList<VenueSmart> arrayVenues = (ArrayList<VenueSmart>) obj[0];
			@SuppressWarnings("unchecked")
			ArrayList<UserSmart> arrayUsers = (ArrayList<UserSmart>) obj[1];
			
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", counterdata.type);
			if (type.equals("people")) {
				bundle.putParcelableArrayList("users", arrayUsers);
			} else {
				bundle.putParcelableArrayList("venues", arrayVenues);
			}
			message.setData(bundle);
			
			if (Constants.debugLog)
				Log.d("PeoplePlaces","ActivityPeopleAndPlaces.update: Sending handler message...");
			taskHandler.sendMessage(message);
			
			
		}
		else
			if (Constants.debugLog)
				Log.d("PeoplePlaces","Error: Received unexpected data type: " + data.getClass().toString());
	}
	


}
