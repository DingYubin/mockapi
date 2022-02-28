package com.yubin.medialibrary.album.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.yubin.medialibrary.R
import com.yubin.medialibrary.util.UnitHelper

/**
 * description 分割线
 *
 * @author laiwei
 */
class ImagePickerFolderItemDecoration(context: Context?) :
    ItemDecoration() {
    private var mWight: Int = 0
    private var mDividerHeight: Float = 0.0f
    private var mPaint: Paint? = null
    private var mStartPadding: Float = 0.0f
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)
        if (parent.adapter == null) {
            return
        }
        val count = parent.childCount
        val itemCount = parent.adapter!!.itemCount
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val top = view.bottom
            val bottom = top + mDividerHeight
            /*
             *最后一行不添加分割线
             */if (position >= itemCount - 1) {
                mPaint?.let { c.drawRect(0f, 0f, 0f, 0f, it) }
                return
            }
            mPaint?.let {
                c.drawRect(
                    mStartPadding, top.toFloat(), mWight.toFloat(), bottom.toFloat(),
                    it
                )
            }
        }
    }

    init {
        var context = context
        if (context != null) {
            mWight = context?.resources?.displayMetrics?.widthPixels!!
            mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            mPaint?.color = context?.resources?.getColor(R.color.e8e8e8)!!
            mDividerHeight = UnitHelper.dp2px(context, 0.5f)
            mStartPadding = UnitHelper.dp2px(context, 72f)
        }

    }
}