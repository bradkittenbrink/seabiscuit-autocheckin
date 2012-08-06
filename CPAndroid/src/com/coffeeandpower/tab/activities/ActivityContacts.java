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
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class ActivityContacts extends RootActivity implements TabMenu, UserMenu {

	private static final int SCREEN_SETTINGS = 0;
	private static final int SCREEN_USER = 1;

	private HorizontalPagerModified pager;

	private MyUsersAdapter adapterUsers;

	private UserAndTabMenu menu;

    // private Executor exe;

	private ListView listView;
	private ProgressDialog progress;

	private ArrayList<UserSmart> arrayUsers;

    // private DataHolder result;

	private boolean initialLoad = true;

	private ImageView blankSlateImg;

	private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();
	private MyAutoCheckinTriggerObserver myAutoCheckinObserver = new MyAutoCheckinTriggerObserver();
	
	// Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
	protected Handler mainThreadTaskHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			String type = msg.getData().getString("type");
			
			if (type.equalsIgnoreCase("AutoCheckinTrigger")) {
				// Update view
				setupTabBar();
			} else {
        			// pass message data along to venue update method
            ArrayList<UserSmart> usersArray = msg.getData()
                    .getParcelableArrayList("contacts");
        			updateUsersAndCheckinsFromApiResult(usersArray);

        			progress.dismiss();
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_activity_contacts);

		// Executor
		/*
         * exe = new Executor(ActivityContacts.this);
         * exe.setExecutorListener(new ExecutorInterface() {
         * 
         * @Override public void onErrorReceived() { errorReceived(); }
         * 
         * @Override public void onActionFinished(int action) {
         * actionFinished(action); } });
         */

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP
                .getLoggedInUserNickname());

		// Horizontal Pager
		pager = (HorizontalPagerModified) findViewById(R.id.pager);
		pager.setCurrentScreen(SCREEN_USER, false);

		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();

		// User and Tab Menu
		menu = new UserAndTabMenu(this);
		menu.setOnUserStateChanged(new OnUserStateChanged() {

			@Override
			public void onLogOut() {
				if (Constants.debugLog)
                    Log.d("Contacts", "onLogOut()");

			}

			@Override
			public void onCheckOut() {
				if (Constants.debugLog)
                    Log.d("Contacts", "onCheckOut()");
				setupTabBar();
			}
		});

		if (AppCAP.isLoggedIn()) {
            ((RelativeLayout) findViewById(R.id.rel_contacts))
                    .setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_contacts))
                    .setImageResource(R.drawable.tab_contacts_pressed);

			// Get contacts list
            // FIXME
            // We are eliminating all .exe's
            // exe.getContactsList();

            // setupTabBar();

            // Display the list of users if the user is logged in
			listView = (ListView) findViewById(R.id.contacts_listview);
            // TODO Need to add listview listener here
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long arg3) {
        				if (!AppCAP.isLoggedIn()) {
        					showDialog(DIALOG_MUST_BE_A_MEMBER);
        				} else {
                        Intent intent = new Intent(ActivityContacts.this,
                                ActivityUserDetails.class);
                        intent.putExtra("mapuserobject",
                                (UserSmart) adapterUsers.getItem(position));
        					intent.putExtra("from_act", "list");
        					startActivity(intent);
        				}
				}
			});

			blankSlateImg = (ImageView) findViewById(R.id.contacts_blank_slate_img);

		} else {
			setContentView(R.layout.tab_activity_login);
            ((RelativeLayout) findViewById(R.id.rel_log_in))
                    .setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_log_in))
                    .setImageResource(R.drawable.tab_login_pressed);

			RelativeLayout r = (RelativeLayout) findViewById(R.id.rel_log_in);
			RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rel_contacts);

			if (r != null) {
				r.setVisibility(View.VISIBLE);
			}
			if (r1 != null) {
				r1.setVisibility(View.GONE);
			}

		}

    }

	private void setupTabBar() {
		if (AppCAP.isUserCheckedIn()) {
            ((TextView) findViewById(R.id.textview_check_in))
                    .setText("Check Out");
		} else {
            ((TextView) findViewById(R.id.textview_check_in))
                    .setText("Check In");
		}
	}

	public void onClickLinkedIn(View v) {
		AppCAP.setShouldFinishActivities(true);
		AppCAP.setStartLoginPageFromContacts(true);
		onBackPressed();
	}

	public void onClickMenu(View v) {
		if (pager.getCurrentScreen() == SCREEN_USER) {
			pager.setCurrentScreen(SCREEN_SETTINGS, true);
		} else {
			pager.setCurrentScreen(SCREEN_USER, true);
		}
	}

	@Override
	protected void onStart() {
		if (Constants.debugLog)
            Log.d("Contacts", "ActivityContacts.onStart()");
		super.onStart();

		setupTabBar();

        // If the user isn't logged in then we will displaying the login screen
        // not the list of contacts.
        if (AppCAP.isLoggedIn()) {
			UAirship.shared().getAnalytics().activityStarted(this);
			CacheMgrService.startObservingAPICall("contactsList",myCachedDataObserver);
			LocationDetectionStateMachine.startObservingAutoCheckinTrigger(myAutoCheckinObserver);
		}
	}

	@Override
	public void onStop() {
		if (Constants.debugLog)
            Log.d("Contacts", "ActivityContacts.onStop()");
		super.onStop();
        if (AppCAP.isLoggedIn()) {
			UAirship.shared().getAnalytics().activityStopped(this);
			CacheMgrService.stopObservingAPICall("contactsList",myCachedDataObserver);
			LocationDetectionStateMachine.stopObservingAutoCheckinTrigger(myAutoCheckinObserver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (AppCAP.shouldFinishActivities()) {
			onBackPressed();
		} else {
			if (!AppCAP.isLoggedIn()) {
				progress.dismiss();
			}
		}
	}

	/*
     * private void errorReceived() {
     * 
     * }
     * 
     * 
     * private void actionFinished(int action) { result = exe.getResult();
     * 
     * switch (action) {
     * 
     * case 0:
     * 
     * break; } }
     */

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
	public void onClickNotifications(View v) {
		menu.onClickNotifications(v);
		
	}

	@Override
	public void onClickPlaces(View v) {
		menu.onClickPlaces(v);
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
	public void onClickPeople(View v) {
		menu.onClickPeople(v);
		finish();
	}

	@Override
	public void onClickContacts(View v) {
		// menu.onClickContacts(v);
	}
	
	private void updateUsersAndCheckinsFromApiResult(ArrayList<UserSmart> newUsersArray) {
        if (Constants.debugLog)
            Log.d("Contacts","updateUsersAndCheckinsFromApiResult()");

		// Sort users list
		if (newUsersArray != null) {
			Collections.sort(newUsersArray, new Comparator<UserSmart>() {
				@Override
				public int compare(UserSmart m1, UserSmart m2) {
                    // if (m1.getCheckedIn() > m2.getCheckedIn()) {
					//	return -1;
                    // }
                    return m1.getNickName().compareToIgnoreCase(
                            m2.getNickName());
                    // return 1;
				}
			});
		}

		if (newUsersArray.size() == 0) {
			blankSlateImg.setVisibility(View.VISIBLE);
		} else {
			blankSlateImg.setVisibility(View.INVISIBLE);
		}

        // Populate table view
		this.arrayUsers = newUsersArray;

        if (initialLoad) {
			if (Constants.debugLog)
                Log.d("ActivityContacts", "Contacts List Initial Load");
            adapterUsers = new MyUsersAdapter(ActivityContacts.this,
                    this.arrayUsers);
			listView.setAdapter(adapterUsers);
			Utils.animateListView(listView);
			initialLoad = false;
        } else {
			adapterUsers.setNewData(arrayUsers);
			adapterUsers.notifyDataSetChanged();
		}

		if (Constants.debugLog)
            Log.d("Contacts", "Set local array with " + newUsersArray.size()
                    + " contacts.");
	}
	
	private class MyAutoCheckinTriggerObserver implements Observer {

		@Override
		public void update(Observable arg0, Object arg1) {

			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", "AutoCheckinTrigger");
			
			message.setData(bundle);
			
			Log.d("AutoCheckin","Received Autocheckin Observable...");
			mainThreadTaskHandler.sendMessage(message);
			
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
			
			DataHolder contacts = counterdata.getData();
			//Object[] obj = (Object[]) contacts.getObject();
			@SuppressWarnings("unchecked")
			List<UserSmart> arrayContacts = (List<UserSmart>) contacts.getObject();
			
			if (Constants.debugLog)
				Log.d("Contacts","Warning: API callback temporarily disabled...");
			
			// Remove self from user array
			UserSmart selfUser = null;
			ArrayList<UserSmart> mutableArrayContacts = new ArrayList<UserSmart>(arrayContacts);
			for (UserSmart aUser:mutableArrayContacts) {
				
				if (AppCAP.getLoggedInUserId() == aUser.getUserId()) {
					if (Constants.debugLog)
						Log.d("Contacts"," - Removing self from users array: " + aUser.getNickName());
					selfUser = aUser;
				}
			}
			if (selfUser != null) {
				mutableArrayContacts.remove(selfUser);
			}
				
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("type", counterdata.type);
			bundle.putParcelableArrayList("contacts", mutableArrayContacts);
			message.setData(bundle);
			
			if (Constants.debugLog)
				Log.d("Contacts","Contacts.update: Sending handler message with " + mutableArrayContacts.size() + " contacts:");
			
			
			
			mainThreadTaskHandler.sendMessage(message);			
		}
		else
			if (Constants.debugLog)
				Log.d("Contacts","Error: Received unexpected data type: " + data.getClass().toString());
		}
	}
	
	

}
