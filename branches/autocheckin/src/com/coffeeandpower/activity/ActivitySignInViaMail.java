package com.coffeeandpower.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.views.CustomDialog;

/**
 * Unused
 * 
 * @author Desktop1
 * 
 */
public class ActivitySignInViaMail extends RootActivity {

	private EditText editTextPassword;
	private EditText editTextEmail;

	private TextView textViewErrorMsg;

	private ProgressDialog progress;

	private String userName;
	private String password;

	private DataHolder result;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what) {

			case AppCAP.HTTP_ERROR:
				textViewErrorMsg.setVisibility(View.INVISIBLE);
				new CustomDialog(ActivitySignInViaMail.this, "Error", "Internet connection error").show();
				break;

			case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:

				// now i should show message like
				// result.getResponseMess() but
				// it's ok like this for now
				textViewErrorMsg.setText("Unable to login. Email and password do not match.");
				textViewErrorMsg.setVisibility(View.VISIBLE);
				break;

			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				textViewErrorMsg.setVisibility(View.INVISIBLE);

				Intent intent = new Intent(ActivitySignInViaMail.this, ActivityMap.class);

				// Get user data from login response

				// User user = (User) result.getObject();
				// intent.putExtra("user", user);
				startActivity(intent);
				finish();

				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		// Views
		editTextEmail = (EditText) findViewById(R.id.edittext_sign_in_email);
		editTextPassword = (EditText) findViewById(R.id.edittext_sign_in_password);
		textViewErrorMsg = (TextView) findViewById(R.id.textview_unable_to_login);
		progress = new ProgressDialog(ActivitySignInViaMail.this);

		// Views states
		textViewErrorMsg.setVisibility(View.INVISIBLE);
		editTextEmail.setText(AppCAP.getUserEmail());
		progress.setMessage("Logging in...");

		// Other

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClickSignIn(View v) {

		userName = editTextEmail.getText().toString();
		password = editTextPassword.getText().toString();

		// Check for valid email
		if (userName.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {

			AppCAP.setUserEmail(userName);
			progress.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					result = AppCAP.getConnection().login(userName, password);
					if (result.getHandlerCode() == AppCAP.HTTP_ERROR) {
						handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
					} else {
						handler.sendEmptyMessage(result.getHandlerCode());
					}
				}
			},"ActivitySignInViaMail.onClickSignIn").start();

		} else {
			textViewErrorMsg.setText("You did not enter a valid email address.");
			textViewErrorMsg.setVisibility(View.VISIBLE);
		}
	}

	public void onClickJoin(View v) {
		startActivity(new Intent(ActivitySignInViaMail.this, ActivityJoin.class));
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
