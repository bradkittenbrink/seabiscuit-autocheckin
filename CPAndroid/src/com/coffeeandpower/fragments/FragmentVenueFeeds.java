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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.coffeeandpower.activity.ActivityFeedsForOneVenue;
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

import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.urbanairship.UAirship;

public class FragmentVenueFeeds extends Fragment {

    private MyVenueFeedsAdapter adapterFeeds;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<VenueNameAndFeeds> arrayFeeds;

    private boolean initialLoad = true;

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (listView != null && listView.isShown() == true) {
                String type = msg.getData().getString("type");

                if (!type.equalsIgnoreCase("AutoCheckinTrigger")) {
                    // pass message data along to venue update method
                    arrayFeeds = msg.getData().getParcelableArrayList(
                            "venueFeeds");
                    updateVenueFeedsFromApiResult(arrayFeeds);

                    progress.dismiss();
                }
            }
            super.handleMessage(msg);
        }
    };
    private Bundle intentExtras;

    public FragmentVenueFeeds(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }
    
    public void startUpdate() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = null;
        mainView = inflater.inflate(R.layout.tab_fragment_venue_feeds, null);
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

        if (AppCAP.isLoggedIn()) {
            listView = (ListView) getView().findViewById(
                    R.id.venue_feeds_listview);
        }
        if (arrayFeeds != null) {
            initialLoad = true;
            ActivityVenueFeeds act = (ActivityVenueFeeds) getActivity();
            act.updateMenuOnFragmentchange(R.id.tab_fragment_area_feed);
            act.setFragmentId(R.id.tab_fragment_area_feed);
        }

    }

    @Override
    public void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "FragmentVenueFeeds.onStart()");
        super.onStart();
        if (AppCAP.isLoggedIn()) {
            CacheMgrService.startObservingAPICall("venueFeedsList",
                    myCachedDataObserver);
        }
    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "ActivityvenueFeeds.onStop()");
        super.onStop();
        if (AppCAP.isLoggedIn()) {
            CacheMgrService.stopObservingAPICall("venueFeeds",
                    myCachedDataObserver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (listView.isShown()) {
            startUpdate();
        }
        if (!AppCAP.shouldFinishActivities()) {
            if (!AppCAP.isLoggedIn()) {
                progress.dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        progress.dismiss();
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void onClickOpenVenueFeeds(View v) {
        VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) v
                .getTag(R.id.venue_name_and_feeds);
        if (venueNameAndFeeds != null) {
            openVenue(venueNameAndFeeds);
        }
    }

    public void refresh() {

        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
    }

    private void updateVenueFeedsFromApiResult(
            ArrayList<VenueNameAndFeeds> newFeedsArray) {
        if (Constants.debugLog)
            Log.d("venueFeeds", "updateVenueFeedsFromApiResult()");
        if (AppCAP.isLoggedIn()) {

            // Populate table view
            this.arrayFeeds = newFeedsArray;
        } else {
            this.arrayFeeds = new ArrayList<VenueNameAndFeeds>();
        }

        if (initialLoad && listView != null) {
            adapterFeeds = new MyVenueFeedsAdapter(getActivity(),
                    this.arrayFeeds);
            listView.setAdapter(adapterFeeds);
            // TODO Need to add listview listener here
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long arg3) {
                    if (!AppCAP.isLoggedIn()) {
                        getActivity().showDialog(
                                RootActivity.DIALOG_MUST_BE_A_MEMBER);
                    } else {
                        VenueNameAndFeeds venueNameAndFeeds = (VenueNameAndFeeds) adapterFeeds
                                .getItem(position);
                        openVenue(venueNameAndFeeds);
                    }
                }
            });
            listView.setSelection(0);
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
            Intent intent = new Intent(getActivity().getApplicationContext(),
                    ActivityFeedsForOneVenue.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", venue_name);
            intent.putExtra("caller", "feeds_list");
            startActivity(intent);
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
                @SuppressWarnings("unchecked")
                List<Feed> arrayFeeds = (List<Feed>) feeds.getObject();

                if (Constants.debugLog)
                    Log.d("Contacts",
                            "Warning: API callback temporarily disabled...");

                ArrayList<Feed> mutableArrayContacts = new ArrayList<Feed>(
                        arrayFeeds);

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", counterdata.type);
                bundle.putParcelableArrayList("venueFeeds",
                        mutableArrayContacts);
                message.setData(bundle);

                if (Constants.debugLog)
                    Log.d("venueFeeds",
                            "venueFeeds.update: Sending handler message with "
                                    + mutableArrayContacts.size()
                                    + " venueFeeds:");

                mainThreadTaskHandler.sendMessage(message);
            } else if (Constants.debugLog)
                Log.d("venueFeeds", "Error: Received unexpected data type: "
                        + data.getClass().toString());
        }
    }

}
