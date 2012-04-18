package com.coffeeandpower.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils {

	/**
	 * Use this for ListView inside ScrollView
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter(); 

		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {

			View listItem = listView.getChildAt(0); // childs have same size
			if (listItem!=null){
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}


	/**
	 * Get Date and Time from epoch GMT
	 * @param epoch
	 * @return
	 */
	public static String getDateFromEpoch (String epoch){

		long epoch_ = 0;
		try {
			epoch_ = Long.parseLong(epoch);
		} catch (NumberFormatException e){}
		epoch_ = epoch_ * 1000;
		
		Date d = new Date(epoch_);
		return d.toLocaleString();	
	}
}
