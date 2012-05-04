package com.coffeeandpower.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.utils.GraphicUtils;
import com.coffeeandpower.utils.HttpUtil;
import com.coffeeandpower.views.CustomDialog;

public class ActivitySettings extends RootActivity{

	public static final String IMAGE_FOLDER = "/CoffeeAndPower";

	private final static int HANDLE_EMAIL_CHANGE = 1222;
	private final static int HANDLE_NICK_NAME_CHANGE = 1223;
	private final static int HANDLE_USER_PROFILE_PHOTO = 1224;
	private final static int HANDLE_UPLOAD_PROFILE_PHOTO = 1225;
	private final static int PROFILE_PIC_REQUEST = 1455;

	private User loggedUser;

	private EditText textNickName;
	private EditText textEmail;

	private ImageView imageClearNickNameField;
	private ImageView imageClearEmailField;
	private ImageView imageProfilePhoto;

	private ProgressBar progresNickName;
	private ProgressBar progresEmail;
	private ProgressBar progressPhoto;
	private ProgressDialog progressUploadPhoto;

	private DataHolder result;
	private DataHolder resultPhotoDownload;
	private DataHolder resultPhotoUpload;
	private DataHolder resultLoggedUser;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			progresEmail.setVisibility(View.GONE);
			progresNickName.setVisibility(View.GONE);
			progressPhoto.setVisibility(View.GONE);
			progressUploadPhoto.dismiss();

			switch (msg.what) {

			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivitySettings.this, "Error", "Internet connection error").show();
				break;

			case HANDLE_EMAIL_CHANGE:
				textEmail.setVisibility(View.VISIBLE);
				new CustomDialog(ActivitySettings.this, "Info", result.getResponseMessage()).show();
				break;

			case HANDLE_NICK_NAME_CHANGE:
				textNickName.setVisibility(View.VISIBLE);
				new CustomDialog(ActivitySettings.this, "Info", result.getResponseMessage()).show();
				break;

			case HANDLE_USER_PROFILE_PHOTO:
				if (resultPhotoDownload!=null){
					if (resultPhotoDownload.getObject()!=null){
						if (resultPhotoDownload.getObject() instanceof Bitmap){
							imageProfilePhoto.setImageBitmap((Bitmap) resultPhotoDownload.getObject());
						}
					} else {
						imageProfilePhoto.setBackgroundResource(R.drawable.default_avatar25);
					}

				}
				break;

			case AppCAP.HTTP_REQUEST_SUCCEEDED:
				if (resultLoggedUser.getObject()!=null){
					loggedUser = (User) resultLoggedUser.getObject();
					useUserData();
				}
				break;

			case HANDLE_UPLOAD_PROFILE_PHOTO:
				if (resultPhotoUpload!=null){
					if (resultPhotoUpload.getObject()!=null){
						if (resultPhotoUpload.getObject() instanceof String){
							new CustomDialog(ActivitySettings.this, "Info", (String)resultPhotoUpload.getObject()).show();
							loadProfilePhoto();
						}
					}
				}
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// Views
		textNickName = (EditText) findViewById(R.id.edit_nickname);
		textEmail = (EditText) findViewById(R.id.edit_email);
		imageClearNickNameField = (ImageView) findViewById(R.id.imageview_delete_nickname);
		imageClearEmailField = (ImageView) findViewById(R.id.imageview_delete_email);
		imageProfilePhoto = (ImageView) findViewById(R.id.imageview_your_face_here);
		progresNickName = (ProgressBar) findViewById(R.id.progress_nickname);
		progresEmail =(ProgressBar) findViewById(R.id.progress_email);
		progressPhoto = (ProgressBar) findViewById(R.id.progress_photo);
		progressUploadPhoto = new ProgressDialog(this);


		imageClearEmailField.setVisibility(View.GONE);
		imageClearNickNameField.setVisibility(View.GONE);
		progresEmail.setVisibility(View.GONE);
		progresNickName.setVisibility(View.GONE);
		progressPhoto.setVisibility(View.GONE);
		progressUploadPhoto.setMessage("Uploading photo...");

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

		// Get logged user
		progresNickName.setVisibility(View.GONE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultLoggedUser = AppCAP.getConnection().getUserData();
				if (resultLoggedUser.getResponseCode()==AppCAP.HTTP_ERROR){
					handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
				} else {
					handler.sendEmptyMessage(resultLoggedUser.getResponseCode());
				}
			}
		}).start();


		// Load profile image if exist
		loadProfilePhoto();


		// Change Nick Name
		textNickName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					textNickName.setVisibility(View.GONE);
					progresNickName.setVisibility(View.VISIBLE);

					if (loggedUser!=null){
						loggedUser.setNickName(textNickName.getText().toString());

						new Thread(new Runnable() {
							@Override
							public void run() {
								result = AppCAP.getConnection().setUserProfileData(loggedUser, false);
								if (result.getResponseCode()==AppCAP.HTTP_ERROR){
									handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
								} else {
									handler.sendEmptyMessage(HANDLE_NICK_NAME_CHANGE);
								}
							}
						}).start();
					}
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

					if (loggedUser!=null){
						loggedUser.setUserName(textEmail.getText().toString());

						new Thread(new Runnable() {
							@Override
							public void run() {
								result = AppCAP.getConnection().setUserProfileData(loggedUser, true);
								if (result.getResponseCode()==AppCAP.HTTP_ERROR){
									handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
								} else {
									handler.sendEmptyMessage(HANDLE_EMAIL_CHANGE);
								}
							}
						}).start();
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
	}


	/**
	 *  Use user data in GUI
	 */
	private void useUserData(){
		// Set views
		if (loggedUser!=null){
			textNickName.setText(loggedUser.getNickName());
			textEmail.setText(loggedUser.getUserName());
		}
	}


	/**
	 * Load profile photo
	 */
	private void loadProfilePhoto(){
		if (AppCAP.getLocalUserPhotoURL().length()>5){

			progressPhoto.setVisibility(View.VISIBLE);

			new Thread(new Runnable() {
				@Override
				public void run() {
					resultPhotoDownload = HttpUtil.getBitmapFromURL(AppCAP.getLocalUserPhotoURL());
					if (resultPhotoDownload.getResponseCode()==AppCAP.HTTP_ERROR){
						handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
					} else {
						handler.sendEmptyMessage(HANDLE_USER_PROFILE_PHOTO);
					}
				}
			}).start();
		} else {
			imageProfilePhoto.setBackgroundResource(R.drawable.default_avatar25);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private Uri imageUri;

	public void onClickPhoto (View v){

		// Create folders on sdcard for putting images and audio
		File dir = new File (Environment.getExternalStorageDirectory() + IMAGE_FOLDER);
		boolean res = dir.mkdir();

		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		File photo = new File(Environment.getExternalStorageDirectory() + IMAGE_FOLDER, "photo_profile.jpg");

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

		imageUri = Uri.fromFile(photo);
		startActivityForResult(cameraIntent, PROFILE_PIC_REQUEST);

	}

	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent intent){

		switch (requestCode) {

		case PROFILE_PIC_REQUEST:

			if (resultCode == RESULT_OK) { 
				Uri selectedImage = imageUri;

				if (selectedImage!=null){
					getContentResolver().notifyChange(selectedImage, null);

					try{
						OutputStream fOut = null;

						Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
						Bitmap resizedImage = GraphicUtils.resizeProfileImage(bitmap);

						File file = new File(new URI(selectedImage.toString()));
						fOut = new FileOutputStream(file);

						// Set picture compression
						resizedImage.compress(CompressFormat.JPEG, 70, fOut);
						fOut.flush();
						fOut.close();

					} catch (IOException e) {
					} catch (URISyntaxException e) {
					}

					// Upload user Photo
					progressUploadPhoto.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							resultPhotoUpload = AppCAP.getConnection().uploadUserProfilePhoto();
							if (resultPhotoUpload.getResponseCode()==AppCAP.HTTP_ERROR){
								handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
							} else {
								handler.sendEmptyMessage(HANDLE_UPLOAD_PROFILE_PHOTO);
							}
						}
					}).start();

				} else {
					new CustomDialog(ActivitySettings.this, "Info", "Unable to save picture! We are working on that...").show();
				}
			}




			break;
		}
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
		super.onDestroy();
	}
	
	public void onClickJobCategory (View v){
		Intent intent = new Intent(this, ActivityJobCategory.class);
		startActivity(intent);
	}
	
	public void onClickSmartererBadges (View v){
		startActivity(new Intent(this, ActivitySmarterer.class));
	}

}
