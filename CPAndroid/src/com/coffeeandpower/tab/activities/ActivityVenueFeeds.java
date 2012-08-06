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
import com.coffeeandpower.activity.ActivityFeedsForOneVenue;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.adapters.MyVenueFeedsAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueNameAndFeeds;
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

public class ActivityVenueFeeds extends RootActivity implements TabMenu, UserMenu {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;

    private HorizontalPagerModified pager;

    private MyVenueFeedsAdapter adapterFeeds;

    private UserAndTabMenu menu;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<VenueNameAndFeeds> arrayFeeds;

    private boolean initialLoad = true;

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
                ArrayList<VenueNameAndFeeds> feedsArray = msg.getData()
                    .getParcelableArrayList("venueFeeds"); 
                updateVenueFeedsFromApiResult(feedsArray);

                progress.dismiss();
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_venue_feeds);

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());

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
            ((RelativeLayout) findViewById(R.id.rel_contacts)).setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_feed)).setImageResource(R.drawable.tab_feed_pressed);

            // Display the list of users if the user is logged in
            listView = (ListView) findViewById(R.id.venue_feeds_listview);


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
            CacheMgrService.startObservingAPICall("venueFeedsList",myCachedDataObserver);
            LocationDetectionStateMachine.startObservingAutoCheckinTrigger(myAutoCheckinObserver);
        }
    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "ActivityvenueFeeds.onStop()");
        super.onStop();
        if (AppCAP.isLoggedIn()) {
            UAirship.shared().getAnalytics().activityStopped(this);
            CacheMgrService.stopObservingAPICall("venueFeeds",myCachedDataObserver);
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
    }

    public void onClickOpenVenueFeeds(View v) {
        VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) v.getTag(R.id.venue_name_and_feeds);
        if (venueNameAndFeeds != null) {
            openVenue(venueNameAndFeeds);     
        }
    }

    public void onClickRemove(View v) {
        AppCAP.removeUserLastCheckinVenue(((VenueNameAndFeeds) v.getTag()).getVenueId());
        refresh();
    }
    
    public void refresh() {
    
        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
    }
    
    private void updateVenueFeedsFromApiResult(ArrayList<VenueNameAndFeeds> newFeedsArray) {
        if (Constants.debugLog)
            Log.d("venueFeeds", "updateVenueFeedsFromApiResult()");

        // Populate table view
        this.arrayFeeds = newFeedsArray;

        if (initialLoad) {
            adapterFeeds = new MyVenueFeedsAdapter(ActivityVenueFeeds.this,
                    this.arrayFeeds);
            listView.setAdapter(adapterFeeds);
            // TODO Need to add listview listener here
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long arg3) {
                        if (!AppCAP.isLoggedIn()) {
                            showDialog(DIALOG_MUST_BE_A_MEMBER);
                        } else {
                            VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) adapterFeeds.getItem(position);
                            openVenue(venueNameAndFeeds);                    
                        }
                }
            });
            Utils.animateListView(listView);
            initialLoad = false;
        } else {
            adapterFeeds.setNewData(arrayFeeds);
            adapterFeeds.notifyDataSetChanged();
        }

        if (Constants.debugLog)
            Log.d("VenueFeeds", "Set local array with " + newFeedsArray.size()
                    + " VenueFeeds.");
    }
    
    public void openVenue(VenueNameAndFeeds venueNameAndFeeds) {
        int venueId = venueNameAndFeeds.getVenueId();
        if (venueId != 0) {
            String venue_name = venueNameAndFeeds.getName();
            Intent intent = new Intent(ActivityVenueFeeds.this,
                ActivityFeedsForOneVenue.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", venue_name);
            startActivity(intent);
        }
    }
    
    private class MyAutoCheckinTriggerObserver implements Observer {

        @Override
        public void update(Observable arg0, Object arg1) {

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("type", "AutoCheckinTrigger");
            
            message.setData(bundle);
            
            Log.d("AutoCheckin", "Received Autocheckin Observable...");
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
            
            DataHolder feeds = counterdata.getData();
            //Object[] obj = (Object[]) contacts.getObject();
            @SuppressWarnings("unchecked")
            List<Feed> arrayFeeds = (List<Feed>) feeds.getObject();
            
            if (Constants.debugLog)
                Log.d("Contacts", "Warning: API callback temporarily disabled...");
            
            ArrayList<Feed> mutableArrayContacts = new ArrayList<Feed>(arrayFeeds);
                
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("type", counterdata.type);
            bundle.putParcelableArrayList("venueFeeds", mutableArrayContacts);
            message.setData(bundle);
            
            if (Constants.debugLog)
                Log.d("venueFeeds", "venueFeeds.update: Sending handler message with " + mutableArrayContacts.size() + " venueFeeds:");
            
            
            
            mainThreadTaskHandler.sendMessage(message);            
        }
        else
            if (Constants.debugLog)
                Log.d("venueFeeds", "Error: Received unexpected data type: " + data.getClass().toString());
        }
    }
    
    

}
