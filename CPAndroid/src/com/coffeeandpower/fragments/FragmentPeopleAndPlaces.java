package com.coffeeandpower.fragments;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.Utils;

public class FragmentPeopleAndPlaces extends Fragment {

    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_USER = 1;

    private MyUsersAdapter adapterUsers;
    private MyPlacesAdapter adapterPlaces;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<UserSmart> arrayUsers;
    private ArrayList<VenueSmart> arrayVenues;

    private double userLat;
    private double userLng;
    private double data[];

    private boolean isPeopleList;

    private String type;

    private boolean initialLoad = true;

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();

    private Bundle intentExtras;

    public FragmentPeopleAndPlaces(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (listView != null && listView.isShown() == true) {
                if (type.equals("people")) {

                    arrayUsers = msg.getData().getParcelableArrayList("users");

                    // Sort users list
                    if (arrayUsers != null) {
                        Collections.sort(arrayUsers,
                                new Comparator<UserSmart>() {
                                    @Override
                                    public int compare(UserSmart m1,
                                            UserSmart m2) {
                                        if (m1.getCheckedIn() > m2
                                                .getCheckedIn()) {
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
                    arrayVenues = msg.getData()
                            .getParcelableArrayList("venues");
                    setPlaceList();
                }

                progress.dismiss();
            }
            super.handleMessage(msg);
        }
    };

    {
        data = new double[6];
        // default view is People List
        isPeopleList = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = null;
        mainView = inflater.inflate(R.layout.tab_fragment_people_and_places,
                null);
        progress = new ProgressDialog(this.getActivity());
        if (AppCAP.isLoggedIn()) {
            progress.setMessage("Loading...");
        } else {
            progress.setMessage("You must login to see the feeds ...");
        }
        progress.show();
        // if (arrayFeeds != null) {
        if (intentExtras != null) {

            initialLoad = true;
            ActivityVenueFeeds act = (ActivityVenueFeeds) getActivity();
            // Check is it People or Places List
            type = intentExtras.getString("type");
            
            if (type.equals("people")) {
                act.updateMenuOnFragmentchange(R.id.tab_fragment_area_people);
                act.setFragmentId(R.id.tab_fragment_area_people);
            } else {
                act.updateMenuOnFragmentchange(R.id.tab_fragment_area_places);
                act.setFragmentId(R.id.tab_fragment_area_places);
            }
            // Check is it click from Activity or Balloon
            String from = intentExtras.getString("from");
            if (from != null) {
                if (from.equals("from_tab")) {
                    data[0] = intentExtras.getDouble("sw_lat");
                    data[1] = intentExtras.getDouble("sw_lng");
                    data[2] = intentExtras.getDouble("ne_lat");
                    data[3] = intentExtras.getDouble("ne_lng");
                    data[4] = intentExtras.getDouble("user_lat");
                    data[5] = intentExtras.getDouble("user_lng");
                    userLat = data[4];
                    userLng = data[5];
                }
            }
        } else {
            Log.d("FragmentPeopleAndPlaces", "Extras was null!");
        }
        // }
        return mainView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView = (ListView) getView().findViewById(R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                if (isPeopleList) {
                    if (!AppCAP.isLoggedIn()) {
                        getActivity().showDialog(
                                RootActivity.DIALOG_MUST_BE_A_MEMBER);
                    } else {
                        Intent intent = new Intent(getActivity(),
                                ActivityUserDetails.class);
                        intent.putExtra("mapuserobject",
                                (UserSmart) adapterUsers.getItem(position));
                        intent.putExtra("from_act", "list");
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getActivity(),
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

    }

    private void setPeopleList() {

        if (initialLoad) {
            if (Constants.debugLog)
                Log.d("FragmentPeopleAndPlaces", "People List Initial Load");
            adapterUsers = new MyUsersAdapter(getActivity(), arrayUsers,
                    userLat, userLng);
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

        if (initialLoad) {
            if (Constants.debugLog)
                Log.d("FragmenteopleAndPlaces", "Place List Initial Load");
            adapterPlaces = new MyPlacesAdapter(getActivity(), arrayVenues,
                    userLat, userLng);
            listView.setAdapter(adapterPlaces);
            Utils.animateListView(listView);
            initialLoad = false;
        } else {
            adapterPlaces.setNewData(arrayVenues);
            adapterPlaces.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "FragmentPeopleAndPlaces.onStart()");
        super.onStart();

        CacheMgrService.startObservingAPICall("venuesWithCheckins",
             myCachedDataObserver);
    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "FragmentPeopleAndPlaces.onStop()");
        super.onStop();
        CacheMgrService.stopObservingAPICall("venuesWithCheckins",
             myCachedDataObserver);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppCAP.shouldFinishActivities()) {
            if (!AppCAP.isLoggedIn()) {
                progress.dismiss();
            }
        }
    }

    /*
     * public void onBackPressed() { getActivity().onBackPressed(); }
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void refresh() {

        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
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
                    bundle.putParcelableArrayList("users",
                            new ArrayList<UserSmart>(arrayUsers));
                } else {
                    bundle.putParcelableArrayList("venues",
                            new ArrayList<VenueSmart>(arrayVenues));
                }
                message.setData(bundle);

                if (Constants.debugLog)
                    Log.d("PeoplePlaces",
                            "FragmentPeopleAndPlaces.update: Sending handler message...");

                mainThreadTaskHandler.sendMessage(message);

            } else if (Constants.debugLog)
                Log.d("PeoplePlaces", "Error: Received unexpected data type: "
                        + data.getClass().toString());

        }
    }

}
