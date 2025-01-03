package com.yubin.draw.manager

import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.webkit.WebView

object FloatingWebWindowManager {
    private var windowManager: WindowManager? = null
    var webViews: MutableList<View> = arrayListOf()

    fun initialize(context: Context) {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun addView(view: View) {
        webViews.add(view)
    }

    fun removeView() : View {
        val view = webViews.last()
        webViews.remove(view)
        return view
    }

    fun isEmpty(): Boolean {
        return webViews.isEmpty()
    }

    fun showFloatingWebWindow(context: Context, webView: View, isfirst: Boolean) {
        if (webViews.isEmpty()) {
            return
        }
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val params = getParams()


//        for ((index, webView) in webViews.withIndex()) {
//            val offsetX = if (index > 0) index * 50 else 0
//            val offsetY = if (index > 0) index * 50 else 0
//
//            params.x = offsetX // Adjust this value based on your needs
//            params.y = screenHeight - offsetY - 200 // Adjust this value based on your needs
////            windowManager?.addView(webView, params)
//        }
        windowManager?.addView(webView, params)
//        if (isfirst) {
//            windowManager?.addView(webView, params)
//        } else {
//            windowManager?.updateViewLayout(webView, params)
//        }

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
        for (webView in webViews) {
//            webView.destroy()
        }
        webViews.clear()
        windowManager = null
    }
}