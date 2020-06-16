package com.akame.commonlib.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * @Author: Administrator
 * @Date: 2018/10/16
 * @Description: 基础webView
 */
public class BaseWebView extends WebView {
    public BaseWebView(Context context) {
        super(context);
        config();
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        config();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        config();
    }

    private void config() {
        WebSettings webSettings = getSettings();
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //允许js代码
        webSettings.setJavaScriptEnabled(true);
        //允许js弹框
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        // 调整到适合webView大小
        webSettings.setUseWideViewPort(true);
        // 调整到适合webView大小
        webSettings.setLoadWithOverviewMode(true);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
       /* this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });*/
    }

}
