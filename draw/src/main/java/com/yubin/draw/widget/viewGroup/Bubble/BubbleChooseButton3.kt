package com.yubin.draw.widget.viewGroup.Bubble

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.yubin.draw.R

/**
 * Created by yubin.ding on 2017/5/26.
 * 多方法回调
 */
class BubbleChooseButton3(context: Context?, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val centerWindow: BubblePopupWindow = BubblePopupWindow(context)
    private var callback: CallBack? = null

    fun setViewAndCallback(view: View, listener: SimpleCallBack.()-> Unit) {
        val callback = SimpleCallBack()
        callback.listener()
        setCallBack(callback)
        initView(view)
    }

    private fun setCallBack(lis: CallBack) {
        callback = lis
    }

    private fun initView(view: View) {
        val bubbleView = inflate(context, R.layout.top_layout_popup_view, null)
        val deleteTv = bubbleView.findViewById<TextView>(R.id.delete)
        deleteTv.setOnClickListener {
            callback?.delete()
            closePopupWindow()
        }
        bubbleView.findViewById<View>(R.id.top).setOnClickListener {
            callback?.top("置顶")
            closePopupWindow()
        }
        bubbleView.findViewById<View>(R.id.more).setOnClickListener {
            callback?.more()
            closePopupWindow()
        }
        centerWindow.setBubbleView(bubbleView)
        centerWindow.show(view, Gravity.TOP, 30f)
    }

    private fun closePopupWindow() {
        if (centerWindow.isShowing) centerWindow.dismiss()
    }

    interface CallBack {
        fun delete()
        fun top(label : String)
        fun more()
    }

    class SimpleCallBack : CallBack{

        private var delete : (() -> Unit)? = null
        private var top : ((str : String) -> Unit)? = null
        private var more  : (() -> Unit)? = null

        fun delete(listener : ()->Unit) {
            delete = listener
        }

        fun top(listener : (str : String) -> Unit){
            top = listener
        }

        fun more(listener : ()->Unit) {
            more = listener
        }

        override fun delete() {
            delete?.invoke()
        }

        override fun top(label: String) {
            top?.invoke(label)
        }

        override fun more() {
            more?.invoke()
        }

    }

}