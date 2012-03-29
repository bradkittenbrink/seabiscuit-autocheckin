package com.coffeeandpower.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;


public class ActivityLoginPage extends RootActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	public void onClickFacebook (View v){

	}


	public void onClickTwitter (View v){

	}


	public void onClickMail (View v){

		startActivity(new Intent(ActivityLoginPage.this, ActivitySignInViaMail.class));
	}



}