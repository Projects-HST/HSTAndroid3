package com.skilex.skilexserviceperson.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;

public class AboutUsActivity extends BaseActivity {

    private static final String TAG = AboutUsActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


//        WebView wb = (WebView) findViewById(R.id.webView1);
//
//
//            wb.loadUrl("https://www.skilex.in");
//            wb.setWebViewClient(new WebViewClient(){
//
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url){
//                    view.loadUrl(url);
//                    return true;
//                }
//            });
    }

}
