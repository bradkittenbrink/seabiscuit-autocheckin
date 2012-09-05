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

public class FragmentPostableFeedVenue extends Fragment {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;

    private HorizontalPagerModified pager;

    private UserAndTabMenu menu;

    private ListView listView;
    private ProgressDialog progress;
 
    private Executor exe;
    protected DataHolder result;
    private MyPostableVenuesAdapter adapter;
    private Bundle intentExtras;

    public FragmentPostableFeedVenue(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }

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
        act.updateMenuOnFragmentchange(R.id.tab_fragment_area_postable_feed_venue);
        act.setFragmentId(R.id.tab_fragment_area_postable_feed_venue); 
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
            Log.d("Contacts", "FragmentPostableFeedVenue.onStart()");
        super.onStart();

    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "FragmentPostableFeedVenue.onStop()");
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

    @Override
    public void onPause() {
        super.onPause();
    }


    public void onClickOpenVenueFeeds(View v) {
        VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) v.getTag(R.id.venue_name_and_feeds);
        if (venueNameAndFeeds != null) {
            openVenue(venueNameAndFeeds);     
        }
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
                ActivityVenueFeeds.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", venue_name);
            intent.putExtra("caller", "feeds_list");
            intent.putExtra("fragment", R.id.tab_fragment_area_feeds_for_one_venue);
            startActivity(intent);
        }
    }
    

}
