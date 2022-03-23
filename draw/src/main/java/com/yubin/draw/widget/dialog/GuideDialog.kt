package com.yubin.draw.widget.dialog

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.yubin.baselibrary.impl.DebounceOnClickListener

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/03/22
 * desc    : 指导页面
 * version : 1.0
</pre> *
 */
class GuideDialog {
    var onDismissListener: IOnDismissListener? = null
    var isShowing: Boolean = false


    /**
     * 显示引导图
     */
    fun show(activity: Activity, anchorView: View) {
        isShowing = true
        val guideLayout = GuideLayout(activity, anchorView)
        guideLayout.onConfirmClickListener = object : DebounceOnClickListener() {
            override fun doClick(v: View?) {
                isShowing = false
                dismissGuideLayout(activity, guideLayout)
                onDismissListener?.onDismiss()
            }
        }
        showGuideLayout(activity, guideLayout)
    }

    /**
     * 显示引导图
     */
    private fun showGuideLayout(activity: Activity, layout: FrameLayout) {
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        val contentArea = activity.window.decorView.findViewById(android.R.id.content) as ViewGroup?
        contentArea?.addView(layout, layoutParams)
    }

    /**
     * 隐藏引导图
     */
    private fun dismissGuideLayout(activity: Activity, layout: FrameLayout) {
        if (activity.isDestroyed) {
            return
        }
        val contentArea = activity.window.decorView.findViewById(android.R.id.content) as ViewGroup?
        contentArea?.removeView(layout)
    }

    interface IOnDismissListener {
        fun onDismiss()
    }
}