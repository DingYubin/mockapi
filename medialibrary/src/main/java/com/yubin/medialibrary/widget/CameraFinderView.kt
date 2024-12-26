package com.yubin.medialibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CameraFinderView : View {

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    private val p = Paint()
    private val top = RectF()
    private val bottom = RectF()
    private val left = RectF()
    private val right = RectF()

    private var w = 0f
    private var h = 0f

    init {
        p.color = Color.parseColor("#cc000000")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        top.left = 0f
        top.top = 0f
        top.right = width.toFloat()
        top.bottom = (height - h) / 2

        bottom.left = 0f
        bottom.top = (height - h) / 2 + h
        bottom.right = width.toFloat()
        bottom.bottom = height.toFloat()

        left.left = 0f
        left.top = (height - h) / 2
        left.right = (width - w) / 2
        left.bottom = (height - h) / 2 + h

        right.left = (width - w) / 2 + w
        right.top = (height - h) / 2
        right.right = width.toFloat()
        right.bottom = (height - h) / 2 + h

        canvas?.drawRect(top, p)
        canvas?.drawRect(bottom, p)
        canvas?.drawRect(left, p)
        canvas?.drawRect(right, p)

    }

    fun setFinderSize(w: Float, h: Float) {
        this.w = w
        this.h = h
        requestLayout()
    }

}