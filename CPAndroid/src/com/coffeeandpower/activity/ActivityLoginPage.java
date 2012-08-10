package com.coffeeandpower.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.inter.OAuthService;
import com.coffeeandpower.linkedin.LinkedIn;
import com.coffeeandpower.tab.activities.ActivityVenueFeeds;
import com.coffeeandpower.utils.ActivityUtils;

public class ActivityLoginPage extends RootActivity {

	OAuthService lastAuthorize = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);

		AppCAP.setShouldFinishActivities(false);

		// Start loging in process from Contacts Activity
		if (AppCAP.isStartingLoginPageFromContacts()) {
			AppCAP.setStartLoginPageFromContacts(false);
			connectLinkedIn();
		}

		// Get Screen Width and save it for later (dimensions)
		AppCAP.saveScreenWidth(getDisplayMetrics().widthPixels);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClickLater(View v) {
		AppCAP.setLoggedIn(false);
		AppCAP.setShouldFinishActivities(false);
		startActivity(new Intent(ActivityLoginPage.this, ActivityVenueFeeds.class));
		onBackPressed();
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
		lastAuthorize = new LinkedIn();
        lastAuthorize.initialize(
                (String) getResources().getText(R.string.linkedInApiKey),
                (String) getResources().getText(R.string.linkedInApiSec));
		if (AppCAP.getUserLinkedInID().equals("")) {
            new Thread(new OAuthAuthorizeAction(lastAuthorize,
                    new ActivityUtils.JoinProgressHandler(this), null)).start();
		} else {
            new Thread(new LoginAction(lastAuthorize,
                    new ActivityUtils.LoginProgressHandler(this), null))
                    .start();
		}
	}

	public void onClickLinkedIn(View v) {
		AppCAP.setShouldFinishActivities(false);
		connectLinkedIn();
	}

	public void onNewIntent(Intent intent) {
	    if (intent.getData() == null) {
	        return;
	    }
		String verifier = intent.getData().getQueryParameter("oauth_verifier");
		if (lastAuthorize == null)
			return;

		if (lastAuthorize.verify(verifier)) {
			// the service provider
            new Thread(
                    new OAuthSignUpAction(
                            lastAuthorize,
			// the progress handler for this action
                            new ActivityUtils.JoinProgressHandler(this),
                            new LoginAction(
                                    lastAuthorize,
                                    new ActivityUtils.LoginProgressHandler(this),
                                    null))).start();
		}
	}

	public class OAuthAuthorizeAction extends ActivityUtils.Action {
        public OAuthAuthorizeAction(OAuthService service_,
                ActivityUtils.ProgressHandler handler_, Runnable next_) {
			service = service_;
			handler = handler_;
			action = next_;
		}

		public void run() {
			Intent webAuthorize = service.authorize();
			if (webAuthorize != null) {
				ActivityLoginPage.this.startActivity(webAuthorize);
				handler.sendEmptyMessage(AppCAP.HTTP_REQUEST_SUCCEEDED);
			} else
				handler.sendEmptyMessage(AppCAP.ERROR_SUCCEEDED_SHOW_MESS);
		}
	}

	public class OAuthSignUpAction extends ActivityUtils.Action {
        public OAuthSignUpAction(OAuthService service_,
                ActivityUtils.ProgressHandler handler_, Runnable next_) {
			service = service_;
			handler = handler_;
			action = next_;
		}

		@Override
		public void run() {
			// pass the api key and secret for authorization
			service.saveSettings();
			result = AppCAP.getConnection().signupViaOAuthService(service);
			handler.setResult(result);
			if (result != null) {
				handler.sendEmptyMessage(result.getHandlerCode());
                if (result.getHandlerCode() == AppCAP.HTTP_REQUEST_SUCCEEDED
                        && action != null)
					new Thread(action, "ActivityLoginPage.connectLinkedIn.OAuthSignUpAction").start();
			} else {
				handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
			}
		}
	}

	public class LoginAction extends ActivityUtils.Action {
        public LoginAction(OAuthService service_,
                ActivityUtils.ProgressHandler handler_, Runnable next_) {
			service = service_;
			handler = handler_;
			action = next_;
		}

		@Override
		public void run() {
			// proceed to login
			if (!lastAuthorize.isConnected())
                lastAuthorize.reconnect(AppCAP.getUserLinkedInToken(),
                        AppCAP.getUserLinkedInTokenSecret());
			service.getUserId();
			result = AppCAP.getConnection().loginViaOAuthService(service);
			if (result != null) {
				handler.sendEmptyMessage(result.getHandlerCode());
				if (result.getHandlerCode() == AppCAP.ERROR_SUCCEEDED_SHOW_MESS)
					service.clearSettings();
				// assume that the tokens have expired...
			} else {
				handler.sendEmptyMessage(AppCAP.HTTP_ERROR);
			}
		}
	}
}