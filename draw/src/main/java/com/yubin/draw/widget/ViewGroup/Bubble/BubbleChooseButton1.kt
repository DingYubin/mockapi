package com.yubin.draw.widget.ViewGroup.Bubble

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
class BubbleChooseButton1 (context: Context?, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val centerWindow: BubblePopupWindow = BubblePopupWindow(context)

    private var listener: OnTextViewListener? = null

    fun setViewAndCallback(view: View, listener: OnTextViewListener?) {
        this.listener = listener
        initView(view)
    }

    private fun initView(longView: View) {
        val bubbleView = inflate(context, R.layout.top_layout_popup_view, null)
        val deleteTv = bubbleView.findViewById<TextView>(R.id.delete)
        deleteTv.setOnClickListener {
            if (null != listener) {
                listener!!.delete()
                closePopupWindow()
            }
        }
        bubbleView.findViewById<View>(R.id.top).setOnClickListener {
            if (null != listener) {
                listener!!.top()
                closePopupWindow()
            }
        }
        bubbleView.findViewById<View>(R.id.more).setOnClickListener {
            if (null != listener) {
                listener!!.more()
                closePopupWindow()
            }
        }
        centerWindow.setBubbleView(bubbleView)
        centerWindow.show(longView, Gravity.TOP, 30f)
    }

    private fun closePopupWindow() {
        if (centerWindow.isShowing) centerWindow.dismiss()
    }

    interface OnTextViewListener {
        fun delete()
        fun top()
        fun more()
    }

}