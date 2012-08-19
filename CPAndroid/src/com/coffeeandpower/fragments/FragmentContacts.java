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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import com.coffeeandpower.utils.Utils;

public class FragmentContacts extends Fragment {

    private MyUsersAdapter adapterUsers;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<UserSmart> arrayUsers;

    private boolean initialLoad = true;
    private ImageView blankSlateImg;

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();

    private Bundle intentExtras;

    public FragmentContacts(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (listView != null && listView.isShown() == true) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = null;
        if (AppCAP.isLoggedIn()) {
            mainView = inflater.inflate(R.layout.tab_fragment_contacts, null);
        } else {
            mainView = inflater.inflate(R.layout.tab_activity_login, null);
        }
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
            // Display the list of users if the user is logged in
            listView = (ListView) getView()
                    .findViewById(R.id.contacts_listview);
            // TODO Need to add listview listener here
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long arg3) {
                    if (!AppCAP.isLoggedIn()) {
                        getActivity().showDialog(
                                RootActivity.DIALOG_MUST_BE_A_MEMBER);
                    } else {
                        Intent intent = new Intent(getActivity()
                                .getApplicationContext(),
                                ActivityUserDetails.class);
                        intent.putExtra("mapuserobject",
                                (UserSmart) adapterUsers.getItem(position));
                        intent.putExtra("from_act", "list");
                        startActivity(intent);
                    }
                }
            });

            blankSlateImg = (ImageView) getView().findViewById(
                    R.id.contacts_blank_slate_img);
        }

    }

    @Override
    public void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "FragmentPeopleAndPlaces.onStart()");
        super.onStart();

        if (AppCAP.isLoggedIn()) {
            CacheMgrService.startObservingAPICall("contactsList",
                    myCachedDataObserver);
        }
    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "FragmentPeopleAndPlaces.onStop()");
        super.onStop();
        if (AppCAP.isLoggedIn()) {
            CacheMgrService.stopObservingAPICall("contactsList",
                    myCachedDataObserver);
        }
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

    public void refresh() {

        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
    }

    private void updateUsersAndCheckinsFromApiResult(
            ArrayList<UserSmart> newUsersArray) {
        if (Constants.debugLog)
            Log.d("Contacts", "updateUsersAndCheckinsFromApiResult()");

        // Sort users list
        if (newUsersArray != null) {
            Collections.sort(newUsersArray, new Comparator<UserSmart>() {
                @Override
                public int compare(UserSmart m1, UserSmart m2) {
                    // if (m1.getCheckedIn() > m2.getCheckedIn()) {
                    // return -1;
                    // }
                    return m1.getNickName().compareToIgnoreCase(
                            m2.getNickName());
                    // return 1;
                }
            });
        }

        /*
         * if (newUsersArray.size() == 0) {
         * blankSlateImg.setVisibility(View.VISIBLE); } else {
         */
        blankSlateImg.setVisibility(View.INVISIBLE);
        // }

        // Populate table view
        this.arrayUsers = newUsersArray;

        if (initialLoad) {
            if (Constants.debugLog)
                Log.d("FragmentContacts", "Contacts List Initial Load");
            adapterUsers = new MyUsersAdapter(getActivity(), this.arrayUsers);
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
                @SuppressWarnings("unchecked")
                List<UserSmart> arrayContacts = (List<UserSmart>) contacts
                        .getObject();

                if (Constants.debugLog)
                    Log.d("Contacts",
                            "Warning: API callback temporarily disabled...");

                // Remove self from user array
                UserSmart selfUser = null;
                ArrayList<UserSmart> mutableArrayContacts = new ArrayList<UserSmart>(
                        arrayContacts);
                for (UserSmart aUser : mutableArrayContacts) {

                    if (AppCAP.getLoggedInUserId() == aUser.getUserId()) {
                        if (Constants.debugLog)
                            Log.d("Contacts",
                                    " - Removing self from users array: "
                                            + aUser.getNickName());
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
                    Log.d("Contacts",
                            "Contacts.update: Sending handler message with "
                                    + mutableArrayContacts.size()
                                    + " contacts:");

                mainThreadTaskHandler.sendMessage(message);
            } else if (Constants.debugLog)
                Log.d("Contacts", "Error: Received unexpected data type: "
                        + data.getClass().toString());
        }
    }

}
