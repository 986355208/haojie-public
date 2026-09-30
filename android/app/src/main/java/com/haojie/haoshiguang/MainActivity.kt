package com.haojie.haoshiguang

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.webkit.WebViewAssetLoader

class MainActivity : ComponentActivity() {
    lateinit var gameView: WebView
        private set

    private lateinit var assetLoader: WebViewAssetLoader

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )

        assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        gameView = WebView(this).apply {
            setBackgroundColor(android.graphics.Color.rgb(172, 212, 215))
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.mediaPlaybackRequiresUserGesture = true
            settings.setSupportZoom(false)
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                    assetLoader.shouldInterceptRequest(request.url)
            }
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                    if (message.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                        android.util.Log.e("HaojieGame", "${message.message()} (${message.sourceId()}:${message.lineNumber()})")
                    }
                    return true
                }
            }
        }
        setContentView(gameView)
        val url = "https://appassets.androidplatform.net/assets/www/index.html"
        gameView.loadUrl(if (BuildConfig.DEBUG && intent.getBooleanExtra("game_test_mode", false)) "$url?test=1" else url)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::gameView.isInitialized && gameView.canGoBack()) gameView.goBack() else super.onBackPressed()
    }
}
