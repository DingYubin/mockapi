package com.yubin.baselibrary.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.yubin.baselibrary.R
import com.yubin.baselibrary.provider.BasicsContentProvider
import com.yubin.baselibrary.viewmodel.NetworkState

object ToastUtil {

    /**
     * 普通信息
     */
    const val INFO = 0

    /**
     * 错误信息
     */
    const val FAIL = 1

    /**
     * 成功信息
     */
    const val SUCCESS = 2

    /**
     * 统一toast管理
     */
    private var toast: Toast? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    fun String.toast() {
        if (this.isNotBlank()) {
            Handler(Looper.getMainLooper()).post {
                showToast(this, Toast.LENGTH_LONG)
            }
        }
    }

    /**
     * 浮动消息提示(可以设置时间:Toast.LENGTH_SHORT or Toast.LENGTH_LONG)
     */
    fun showToast(msg: String?, duration: Int) {
        showToast(msg, INFO, duration)
    }

    /**
     * 浮动消息提示(可以设置时间:Toast.LENGTH_SHORT or Toast.LENGTH_LONG)
     */
    fun showToast(msg: String?, toastType: Int, duration: Int) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        mainHandler.post{
            if (toast == null) {
                makeText(BasicsContentProvider.basicsContext, msg, toastType, duration)
            } else {
                setToastText(msg)
            }
            if (!toast!!.view!!.isShown) {
                toast!!.show()
            }
        }
    }

    private fun setToastText(msg: String?) {
        val view = toast!!.view
            ?: throw RuntimeException("This Toast was not created with Toast.makeText()")
        view.findViewById<TextView>(R.id.tvMessage)
            ?: throw RuntimeException("This Toast was not created with Toast.makeText()")
        apply(view, msg)
    }

    /**
     * 设置图标和文字
     */
    private fun apply(view: View, text: CharSequence?) {
        val tv = view.findViewById<TextView>(R.id.tvMessage)
        tv.text = text
    }

    private fun makeText(context: Context, text: CharSequence?, toastType: Int, duration: Int) {
        toast = Toast(context.applicationContext)
        val inflate =
            context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflate.inflate(R.layout.toast_custom_view, null)
        //设置图标ivMessage
        apply(v, text)
        toast!!.setView(v)
        toast!!.duration = duration
        toast!!.setGravity(Gravity.CENTER, 0, 0)
    }
}