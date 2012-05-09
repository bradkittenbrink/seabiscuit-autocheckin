package com.coffeeandpower.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;

public class ActivitySmarterer extends RootActivity {

    private static final String SMARTERER_KEY = "3f883e6fc3d54834ac93c3bfe6f33553";
    private static final String SMARTERER_SECRET = "ea670a5ca21c7d54d4e17972059b4f07";
    private static final String SMARTERER_CALLBACK_URL = "candp://smarterer";

    private ProgressDialog progress;

    private WebView webView;

    private String code;

    private boolean haveCode;
    private boolean haveToken;

    {
	// Code for getting credentials
	code = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_smarterer);

	progress = new ProgressDialog(this);
	progress.setMessage("Loading...");

	webView = (WebView) findViewById(R.id.web_view);
	WebSettings webSettings = webView.getSettings();
	webSettings.setJavaScriptEnabled(true);
	webSettings.setPluginsEnabled(true);
	webSettings.setLoadsImagesAutomatically(true);
	webSettings.setSupportZoom(false);

	webView.setWebViewClient(new WebViewClient() {
	    @Override
	    public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		progress.dismiss();
		Log.d("LOG", "URL: " + url);

		if (haveCode && !haveToken) {
		    haveToken = true;
		    webView.loadUrl("https://smarterer.com/oauth/access_token?client_id=" + SMARTERER_KEY + "&client_secret="
			    + SMARTERER_SECRET + "&grant_type=authorization_code&code=" + code);
		}
	    }

	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		Log.d("LOG", "URL started: " + url);
		progress.show();

		if (url.contains("code=") && !haveCode) {
		    code = url.substring(url.indexOf("code=") + 5);
		    if (code != null && code.length() > 0) {
			haveCode = true;
		    }
		}
	    }

	    @Override
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		Log.d("LOG", "onReceivedError: " + errorCode + ":" + description);
	    }

	    @Override
	    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		super.onReceivedSslError(view, handler, error);
		handler.proceed();
		Log.d("LOG", "onReceivedSslError: " + error.getPrimaryError());
	    }

	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	    }
	});

	webView.setWebChromeClient(new WebChromeClient() {
	    @Override
	    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		Log.e("LOG", "JSAlert" + message);
		return true;
	    }
	});
	webView.loadUrl("https://smarterer.com/oauth/authorize?client_id=" + SMARTERER_KEY + "&callback_url="
		+ SMARTERER_CALLBACK_URL);
	// webView.loadUrl("http://www.google.com");
    }

    @Override
    protected void onResume() {
	super.onResume();
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
