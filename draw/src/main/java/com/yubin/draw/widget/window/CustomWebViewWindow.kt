package com.yubin.draw.widget.window

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.draw.R

class CustomWebViewWindow(context: Context) :  View(context) {
    private var windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )
    private var mThumb: ImageView
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    init {
        val view = inflate(context, R.layout.view_video_view_floating_web, null)
        mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        val close = view.findViewById<View>(R.id.close_floating_view) as ImageView

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0

        windowManager.addView(this, params)


        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(this, params)
                }
            }
            true
        }

        close.onViewClick{
            destroy()
        }
    }

    fun setImageBitmap(web: Bitmap) {
        mThumb.setImageBitmap(web)
    }

    fun minimize() {
        visibility = View.GONE
    }

    fun destroy() {
        if (isAttachedToWindow) {
            windowManager.removeView(this)
        }
    }
}