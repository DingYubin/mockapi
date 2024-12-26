package com.yubin.draw.manager

import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import android.webkit.WebView
import java.util.Stack

object FloatingWebWindowManager {
    private var windowManager: WindowManager? = null
    private var webViewStack: Stack<WebView> = Stack()

    fun initialize(context: Context) {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun addWebView(webView: WebView) {
        webViewStack.add(webView)
    }

    fun showFloatingWebWindow(context: Context) {
        if (webViewStack.isEmpty()) {
            return
        }
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val params = getParams()
        for ((index, webView) in webViewStack.withIndex()) {
            val offsetX = if (index > 0) index * 50 else 0
            val offsetY = if (index > 0) index * 50 else 0

            params.x = offsetX
            params.y = screenHeight - offsetY - 200 // Adjust this value based on your needs

            windowManager?.addView(webView, params)
        }
    }

    private fun getParams(): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        //设置悬浮窗口类型
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        //设置悬浮窗口属性
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        //设置悬浮窗口透明
        layoutParams.format = PixelFormat.TRANSLUCENT
        //设置悬浮窗口长宽数据
        layoutParams.width = 340
        layoutParams.height = 600
        //设置悬浮窗显示位置
        layoutParams.x = 600
        layoutParams.y = 500

        return layoutParams
    }

    fun hideFloatingWebWindow(webView: WebView) {
        windowManager?.removeView(webView)
    }

    fun destroy() {
        for (webView in webViewStack) {
            webView.destroy()
        }
        webViewStack.clear()
        windowManager = null
    }
}