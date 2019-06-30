package com.example.chito.activities;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.WebInterface;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.WebPresenter;
import com.example.chito.view.HtmlView;


public class WebActivity extends AppCompatActivity implements HtmlView {
    private WebPresenter webPresenter;
    private HtmlView htmlView;

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        //主要調配器宣告
        webPresenter = new WebPresenter(this, new MainModel());
        webPresenter.onCreate();
        webView = findViewById(R.id.MainWebView);
        WebSettings webSettings = webView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);

        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new WebInterface(WebActivity.this), "test");//AndroidtoJS类对象映射到js的test对象
        // 加载JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webView.loadUrl("file:///android_asset/www/browse.html");
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:callJS(\"測試測試\")");
            }
        });
    }

    @Override
    public void setContentView() {
        //Remove title bar
        setContentView(R.layout.web_main);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
