package com.yubin.engine.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import com.yubin.engine.BuildConfig
import java.util.concurrent.atomic.AtomicInteger

class CassWebView : WebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )


    companion object {
        private const val TAG = "CassWebView"

        /**
         * 调用H5的方法
         */
        private const val CALL_H5_FUNCTION = "window.callH5Function"

        /**
         * H5调用APP时，调用H5的方法返回结果的方法
         */
        private const val APP_CALLBACK = "window.appCallback"
    }

    private val mCallId = AtomicInteger(0)

    private val mainHandler = Handler(Looper.getMainLooper())

    var cassWebViewInterface: ICassWebViewInterface? = null

    init {
        this.setWebViewClient()
        this.setWebSetting()

        addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun callAppFunction(methodName: String, callId: String, paramsJson: String) {
                val result = cassWebViewInterface?.callAppFunction(methodName, callId, paramsJson)
                result?.let {
                    evaluateAppCallback(methodName, callId, it)
                }
            }
        }, "CassApp")
    }

    fun evaluateAppCallback(methodName: String, callId: String, resultJson: String) {
        mainHandler.post {
            evaluateJavascript(
                "javascript:$APP_CALLBACK('${methodName}','${callId}','${resultJson}')"
            ) { Log.i(TAG, "evaluateJavascript:${it}") }
        }
    }

    fun callH5Function(methodName: String, paramsJson: String, callback: ((String) -> Unit)?) {
        mainHandler.post {
            val callId: Int = this.mCallId.incrementAndGet()
            evaluateJavascript(
                "javascript:$CALL_H5_FUNCTION('${methodName}','${callId}','${paramsJson}')"
            ) {
                Log.i(TAG, "evaluateJavascript result:${it}")
                callback?.invoke(it)
            }
        }
    }

    private fun setWebViewClient() {
        webViewClient = object : WebViewClientCompat() {

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSetting() {
        //开启调试模式
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true)
        }
        //5.0以上开启混合模式加载
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setGeolocationEnabled(true)
        //允许js代码 //有安全风险
        settings.javaScriptEnabled = true
        //允许SessionStorage/LocalStorage存储
        settings.domStorageEnabled = true
        //禁用放缩
        settings.displayZoomControls = false
        settings.builtInZoomControls = true
        //禁用文字缩放
        settings.textZoom = 100
        //禁用缓存
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        //允许WebView使用File协议
        settings.allowFileAccess = true
        //自动加载图片
        settings.loadsImagesAutomatically = true
        //自动播放视频
        settings.mediaPlaybackRequiresUserGesture = false
    }

    fun syncCookie(url: String, cookies: List<String>) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.acceptThirdPartyCookies(this)
        cookies.forEach { cookie ->
            cookieManager.setCookie(url, cookie)
        }
        cookieManager.flush()
    }

    fun setUserAgent(userAgent: String) {
        settings.userAgentString = userAgent
    }


}