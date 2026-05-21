package com.ihummingbird.meditationclock

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.util.Rational

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        // --- FULLSCREEN IMMERSIVE MODE ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            window.insetsController?.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        // ---------------------------------

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.textZoom = 100

        // --- CRITICAL FOR SCALING IN PIP ---
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        // -----------------------------------

        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript("var pipBtn = document.getElementById('btn-pip'); if(pipBtn) pipBtn.style.display = 'none';", null)
            }
        }

        webView.webChromeClient = WebChromeClient()

        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    // --- TRIGGER PIP ON HOME BUTTON PRESS ---
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 1. FREEZE THE UI: Lock dimensions in exact pixels BEFORE the window shrinks.
            // This prevents CSS vh/vw from squishing the layout.
            webView.evaluateJavascript("""
                window.lockedWidth = window.innerWidth;
                window.lockedHeight = window.innerHeight;
                
                document.documentElement.style.width = window.lockedWidth + 'px';
                document.documentElement.style.height = window.lockedHeight + 'px';
                document.body.style.width = window.lockedWidth + 'px';
                document.body.style.height = window.lockedHeight + 'px';
                
                var meta = document.querySelector('meta[name="viewport"]');
                if (meta) {
                    meta.setAttribute('content', 'width=' + window.lockedWidth + ', user-scalable=no');
                }
                
                if (document.getElementById('navbar')) {
                    document.getElementById('navbar').style.display = 'none';
                }
            """.trimIndent(), null)

            // 2. ENTER PIP
            val pipBuilder = PictureInPictureParams.Builder()
            val width = webView.width
            val height = webView.height

            if (width > 0 && height > 0) {
                pipBuilder.setAspectRatio(Rational(width, height))
            } else {
                pipBuilder.setAspectRatio(Rational(9, 16))
            }

            enterPictureInPictureMode(pipBuilder.build())
        }
    }

    // --- RESTORE ELEMENTS WHEN EXITING PIP ---
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        // We only need to handle the EXIT here, because the ENTER logic was handled in onUserLeaveHint
        if (!isInPictureInPictureMode) {
            webView.evaluateJavascript("""
                // UNFREEZE UI: Remove fixed pixel heights to restore responsive behavior
                document.documentElement.style.width = '';
                document.documentElement.style.height = '';
                document.body.style.width = '';
                document.body.style.height = '';
                
                var meta = document.querySelector('meta[name="viewport"]');
                if (meta) {
                    meta.setAttribute('content', 'width=device-width, initial-scale=1.0, user-scalable=no');
                }
                
                if (document.getElementById('navbar')) {
                    document.getElementById('navbar').style.display = ''; 
                }
            """.trimIndent(), null)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
