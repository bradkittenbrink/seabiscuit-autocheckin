package com.coffeeandpower.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

public class ActivityEnterInviteCode extends Activity {

    private DataHolder result;

    private Executor exe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_enter_invite_code);

	// Executor
	exe = new Executor(ActivityEnterInviteCode.this);
	exe.setExecutorListener(new ExecutorInterface() {
	    @Override
	    public void onErrorReceived() {
		errorReceived();
	    }

	    @Override
	    public void onActionFinished(int action) {
		actionFinished(action);
	    }
	});

	((EditText) findViewById(R.id.edit_text)).setOnEditorActionListener(new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_SEND) {
		    exe.enterInvitationCode(((EditText) findViewById(R.id.edit_text)).getText().toString(), AppCAP.getUserCoordinates()[4],
			    AppCAP.getUserCoordinates()[5]);
		}
		return false;
	    }
	});
    }

    public void onClickInviteLater(View v) {
	AppCAP.setNotFirstStart();
	onBackPressed();
    }

    private void errorReceived() {

    }

    private void actionFinished(int action) {
	result = exe.getResult();

	switch (action) {

	case Executor.HANDLE_ENTER_INVITATION_CODE:
	    break;

	}
    }

}
