package com.coffeeandpower.activity;

import android.app.ProgressDialog;
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
import com.coffeeandpower.views.CustomDialog;

/**
 * Unused
 */
public class ActivityJoin extends RootActivity {

	private EditText editTextEmail;
	private EditText editTextPassword;
	private EditText editTextRePassword;
	private EditText editTextNickName;

	private TextView textViewEmailError;
	private TextView textViewPassError;
	private TextView textViewNickError;

	private ProgressDialog progress;

	private String userName;
	private String password;
	private String rePassword;
	private String nickName;

	private DataHolder result;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progress.dismiss();
			setAllErrorTextsInvisible();

			switch (msg.what) {

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityJoin.this, "Error", "Internet connection error").show();
				break;

			case AppCAP.ERROR_SUCCEEDED_SHOW_MESS:
				if (result != null) {
					new CustomDialog(ActivityJoin.this, "Error Creating Account", result.getResponseMessage()).show();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		// Views
		editTextNickName = (EditText) findViewById(R.id.edittext_join_nickname);
		editTextEmail = (EditText) findViewById(R.id.edittext_join_email);
		editTextPassword = (EditText) findViewById(R.id.edittext_join_password);
		editTextRePassword = (EditText) findViewById(R.id.edittext_join_re_type_password);
		textViewEmailError = (TextView) findViewById(R.id.textview_login_email);
		textViewPassError = (TextView) findViewById(R.id.textview_login_password);
		textViewNickError = (TextView) findViewById(R.id.textview_nick_name);
		progress = new ProgressDialog(ActivityJoin.this);

		// Views states
		editTextEmail.setText(AppCAP.getUserEmail());
		textViewEmailError.setVisibility(View.INVISIBLE);
		textViewNickError.setVisibility(View.INVISIBLE);
		textViewPassError.setVisibility(View.INVISIBLE);
		progress.setMessage("Creating account...");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClickJoin(View v) {

		setAllErrorTextsInvisible();

		userName = editTextEmail.getText().toString();
		password = editTextPassword.getText().toString();
		rePassword = editTextRePassword.getText().toString();
		nickName = editTextNickName.getText().toString();

		boolean condition = true;

		if (!userName.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
			textViewEmailError.setVisibility(View.VISIBLE);
			condition = false;
		}

		if (password.length() < 5) {
			textViewPassError.setText("Your password must be more than 5 characters.");
			textViewPassError.setVisibility(View.VISIBLE);
			condition = false;
		}

		if (nickName.length() < 3) {
			textViewNickError.setVisibility(View.VISIBLE);
			condition = false;
		}

		if (condition) {

			if (password.equals(rePassword)) {

				progress.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						result = AppCAP.getConnection().signup(userName, password, nickName);
						if (result.getHandlerCode() == AppCAP.HTTP_ERROR) {
							handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
						} else {
							handler.sendEmptyMessage(result.getHandlerCode());
						}
					}
				}).start();
			} else {
				textViewPassError.setText("The passwords did not match.");
				textViewPassError.setVisibility(View.VISIBLE);
			}
		}
	}

	private void setAllErrorTextsInvisible() {
		textViewEmailError.setVisibility(View.INVISIBLE);
		textViewNickError.setVisibility(View.INVISIBLE);
		textViewPassError.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
