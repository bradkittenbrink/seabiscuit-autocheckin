package com.coffeeandpower.tab.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.activity.ActivityCheckIn;
import com.coffeeandpower.adapters.MyVenuesAdapter;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.Utils;
import com.google.android.maps.GeoPoint;

public class ActivityCheckInList extends ListActivity {

    private DataHolder result;

    private Executor exe;

    private MyVenuesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_check_in_list);

	// Executor
	exe = new Executor(ActivityCheckInList.this);
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

	// Get data from Intent
	Bundle extras = getIntent().getExtras();
	if (extras != null) {
	    int lng = extras.getInt("lng");
	    int lat = extras.getInt("lat");

	    GeoPoint gp = new GeoPoint(lat, lng);
	    exe.getVenuesCloseToLocation(gp, 20);
	}
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);

	if (((Venue) adapter.getItem(position)).getId().equals("add_place")) {
	    final EditText input = new EditText(this);
	    new AlertDialog.Builder(ActivityCheckInList.this).setTitle("Name of New Place").setView(input)
		    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			    exe.addPlace(input.getText().toString());
			}
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			    dialog.dismiss();
			}
		    }).show();
	} else {
	    Intent intent = new Intent(ActivityCheckInList.this, ActivityCheckIn.class);
	    intent.putExtra("venue", (Venue) adapter.getItem(position));
	    startActivityForResult(intent, AppCAP.ACT_CHECK_IN);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	switch (requestCode) {

	case AppCAP.ACT_CHECK_IN:
	    if (resultCode == AppCAP.ACT_QUIT) {
		ActivityCheckInList.this.finish();
	    }
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
    }

    public void onClickCancel(View v) {
	onBackPressed();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    public void errorReceived() {
    }

    private void actionFinished(int action) {
	result = exe.getResult();

	switch (action) {
	case Executor.HANDLE_VENUES_CLOSE_TO_LOCATION:
	    if (result.getObject() != null) {
		((ArrayList<Venue>) result.getObject()).add(new Venue("add_place", "Add Place...", "", "", 0, 0, 0, "", "", "", "", "", "", "", 0, 0,
			0, 0, "", ""));
		adapter = new MyVenuesAdapter(ActivityCheckInList.this, (ArrayList<Venue>) result.getObject());
		setListAdapter(adapter);
		Utils.animateListView(getListView());
	    }
	    break;

	case Executor.HANDLE_ADD_PLACE:
	    if (result.getObject() != null) {
		Intent intent = new Intent(ActivityCheckInList.this, ActivityCheckIn.class);
		intent.putExtra("venue", (Venue) result.getObject());
		startActivityForResult(intent, AppCAP.ACT_CHECK_IN);
	    }
	    break;
	}
    }
}
