package com.github.lany192.yellow;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.github.lany192.yellow.R;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/test/webview")
public class TestWebview extends Activity {
    @Autowired
    String url;

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);
        webview = findViewById(R.id.webview);
        webview.loadUrl(url);
    }
}
