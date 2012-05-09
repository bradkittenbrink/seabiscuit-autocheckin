package com.coffeeandpower.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.views.CustomDialog;

public class ActivityEnterInviteCode extends Activity {

    public static final int HANDLE_ENTER_INV_CODE = 1500;

    private DataHolder result;

    private ProgressDialog progress;

    private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);

	    progress.dismiss();

	    switch (msg.what) {

	    case AppCAP.HTTP_ERROR:
		new CustomDialog(ActivityEnterInviteCode.this, "Error", result.getResponseMessage()).show();
		break;

	    case HANDLE_ENTER_INV_CODE:
		// Do something
		break;
	    }
	}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_enter_invite_code);

	progress = new ProgressDialog(this);
	progress.setMessage("Sending...");

	((EditText) findViewById(R.id.edit_text)).setOnEditorActionListener(new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_SEND) {
		    progress.show();
		    new Thread(new Runnable() {
			@Override
			public void run() {
			    result = AppCAP.getConnection().enterInvitationCode(
				    ((EditText) findViewById(R.id.edit_text)).getText().toString(),
				    AppCAP.getUserCoordinates()[4], AppCAP.getUserCoordinates()[5]);
			    handler.sendEmptyMessage(result.getResponseCode());
			}
		    }).start();
		}
		return false;
	    }
	});
    }

    public void onClickInviteLater(View v) {
	onBackPressed();
    }

}
