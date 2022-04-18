package com.yubin.draw.widget.viewGroup.exposure

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import com.yubin.baselibrary.util.LogUtil

class ExposureHandler(private val view: View) : ViewTreeObserver.OnPreDrawListener {

    private var mAttachedToWindow = false //添加到视图中的状态
    private var mHasWindowFocus = true // 视图获取到焦点的状态 ，默认为true，避免某些场景不被调用
    private var mVisibilityAggregated = true //可见性的状态 ，默认为true，避免某些场景不被调用
    private var mExposure = false //当前是否处于曝光状态
    private var mExposureCallback: IExposureCallback? = null //曝光回调
    private var mStartExposureTime: Long = 0L //开始曝光时间戳
    private var mShowRatio: Float = 0f //曝光条件超过多大面积 0~1f
    private var mTimeLimit: Int = 0 //曝光条件超过多久才算曝光，例如2秒(2000)
    private val mRect = Rect() //实时曝光面积


    /**
     * 添加到视图时添加OnPreDrawListener
     */
    fun onAttachedToWindow() {
        mAttachedToWindow = true
        view.viewTreeObserver.addOnPreDrawListener(this)

    }

    /**
     * 从视图中移除时去掉OnPreDrawListener
     * 尝试取消曝光
     */
    fun onDetachedFromWindow() {
        mAttachedToWindow = false
        view.viewTreeObserver.removeOnPreDrawListener(this)
        LogUtil.d("从视图中移除时去掉")
        tryStopExposure()

    }

    /**
     * 视图焦点改变
     * 尝试取消曝光
     */
    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        mHasWindowFocus = hasWindowFocus
        LogUtil.d("视图焦点改变 hasWindowFocus = $hasWindowFocus")
        tryStopExposure()

    }

    /**
     * 可见性改变
     * 尝试取消曝光
     */
    fun onVisibilityAggregated(isVisible: Boolean) {
        mVisibilityAggregated = isVisible
        LogUtil.d("可见性改变 isVisible = $isVisible")
        tryStopExposure()

    }

    /**
     * 视图预绘制
     * 当曝光面积达到条件是尝试曝光
     * 当视图面积不满足条件时尝试取消曝光
     */
    override fun onPreDraw(): Boolean {
        val visible = view.getLocalVisibleRect(mRect) && view.isShown //获取曝光面积 和View的Visible
        if (!visible) {
            LogUtil.d("不达到曝光条件时尝试取消曝光，tryStopExposure()")
            tryStopExposure()//不达到曝光条件时尝试取消曝光
            return true
        }
        if (mShowRatio > 0) {//存在曝光面积限制条件时
            if (kotlin.math.abs(mRect.bottom - mRect.top) > view.height * mShowRatio
                && kotlin.math.abs(mRect.right - mRect.left) > view.width * mShowRatio
            ) {
                LogUtil.d("mShowRatio = $mShowRatio, 在曝光区域内，达到曝光条件，tryExposure()")
                tryExposure() //达到曝光条件时尝试曝光
            } else {
                LogUtil.d("mShowRatio = $mShowRatio, 在曝光区域内，不达到曝光条件，tryStopExposure()")
                tryStopExposure()//不达到曝光条件时尝试取消曝光
            }
        } else {
            LogUtil.d("不在 === 在曝光区域内，达到曝光条件，tryExposure()")
            tryExposure() //达到曝光条件时尝试曝光
        }
        return true
    }

    /**
     * 曝光回调
     */
    fun setExposureCallback(callback: IExposureCallback) {
        mExposureCallback = callback
    }

    /**
     * 设置曝光面积条件
     */
    fun setShowRatio(area: Float) {
        mShowRatio = area
    }

    /**
     * 设置曝光时间限制条件
     */
    fun setTimeLimit(index: Int) {
        this.mTimeLimit = index
    }

    /**
     * 尝试曝光
     */
    private fun tryExposure() {
        if (mAttachedToWindow && mHasWindowFocus && mVisibilityAggregated && !mExposure) {
            mExposure = true //曝光中
            mStartExposureTime = System.currentTimeMillis() //曝光开始时间
            if (mTimeLimit == 0) {
                LogUtil.d("尝试曝光，回调 mExposureCallback?.show()")
                mExposureCallback?.show() //回调开始曝光
            }
        }
    }

    /**
     * 尝试取消曝光
     */
    private fun tryStopExposure() {
        if ((!mAttachedToWindow || !mHasWindowFocus || !mVisibilityAggregated) && mExposure) {
            mExposure = false //重置曝光状态
            if (mTimeLimit > 0 && System.currentTimeMillis() - mStartExposureTime > mTimeLimit) {
                //满足时长限制曝光
                LogUtil.d("尝试取消曝光，回调 mExposureCallback?.show()")
                mExposureCallback?.show()
            }

        }
    }

}

