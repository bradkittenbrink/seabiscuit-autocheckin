package com.coffeeandpower.activity;

import java.text.DecimalFormat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserTransaction;

public class ActivityWallet extends RootActivity {

    public static final int HANDLE_GET_TRANSACTION_DATA = 1500;

    private ProgressDialog progress;

    private DataHolder result;

    private UserTransaction transaction;

    private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);

	    progress.dismiss();

	    switch (msg.what) {

	    case AppCAP.HTTP_ERROR:

		break;

	    case HANDLE_GET_TRANSACTION_DATA:
		if (result.getObject() instanceof UserTransaction) {
		    transaction = (UserTransaction) result.getObject();
		    fillUserData();
		}
		break;
	    }
	}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_wallet);

	progress = new ProgressDialog(this);

    }

    @Override
    protected void onResume() {
	super.onResume();

	getTransactionData();
    }

    // Fill user transaction data
    private void fillUserData() {
	DecimalFormat oneDForm = new DecimalFormat("#.##");
	float d = Float.valueOf(oneDForm.format(transaction.getBalance()));
	((TextView) findViewById(R.id.balance)).setText("$" + d);
    }

    // Get transaction Data
    private void getTransactionData() {
	progress.setMessage("Loading...");
	progress.show();
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		result = AppCAP.getConnection().getUserTransactionData();
		handler.sendEmptyMessage(result.getResponseCode());
	    }
	}).start();
    }

    public void onClickAddFunds(View v) {
	startActivity(new Intent(ActivityWallet.this, ActivityAddFunds.class));
    }

    public void onClickBack(View v) {
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
