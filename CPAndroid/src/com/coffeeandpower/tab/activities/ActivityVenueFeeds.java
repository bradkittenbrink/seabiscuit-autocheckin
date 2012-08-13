package com.coffeeandpower.tab.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityEnterInviteCode;
import com.coffeeandpower.activity.ActivityFeedsForOneVenue;
import com.coffeeandpower.adapters.MyVenueFeedsAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.fragments.FragmentPostableFeedVenue;
import com.coffeeandpower.fragments.FragmentVenueFeeds;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.coffeeandpower.views.CustomDialog.ClickListener;
import com.urbanairship.UAirship;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;

public class ActivityVenueFeeds extends FragmentActivity   implements   TabMenu, UserMenu {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;
    private static final int TAB_CHECKIN_MOVE_DISTANCE = 230;
    private static final int TAB_CHECKIN_MOVE_DURATION = 800;

    public static final int DIALOG_MUST_BE_A_MEMBER = 30;

    private HorizontalPagerModified pager;

    private MyVenueFeedsAdapter adapterFeeds;

    private UserAndTabMenu menu;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<VenueNameAndFeeds> arrayFeeds;

    private boolean initialLoad = true;

    private MyAutoCheckinTriggerObserver myAutoCheckinObserver = new MyAutoCheckinTriggerObserver();
    
    // Scheduler - create a custom message handler for use in passing venue data from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            String type = msg.getData().getString("type");
            
            if (type.equalsIgnoreCase("AutoCheckinTrigger")) {
                // Update view
                
            }
            super.handleMessage(msg);
        }
    };
    private String  fragmentName = "FragmentVenueFeeds";
    
    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_venue_feeds);
        ((RelativeLayout) findViewById(R.id.rel_feed)).setBackgroundResource(R.drawable.bg_tabbar_selected);
        ((ImageView) findViewById(R.id.imageview_feed)).setImageResource(R.drawable.tab_feed_pressed);

        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());
        
        // Get userId form intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragmentName  = bundle.getString("fragment");
            if (fragmentName == null) {
                fragmentName = "FragmentVenueFeeds";
            }
        }
        
        // Horizontal Pager
        pager = (HorizontalPagerModified) findViewById(R.id.pager);
        pager.setCurrentScreen(SCREEN_USER, false);
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
            }
        });
    }
    
    public void displayFragment(String fragmentName) {
        Fragment newFragment;
        this.fragmentName = fragmentName;
        updateMenuOnFragmentchange();
        // Create new fragment and transaction
        if (fragmentName.contentEquals("FragmentPostableFeedVenue")) {
            newFragment = new FragmentPostableFeedVenue();
        } else {
            CacheMgrService.resetVenueFeedsData(true);
            newFragment = new FragmentVenueFeeds();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        if (manager.findFragmentById(R.id.tab_fragment_area) == null) {
            transaction.add(R.id.tab_fragment_area, newFragment);
        } else {
            transaction.replace(R.id.tab_fragment_area, newFragment);
            transaction.addToBackStack(null);
        }

        // Commit the transaction
        transaction.commit(); 
        
    }
    
    public void updateMenuOnFragmentchange() {
        Fragment newFragment;
        // Create new fragment and transaction
        if (fragmentName.contentEquals("FragmentPostableFeedVenue")) {
            if (findViewById(R.id.textview_contact_list) != null) {
                ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(
                        getResources().getString(R.string.message_choose_feed));
            }
            newFragment = new FragmentPostableFeedVenue();
        } else {
            if (findViewById(R.id.textview_contact_list) != null) {
                ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(
                        getResources().getString(R.string.message_active_feeds));
            }
            newFragment = new FragmentVenueFeeds();
        }
    }
    private OnBackStackChangedListener getListener()
    {
        OnBackStackChangedListener result = new OnBackStackChangedListener()
        {
            public void onBackStackChanged() 
            {                   
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null)
                {
                    Fragment currFrag = (Fragment)manager.findFragmentById(R.id.tab_fragment_area);
                    if (currFrag != null) {
                        updateMenuOnFragmentchange();

                    }
                }                   
            }
        };

        return result;
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

        // If the user isn't logged in then we will displaying the login screen
        // not the list of contacts.
        if (AppCAP.isLoggedIn()) {
            UAirship.shared().getAnalytics().activityStarted(this);
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
            LocationDetectionStateMachine.stopObservingAutoCheckinTrigger(myAutoCheckinObserver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppCAP.shouldFinishActivities()) {
            onBackPressed();
        } else {
            if (AppCAP.shouldShowInfoDialog()
                    && AppCAP.getEnteredInviteCode() == false) {
                CustomDialog cd = new CustomDialog(
                        ActivityVenueFeeds.this,
                        "Coffee & Power requires an invite for full membership but you have 30 days of full access to try us out.",
                        "If you get an invite from another C&P user you can enter it anytime by going to the Account page/Enter invite code tab.");
                cd.setOnClickListener(new ClickListener() {
                    @Override
                    public void onClick() {
                        AppCAP.dontShowInfoDialog();
                    }
                });
                cd.show();
            }
            this.displayFragment(fragmentName);

            getSupportFragmentManager().addOnBackStackChangedListener(getListener());
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
    public boolean onClickVenueFeeds(View v) {
        if (menu.onClickVenueFeeds(v)) {
            finish();
        }
        return initialLoad;
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
    public void onClickCheckOut(View v) {
        if (AppCAP.isLoggedIn()) {
            menu.onClickCheckOut(v);
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
        menu.onClickContacts(v);
        finish();
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
 
    public void openVenue(VenueNameAndFeeds venueNameAndFeeds) {
        int venueId = venueNameAndFeeds.getVenueId();
        if (venueId != 0) {
            String venue_name = venueNameAndFeeds.getName();
            Intent intent = new Intent(ActivityVenueFeeds.this,
                ActivityFeedsForOneVenue.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", venue_name);
            intent.putExtra("caller", "feeds_list");
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

    @Override
    public void onClickMinus(View v) {
        menu.onClickMinus(v);
    }

    @Override
    public void onClickPlus(View v) {
        menu.onClickPlus(v);
    }

    @Override
    public void onClickFeed(View v) { 
        menu.onClickFeed(v, this);
    }


}
