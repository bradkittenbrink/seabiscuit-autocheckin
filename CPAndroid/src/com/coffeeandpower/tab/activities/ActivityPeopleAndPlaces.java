package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class ActivityPeopleAndPlaces extends RootActivity implements TabMenu, UserMenu{

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;

	private static final String PLACES_SCREEN_TITLE = "Venues";
	private static final String PEOPLE_SCREEN_TITLE = "People";

	private MyUsersAdapter adapterUsers;
	private MyPlacesAdapter adapterPlaces;

	private ProgressDialog progress;

	private ListView listView;

	private HorizontalPagerModified pager;

    // private DataHolder result;

	private ArrayList<UserSmart> arrayUsers;
	private ArrayList<VenueSmart> arrayVenues;

	private double userLat;
	private double userLng;
	private double data[];

	private boolean isPeopleList;

	private UserAndTabMenu menu;

	private String type;

	private boolean initialLoad = true;

	private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();
	private MyAutoCheckinTriggerObserver myAutoCheckinObserver = new MyAutoCheckinTriggerObserver();
	
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	private Handler mainThreadTaskhandler = new Handler() {

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
                // Populate table view
				setPeopleList();
            } else {
				// pass message data along to venue update method
				arrayVenues = msg.getData().getParcelableArrayList("venues");
                setPlaceList();
			}

			progress.dismiss();

			super.handleMessage(msg);
		}
	};

	{
		data = new double[6];
		// default view is People List
		isPeopleList = true;
	}


    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Constants.debugLog)
            Log.d("ActivityPeopleAndPlaces",
                    "ActivityPeopleAndPlaces.onCreate()");
		setContentView(R.layout.tab_activity_people_and_places);

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP
                .getLoggedInUserNickname());

		// Default View
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		initialLoad = true;

		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
				if (isPeopleList) {
					if (!AppCAP.isLoggedIn()) {
						showDialog(DIALOG_MUST_BE_A_MEMBER);
					} else {
                        Intent intent = new Intent(
                                ActivityPeopleAndPlaces.this,
                                ActivityUserDetails.class);
                        intent.putExtra("mapuserobject",
                                (UserSmart) adapterUsers.getItem(position));
						intent.putExtra("from_act", "list");
						startActivity(intent);
					}
				} else {
                    Intent intent = new Intent(ActivityPeopleAndPlaces.this,
                            ActivityPlaceDetails.class);
                    intent.putExtra("venueSmart",
                            (VenueSmart) adapterPlaces.getItem(position));
                    // We are sending the whole place object so we won't need
                    // the 4sqId separately
                    // intent.putExtra("foursquare_id",
                    // arrayVenues.get(position).getFoursquareId());
                    // I don't know what data is, but I don't think we will need
                    // intent.putExtra("coords", data);
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
                ((RelativeLayout) findViewById(R.id.rel_people))
                        .setBackgroundResource(R.drawable.bg_tabbar_selected);
                ((ImageView) findViewById(R.id.imageview_people))
                        .setImageResource(R.drawable.tab_people_pressed);
                ((CustomFontView) findViewById(R.id.textview_location_name))
                        .setText(PEOPLE_SCREEN_TITLE);
			} else {
                ((RelativeLayout) findViewById(R.id.rel_places))
                        .setBackgroundResource(R.drawable.bg_tabbar_selected);
                ((CustomFontView) findViewById(R.id.textview_location_name))
                        .setText(PLACES_SCREEN_TITLE);
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
		} else {
            Log.d("PeopleAndPlaces", "Extras was null!");
		}

	}   // end onCreate()

	

	/**
	 * Check if user is checked in or not
	 */
	private void checkUserState() {
		if (AppCAP.isUserCheckedIn()) {
			((TextView) findViewById(R.id.textview_check_in)).setText("Check Out");
			//((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).setAnimation(AnimationUtils.loadAnimation(ActivityPeopleAndPlaces.this,
			//		R.anim.rotate_indefinitely));
		} else {
			((TextView) findViewById(R.id.textview_check_in)).setText("Check In");
			//((ImageView) findViewById(R.id.imageview_check_in_clock_hand)).clearAnimation();
		}
	}
	
	private void setPeopleList() {

        if (initialLoad) {
            if (Constants.debugLog)
                Log.d("ActivityPeopleAndPlaces", "People List Initial Load");
            adapterUsers = new MyUsersAdapter(ActivityPeopleAndPlaces.this,
                    arrayUsers, userLat, userLng);
            listView.setAdapter(adapterUsers);
            Utils.animateListView(listView);
            initialLoad = false;
        } else {
            adapterUsers.setNewData(arrayUsers);
            adapterUsers.notifyDataSetChanged();
        }

    }

    private void setPlaceList() {
        isPeopleList = false;
        ((CustomFontView) findViewById(R.id.textview_location_name))
                .setText(PLACES_SCREEN_TITLE);

        if (initialLoad) {
            if (Constants.debugLog)
                Log.d("ActivityPeopleAndPlaces", "Place List Initial Load");
            adapterPlaces = new MyPlacesAdapter(ActivityPeopleAndPlaces.this,
                    arrayVenues, userLat, userLng);
            listView.setAdapter(adapterPlaces);
            Utils.animateListView(listView);
            initialLoad = false;
        } else {
            adapterPlaces.setNewData(arrayVenues);
            adapterPlaces.notifyDataSetChanged();
        }
    }



	
	
	@Override
	protected void onStart() {

		checkUserState();

		if (Constants.debugLog)
            Log.d("PeoplePlaces", "ActivityPeopleAndPlaces.onStart()");
		super.onStart();

        // initialLoad = true;
		UAirship.shared().getAnalytics().activityStarted(this);
		
		CacheMgrService.startObservingAPICall("venuesWithCheckins",myCachedDataObserver);
		LocationDetectionStateMachine.startObservingAutoCheckinTrigger(myAutoCheckinObserver);
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
            Log.d("PeoplePlaces", "ActivityPeopleAndPlaces.onStop()");
		super.onStop();
		UAirship.shared().getAnalytics().activityStopped(this);
		CacheMgrService.stopObservingAPICall("venuesWithCheckins",myCachedDataObserver);
		LocationDetectionStateMachine.stopObservingAutoCheckinTrigger(myAutoCheckinObserver);
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkUserState();

		if (AppCAP.shouldFinishActivities()) {
			onBackPressed();
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
	public void onClickNotifications(View v) {
		menu.onClickNotifications(v);
		
	}

	@Override
    public void onClickSettings(View v) {
        menu.onClickSettings(v);

	}

	@Override
    public void onClickSupport(View v) {
        menu.onClickSupport(v);
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
    public void onClickVenueFeeds(View v) {
        menu.onClickVenueFeeds(v);
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

	
	private class MyAutoCheckinTriggerObserver implements Observer {

		@Override
		public void update(Observable arg0, Object arg1) {

			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", "AutoCheckinTrigger");
			
			message.setData(bundle);
			
			Log.d("AutoCheckin","Received Autocheckin Observable...");
			mainThreadTaskhandler.sendMessage(message);
			
		}
		
	}
	
	private class MyCachedDataObserver implements Observer {
		
		@Override
		public void update(Observable observable, Object data) {
			/*
			 * verify that the data is really of type CounterData, and log the
			 * details
			 */
			if (data instanceof CachedDataContainer) {
				CachedDataContainer counterdata = (CachedDataContainer) data;
				DataHolder venuesWithCheckins = counterdata.getData();

				Object[] obj = (Object[]) venuesWithCheckins.getObject();
				@SuppressWarnings("unchecked")
				List<VenueSmart> arrayVenues = (List<VenueSmart>) obj[0];
				@SuppressWarnings("unchecked")
				List<UserSmart> arrayUsers = (List<UserSmart>) obj[1];

				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putCharSequence("type", counterdata.type);
				if (type.equals("people")) {
					bundle.putParcelableArrayList("users", new ArrayList<UserSmart>(arrayUsers));
				} else {
					bundle.putParcelableArrayList("venues", new ArrayList<VenueSmart>(arrayVenues));
				}
				message.setData(bundle);

				if (Constants.debugLog)
                Log.d("PeoplePlaces",
                        "ActivityPeopleAndPlaces.update: Sending handler message...");

				mainThreadTaskhandler.sendMessage(message);

        } 
		else if (Constants.debugLog)
            Log.d("PeoplePlaces", "Error: Received unexpected data type: "
                    + data.getClass().toString());

		}
	}
	
	
	

}
