package com.coffeeandpower.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.imageutil.ImageLoader;
import com.coffeeandpower.tab.activities.ActivityMap;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;
import com.coffeeandpower.utils.GraphicUtils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomFontView;

public class ActivitySettings extends RootActivity {

    public static final String IMAGE_FOLDER = "/CoffeeAndPower";
    private final static int PROFILE_PIC_REQUEST = 1455;

    private UserSmart loggedUser;

    private CustomFontView title;
    private EditText textNickName;
    private EditText textEmail;
    private ImageView imageProfilePhoto;
    private RelativeLayout backgroundPhoto;
    private Button deleteAccount;

    private Spinner strongestSkill;
    private Button jobCategory;
    private Spinner profileVisibility;

    private DataHolder result;
    private Executor exe;
    private ImageLoader imageLoader;
    private boolean isUserDataChanged = false;
    private String email_required = "";

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

        case Executor.HANDLE_GET_SKILLS:
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
            
        case Executor.HANDLE_ACCOUNT_DELETE_SUCCEEDED:
            Intent i = new Intent();
            i.setClass(getApplicationContext(), ActivityLoginPage.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            break;
            
        case Executor.HANDLE_ACCOUNT_DELETE_FAILED:
            if (result != null) {
                if (result.getObject() != null) {
                    if (result.getObject() instanceof String) {
                        new CustomDialog(ActivitySettings.this, "Info",
                                (String) result.getObject()).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email_required = extras.getString("email_required");
        }
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
        title = (CustomFontView) findViewById(R.id.textview_tile);
        textNickName = (EditText) findViewById(R.id.edit_nickname);
        textEmail = (EditText) findViewById(R.id.edit_email);
        imageProfilePhoto = (ImageView) findViewById(R.id.imagebutton_user_face);
        backgroundPhoto = (RelativeLayout) findViewById(R.id.activity_user_profile_root);
        strongestSkill = (Spinner) findViewById(R.id.strongSkill);
        jobCategory = (Button) findViewById(R.id.jobCategory);
        profileVisibility = (Spinner) findViewById(R.id.profileVisibility);
        deleteAccount = (Button) findViewById(R.id.deleteAccount);
        
        jobCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),
                        ActivityJobCategory.class);
                startActivity(intent);
            }
        });

        imageProfilePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPhoto(v);
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

                if ((actionId == EditorInfo.IME_ACTION_SEND) || (actionId == EditorInfo.IME_NULL && 
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (loggedUser != null) {
                        loggedUser.setNickName(textNickName.getText()
                                .toString());
                        exe.setUserProfileData(loggedUser, false);
                    }
                }
                return false;
            }
        });

        // Change Email
        textEmail.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEND) || (actionId == EditorInfo.IME_NULL && 
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (loggedUser != null) {
                        loggedUser.setUsername(textEmail.getText().toString());
                        exe.setUserProfileData(loggedUser, true);
                    }
                }
                return false;
            }
        });

        // -- Setup the strongest skill spinner
        String[] skillsList = {};
        ArrayAdapter<String> skillAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, skillsList);
        skillAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strongestSkill.setAdapter(skillAdapter);

        // -- Setup the visibility options spinner
        String[] visibilityOptions = { "Allow anyone", "Only logged in users" };
        ArrayAdapter<String> voAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, visibilityOptions);
        voAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileVisibility.setAdapter(voAdapter);

        deleteAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.activity_settings_profile_delete)
                .setMessage("Delete your user account?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exe.deleteAccount();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    /**
     * Use user data in GUI
     */
    private void useUserData() {
        // Set views
        if (loggedUser != null) {
            imageLoader.DisplayImage(loggedUser.getPhoto(),
                    imageProfilePhoto, R.drawable.default_avatar25, 400);

            class LoadPhotoThread implements Runnable {
                public Activity activity;
                public RelativeLayout layout;
                public String url;

                @Override
                public void run() {
                    Bitmap b = imageLoader.getBitmap(url);
                    final Bitmap background = ImageLoader.FastBlur(b, 4);
                    b.recycle();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (background != null) {
                                layout.setBackgroundDrawable(new BitmapDrawable(
                                        background));
                            }
                        }
                    });
                }
            }

            LoadPhotoThread t = new LoadPhotoThread();
            t.activity = this;
            t.layout = backgroundPhoto;
            t.url = loggedUser.getPhoto();
//            new Thread(t).start();
            
            
            title.setText(getResources().getString(R.string.settings));
            textNickName.setText(loggedUser.getNickName());
            textEmail.setText(loggedUser.getUsername());
            
            String jobCategories = "";
            
            if (loggedUser.getMajorJobCategory().compareTo("") != 0) {
                jobCategories = loggedUser.getMajorJobCategory();
            }
            if (loggedUser.getMinorJobCategory().compareTo("") != 0) {
                if (jobCategories.compareTo("") != 0) {
                    jobCategories = jobCategories + ", ";
                }
                jobCategories = jobCategories + loggedUser.getMinorJobCategory(); 
            }
            
            if (jobCategories.compareTo("") != 0) {
                jobCategory.setText(jobCategories);
            }

            // TODO: Get current skill
        }
    }

    /**
     * Load profile photo
     */
    private void loadProfilePhoto() {
        if (AppCAP.getLocalUserPhotoURL().length() > 5) {
            imageLoader.DisplayImage(AppCAP.getLocalUserPhotoLargeURL(),
                    imageProfilePhoto, R.drawable.default_avatar25, 400);

            class LoadPhotoThread implements Runnable {
                public Activity activity;
                public RelativeLayout layout;
                public String url;

                @Override
                public void run() {
                    Bitmap b = imageLoader.getBitmap(url);
                    final Bitmap background = ImageLoader.FastBlur(b, 4);
                    b.recycle();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (background != null) {
                                layout.setBackgroundDrawable(new BitmapDrawable(
                                        background));
                            }
                        }
                    });
                }
            }

            LoadPhotoThread t = new LoadPhotoThread();
            t.activity = this;
            t.layout = backgroundPhoto;
            t.url = AppCAP.getLocalUserPhotoLargeURL();
            new Thread(t).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (email_required.contentEquals("yes")) {
            Toast.makeText(ActivitySettings.this, "Please enter your email and save your settings!",
                Toast.LENGTH_LONG).show();
        }
    }

    private Uri imageUri;

    public void onClickPhoto(View v) {
        // Create folders on sdcard for putting images and audio
        File dir = new File(Environment.getExternalStorageDirectory()
                + IMAGE_FOLDER);
        dir.mkdir();

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
        if (email_required.contentEquals("yes")) {
            Intent intent = new Intent(ActivitySettings.this, ActivityVenueFeeds.class);
            ActivitySettings.this.startActivity(intent);
            ActivitySettings.this.finish();
            
        } else {
            super.onBackPressed();
        }
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
