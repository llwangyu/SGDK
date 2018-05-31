package com.mwj.lhn.sgdk.mwj;

import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.mwj.lhn.sgdk.R;
import com.mwj.lhn.sgdk.pub.Basic;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebviewActivity extends AppCompatActivity {

    private com.tencent.smtt.sdk.WebView tencent_webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        tencent_webview =  findViewById(R.id.wb);
        tencent_webview.loadUrl(Basic.weburl);
        com.tencent.smtt.sdk.WebSettings webSettings = tencent_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tencent_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tencent_webview.canGoBack()) {
            tencent_webview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
