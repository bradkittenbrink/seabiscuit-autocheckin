package com.coffeeandpower.maps;

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
		
		//Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
		Boolean testing =  intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		String info = intent.getStringExtra(LocationManager.KEY_PROXIMITY_ENTERING);

		//Grab the VenueSmart from the intent
		VenueSmart venue = (VenueSmart) bundle.getParcelable("venue");
		
		if(entering && testing)
		{
			Log.d(getClass().getSimpleName(), "onReceive: Both True");
		}
		if(entering == false && testing==false)
		{
			Log.d(getClass().getSimpleName(), "onReceive: Both False");
		}
		if(entering == false && testing)
		{
			Log.d(getClass().getSimpleName(), "onReceive: Defaults");
		}
		if(entering && testing == false)
		{
			Log.d(getClass().getSimpleName(), "onReceive: Inverse of defaults");
		}
		
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
