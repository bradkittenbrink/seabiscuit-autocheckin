package com.coffeeandpower.utils;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.activity.ActivityLoginPage;
import com.coffeeandpower.activity.ActivityMap;
import com.coffeeandpower.activity.ActivitySignInViaMail;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.views.CustomDialog;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
	
	public static abstract class Action implements Runnable
	{
		protected DataHolder result;
		protected OAuthService service;
		protected ActivityUtils.ProgressHandler handler;
		protected Runnable action;
		
		public DataHolder getResult() { return result; }
	}
	
	public static class ProgressHandler extends Handler 
	{
		ProgressDialog progress;
		DataHolder result;		
				
		public void setResult(DataHolder result_)
		{
			result = result_;
		}
	}

	public static class JoinProgressHandler extends ProgressHandler {
		
		
		public JoinProgressHandler(Activity a)
		{
			progress = new ProgressDialog(a);
			progress.setOwnerActivity(a);
			progress.setMessage("Accessing Account...");
			progress.show();			
		}
								
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			
			Activity a = progress.getOwnerActivity();
			progress.dismiss();
			
			switch (msg.what){

			case AppCAP.HTTP_ERROR:
				new CustomDialog(a, "Error", "Internet connection error").show();
				break;

			case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:
				if (result!=null){
					new CustomDialog(a, "Error Accessing Account", result.getResponseMessage()).show();
				}
				break;						
			}
		}
	};
	
public static class LoginProgressHandler extends ProgressHandler {
		
		
		public LoginProgressHandler(Activity a)
		{
			progress = new ProgressDialog(a);
			progress.setOwnerActivity(a);
			progress.setMessage("Logging in...");
			progress.show();			
		}
								
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Activity a = progress.getOwnerActivity();
			progress.dismiss();

			switch (msg.what){

			case AppCAP.HTTP_ERROR:				
				new CustomDialog(a, "Error", "Internet connection error").show();
				break;

			case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:				
				new CustomDialog(a, "Error", "Could not login").show();
				break;
				
			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				Intent intent = new Intent(a, ActivityMap.class);							
				a.startActivity(intent);
				a.finish();
				break;
			}
		}
	};

}
