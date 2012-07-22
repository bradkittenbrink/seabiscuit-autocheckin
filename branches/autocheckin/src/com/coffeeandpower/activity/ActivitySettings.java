package com.coffeeandpower.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.GraphicUtils;
import com.coffeeandpower.views.CustomDialog;

public class ActivitySettings extends RootActivity {

	public static final String IMAGE_FOLDER = "/CoffeeAndPower";
	private final static int PROFILE_PIC_REQUEST = 1455;

	private UserSmart loggedUser;

	private EditText textNickName;
	private EditText textEmail;
	private ImageView imageClearNickNameField;
	private ImageView imageClearEmailField;
	private ImageView imageProfilePhoto;

	private DataHolder result;
	private Executor exe;
	private ImageLoader imageLoader;
    private boolean isUserDataChanged = false;

	private void errorReceived() {

	}

	private void actionFinished(int action) {
		result = exe.getResult();

		switch (action) {

		case Executor.HANDLE_SET_USER_PROFILE_DATA:
			textEmail.setVisibility(View.VISIBLE);
			textNickName.setVisibility(View.VISIBLE);
            new CustomDialog(ActivitySettings.this, "Info",
                    result.getResponseMessage()).show();
			isUserDataChanged = true;
			break;

		case Executor.HANDLE_GET_USER_DATA:
			if (result.getObject() != null) {
				loggedUser = (UserSmart) result.getObject();
				useUserData();
			}
			break;

		case Executor.HANDLE_UPLOAD_USER_PROFILE_PHOTO:
			if (result != null) {
				if (result.getObject() != null) {
					if (result.getObject() instanceof String) {
                        new CustomDialog(ActivitySettings.this, "Info",
                                (String) result.getObject()).show();
						loadProfilePhoto();
					}
				}
			}
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// Executor
		exe = new Executor(ActivitySettings.this);
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

		imageLoader = new ImageLoader(this);

		// Views
		textNickName = (EditText) findViewById(R.id.edit_nickname);
		textEmail = (EditText) findViewById(R.id.edit_email);
		imageClearNickNameField = (ImageView) findViewById(R.id.imageview_delete_nickname);
		imageClearEmailField = (ImageView) findViewById(R.id.imageview_delete_email);
		imageProfilePhoto = (ImageView) findViewById(R.id.imageview_your_face_here);
		imageClearEmailField.setVisibility(View.GONE);
		imageClearNickNameField.setVisibility(View.GONE);

		textNickName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imageClearNickNameField.setVisibility(View.VISIBLE);
				} else {
					imageClearNickNameField.setVisibility(View.GONE);
				}
			}
		});

		// Get logged user
		exe.getUserData();

		// Load profile image if exist
		loadProfilePhoto();

		// Change Nick Name
		textNickName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					textNickName.setVisibility(View.GONE);

					if (loggedUser != null) {
                        loggedUser.setNickName(textNickName.getText()
                                .toString());
						exe.setUserProfileData(loggedUser, false);
					}
					break;
				}
				return false;
			}
		});

		textEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imageClearEmailField.setVisibility(View.VISIBLE);
				} else {
					imageClearEmailField.setVisibility(View.GONE);
				}
			}
		});

		// Change Email
		textEmail.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					textEmail.setVisibility(View.GONE);

					if (loggedUser != null) {
						loggedUser.setNickName(textEmail.getText().toString());
						exe.setUserProfileData(loggedUser, true);
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
	 * Use user data in GUI
	 */
	private void useUserData() {
		// Set views
		if (loggedUser != null) {
			textNickName.setText(loggedUser.getNickName());
			textEmail.setText(loggedUser.getNickName());
		}
	}

	/**
	 * Load profile photo
	 */
	private void loadProfilePhoto() {
		if (AppCAP.getLocalUserPhotoURL().length() > 5) {
            imageLoader.DisplayImage(AppCAP.getLocalUserPhotoURL(),
                    imageProfilePhoto, R.drawable.default_avatar25, 70);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private Uri imageUri;

	public void onClickPhoto(View v) {
		// Create folders on sdcard for putting images and audio
        File dir = new File(Environment.getExternalStorageDirectory()
                + IMAGE_FOLDER);
		boolean res = dir.mkdir();

        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory()
                + IMAGE_FOLDER, "photo_profile.jpg");

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

		imageUri = Uri.fromFile(photo);
		startActivityForResult(cameraIntent, PROFILE_PIC_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		switch (requestCode) {

		case PROFILE_PIC_REQUEST:

			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageUri;

				if (selectedImage != null) {
					getContentResolver().notifyChange(selectedImage, null);

					try {
						OutputStream fOut = null;

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), selectedImage);
                        Bitmap resizedImage = GraphicUtils
                                .resizeProfileImage(bitmap);

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
					exe.uploadUserProfilePhoto();

				} else {
                    new CustomDialog(ActivitySettings.this, "Info",
                            "Unable to save picture! We are working on that...")
                            .show();
				}
			}

			break;
		}
	}

	public void onClickClearNickName(View v) {
		textNickName.setText("");
	}

	public void onClickClearEmail(View v) {
		textEmail.setText("");
	}

	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (isUserDataChanged) {
			setResult(ActivityMap.ACCOUNT_CHANGED);
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClickJobCategory(View v) {
		Intent intent = new Intent(this, ActivityJobCategory.class);
		startActivity(intent);
	}

	public void onClickSmartererBadges(View v) {
		startActivity(new Intent(this, ActivitySmarterer.class));
	}

}
