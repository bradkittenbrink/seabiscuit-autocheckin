package com.coffeeandpower.utils;

import java.util.Date;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
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
	
	
	/**
	 * Animate List View (try with other views)
	 * @param lv
	 */
	public static void animateListView(ListView lv){
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
				);
		animation.setDuration(300);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);       
		lv.setLayoutAnimation(controller);
	}
}
