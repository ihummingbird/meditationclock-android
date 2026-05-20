package com.ihummingbird.meditationclock

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        webView = WebView(this)

        // --- FULLSCREEN IMMERSIVE MODE ---
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            window.insetsController?.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        // ---------------------------------


        // WebSettings
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true // For your meditation timers/state
        settings.textZoom = 100


        // These four are CRITICAL for loading local CSS/JS/Fonts from the assets folder
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // 1. SET THE CONTENT VIEW FIRST
        setContentView(webView)

        // 2. LOAD YOUR URL
        webView.loadUrl("file:///android_asset/index.html")


    }




    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
