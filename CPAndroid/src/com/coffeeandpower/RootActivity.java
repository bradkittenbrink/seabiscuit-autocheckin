package com.coffeeandpower;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import com.coffeeandpower.tab.activities.ActivityMap;
import com.google.android.maps.MapActivity;

public class RootActivity extends MapActivity{

	public static final int DIALOG_MUST_BE_A_MEMBER = 30;

	AlertDialog alert;

	/**
	 * Easy log
	 * @param msg
	 */
	public static void log(String msg){
		Log.d(AppCAP.TAG, msg);
	}

	/**
	 * Get distance between points
	 * @param startLat
	 * @param startLng
	 * @param endLat
	 * @param endLng
	 * @return String 100m or 5.4km
	 */
	public static String getDistanceBetween (double startLat, double startLng, double endLat, double endLng) {
		float[] results = new float[1];
		Location.distanceBetween(startLat, startLng, endLat, endLng, results);

		String distanceS = "";
		DecimalFormat oneDForm = new DecimalFormat("#.#");
		if (results[0] < 100){
			float d = Float.valueOf(oneDForm.format(results[0]));
			distanceS = d + "m";
		} else {
			float d = Float.valueOf(oneDForm.format(results[0]/1000));
			distanceS = d + "km";
		}
		return distanceS;
	}

	
	protected DisplayMetrics getDisplayMetrics (){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case DIALOG_MUST_BE_A_MEMBER:
			AlertDialog.Builder builder = new AlertDialog.Builder(RootActivity.this);
			builder.setMessage("You must be a member to use this feature.")
			.setCancelable(false)
			.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Activity a = alert.getOwnerActivity();
					if (a!=null){

						if (a.getClass()==ActivityMap.class){
							AppCAP.setShouldStartLogIn(true);
						}
						AppCAP.setShouldFinishActivities(true);
						a.finish();
					}
					dialog.cancel();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			alert = builder.create();
			break;

		default:
			alert = null;
			break;
		}

		return alert;
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
