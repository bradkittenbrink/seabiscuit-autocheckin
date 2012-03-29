package com.coffeeandpower;

import android.app.Activity;
import android.util.Log;

public class RootActivity extends Activity{
	
	/**
	 * Easy log
	 * @param msg
	 */
	public static void log(String msg){
		Log.d(AppCAP.TAG, msg);
	}

}
