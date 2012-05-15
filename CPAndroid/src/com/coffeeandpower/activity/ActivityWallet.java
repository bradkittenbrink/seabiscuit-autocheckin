package com.coffeeandpower.activity;

import java.text.DecimalFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserTransaction;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

public class ActivityWallet extends RootActivity {

    private DataHolder result;
    
    private Executor exe;

    private UserTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_wallet);

	// Executor
	exe = new Executor(ActivityWallet.this);
	exe.setExecutorListener(new ExecutorInterface() {
	    @Override
	    public void onErrorReceived() {
	    }

	    @Override
	    public void onActionFinished(int action) {
		result = exe.getResult();
		switch (action) {
		
		case Executor.HANDLE_GET_USER_TRANSACTION_DATA:
		    if (result.getObject() instanceof UserTransaction) {
			    transaction = (UserTransaction) result.getObject();
			    fillUserData();
			}
		    break;
		}
	    }
	});
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
	exe.getUserTransactionData();
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
