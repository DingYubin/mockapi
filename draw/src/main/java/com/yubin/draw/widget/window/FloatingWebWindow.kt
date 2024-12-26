package com.yubin.draw.widget.window

import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import kotlin.math.abs

/**
 * 悬浮窗
 */
class FloatingWebWindow {

     private var mWindowManager: WindowManager? = null
     private var mShowView: View ?= null
     private var mFloatParams: WindowManager.LayoutParams? = null

    fun showFloatingWindowView(context: Context, view : View) {
        //悬浮窗显示视图
        mShowView = view
        //获取系统窗口管理服务
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        //悬浮车口参数设置及返回
        mFloatParams = getParams()
        //设置窗口触摸移动事件
        mShowView?.setOnTouchListener(FloatViewMoveListener())
        //悬浮窗生成
        mWindowManager?.addView(mShowView, mFloatParams)
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
        layoutParams.width = 600
        layoutParams.height = 340
        //设置悬浮窗显示位置
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.x = 100
        layoutParams.y = 100
        return layoutParams
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context 上下文
     */
    fun setTopApp(context: Context) {
        //获取ActivityManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //获得当前运行的task(任务)
        val taskInfoList: List<RunningTaskInfo>? = activityManager.getRunningTasks(100)
        if (taskInfoList != null) {
            for (taskInfo in taskInfoList) {
                //找到本应用的 task，并将它切换到前台
                if (taskInfo.topActivity != null && taskInfo.topActivity!!.packageName == context.packageName) {
                    activityManager.moveTaskToFront(taskInfo.id, 0)
                    break
                }
            }
        }
    }

    fun dismiss() {
        if (mShowView?.isAttachedToWindow == true) {
            mWindowManager?.removeView(mShowView)
        }
    }

    /**
     * 浮窗移动/点击监听
     */
    private inner class FloatViewMoveListener : OnTouchListener {
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
                    mFloatParams?.x = mFloatParams?.x?.plus(mTouchCurrentX - mTouchStartX)
                    mFloatParams?.y = mFloatParams?.y?.plus(mTouchCurrentY - mTouchStartY)
                    mWindowManager?.updateViewLayout(mShowView, mFloatParams)
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

