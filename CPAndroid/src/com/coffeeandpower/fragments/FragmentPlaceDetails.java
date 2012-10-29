package com.coffeeandpower.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyUserSmartAdapter;
import com.coffeeandpower.app.R;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.SkillCategory;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.location.LocationDetectionStateMachine;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;
import com.urbanairship.UAirship;

import java.util.*;

public class FragmentPlaceDetails extends Fragment {

    private Bundle intentExtras;
    private LinearLayout listHereNow;
    private ListView listWereHere;

    private ImageLoader imageLoader;
    private ImageLoader imageLoaderUser;

    private VenueSmart selectedVenue;
    private ArrayList<VenueSmart> cachedVenues = new ArrayList<VenueSmart>();
    private ArrayList<UserSmart> cachedUsers = new ArrayList<UserSmart>();
    private ArrayList<UserSmart> arrayUsersWereHere = new ArrayList<UserSmart>();
    private ArrayList<SkillCategory> arrayUsersHereNowForSkill = new ArrayList<SkillCategory>();

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();
    private MyAutoCheckinTriggerObserver myAutoCheckinObserver = new MyAutoCheckinTriggerObserver();

    private MyUserSmartAdapter listWereHereAdapter;

    private ProgressDialog progress;

    private boolean initialLoadWere = true;
    private boolean firstDisplay = true;

    public FragmentPlaceDetails(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = layoutInflater.inflate(R.layout.activity_places_details, null);
        progress = new ProgressDialog(this.getActivity());
        progress.setMessage("Loading...");
        progress.show();

        if (intentExtras != null) {
        }

        return mainView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageLoader = new ImageLoader(getActivity());
        imageLoaderUser = new ImageLoader(getActivity());

        listHereNow = (LinearLayout) getView().findViewById(R.id.list_here_now);
        listWereHere = (ListView) getView().findViewById(R.id.list_were_here);

        listWereHere.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (AppCAP.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), ActivityUserDetails.class);
                    intent.putExtra("mapuserobject", arrayUsersWereHere.get(position));
                    intent.putExtra("from_act", "list");
                    startActivity(intent);

                } else {
                    getActivity().showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
                }
            }
        });

        View placeAddress = getView().findViewById(R.id.textview_place_address);
        placeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddress(view);
            }
        });

        View placePhone = getView().findViewById(R.id.textview_phone_number);
        placePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPhone(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        UAirship.shared().getAnalytics().activityStarted(getActivity());
        initialLoadWere = true;
        CacheMgrService.startObservingAPICall("venuesWithCheckins", myCachedDataObserver);
        LocationDetectionStateMachine.startObservingAutoCheckinTrigger(myAutoCheckinObserver);
    }

    @Override
    public void onStop() {
        UAirship.shared().getAnalytics().activityStopped(getActivity());
        CacheMgrService.stopObservingAPICall("venuesWithCheckins", myCachedDataObserver);
        LocationDetectionStateMachine.stopObservingAutoCheckinTrigger(myAutoCheckinObserver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arrayUsersWereHere.size() > 0) {
            arrayUsersWereHere.clear();
        }
    }

    private void onClickAddress(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void onClickPhone(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            String type = msg.getData().getString("type");

            if (!type.equalsIgnoreCase("AutoCheckinTrigger")) {
                // call fillData with previously cached data, to redraw the
                // checkin options
                // called below
                cachedUsers = msg.getData().getParcelableArrayList("users");
                cachedVenues = msg.getData().getParcelableArrayList("venues");
            }

            try {
                fillData(cachedUsers, cachedVenues);
            } catch (Exception e) {
                Log.d("dde", e.getLocalizedMessage());
            }

            progress.dismiss();
            super.handleMessage(msg);
        }
    };

    private void fillData(ArrayList<UserSmart> arrayUsers, ArrayList<VenueSmart> arrayVenues) {

        selectedVenue = (VenueSmart) intentExtras.getParcelable("venueSmart");

        if (selectedVenue != null && getView() != null) {

            getView().findViewById(R.id.textview_phone_number).setVisibility(
                    !selectedVenue.getPhone().equals("") ? View.VISIBLE : View.GONE);

            ((CustomFontView) getView().findViewById(R.id.textview_phone_number)).setText(
                    PhoneNumberUtils.formatNumber(selectedVenue.getPhone()));

            ((CustomFontView) getView().findViewById(R.id.textview_chat_name)).setText(
                    AppCAP.cleanResponseString(selectedVenue.getName()));


            ((CustomFontView) getView().findViewById(R.id.textview_place_name)).setText(
                    AppCAP.cleanResponseString(selectedVenue.getName()));

            ((CustomFontView) getView().findViewById(R.id.textview_place_address)).setText(
                    AppCAP.cleanResponseString(selectedVenue.getAddress()));

            // Try to load image
            imageLoader.DisplayImage(
                    selectedVenue.getPhotoURL(), (ImageView) getView().findViewById(R.id.image_view),
                    R.drawable.picture_coming_soon_rectangle, 200);


            // Find the selected venue in the venue array and use the data from
            // the Counter
            // If the venue is not in the list keep using the data from the
            // intent
            int selectedId = selectedVenue.getVenueId();
            int testId;
            for (VenueSmart testVenue : arrayVenues) {
                testId = testVenue.getVenueId();
                if (selectedId == testId) {
                    selectedVenue = testVenue;
                    break;
                }
            }

            ArrayList<VenueSmart.CheckinData> arrayUsersInVenue = selectedVenue.getArrayCheckins();

            arrayUsersWereHere.clear();
            arrayUsersHereNowForSkill.clear();
            for (VenueSmart.CheckinData cd : arrayUsersInVenue) {

                if (cd.getCheckedIn() == 1) {
                    // user is here now
                    if (cd.getCheckedIn() == 1) {
                        SkillCategory.addNewUserInTheList(
                                UserSmart.getUserById(cd.getUserId(), arrayUsers), arrayUsersHereNowForSkill);
                    }
                } else {
                    // users were here
                    arrayUsersWereHere.add(UserSmart.getUserById(cd.getUserId(), arrayUsers));
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
                getView().findViewById(R.id.textview_worked).setVisibility(View.GONE);

            } else {
                listWereHere.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.textview_worked).setVisibility(View.VISIBLE);

                if (initialLoadWere) {
                    this.listWereHereAdapter = new MyUserSmartAdapter(getActivity(), arrayUsersWereHere);
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
                    // Update the ListView with the new data
                    this.listWereHereAdapter.setNewData(arrayUsersWereHere);
                    this.listWereHereAdapter.notifyDataSetChanged();
                }
            }
        }

        if (firstDisplay) {
            firstDisplay = false;
            ScrollView mainScrollView = (ScrollView) getView().findViewById(R.id.activity_places_scrollview);
            mainScrollView.smoothScrollTo(0, 0);
        }
    }

    private void populateCategoryList(ArrayList<SkillCategory> skillCategory, LinearLayout layoutForInflateTheList) {
        layoutForInflateTheList.removeAllViews();
        if (skillCategory != null) {
            for (int i = 0; i < skillCategory.size(); i++) {
                LinearLayout layoutForInflateOneCategory = new LinearLayout(getActivity());
                layoutForInflateOneCategory.setOrientation(LinearLayout.VERTICAL);
                layoutForInflateOneCategory.setVisibility(View.GONE);
                populateUsersList(skillCategory.get(i), layoutForInflateOneCategory, (i + 1 == skillCategory.size()));
                layoutForInflateTheList.addView(layoutForInflateOneCategory);
            }
        }

        int childCount = layoutForInflateTheList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = layoutForInflateTheList.getChildAt(i);
            if (v.isShown()) {
                layoutForInflateTheList.removeViewAt(i);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private void populateUsersList(SkillCategory skillCat, LinearLayout layoutForInflateSkillCat, boolean noSep) {
        int pixels10 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics()));

        int pixels2 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, this.getResources().getDisplayMetrics()));

        int pixels5 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources().getDisplayMetrics()));

        int pixels80 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, this.getResources().getDisplayMetrics()));

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "LeagueGothic.otf");
        ArrayList<UserSmart> checkedInUsers = skillCat.getUsers();

        if (checkedInUsers != null) {
            if (checkedInUsers.size() > 0) {
                LinearLayout layoutForInflateCategoryHeader = new LinearLayout(getActivity());
                layoutForInflateCategoryHeader.setOrientation(LinearLayout.HORIZONTAL);
                // Set category name
                TextView catName = new TextView(getActivity());
                String name = skillCat.getName();
                String upperString = name.substring(0, 1).toUpperCase() + name.substring(1);
                catName.setText(upperString);
                catName.setTextColor(Color.parseColor("#108086"));
                catName.setPadding(pixels10, pixels5, 0, 0);
                catName.setTypeface(font);

                layoutForInflateCategoryHeader.addView(catName);
                // Set the number of users in the category
                TextView numInCat = new TextView(getActivity());
                numInCat.setText(" - " + checkedInUsers.size());
                numInCat.setTextColor(Color.parseColor("#999999"));
                numInCat.setTypeface(font);
                layoutForInflateCategoryHeader.addView(numInCat);
                layoutForInflateSkillCat.addView(layoutForInflateCategoryHeader);
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
                ViewGroup.LayoutParams lph = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                horizontalScrollView.setLayoutParams(lph);
                layoutForInflateSkillCat.addView(horizontalScrollView);
                LinearLayout layoutForInflateUsers = new LinearLayout(getActivity());
                layoutForInflateUsers.setOrientation(LinearLayout.HORIZONTAL);

                for (UserSmart checkedInUser : checkedInUsers) {

                    ImageView image = new ImageView(getActivity());
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(pixels80, pixels80);
                    image.setLayoutParams(lp);
                    image.setPadding(pixels10, pixels5, pixels10, pixels10);
                    image.setTag(checkedInUser.getUserId());
                    image.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int user_id = (Integer) v.getTag();
                            if (AppCAP.isLoggedIn()) {
                                Intent intent = new Intent(getActivity(), ActivityUserDetails.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("from_act", "user_id");
                                startActivity(intent);

                            } else {
                                getActivity().showDialog(RootActivity.DIALOG_MUST_BE_A_MEMBER);
                            }
                        }
                    });

                    imageLoaderUser.DisplayImage(checkedInUser.getFileName(), image, R.drawable.default_avatar50, 70);
                    layoutForInflateUsers.addView(image);

                }

                horizontalScrollView.addView(layoutForInflateUsers);
                // Separator
                if (!noSep) {
                    LinearLayout layoutForInflateCategorySeparator = new LinearLayout(getActivity());
                    layoutForInflateCategorySeparator.setOrientation(LinearLayout.HORIZONTAL);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, pixels2);
                    layoutForInflateCategorySeparator.setLayoutParams(lp);
                    layoutForInflateCategorySeparator.setBackgroundColor(Color.parseColor("#999999"));
                    layoutForInflateSkillCat.addView(layoutForInflateCategorySeparator);
                }
            }
        }
    }

    private class MyAutoCheckinTriggerObserver implements Observer {

        @Override
        public void update(Observable arg0, Object arg1) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("type", "AutoCheckinTrigger");
            message.setData(bundle);

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

                CachedDataContainer cachedDataContainer = (CachedDataContainer) data;
                DataHolder result = cachedDataContainer.getData();

                Object[] obj = (Object[]) result.getObject();
                @SuppressWarnings("unchecked")
                List<VenueSmart> listVenues = (List<VenueSmart>) obj[0];
                @SuppressWarnings("unchecked")
                List<UserSmart> listUsers = (List<UserSmart>) obj[1];

                ArrayList<VenueSmart> arrayVenues = new ArrayList<VenueSmart>(listVenues);
                ArrayList<UserSmart> arrayUsers = new ArrayList<UserSmart>(listUsers);

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", cachedDataContainer.type);
                bundle.putParcelableArrayList("users", arrayUsers);
                bundle.putParcelableArrayList("venues", arrayVenues);

                message.setData(bundle);

                mainThreadTaskHandler.sendMessage(message);

            } else {
                Log.e("PlaceDetail", String.format("Received unexpected data type: %s", data.getClass()));
            }
        }
    }
}
