package com.yubin.draw.widget.window

import android.content.Context
import android.graphics.PixelFormat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import com.yubin.draw.ui.WindowActivity
import kotlin.math.abs

class FloatingRootWindow {
    private var mWindowManager: WindowManager? = null
    private var mFloatParams: WindowManager.LayoutParams? = null
    private var mFloatView: View?= null

    fun showFloatingWindowView(context: Context, view : View) {
        //悬浮窗显示视图
        mFloatView = view

        //获取系统窗口管理服务
        mWindowManager = (context as WindowActivity).windowManager

        //悬浮车口参数设置及返回
        mFloatParams = WindowManager.LayoutParams().apply {

//            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSLUCENT
            //设置大小，自适应
            width = WRAP_CONTENT
            height = WRAP_CONTENT
//            width = 600
//            height = 340
            //设置浮窗显示位置
//            gravity = Gravity.START or Gravity.TOP
//            x = 100
//            y = 100
        }

        //设置窗口触摸移动事件
        mFloatView?.setOnTouchListener(ItemViewTouchListener(mFloatParams, mWindowManager))

        //悬浮窗生成
        mWindowManager?.addView(mFloatView,mFloatParams)
    }

    fun dismiss() {
        if (mFloatView?.isAttachedToWindow == true) {
            mWindowManager?.removeView(mFloatView)
        }
    }

    class ItemViewTouchListener(private val params: WindowManager.LayoutParams?, private val windowManager: WindowManager?) :
        View.OnTouchListener {
        //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
        private var mTouchStartX = 0
        private var mTouchStartY = 0

        //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
        private var mStartX = 0
        private var mStartY = 0

        //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
        private var isMove = false

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            val action = motionEvent.action
            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt()
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    isMove = false
                    mTouchStartX = motionEvent.rawX.toInt()
                    mTouchStartY = motionEvent.rawY.toInt()
                    mStartX = x
                    mStartY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val mTouchCurrentX = motionEvent.rawX.toInt()
                    val mTouchCurrentY = motionEvent.rawY.toInt()
                    params?.x = params?.x?.plus(mTouchCurrentX - mTouchStartX)
                    params?.y = params?.y?.plus(mTouchCurrentY - mTouchStartY)
                    windowManager?.updateViewLayout(view, params)
                    mTouchStartX = mTouchCurrentX
                    mTouchStartY = mTouchCurrentY
                    val deltaX = (x - mStartX).toFloat()
                    val deltaY = (y - mStartY).toFloat()
                    if (abs(deltaX) >= 5 || abs(deltaY) >= 5) {
                        isMove = true
                    }
                }
                MotionEvent.ACTION_UP -> {}
                else -> {}
            }
            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove
        }
    }
}