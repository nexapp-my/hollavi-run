package com.groomify.hollavirun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class TermsAndConditionActivity extends AppCompatActivity {

    private static final String ASSETS_TERM_OF_USE_LOCATION = "file:///android_asset/GroomifyTermsofUse.html";
    private static final String ASSETS_PRIVACY_POLICY_LOCATION = "file:///android_asset/GroomifyPrivacyPolicy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        int type = getIntent().getIntExtra("TYPE", 1);

        if(type == 1){
            myWebView.loadUrl(ASSETS_TERM_OF_USE_LOCATION);
        }else{
            myWebView.loadUrl(ASSETS_PRIVACY_POLICY_LOCATION);
        }



    }
}
