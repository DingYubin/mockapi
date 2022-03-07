package com.yubin.baselibrary.ui.basemvvm

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.yubin.baselibrary.common.Constants
import com.yubin.baselibrary.receiver.ScreenBroadcastReceiver
import com.yubin.baselibrary.receiver.ScreenStatus
import com.yubin.baselibrary.toolBar.IToolBarBuilder
import com.yubin.baselibrary.util.CMStatusBarUtil
import com.yubin.baselibrary.viewmodel.ApplicationViewModelProvider
import com.yubin.baselibrary.widget.LoadingDialog


/**
 * Description
 * @Date 2020/7/30 4:52 PM
 */
abstract class BaseActivity : AppCompatActivity(){

    private var protocolUri: String? = null
    private var loading: LoadingDialog? = null
    private lateinit var mToolBarBuilder: IToolBarBuilder
    private var mReceiver: ScreenBroadcastReceiver? = null
    private lateinit var mBaseLayout: ConstraintLayout
    private var mContentView: View? = null
    private var mLoginDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mToolBarBuilder = ToolBarBuilder(this)
        CMStatusBarUtil.setStatusColor(this, false, true, Color.WHITE)
//        super.setContentView(R.layout.activity_base_layout)
//        mBaseLayout = findViewById(R.id.base_content)
        registerScreenBroadcastReceiver()
    }

    open fun getContentView(): View? {
        return mContentView
    }

    /**
     * 设置title
     */
    open fun setTitleWithinToolBar(title: String?) {
        if (this.supportActionBar != null) {
            this.supportActionBar!!.show()
        }
        mToolBarBuilder.setTitle(title)
    }

    /**
     * 设置title点击事件
     */
    open fun setTitleClickListener(onClickListener: View.OnClickListener?) {
        mToolBarBuilder.setTitleOnClickListener(onClickListener)
    }

    /**
     * 设置title
     */
    open fun setTitleWithinToolBar(@StringRes title: Int) {
        if (this.supportActionBar != null) {
            this.supportActionBar!!.show()
        }
        mToolBarBuilder.setTitle(title)
    }

    fun getViewModelProvider() = ApplicationViewModelProvider.getInstance()

    /**
     * 无文案可取消的loading
     */
    fun showLoading() {
        this.showLoading(null, true)
    }

    /**
     * 无文案可设置是否取消的loading
     */
    fun showLoading(cancel: Boolean) {
        this.showLoading(null, cancel)
    }

    /**
     * 带文案可取消的loading
     */
    fun showLoading(content: String?) {
        this.showLoading(content, true)
    }

    /**
     * 带文案可设置是否取消的loading
     */
    fun showLoading(content: String?, cancel: Boolean) {
        if (null == loading) {
            loading = LoadingDialog.newInstance()
        }
        loading?.show(supportFragmentManager, javaClass.simpleName, content, cancel)
    }

    /**
     * 注册屏幕状态广播接收器
     */
    private fun registerScreenBroadcastReceiver() {
        if (mReceiver != null) {
            return
        }
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        mReceiver =
            ScreenBroadcastReceiver(ScreenStatus())
        this.registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
    }

    //用于定义页面黑白风格的参数
    open fun getSkinStyle(): Int {
        return Constants.BLACK_STYLE
    }
}