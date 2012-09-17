package com.coffeeandpower.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;

public class ActivityAddFunds extends RootActivity {

    private ProgressDialog progress;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");

        webView = (WebView) findViewById(R.id.web_view);
        new WebViewTask().execute();
    }

    /**
     * WebView Task
     * 
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
                if (Constants.debugLog)
                    Log.d("LOG", "addfunds: " + sessionCookie);
                cookieManager.setCookie("coffeeandpower.com", sessionCookie);
                CookieSyncManager.getInstance().sync();
            }
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
                }

                @Override
                public void onPageStarted(WebView view, String url,
                        Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progress.show();
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                        String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description,
                            failingUrl);
                    if (Constants.debugLog)
                        Log.d("LOG", "onReceivedError: " + errorCode + ":"
                                + description);
                }

                @Override
                public void onReceivedSslError(WebView view,
                        SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    if (Constants.debugLog)
                        Log.d("LOG",
                                "onReceivedSslError: "
                                        + error.getPrimaryError());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url,
                        String message, JsResult result) {
                    Log.e("LOG", "JSAlert" + message);
                    return false;
                }
            });
            webView.loadUrl(AppCAP.URL_FUNDS);
        }
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
