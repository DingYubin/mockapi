package com.yubin.draw.widget.snackbar

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.baselibrary.util.EmptyUtil
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.draw.R

/**
 * Created by chuhuan.yan on 2022/7/27 14:07
 * Description:
 */
class  SnackBar1 {

    private var mCallback: (() -> Unit)? = null

    fun addCallback(mCallback: () -> Unit) {
        this.mCallback = mCallback
    }

    fun showSnackBar(view: View, taskName: String?, point: String?) {
        if (EmptyUtil.isStringEmpty(taskName) || EmptyUtil.isStringEmpty(point)) {
            return
        }
        val snackbar = Snackbar.make(view, "", 3000)
        snackbar.view.setPadding(0,0,0,0)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        val layoutParam = snackbarView.layoutParams as ViewGroup.LayoutParams
        if (layoutParam is CoordinatorLayout.LayoutParams) {
            val cl = CoordinatorLayout.LayoutParams(layoutParam.width, layoutParam.height)
            cl.topMargin = 20.dp
            cl.gravity = Gravity.TOP
            snackbarView.layoutParams = cl
        } else {
            val fl = FrameLayout.LayoutParams(layoutParam.width, layoutParam.height)
            fl.topMargin = 20.dp
            fl.gravity = Gravity.TOP
            snackbarView.layoutParams = fl
        }
        val inflateView = LayoutInflater.from(snackbar.context).inflate(R.layout.layout_snackbar_view, null)
        val snackbarDesc = inflateView.findViewById<AppCompatTextView>(R.id.snackbar_tv_desc)
        val snackbarPoint = inflateView.findViewById<AppCompatTextView>(R.id.snackbar_tv_point)
        snackbarDesc.text = String.format(ResourceUtil.getString(R.string.main_finish_task_notification), taskName)
        snackbarPoint.text = String.format(ResourceUtil.getString(R.string.main_point), point)
        snackbarView.addView(inflateView)
        snackbarView.elevation = 0f
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                mCallback?.invoke()
            }
        })
    }

}