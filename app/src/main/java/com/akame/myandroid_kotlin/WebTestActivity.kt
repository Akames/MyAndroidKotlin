package com.akame.myandroid_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.showLog
import kotlinx.android.synthetic.main.activity_web_test.*

class WebTestActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_test)
        webView.settings.javaScriptEnabled=true

        webView.loadUrl("https://www.realtor.ca/meet-a-realtor/#v=realtor")

        webView.webChromeClient =  object :WebChromeClient(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                showLog("---url---"+webView.url)
            }
        }


    }



}
