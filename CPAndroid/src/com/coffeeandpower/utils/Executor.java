package com.coffeeandpower.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.views.CustomDialog;
import com.google.android.maps.GeoPoint;

public class Executor {
    public static final int HANDLE_GET_USER_RESUME = 1600;
    public static final int HANDLE_SENDING_PROP = 1601;
    public static final int HANDLE_GET_VENUES_AND_USERS_IN_BOUNDS = 1602;
    public static final int HANDLE_GET_USER_DATA = 1603;
    public static final int HANDLE_VENUES_CLOSE_TO_LOCATION = 1604;

    private DataHolder result;

    private ProgressDialog progress;

    private Context context;

    public interface ExecutorInterface {
	public void onActionFinished(int action);

	public void onErrorReceived();
    };

    ExecutorInterface exeInter = new ExecutorInterface() {
	@Override
	public void onActionFinished(int action) {
	}

	@Override
	public void onErrorReceived() {
	}
    };

    public void setExecutorListener(ExecutorInterface exeInter) {
	this.exeInter = exeInter;
    }

    public Executor(Context context) {
	this.context = context;
	progress = new ProgressDialog(context);
    }

    private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);

	    progress.dismiss();

	    if (msg.what == AppCAP.HTTP_ERROR) {
		exeInter.onErrorReceived();
		new CustomDialog(context, "Error", result.getResponseMessage()).show();
	    } else {
		exeInter.onActionFinished(msg.what);
	    }

	}
    };

    public synchronized DataHolder getResult() {
	return result;
    }

    public synchronized void getUserResume(final int userId) {
	progress.setMessage("Loading");
	progress.show();
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().getResumeForUserId(userId);
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }

    public synchronized void sendReviewProp(final UserResume userResume, final String review) {
	progress.setMessage("Sending...");
	progress.show();
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().sendReview(userResume, review);
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }

    public synchronized void getVenuesAndUsersWithCheckinsInBoundsDuringInterval(final double[] coords, boolean withProgress) {
	if (withProgress) {
	    progress.setMessage("Loading...");
	    progress.show();
	}
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().getVenuesAndUsersWithCheckinsInBoundsDuringInterval(coords, 7);
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }

    public synchronized void getUserData() {
	progress.setMessage("Loading...");
	progress.show();
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().getUserData();
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }
    
    public synchronized void getVenuesCloseToLocation(final GeoPoint gp, final int number) {
	progress.setMessage("Loading nearby places...");
	progress.show();
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().getVenuesCloseToLocation(gp, number);
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }
}
