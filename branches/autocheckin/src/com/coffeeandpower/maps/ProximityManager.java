package com.coffeeandpower.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);

		if (entering) {
			Log.d(getClass().getSimpleName(), "onReceive: entering");
			Toast.makeText(context, "Entering fence...", Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(getClass().getSimpleName(), "onReceive: exiting");
			Toast.makeText(context, "Exiting fence...", Toast.LENGTH_LONG).show();
		}


		
	}

}
