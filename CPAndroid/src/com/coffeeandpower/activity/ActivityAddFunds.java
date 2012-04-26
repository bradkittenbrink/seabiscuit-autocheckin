package com.coffeeandpower.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

public class ActivityAddFunds extends RootActivity{

	private ProgressDialog progress;

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_funds);

		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");


		webView = (WebView)findViewById(R.id.web_view);
		new WebViewTask().execute(); 

		/*
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.dismiss();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progress.show();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return true;
			}

		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setBlockNetworkImage(false);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setPluginsEnabled(true);

		CookieManager.getInstance().setAcceptCookie(true);

		//webSettings.setJavaScriptEnabled(true);
		//webSettings.setUserAgentString("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1C25 Safari/419.3");
		webSettings.setUserAgent(0);

		// Load Url
		webView.loadUrl(AppCAP.URL_FUNDS);
		 */
	}


	/**
	 * WebView Task
	 * @author Desktop1
	 *
	 */
	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {  
		String sessionCookie;  
		CookieManager cookieManager;  

		@Override  
		protected void onPreExecute() {  
			CookieSyncManager.createInstance(ActivityAddFunds.this);  
			cookieManager = CookieManager.getInstance();  

			sessionCookie = AppCAP.getCookieString();  
			if (sessionCookie != null) {  
				// delete old cookies  
				cookieManager.removeSessionCookie();   
			}  
			super.onPreExecute();  
		}  

		protected Boolean doInBackground(Void... param) {  
			SystemClock.sleep(1000);  
			return false;  
		}  

		@Override  
		protected void onPostExecute(Boolean result) {  
			if (sessionCookie != null) {  
				Log.d("LOG", "addfunds: " + sessionCookie);
				cookieManager.setCookie("coffeeandpower.com", sessionCookie);  
				CookieSyncManager.getInstance().sync();  
			}  
			WebSettings webSettings = webView.getSettings();  
			webSettings.setJavaScriptEnabled(true);  
			webSettings.setPluginsEnabled(true);

			webView.setWebViewClient(new WebViewClient() { 
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					progress.dismiss();
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					progress.show();
				}
				@Override  
				public boolean shouldOverrideUrlLoading(WebView view, String url) {  
					return super.shouldOverrideUrlLoading(view, url);  
				}  
			});  
			webView.loadUrl(AppCAP.URL_FUNDS);  
		}  
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClickBack (View v){
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
