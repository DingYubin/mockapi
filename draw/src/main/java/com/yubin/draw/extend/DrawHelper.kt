package com.yubin.draw.extend

import android.view.View
import android.view.ViewGroup
import com.yubin.baselibrary.ui.basemvvm.BaseActivity

object DrawHelper {

    private const val NAVIGATION = "navigationBarBackground"

    /**
     * 获取底部导航栏高度
     */
    fun getNavigationBarHeight(activity: BaseActivity?): Int {
        val decorView = activity?.window?.decorView as? ViewGroup
        if (decorView == null || decorView.childCount == 0) {
            return 0
        }
        for (i in 0..decorView.childCount) {
            val childView = decorView.getChildAt(i) ?: continue
            if (childView.id != View.NO_ID && NAVIGATION == activity.resources.getResourceEntryName(childView.id)) {
                return childView.height
            }
        }
        return 0
    }
}