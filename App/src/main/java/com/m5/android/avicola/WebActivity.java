package com.m5.android.avicola;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.tracking.GoogleAnalytics;

public class WebActivity extends ActionBarActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.web);
        webView.loadUrl(getIntent().getStringExtra(Constants.EXTRAS_URL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppContext.ga().sendView(GoogleAnalytics.ScreenName.WEB);
    }
}
