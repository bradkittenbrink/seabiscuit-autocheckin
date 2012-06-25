package com.coffeeandpower.location;

import com.coffeeandpower.cont.VenueSmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ProximityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Boolean entering = bundle.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);

		//Grab the VenueSmart from the intent
		VenueSmart venue = (VenueSmart) bundle.getParcelable("venue");
				
		if (entering) {
			Log.d(getClass().getSimpleName(), "onReceive: entering");
			Toast.makeText(context, "Entering fence for " + venue.getName(), Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(getClass().getSimpleName(), "onReceive: exiting");
			Toast.makeText(context, "Exiting fence for " + venue.getName(), Toast.LENGTH_LONG).show();
		}


		
	}

}
