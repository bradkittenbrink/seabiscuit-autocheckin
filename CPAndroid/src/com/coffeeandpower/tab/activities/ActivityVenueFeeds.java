package com.coffeeandpower.tab.activities;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.fragments.FragmentContacts;
import com.coffeeandpower.fragments.FragmentMap;
import com.coffeeandpower.fragments.FragmentPeopleAndPlaces;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class ActivityVenueFeeds extends RootActivity   implements   TabMenu, UserMenu {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;

    public static final int DIALOG_MUST_BE_A_MEMBER = 30;

    private HorizontalPagerModified pager;

    private UserAndTabMenu menu;

    private boolean initialLoad = true;
    private Bundle intentExtras;

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
    private int  fragment_id = R.id.tab_fragment_area_map;
    private DataHolder result; 
    
    public int getFragmentId() {
        return fragment_id;
    }
    
    public void setFragmentId(int fragment_id) {
        this.fragment_id = fragment_id;
    }
    


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_venue_feeds);


        ((CustomFontView) findViewById(R.id.text_nick_name)).setText(AppCAP.getLoggedInUserNickname());
        
        // Get userId form intent
        intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            int new_fragment_id  = intentExtras.getInt("fragment");
            if (new_fragment_id > 0) {
                fragment_id = new_fragment_id;
            }
        } else {
            intentExtras = new Bundle();
            intentExtras.putInt("fragment", fragment_id);
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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        AppCAP.mainActivityDidStart(this);
    }
    
    public void displayFragment(int fragment_id) {
        Fragment newFragment;
        int hide_fragment_id = 0;
        if (this.fragment_id != fragment_id) {
            hide_fragment_id = this.fragment_id;
        }
        updateMenuOnFragmentchange(fragment_id);
        this.fragment_id = fragment_id;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack 
        Fragment fragment = manager.findFragmentById(fragment_id);
        if (fragment == null) {
            // Create new fragment and transaction
          if (fragment_id == R.id.tab_fragment_area_contacts) { 
              newFragment = new FragmentContacts(this.intentExtras);
          } else if (fragment_id == R.id.tab_fragment_area_map) { 
              newFragment = new FragmentMap(this.intentExtras);
          } else if (fragment_id == R.id.tab_fragment_area_people) {
              if (intentExtras.getInt("fragment") == 0) {
                  intentExtras.putInt("fragment", R.id.tab_fragment_area_people);
              }
              newFragment = new FragmentPeopleAndPlaces(this.intentExtras);
          } else if (fragment_id == R.id.tab_fragment_area_places) {
              if (intentExtras.getInt("fragment") == 0) {
                  intentExtras.putInt("fragment", R.id.tab_fragment_area_places);
              }
              newFragment = new FragmentPeopleAndPlaces(this.intentExtras);
          } else {
              newFragment = new FragmentMap(this.intentExtras);
          }
          transaction.add(fragment_id, newFragment);
        } else {
            transaction.show(fragment);
        }
        if (hide_fragment_id != 0) {
            Fragment hide_fragment = manager.findFragmentById(hide_fragment_id);
            if (hide_fragment != null) {
                transaction.hide(hide_fragment);
            }
        }
        // Commit the transaction
        transaction.commit(); 
        
    }
    
    public void switchTabBackground(int onRelativeLayout) {
        if (onRelativeLayout == R.id.rel_people) {
            ((Button) findViewById(R.id.btn_top_map)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_top_list)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_top_cancel)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.rel_people)).setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_people)).setImageResource(R.drawable.tab_people_pressed);
        } else {
            ((RelativeLayout) findViewById(R.id.rel_people)).setBackgroundResource(R.drawable.bg_tabbar_press);
            ((ImageView) findViewById(R.id.imageview_people)).setImageResource(R.drawable.tab_people_a);
        }
        if (onRelativeLayout == R.id.rel_places) {
            ((Button) findViewById(R.id.btn_top_map)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.btn_top_list)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_top_cancel)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.rel_places)).setBackgroundResource(R.drawable.bg_tabbar_selected);
            ((ImageView) findViewById(R.id.imageview_places)).setImageResource(R.drawable.tab_places_pressed);
        } else {
            if (onRelativeLayout == R.id.rel_map) {
                ((Button) findViewById(R.id.btn_top_map)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btn_top_list)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.btn_top_cancel)).setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.rel_places)).setBackgroundResource(R.drawable.bg_tabbar_selected);
                ((ImageView) findViewById(R.id.imageview_places)).setImageResource(R.drawable.tab_places_pressed);
            } else {
                ((Button) findViewById(R.id.btn_top_map)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btn_top_list)).setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.rel_places)).setBackgroundResource(R.drawable.bg_tabbar_press);
                ((ImageView) findViewById(R.id.imageview_places)).setImageResource(R.drawable.tab_places_a);
            }
        }
        if (onRelativeLayout == R.id.rel_contacts) {
            ((Button) findViewById(R.id.btn_top_map)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_top_list)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_top_cancel)).setVisibility(View.GONE);
            if (AppCAP.isLoggedIn()) {
                ((RelativeLayout) findViewById(R.id.rel_contacts)).setBackgroundResource(R.drawable.bg_tabbar_selected);
                ((ImageView) findViewById(R.id.imageview_contacts)).setImageResource(R.drawable.tab_contacts_pressed);
            } else {
                ((RelativeLayout) findViewById(R.id.rel_log_in)).setBackgroundResource(R.drawable.bg_tabbar_selected);
                ((ImageView) findViewById(R.id.imageview_log_in)).setImageResource(R.drawable.tab_login_pressed);
            }
        } else {
            RelativeLayout r = (RelativeLayout) findViewById(R.id.rel_log_in);
            RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rel_contacts);

            if (AppCAP.isLoggedIn()) {
                ((RelativeLayout) findViewById(R.id.rel_contacts)).setBackgroundResource(R.drawable.bg_tabbar_press);
                ((ImageView) findViewById(R.id.imageview_contacts)).setImageResource(R.drawable.tab_contacts_a);
                if (r != null) {
                    r.setVisibility(View.GONE);
                }
                if (r1 != null) {
                    r1.setVisibility(View.VISIBLE);
                }
            } else {
                ((RelativeLayout) findViewById(R.id.rel_log_in)).setBackgroundResource(R.drawable.bg_tabbar_press);
                ((ImageView) findViewById(R.id.imageview_log_in)).setImageResource(R.drawable.tab_login_a);
                if (r != null) {
                    r.setVisibility(View.VISIBLE);
                }
                if (r1 != null) {
                    r1.setVisibility(View.GONE);
                }
            }
        }
    }
    
    public void showFullActionBar() {
        ImageView plus = (ImageView) findViewById(R.id.imageview_button_plus);
        ImageView minus = (ImageView) findViewById(R.id.imageview_button_minus);

        ImageView imageview_button_question = (ImageView) findViewById(R.id.imageview_button_question_no_action);
        ImageView imageview_button_update = (ImageView) findViewById(R.id.imageview_button_update_no_action);

        RelativeLayout rel_map = (RelativeLayout) findViewById(R.id.rel_map);
        RelativeLayout rel_places = (RelativeLayout) findViewById(R.id.rel_places);
        RelativeLayout rel_people = (RelativeLayout) findViewById(R.id.rel_people);
        RelativeLayout rel_contacts = (RelativeLayout) findViewById(R.id.rel_contacts);
        
        minus.setVisibility(View.GONE);
        plus.setVisibility(View.VISIBLE);
        imageview_button_question.setVisibility(View.GONE);
        imageview_button_update.setVisibility(View.GONE);
        rel_map.setVisibility(View.VISIBLE);
        rel_places.setVisibility(View.VISIBLE);
        rel_people.setVisibility(View.VISIBLE);
        rel_contacts.setVisibility(View.VISIBLE);

    }
    
    public void updateMenuOnFragmentchange(int next_fragment_id) {
        ((RelativeLayout) findViewById(next_fragment_id)).setVisibility(View.VISIBLE);
        ((CustomFontView) findViewById(R.id.textview_contact_list)).setVisibility(View.VISIBLE);
        if (fragment_id != 0 && fragment_id != next_fragment_id) {
            ((RelativeLayout) findViewById(fragment_id)).setVisibility(View.GONE);
        }
        
        // Create new fragment and transaction
        if (next_fragment_id == R.id.tab_fragment_area_people) {
            switchTabBackground(R.id.rel_people);
            ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(getResStr(R.string.people_screen_title));
        } else if (next_fragment_id == R.id.tab_fragment_area_places) {
            switchTabBackground(R.id.rel_places);
            ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(getResStr(R.string.places_screen_title));
        } else if (next_fragment_id == R.id.tab_fragment_area_map) {
            switchTabBackground(R.id.rel_map);
            ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(getResStr(R.string.map_screen_title));
        } else if (next_fragment_id == R.id.tab_fragment_area_contacts) {
            switchTabBackground(R.id.rel_contacts);
            if (AppCAP.isLoggedIn()) {
                ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(getResStr(R.string.contacts_screen_title));
            } else {
                ((CustomFontView) findViewById(R.id.textview_contact_list)).setVisibility(View.GONE);
            }
        } else {
            switchTabBackground(R.id.rel_map);
            ((CustomFontView) findViewById(R.id.textview_contact_list)).setText(getResStr(R.string.map_screen_title));
            
        }
    }

    public void onClickRefresh(View v) {
        FragmentManager manager = getSupportFragmentManager();

        if (manager != null)
        {
            FragmentMap currFrag = (FragmentMap)manager.findFragmentById(R.id.tab_fragment_area_map);
            if (currFrag != null) {        
                currFrag.onClickRefresh(v);
            }
        }
    }
    public void onClickLocateMe(View v) {
        FragmentManager manager = getSupportFragmentManager();

        if (manager != null) {
            FragmentMap currFrag = (FragmentMap)manager.findFragmentById(R.id.tab_fragment_area_map);
            if (currFrag != null) {        
                currFrag.onClickLocateMe(v);
            }
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
/*                    
                    Fragment currFrag = (Fragment)manager.findFragmentById(R.id.tab_fragment_area);
                    if (currFrag != null) {
                    updateMenuOnFragmentchange(fragment_id);
                    }*/
                }                 
            }
        };

        return result;
    }
    public void onClickLinkedIn(View v) {
        AppCAP.setShouldFinishActivities(false);
        AppCAP.setStartLoginPageFromContacts(true);
        this.finish();
        Intent i = new Intent();
        i.setClass(getApplicationContext(), ActivityLoginPage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
            Log.d("Contacts", "ActivityVenueFeeds.onStart()");
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
    protected void onSaveInstanceState(Bundle outState) {
        // Job 18344: No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppCAP.shouldFinishActivities()) {
            onBackPressed();
        } else {
            this.displayFragment(fragment_id);

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
        Intent i = new Intent();
        i.setClass(getApplicationContext(), ActivityLoginPage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onClickMap(View v) {
        displayFragment(R.id.tab_fragment_area_map);
    }
    
    
    @Override
    public void onClickNotifications(View v) {
        menu.onClickNotifications(v);
        
    }

    @Override
    public void onClickPlaces(View v) {
        menu.onClickPlaces(v);
    }

    @Override
    public void onClickMapFromTab(View v) {
        menu.onClickMapFromTab(v);
    }


    public void changeIntentCaller(String caller) {
        intentExtras.putString("caller", caller);
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
    public void onClickCheckOut(View v, Activity finishActivity) {
        if (AppCAP.isLoggedIn()) {
            menu.onClickCheckOut(v, finishActivity);
        } else {
            showDialog(DIALOG_MUST_BE_A_MEMBER);
        }
    }

    @Override
    public void onClickPeople(View v) {
        menu.onClickPeople(v);
//        finish();
    }

    @Override
    public void onClickContacts(View v) {
        menu.onClickContacts(v);
//        finish();
    }

    public void onClickRemove(View v) {
        AppCAP.removeUserLastCheckinVenue(((VenueNameAndFeeds) v.getTag()).getVenueId());
        refresh();
    }
    
    public void refresh() {
    
        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
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
    public boolean startSmartActivity(Intent intent, String activityName) {
        if (activityName == "ActivityMap") {
            this.intentExtras = intent.getExtras();
            displayFragment(R.id.tab_fragment_area_map);
            return true;
        } else if (activityName == "ActivityContacts") {
            this.intentExtras = intent.getExtras();
            displayFragment(R.id.tab_fragment_area_contacts);
            return true;
        } else if (activityName == "ActivityPeopleAndPlaces") {
            this.intentExtras = intent.getExtras();
            String type = intent.getStringExtra("type");
            if (type.equals("people")) {
                displayFragment(R.id.tab_fragment_area_people);
            } else {
                displayFragment(R.id.tab_fragment_area_places);
            }
            return true;
        } else {
            super.startSmartActivity(intent, activityName);
        }
        return false;
    }



}
