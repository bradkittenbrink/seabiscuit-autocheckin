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

public class RootActivity extends MapActivity {

	public static final int DIALOG_MUST_BE_A_MEMBER = 30;

	private AlertDialog alert;

	/**
	 * Easy log
	 * 
	 * @param msg
	 */
	public static void log(String msg) {
		Log.d(AppCAP.TAG, msg);
	}

	/**
	 * Get distance between points
	 * 
	 * @param startLat
	 * @param startLng
	 * @param endLat
	 * @param endLng
	 * @return String 100m or 5.4km
	 */
	public static String getDistanceBetween(double startLat, double startLng, double endLat, double endLng) {
		float[] results = new float[1];
		Location.distanceBetween(startLat, startLng, endLat, endLng, results);
		return formatToMetricsOrImperial(results[0]);
	}

	public static String formatToMetricsOrImperial(float distance) {
		DecimalFormat oneDForm = new DecimalFormat("#.#");
		String distanceS = "";

		if (AppCAP.isMetrics()) {
			if (distance < 100) {
				float d = Float.valueOf(oneDForm.format(distance));
				distanceS = d + "m";
			} else {
				float d = Float.valueOf(oneDForm.format(distance / 1000));
				distanceS = d + "km";
			}
		} else {
			distance = distance * 3.28f; // feets
			if (distance < 1000) {
				float d = Float.valueOf(oneDForm.format(distance));
				distanceS = d + "ft";
			} else {
				float d = Float.valueOf(oneDForm.format(distance / 5280));
				distanceS = d + "mi";
			}
		}
		return distanceS;
	}

	protected DisplayMetrics getDisplayMetrics() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(RootActivity.this);

		switch (id) {

		case DIALOG_MUST_BE_A_MEMBER:
			builder.setMessage("You must be a member to use this feature.").setCancelable(false)
					.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Activity a = alert.getOwnerActivity();
							if (a != null) {

								if (a.getClass() == ActivityMap.class) {
									AppCAP.setShouldStartLogIn(true);
								}
								AppCAP.setShouldFinishActivities(true);
								a.finish();
							}
							dialog.cancel();
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
