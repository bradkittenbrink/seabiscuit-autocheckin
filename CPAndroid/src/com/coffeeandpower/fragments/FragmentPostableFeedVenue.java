package com.coffeeandpower.fragments;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityFeedsForOneVenue;
import com.coffeeandpower.adapters.MyFeedsAdapter;
import com.coffeeandpower.adapters.MyPostableVenuesAdapter;
import com.coffeeandpower.adapters.MyVenueFeedsAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Feed;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.inter.TabMenu;
import com.coffeeandpower.inter.UserMenu;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship; 

public class FragmentPostableFeedVenue extends Fragment implements TabMenu, UserMenu {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;

    private HorizontalPagerModified pager;

    private UserAndTabMenu menu;

    private ListView listView;
    private ProgressDialog progress;
 
    private Executor exe;
    protected DataHolder result;
    private MyPostableVenuesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = null;
         mainView = inflater.inflate(R.layout.tab_fragment_postable_feed_venue, null);   
        progress = new ProgressDialog(this.getActivity());
        if (AppCAP.isLoggedIn()) {
            progress.setMessage("Loading...");
        } else {
            progress.setMessage("You must login to see the feeds ...");
        }
        progress.show();
        return mainView;        

    }

        @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
        ActivityVenueFeeds act = (ActivityVenueFeeds) getActivity();
        act.setFragmentName("FragmentPostableFeedVenue");
        act.updateMenuOnFragmentchange();
        if (AppCAP.isLoggedIn()) {
            listView = (ListView) getView().findViewById(R.id.venue_feeds_listview);
            // Executor
            exe = new Executor(getActivity());
            exe.setExecutorListener(new ExecutorInterface() {
                @Override
                public void onErrorReceived() {
                }

                @Override
                public void onActionFinished(int action) {
                    result = exe.getResult();

                    switch (action) {
                    case Executor.HANDLE_GET_POSTABLE_VENUES: 
                        if (result != null && result.getObject() != null
                                && (result.getObject() instanceof ArrayList<?>)) {
                            ArrayList<VenueNameAndFeeds> VenueNameArray = (ArrayList<VenueNameAndFeeds>) result.getObject(); 

                            populateList(VenueNameArray);
                        }
                        break;
                    }
                }
            });

        }
    }
        private void populateList(ArrayList<VenueNameAndFeeds> VenueNameArray) {
            adapter = new MyPostableVenuesAdapter(this, VenueNameArray);            
            listView.setAdapter(adapter);
            progress.dismiss();
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
    public void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "ActivityContacts.onStart()");
        super.onStart();

    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "ActivityvenueFeeds.onStop()");
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppCAP.shouldFinishActivities()) {
            if (!AppCAP.isLoggedIn()) {
                progress.dismiss();
            } else {
                exe.postableVenues();
            }
        }
    }

   
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        getActivity().finish();
    }
    
    @Override
    public boolean onClickVenueFeeds(View v) {
        if (menu.onClickVenueFeeds(v)) {
            getActivity().finish();
        }
        return false;
    }
    
    @Override
    public void onClickNotifications(View v) {
        menu.onClickNotifications(v);
        
    }

    @Override
    public void onClickPlaces(View v) {
        menu.onClickPlaces(v);
        getActivity().finish();
    }

    @Override
    public void onClickCheckIn(View v) {
        if (AppCAP.isLoggedIn()) {
            menu.onClickCheckIn(v);
        } else {
            getActivity().showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
        }
    }

    @Override
    public void onClickCheckOut(View v) {
        if (AppCAP.isLoggedIn()) {
            menu.onClickCheckOut(v);
        } else {
            getActivity().showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
        }
    }

    @Override
    public void onClickPeople(View v) {
        menu.onClickPeople(v);
        getActivity().finish();
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
    

    public void openVenue(VenueNameAndFeeds venueNameAndFeeds) {
        int venueId = venueNameAndFeeds.getVenueId();
        if (venueId != 0) {
            String venue_name = venueNameAndFeeds.getName();
            Intent intent = new Intent(getActivity().getApplicationContext(),
                ActivityFeedsForOneVenue.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", venue_name);
            intent.putExtra("caller", "feeds_list");
            startActivity(intent);
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
        menu.onClickFeed(v);
    }


}
