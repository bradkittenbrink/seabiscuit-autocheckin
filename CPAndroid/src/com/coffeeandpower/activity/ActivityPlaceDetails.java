package com.coffeeandpower.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.adapters.MyUserSmartAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.UserAndTabMenu;
import com.coffeeandpower.utils.UserAndTabMenu.OnUserStateChanged;
import com.coffeeandpower.utils.Utils;
import com.coffeeandpower.views.CustomFontView;

public class ActivityPlaceDetails extends RootActivity {

    private String foursquareId;

    private DataHolder result;

    private Executor exe;

    private ArrayList<UserSmart> arrayUsers;
    private ArrayList<VenueSmart> arrayVenues;
    private ArrayList<CheckinData> arrayUsersInVenue;
    private ArrayList<UserSmart> arrayUsersHereNow;
    private ArrayList<UserSmart> arrayUsersWereHere;

    private VenueSmart selectedVenue;

    private ListView listWereHere;
    private ListView listHereNow;

    private ImageLoader imageLoader;

    private boolean amICheckedIn;

    private double data[];

    {
	arrayUsersHereNow = new ArrayList<UserSmart>();
	arrayUsersWereHere = new ArrayList<UserSmart>();
	amICheckedIn = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_places_details);

	// Executor
	exe = new Executor(ActivityPlaceDetails.this);
	exe.setExecutorListener(new ExecutorInterface() {
	    @Override
	    public void onErrorReceived() {
		errorReceived();
	    }

	    @Override
	    public void onActionFinished(int action) {
		actionFinished(action);
	    }
	});

	imageLoader = new ImageLoader(this);

	listHereNow = (ListView) findViewById(R.id.list_here_now);
	listWereHere = (ListView) findViewById(R.id.list_were_here);

	// Get foursquareId from Intent
	Bundle bundle = getIntent().getExtras();
	if (bundle != null) {
	    foursquareId = bundle.getString("foursquare_id");
	    data = bundle.getDoubleArray("coords");
	}

	// On item list click
	listHereNow.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (AppCAP.isLoggedIn()) {
		    Intent intent = new Intent(ActivityPlaceDetails.this, ActivityUserDetails.class);
		    intent.putExtra("mapuserobject", arrayUsersHereNow.get(position));
		    intent.putExtra("from_act", "list");
		    startActivity(intent);
		} else {
		    showDialog(DIALOG_MUST_BE_A_MEMBER);
		}
	    }
	});

	listWereHere.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (AppCAP.isLoggedIn()) {
		    Intent intent = new Intent(ActivityPlaceDetails.this, ActivityUserDetails.class);
		    intent.putExtra("mapuserobject", arrayUsersWereHere.get(position));
		    intent.putExtra("from_act", "list");
		    startActivity(intent);
		} else {
		    showDialog(DIALOG_MUST_BE_A_MEMBER);
		}
	    }
	});

    }

    private void fillData() {
	if (selectedVenue != null) {
	    ((CustomFontView) findViewById(R.id.textview_chat_name)).setText(AppCAP.cleanResponseString(selectedVenue.getName()));
	    ((CustomFontView) findViewById(R.id.textview_place_name)).setText(AppCAP.cleanResponseString(selectedVenue.getName()));
	    ((CustomFontView) findViewById(R.id.textview_place_address)).setText(AppCAP.cleanResponseString(selectedVenue.getAddress()));
	    ((TextView) findViewById(R.id.textview_place_check_in)).setText("Check in to " + AppCAP.cleanResponseString(selectedVenue.getName()));

	    // Try to load image
	    imageLoader
		    .DisplayImage(selectedVenue.getPhotoURL(), (ImageView) findViewById(R.id.image_view), R.drawable.picture_coming_soon_rectangle, 200);

	    arrayUsersInVenue = selectedVenue.getArrayCheckins();

	    for (CheckinData cd : arrayUsersInVenue) {
		if (cd.getCheckedIn() == 1) {
		    // user is here now
		    arrayUsersHereNow.add(getUserById(cd.getUserId()));
		} else {
		    // users were here
		    arrayUsersWereHere.add(getUserById(cd.getUserId()));
		}

		// Check if I am checked in or not
		if (AppCAP.getLoggedInUserId() == cd.getUserId() && cd.getCheckedIn() == 1) {
		    ((TextView) findViewById(R.id.textview_place_check_in)).setText("Check out of "
			    + AppCAP.cleanResponseString(selectedVenue.getName()));
		    amICheckedIn = true;
		}
	    }

	    // Create adapters and populate Lists
	    if (arrayUsersHereNow.isEmpty()) {
		listHereNow.setVisibility(View.GONE);
		((CustomFontView) findViewById(R.id.textview_here)).setVisibility(View.GONE);
	    } else {
		listHereNow.setAdapter(new MyUserSmartAdapter(ActivityPlaceDetails.this, arrayUsersHereNow));
		listHereNow.postDelayed(new Runnable() {
		    @Override
		    public void run() {
			Utils.setListViewHeightBasedOnChildren(listHereNow);
		    }
		}, 400);
		Utils.animateListView(listHereNow);
	    }
	    if (arrayUsersWereHere.isEmpty()) {
		listWereHere.setVisibility(View.GONE);
		((CustomFontView) findViewById(R.id.textview_worked)).setVisibility(View.GONE);
	    } else {
		listWereHere.setAdapter(new MyUserSmartAdapter(ActivityPlaceDetails.this, arrayUsersWereHere));
		listWereHere.postDelayed(new Runnable() {
		    @Override
		    public void run() {
			Utils.setListViewHeightBasedOnChildren(listWereHere);
		    }
		}, 400);
		Utils.animateListView(listWereHere);
	    }

	}
    }

    public VenueSmart getVenueSmart() {
	return selectedVenue;
    }

    public void onClickAddress(View v) {
	AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPlaceDetails.this);
	builder.setTitle("Directions");
	builder.setMessage("Do you want directions to " + AppCAP.cleanResponseString(selectedVenue.getName())).setCancelable(false)
		.setPositiveButton("Launch Map", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="
				+ AppCAP.getUserCoordinates()[4] + "," + AppCAP.getUserCoordinates()[5] + "&daddr=" + selectedVenue.getLat() + ","
				+ selectedVenue.getLng()));
			startActivity(intent);
			dialog.cancel();
		    }
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			dialog.cancel();
		    }
		});
	builder.create().show();
    }

    private UserSmart getUserById(int userId) {
	for (UserSmart us : arrayUsers) {
	    if (us.getUserId() == userId) {
		return us;
	    }
	}
	return null;
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
		exe.getVenuesAndUsersWithCheckinsInBoundsDuringInterval(data, true);
	    }
	});

	if (AppCAP.isLoggedIn()) {
	    if (amICheckedIn) {
		menu.onClickCheckIn(v);
	    } else if (selectedVenue != null) {
		Venue venue = new Venue();
		venue.setAddress(AppCAP.cleanResponseString(selectedVenue.getAddress()));
		venue.setCity(AppCAP.cleanResponseString(selectedVenue.getCity()));
		venue.setId(selectedVenue.getFoursquareId());
		venue.setName(AppCAP.cleanResponseString(selectedVenue.getName()));
		venue.setLat(selectedVenue.getLat());
		venue.setLng(selectedVenue.getLng());
		venue.setState(AppCAP.cleanResponseString(selectedVenue.getState()));

		Intent intent = new Intent(ActivityPlaceDetails.this, ActivityCheckIn.class);
		intent.putExtra("venue", venue);
		startActivity(intent);
	    }
	} else {
	    showDialog(DIALOG_MUST_BE_A_MEMBER);
	}
    }

    @Override
    protected void onResume() {
	super.onResume();

	if (foursquareId != null && foursquareId.length() > 0) {
	    arrayUsersHereNow.clear();
	    arrayUsersWereHere.clear();
	    exe.getVenuesAndUsersWithCheckinsInBoundsDuringInterval(data, true);
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

    private void errorReceived() {
    }

    private void actionFinished(int action) {
	result = exe.getResult();

	switch (action) {
	case Executor.HANDLE_GET_VENUES_AND_USERS_IN_BOUNDS:
	    if (result.getObject() != null && result.getObject() instanceof Object[]) {
		Object[] obj = (Object[]) result.getObject();
		arrayVenues = (ArrayList<VenueSmart>) obj[0];
		arrayUsers = (ArrayList<UserSmart>) obj[1];

		for (VenueSmart v : arrayVenues) {
		    if (v.getFoursquareId().equals(foursquareId)) {
			selectedVenue = v;
		    }
		}

		// Sort users list
		if (arrayUsers != null) {
		    Collections.sort(arrayUsers, new Comparator<UserSmart>() {
			@Override
			public int compare(UserSmart m1, UserSmart m2) {
			    if (m1.getCheckedIn() > m2.getCheckedIn()) {
				return -1;
			    }
			    return 1;
			}
		    });
		}

		// Fill veneu and users data
		fillData();
	    }
	    break;
	}
    }
}
