package com.example.javBridge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_web)
        val webView = WebView(this)
//        webView.webViewClient = WebViewClient()
        setContentView(webView)
        webView.loadUrl("https://tellme.pw/avmo")
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
//            webView.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
}