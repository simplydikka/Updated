package com.solodroid.androidebookappdemo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnKeyListener;

import com.solodroid.androidebookappdemo.Config;
import com.solodroid.androidebookappdemo.R;
import com.solodroid.androidebookappdemo.utilities.JsonConstant;

public class ActivityPdfView extends AppCompatActivity {

    int position;
    WebView webview;
    String str_title, str_url;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);
        swipeRefreshLayout.setEnabled(false);

        Intent i = getIntent();
        position = i.getIntExtra("POSITION", 0);
        str_title = i.getStringExtra("TITLE");
        str_url = i.getStringExtra("URL");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(str_title);
        }

        webview = (WebView) findViewById(R.id.webView);

        // web settings
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setSupportZoom(true);
        //webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webview.setWebViewClient(new myWebClient());
        webview.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Config.SERVER_URL + "/upload/pdf/" + str_url);

        webview.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
                    webview.goBack();
                    return true;
                }
                return false;
            }
        });

    }

    public class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed to Connect Network!", Toast.LENGTH_LONG).show();
            view.loadUrl("about:blank");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
