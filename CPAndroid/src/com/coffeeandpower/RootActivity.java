package com.coffeeandpower;

import com.coffeeandpower.tab.activities.ActivityMap;
import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

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
