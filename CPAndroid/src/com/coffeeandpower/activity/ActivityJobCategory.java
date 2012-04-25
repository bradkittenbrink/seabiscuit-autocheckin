package com.coffeeandpower.activity;

import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

public class ActivityJobCategory extends RootActivity{



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_category);
	}

	public void onClickMajor (View v){
		
	}
	
	public void onClickMinor (View v){
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void onClickBack (View v){
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	
}
