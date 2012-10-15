package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyUserSmartAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.SkillCategory;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueChatEntry;
import com.coffeeandpower.cont.VenueNameAndFeeds;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.urbanairship.UAirship;

public class ActivityPlaceDetails extends RootActivity {

    private String foursquareId;
    private DataHolder result;
    private Executor exe;

    private ArrayList<VenueSmart> cachedVenues = new ArrayList<VenueSmart>();
    private ArrayList<UserSmart> cachedUsers = new ArrayList<UserSmart>();

    private ArrayList<SkillCategory> arrayUsersHereNowForSkill = new ArrayList<SkillCategory>();
    private ArrayList<UserSmart> arrayUsersHereNow = new ArrayList<UserSmart>();
    private ArrayList<UserSmart> arrayUsersWereHere = new ArrayList<UserSmart>();

    private ArrayList<CheckinData> arrayUsersInVenue;

    private VenueSmart selectedVenue;

    private ListView listWereHere;
    private MyUserSmartAdapter listWereHereAdapter;

    private LinearLayout listHereNow;
    private MyUserSmartAdapter listHereNowAdapter;

    private ImageLoader imageLoader;
    private ImageLoader imageLoaderUser;

    private boolean amICheckedIn = false;
    private boolean initialLoadNow = true;
    private boolean initialLoadWere = true;

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();
    private MyAutoCheckinTriggerObserver myAutoCheckinObserver = new MyAutoCheckinTriggerObserver();
    // private double data[];

    // amICheckedIn = false;

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            String type = msg.getData().getString("type");

            if (type.equalsIgnoreCase("AutoCheckinTrigger")) {
                // call fillData with previously cached data, to redraw the
                // checkin options
                // called below
            } else {
                cachedUsers = msg.getData().getParcelableArrayList("users");
                cachedVenues = msg.getData().getParcelableArrayList("venues");
            }

            fillData(cachedUsers, cachedVenues);

            super.handleMessage(msg);
        }
    };
    private boolean firstDisplay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_details);

        imageLoader = new ImageLoader(this);

        imageLoaderUser = new ImageLoader(this);

        listHereNow = (LinearLayout) findViewById(R.id.list_here_now);
        listWereHere = (ListView) findViewById(R.id.list_were_here);

        // Get foursquareId from Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedVenue = (VenueSmart) bundle.getParcelable("venueSmart");
            // foursquareId = bundle.getString("foursquare_id");
            // data = bundle.getDoubleArray("coords");
        }

        listWereHere.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (AppCAP.isLoggedIn()) {
                    Intent intent = new Intent(ActivityPlaceDetails.this,
                            ActivityUserDetails.class);
                    intent.putExtra("mapuserobject",
                            arrayUsersWereHere.get(position));
                    intent.putExtra("from_act", "list");
                    startActivity(intent);
                } else {
                    showDialog(DIALOG_MUST_BE_A_MEMBER);
                }
            }
        });
        // Executor

    }

    @Override
    protected void onStart() {
        if (Constants.debugLog)
            Log.d("PlaceDetail", "ActivityPlaceDetail.onStart()");
        super.onStart();
        UAirship.shared().getAnalytics().activityStarted(this);
        CacheMgrService.startObservingAPICall("venuesWithCheckins",
                myCachedDataObserver);
        LocationDetectionStateMachine
                .startObservingAutoCheckinTrigger(myAutoCheckinObserver);

        initialLoadNow = true;
        initialLoadWere = true;
    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("PlaceDetail", "ActivityPlaceDetail.onStop()");
        super.onStop();
        UAirship.shared().getAnalytics().activityStopped(this);

        CacheMgrService.stopObservingAPICall("venuesWithCheckins",
                myCachedDataObserver);
        LocationDetectionStateMachine
                .stopObservingAutoCheckinTrigger(myAutoCheckinObserver);
    }

    private void fillData(ArrayList<UserSmart> arrayUsers,
            ArrayList<VenueSmart> arrayVenues) {
        if (selectedVenue != null) {

            Log.d("PlaceDetails", "fillData()");

            ((CustomFontView) findViewById(R.id.textview_phone_number))
                    .setVisibility(!selectedVenue.getPhone().equals("") ? View.VISIBLE
                            : View.GONE);
            ((CustomFontView) findViewById(R.id.textview_phone_number))
                    .setText(PhoneNumberUtils.formatNumber(selectedVenue
                            .getPhone()));
            ((CustomFontView) findViewById(R.id.textview_chat_name))
                    .setText(AppCAP.cleanResponseString(selectedVenue.getName()));
            ((CustomFontView) findViewById(R.id.textview_place_name))
                    .setText(AppCAP.cleanResponseString(selectedVenue.getName()));
            ((CustomFontView) findViewById(R.id.textview_place_address))
                    .setText(AppCAP.cleanResponseString(selectedVenue
                            .getAddress()));
            ((TextView) findViewById(R.id.textview_place_check_in))
                    .setText("Check in to "
                            + AppCAP.cleanResponseString(selectedVenue
                                    .getName()));

            // Try to load image
            imageLoader.DisplayImage(selectedVenue.getPhotoURL(),
                    (ImageView) findViewById(R.id.image_view),
                    R.drawable.picture_coming_soon_rectangle, 200);
            // Find the selected venue in the venue array and use the data from
            // the Counter
            // If the venue is not in the list keep using the data from the
            // intent
            int selectedId = this.selectedVenue.getVenueId();
            int testId = 0;
            for (VenueSmart testVenue : arrayVenues) {
                testId = testVenue.getVenueId();
                if (selectedId == testId) {
                    this.selectedVenue = testVenue;
                    break;
                }
            }

            arrayUsersInVenue = selectedVenue.getArrayCheckins();

            arrayUsersHereNow.clear();
            arrayUsersWereHere.clear();
            arrayUsersHereNowForSkill.clear();
            for (CheckinData cd : arrayUsersInVenue) {
                Log.d("PlaceDetail", "Filling data: " + cd.toString());
                if (cd.getCheckedIn() == 1) {
                    // user is here now
                    if (cd.getCheckedIn() == 1) {
                        
                        SkillCategory.addNewUserInTheList(UserSmart
                                .getUserById(cd.getUserId(), arrayUsers),
                                arrayUsersHereNowForSkill);
                        arrayUsersHereNow.add(UserSmart.getUserById(
                                cd.getUserId(), arrayUsers));
                    }
                } else {
                    // users were here
                    arrayUsersWereHere.add(UserSmart.getUserById(
                            cd.getUserId(), arrayUsers));
                }

                // Check if I am checked in or not
                if (AppCAP.getLoggedInUserId() == cd.getUserId()
                        && cd.getCheckedIn() == 1) {
                    ((TextView) findViewById(R.id.textview_place_check_in))
                            .setText("Check out of "
                                    + AppCAP.cleanResponseString(selectedVenue
                                            .getName()));
                    amICheckedIn = true;
                }
            }

            // Create adapters and populate Lists
            if (arrayUsersHereNowForSkill.isEmpty()) {
                listHereNow.setVisibility(View.GONE);
            } else {
                listHereNow.setVisibility(View.VISIBLE);
                Collections.sort(arrayUsersHereNowForSkill, Collections.reverseOrder());
                populateCategoryList(arrayUsersHereNowForSkill, listHereNow);
            }
            if (arrayUsersWereHere.isEmpty()) {
                listWereHere.setVisibility(View.GONE);
                ((CustomFontView) findViewById(R.id.textview_worked))
                        .setVisibility(View.GONE);
            } else {
                listWereHere.setVisibility(View.VISIBLE);
                ((CustomFontView) findViewById(R.id.textview_worked))
                        .setVisibility(View.VISIBLE);

                if (initialLoadWere) {
                    this.listWereHereAdapter = new MyUserSmartAdapter(
                            ActivityPlaceDetails.this, arrayUsersWereHere);
                    listWereHere.setAdapter(this.listWereHereAdapter);
                    listWereHere.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.setListViewHeightBasedOnChildren(listWereHere);
                        }
                    }, 400);
                    initialLoadWere = false;
                    Utils.animateListView(listWereHere);
                } else {
                    // Update the listview with the new data
                    this.listWereHereAdapter.setNewData(arrayUsersWereHere);
                    this.listWereHereAdapter.notifyDataSetChanged();
                }

            }

        }
        if (firstDisplay == true) {
            firstDisplay = false;
            ScrollView mainScrollView = (ScrollView)findViewById(R.id.activity_places_scrollview);
            mainScrollView.smoothScrollTo(0, 0);
        }
    }

    private void populateCategoryList(ArrayList<SkillCategory> skillCategory,
            LinearLayout layoutForInflateTheList) {
        layoutForInflateTheList.removeAllViews();
        if (skillCategory != null) {
            for (int i = 0; i < skillCategory.size(); i++) {
                LinearLayout layoutForInflateOneCategory = new LinearLayout(
                        this);
                layoutForInflateOneCategory.setOrientation(LinearLayout.VERTICAL);
                layoutForInflateOneCategory.setVisibility(View.GONE);
                populateUsersList(skillCategory.get(i),
                        layoutForInflateOneCategory, (i+1 == skillCategory.size()));
                layoutForInflateTheList.addView(layoutForInflateOneCategory);
            }

        }
        int childcount = layoutForInflateTheList.getChildCount();
        for (int i=0; i < childcount; i++){
              View v = layoutForInflateTheList.getChildAt(i);
              if (v.isShown()) {
                  layoutForInflateTheList.removeViewAt(i);
              } else {
                  v.setVisibility(View.VISIBLE);
              }
        }
    }

    private void populateUsersList(SkillCategory skillCat,
            LinearLayout layoutForInflateSkillCat, boolean noSep) {
        int pixels10 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                10, this.getResources().getDisplayMetrics()));
        int pixels2 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                2, this.getResources().getDisplayMetrics()));
        int pixels5 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                5, this.getResources().getDisplayMetrics()));
        int pixels80 = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                80, this.getResources().getDisplayMetrics()));
        Typeface font = Typeface.createFromAsset(this.getAssets(),
                "LeagueGothic.otf");
        ArrayList<UserSmart> checkedInUsers = skillCat.getUsers();
        if (checkedInUsers != null) {
            if (checkedInUsers.size() > 0) {
                LinearLayout layoutForInflateCategoryHeader = new LinearLayout(
                        this);
                layoutForInflateCategoryHeader.setOrientation(LinearLayout.HORIZONTAL);
                // Set category name
                TextView catName = new TextView(this);
                String name = skillCat.getName();
                String upperString = name.substring(0,1).toUpperCase() + name.substring(1);
                catName.setText(upperString);
                catName.setTextColor(Color.parseColor("#108086"));
                catName.setPadding(pixels10, pixels5, 0, 0);
                catName.setTypeface(font);

                layoutForInflateCategoryHeader.addView(catName);
                // Set the number of users in the category
                TextView numInCat = new TextView(this);
                numInCat.setText(" - " + checkedInUsers.size());
                numInCat.setTextColor(Color.parseColor("#999999"));
                numInCat.setTypeface(font);
                layoutForInflateCategoryHeader.addView(numInCat);
                layoutForInflateSkillCat
                        .addView(layoutForInflateCategoryHeader);
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
                LayoutParams lph = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                horizontalScrollView.setLayoutParams(lph); 
                layoutForInflateSkillCat.addView(horizontalScrollView);
                LinearLayout layoutForInflateUsers = new LinearLayout(this);
                layoutForInflateUsers.setOrientation(LinearLayout.HORIZONTAL);

                for (int i = 0; i < checkedInUsers.size(); i++) {

                    ImageView image = new ImageView(this);
                    LayoutParams lp = new LayoutParams(pixels80,pixels80);
                    image.setLayoutParams(lp);          
                    image.setPadding(pixels10, pixels5, pixels10, pixels10);
                    image.setTag(checkedInUsers.get(i).getUserId());
                    image.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int user_id = (Integer) v.getTag();
                            if (AppCAP.isLoggedIn()) {
                                Intent intent = new Intent(ActivityPlaceDetails.this,
                                        ActivityUserDetails.class);
                                intent.putExtra("user_id",
                                        user_id);
                                intent.putExtra("from_act", "user_id");
                                startActivity(intent);
                            } else {
                                showDialog(DIALOG_MUST_BE_A_MEMBER);
                            }

                        }
                    });
                    imageLoaderUser.DisplayImage(checkedInUsers.get(i).getFileName(),
                            image, R.drawable.default_avatar50, 70);
                    layoutForInflateUsers.addView(image);

                }
                horizontalScrollView.addView(layoutForInflateUsers);
                // Separator
                if (!noSep) {
                    LinearLayout layoutForInflateCategorySeparator = new LinearLayout(
                            this);
                    layoutForInflateCategorySeparator.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, pixels2);
                    layoutForInflateCategorySeparator.setLayoutParams(lp); 
                    layoutForInflateCategorySeparator.setBackgroundColor(Color.parseColor("#999999"));
                    layoutForInflateSkillCat.addView(layoutForInflateCategorySeparator);
                }
            }
        }
    }

    public void onClickAddress(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ActivityPlaceDetails.this);
        builder.setTitle("Directions");
        builder.setMessage(
                "Do you want directions to "
                        + AppCAP.cleanResponseString(selectedVenue.getName()))
                .setCancelable(false)
                .setPositiveButton("Launch Map",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?saddr="
                                                + AppCAP.getUserCoordinates()[4]
                                                + ","
                                                + AppCAP.getUserCoordinates()[5]
                                                + "&daddr="
                                                + selectedVenue.getLat()
                                                + ","
                                                + selectedVenue.getLng()));
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();
    }

    public void onClickPhone(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ActivityPlaceDetails.this);
        builder.setTitle("Call " + selectedVenue.getName() + "?");
        builder.setMessage(
                "" + PhoneNumberUtils.formatNumber(selectedVenue.getPhone()))
                .setCancelable(false)
                .setPositiveButton("Call",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_CALL,
                                        Uri.parse("tel:"
                                                + selectedVenue.getPhone()));
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();
    }


    public void onClickBack(View v) {
        onBackPressed();
    }

    public void onClickCheckIn(View v) {
        UserAndTabMenu menu = new UserAndTabMenu(ActivityPlaceDetails.this);
        menu.setOnUserStateChanged(new OnUserStateChanged() {
            @Override
            public void onLogOut() {
            }

            @Override
            public void onCheckOut() {
                arrayUsersHereNow.clear();
                arrayUsersWereHere.clear();
                amICheckedIn = false;

                // Restart the activity so user lists load correctly
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);

            }
        });

        if (AppCAP.isLoggedIn()) {
            if (amICheckedIn) {
                menu.onClickCheckIn(v);
            } else if (selectedVenue != null) {
                Venue venue = new Venue();
                venue.setAddress(AppCAP.cleanResponseString(selectedVenue
                        .getAddress()));
                venue.setCity(AppCAP.cleanResponseString(selectedVenue
                        .getCity()));
                venue.setFoursquareId(selectedVenue.getFoursquareId());
                venue.setName(AppCAP.cleanResponseString(selectedVenue
                        .getName()));
                venue.setLat(selectedVenue.getLat());
                venue.setLng(selectedVenue.getLng());
                venue.setState(AppCAP.cleanResponseString(selectedVenue
                        .getState()));

                Intent intent = new Intent(ActivityPlaceDetails.this,
                        ActivityCheckIn.class);
                // intent.putExtra("venue", venue);
                intent.putExtra("venue", this.selectedVenue);
                startActivity(intent);
            }
        } else {
            showDialog(DIALOG_MUST_BE_A_MEMBER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arrayUsersHereNow.size() > 0) {
            arrayUsersHereNow.clear();
        }
        if (arrayUsersWereHere.size() > 0) {
            arrayUsersWereHere.clear();
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
                DataHolder result = counterdata.getData();

                Object[] obj = (Object[]) result.getObject();
                @SuppressWarnings("unchecked")
                List<VenueSmart> listVenues = (List<VenueSmart>) obj[0];
                @SuppressWarnings("unchecked")
                List<UserSmart> listUsers = (List<UserSmart>) obj[1];

                ArrayList<VenueSmart> arrayVenues = new ArrayList<VenueSmart>(
                        listVenues);
                ArrayList<UserSmart> arrayUsers = new ArrayList<UserSmart>(
                        listUsers);

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", counterdata.type);
                bundle.putParcelableArrayList("users", arrayUsers);
                bundle.putParcelableArrayList("venues", arrayVenues);

                message.setData(bundle);

                if (Constants.debugLog)
                    Log.d("PlaceDetail",
                            "ActivityPlaceDetail.update: Sending handler message...");
                mainThreadTaskHandler.sendMessage(message);

            } else if (Constants.debugLog)
                Log.d("PlaceDetail", "Error: Received unexpected data type: "
                        + data.getClass().toString());
        }
    }
}
