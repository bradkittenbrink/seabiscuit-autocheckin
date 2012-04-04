package com.coffeeandpower.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.views.CustomDialog;

public class ActivitySettings extends RootActivity{

	private final static int HANDLE_EMAIL_CHANGE = 1222;
	private final static int HANDLE_NICK_NAME_CHANGE = 1223;

	private User loggedUser;

	private EditText textNickName;
	private EditText textEmail;

	private ImageView imageClearNickNameField;
	private ImageView imageClearEmailField;

	private ProgressBar progresNickName;
	private ProgressBar progresEmail;

	private DataHolder result;


	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivitySettings.this, "Error", "Internet connection error").show();
				break;

			case HANDLE_EMAIL_CHANGE:
				progresEmail.setVisibility(View.GONE);
				textEmail.setVisibility(View.VISIBLE);
				new CustomDialog(ActivitySettings.this, "Info", result.getResponseMessage()).show();
				break;

			case HANDLE_NICK_NAME_CHANGE:
				progresNickName.setVisibility(View.GONE);
				textNickName.setVisibility(View.VISIBLE);
				new CustomDialog(ActivitySettings.this, "Info", result.getResponseMessage()).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);


		// Get User Obj from intent
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null){

			loggedUser = (User) bundle.getSerializable("user_obj");
		}


		// Views
		textNickName = (EditText) findViewById(R.id.edit_nickname);
		textEmail = (EditText) findViewById(R.id.edit_email);
		imageClearNickNameField = (ImageView) findViewById(R.id.imageview_delete_nickname);
		imageClearEmailField = (ImageView) findViewById(R.id.imageview_delete_email);
		progresNickName = (ProgressBar) findViewById(R.id.progress_nickname);
		progresEmail =(ProgressBar) findViewById(R.id.progress_email);


		// Set views
		if (loggedUser!=null){
			textNickName.setText(loggedUser.getNickName());
			textEmail.setText(loggedUser.getUserName());
		}
		imageClearEmailField.setVisibility(View.GONE);
		imageClearNickNameField.setVisibility(View.GONE);

		progresEmail.setVisibility(View.GONE);
		progresNickName.setVisibility(View.GONE);

		textNickName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					imageClearNickNameField.setVisibility(View.VISIBLE);
				} else {
					imageClearNickNameField.setVisibility(View.GONE);
				}
			}
		});

		// Change Nick Name
		textNickName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					textNickName.setVisibility(View.GONE);
					progresNickName.setVisibility(View.VISIBLE);

					loggedUser.setNickName(textNickName.getText().toString());

					new Thread(new Runnable() {
						@Override
						public void run() {
							result = AppCAP.getConnection().setUserProfileData(loggedUser, false);
							if (result!=null){
								handler.sendEmptyMessage(HANDLE_NICK_NAME_CHANGE);
							} else {
								handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
							}
						}
					}).start();
					break;

				default:
					break;
				}
				return false;
			}
		});

		textEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					imageClearEmailField.setVisibility(View.VISIBLE);
				} else {
					imageClearEmailField.setVisibility(View.GONE);
				}
			}
		});

		// Change Email
		textEmail.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					textEmail.setVisibility(View.GONE);
					progresEmail.setVisibility(View.VISIBLE);

					loggedUser.setUserName(textEmail.getText().toString());

					new Thread(new Runnable() {
						@Override
						public void run() {
							result = AppCAP.getConnection().setUserProfileData(loggedUser, true);
							if (result!=null){
								handler.sendEmptyMessage(HANDLE_EMAIL_CHANGE);
							} else {
								handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
							}
						}
					}).start();
					break;

				default:
					break;
				}
				return false;
			}
		});
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	public void onClickClearNickName (View v){
		textNickName.setText("");
	}


	public void onClickClearEmail (View v){
		textEmail.setText("");
	}

	public void onClickBack (View v){
		onBackPressed();
	}


	@Override
	public void onBackPressed() {

		if (result!=null){
			if (result.getObject()!=null){
				if ((Boolean) result.getObject()){

					// Nick name or Email was changed, so get user data again, refresh it
					setResult(ActivityMap.ACCOUNT_CHANGED);
				}
			}
		}
		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
