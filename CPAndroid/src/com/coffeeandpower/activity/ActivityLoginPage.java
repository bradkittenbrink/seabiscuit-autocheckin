package com.coffeeandpower.activity;

import org.scribe.exceptions.OAuthException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.app.R;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.linkedin.LinkedInInitException;
import com.coffeeandpower.utils.ActivityUtils;
import com.coffeeandpower.views.CustomDialog;
import com.coffeeandpower.views.CustomDialog.ClickListener;

public class ActivityLoginPage extends RootActivity {

    private static final String TAG = ActivityLoginPage.class.getSimpleName();

    LinkedIn lastAuthorize = null;

    WebView webView;
    ScrollView scrollWebView;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main_login);

        AppCAP.setShouldFinishActivities(false);

        scrollWebView = (ScrollView) findViewById(R.id.webscroller);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        lastAuthorize = new LinkedIn();
        try {
            lastAuthorize.initialize(
                    (String) getResources().getText(R.string.linkedInApiKey),
                    (String) getResources().getText(R.string.linkedInApiSec));
        } catch (LinkedInInitException e) {
            lastAuthorize = null;
            CustomDialog errorDialog = new CustomDialog(this,
                    getString(R.string.error),
                    getString(R.string.message_linkedin_oauth_error));
            errorDialog.setOnClickListener(new ClickListener() {
                @Override
                public void onClick() {
                    ActivityLoginPage.this.finish();
                }
            });
            errorDialog.show();
            return;
        }

        // if the userlinkedinid is already in the application preferences
        // we are going to try to connect directly without the login page
        if (!AppCAP.getUserLinkedInID().equals("")) {
            connectLinkedIn();
        }

        // Get Screen Width and save it for later (dimensions)
        AppCAP.saveScreenWidth(getDisplayMetrics().widthPixels);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start loging in process from Contacts Activity
        if (AppCAP.isStartingLoginPageFromContacts()) {
            AppCAP.setStartLoginPageFromContacts(false);
            connectLinkedIn();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickLater(View v) {
        AppCAP.setLoggedIn(false);
        AppCAP.setShouldFinishActivities(false);
        startSmartActivity(new Intent(), "ActivityMap");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*
     * public void onClickMail (View v){ startActivity(new
     * Intent(ActivityLoginPage.this, ActivitySignInViaMail.class)); }
     */
    private void connectLinkedIn() {

        if (AppCAP.getUserLinkedInID().equals("")) {
            displayLinkedinLogin();
        } else {
            new Thread(new LoginAction(lastAuthorize,
                    new ActivityUtils.LoginProgressHandler(this), null))
                    .start();
        }
    }

    public void displayLinkedinLogin() {
        (findViewById(R.id.btn_linked_in)).setVisibility(View.GONE);
        (findViewById(R.id.btn_later)).setVisibility(View.GONE);
        (findViewById(R.id.candp_logo)).setVisibility(View.GONE);
        (findViewById(R.id.btn_linked_in)).setVisibility(View.GONE);
        (findViewById(R.id.btn_later)).setVisibility(View.GONE);
        (findViewById(R.id.candp_logo)).setVisibility(View.GONE);

        scrollWebView.setVisibility(View.VISIBLE);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    if (!v.hasFocus()) {
                        v.requestFocus();
                    }
                    break;
                }
                return false;
            }
        });
        final String url = lastAuthorize.getAuthorizationUrl();
        final RootActivity activity = this;

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100)
                    progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // check for our custom callback protocol
                // otherwise use default behavior
                if (lastAuthorize.callbackReceived(url)) {

                    String mess = lastAuthorize.getErrorMessage();
                    if (!mess.contentEquals("")) {
                        showDialog("LinkedIn Error", mess);
                        (findViewById(R.id.btn_linked_in))
                                .setVisibility(View.VISIBLE);
                        (findViewById(R.id.btn_later))
                                .setVisibility(View.VISIBLE);
                        (findViewById(R.id.candp_logo))
                                .setVisibility(View.VISIBLE);
                        scrollWebView.setVisibility(View.GONE);

                    } else {
                        new Thread(
                                new LoginAction(lastAuthorize,
                                        new ActivityUtils.LoginProgressHandler(
                                                activity), null)).start();

                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        scrollWebView.setVisibility(View.VISIBLE);

        progressBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        }, 4000);

    }

    public void showDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void onClickLinkedIn(View v) {
        scrollWebView.setVisibility(View.VISIBLE);

        AppCAP.setShouldFinishActivities(false);
        connectLinkedIn();
    }

    public class DisplayLinkedinLoginAction extends ActivityUtils.Action {
        public DisplayLinkedinLoginAction(LinkedIn service_,
                ActivityUtils.ProgressHandler handler_, Runnable next_) {
            service = service_;
            handler = handler_;
            action = next_;
        }

        @Override
        public void run() {
            // proceed to login
            displayLinkedinLogin();
        }
    }

    public class LoginAction extends ActivityUtils.Action {
        public LoginAction(LinkedIn service_,
                ActivityUtils.ProgressHandler handler_, Runnable next_) {
            service = service_;
            handler = handler_;
            action = next_;
        }

        @Override
        public void run() {
            try {
                // proceed to login
                if (lastAuthorize.isConnected() ||
                    lastAuthorize.reconnectUsingAccessToken(
                            AppCAP.getUserLinkedInToken(),
                            AppCAP.getUserLinkedInTokenSecret())) {
                    service.getUserId();
                    result = AppCAP.getConnection().loginViaOAuthService(service);
                }
            } catch (OAuthException oae) {
                // existing info obviously does us no good, so clear it out
                lastAuthorize.clearSettings();

                Log.e(TAG, "LinkedIn authentication failed with OAuthException: "+oae.getMessage());
                handler.sendEmptyMessage(AppCAP.OAUTH_ERROR);
                return;
            }

            if (result != null) {
                handler.sendEmptyMessage(result.getHandlerCode());
                if (result.getHandlerCode() == AppCAP.HTTP_REQUEST_SUCCEEDED) {
                    service.saveSettings();
                } else {
                    // assume that the tokens have expired...
                    service.clearSettings();
                }
            } else {
                handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
            }
        }
    }
}