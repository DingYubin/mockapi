package com.yubin.medialibrary.video

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.yubin.medialibrary.R
import com.yubin.medialibrary.camera.MediaManager

/**
 *description
 *
 *@author laiwei
 *@date create at 4/26/21 8:00 PM
 */
class CircleProgress : View {
    private var underColor: Int = Color.WHITE
    private var progressColor: Int = Color.BLUE
    private var progressWidth: Int = 0

    var mMaxProgress: Int = 10
    var mProgress: Int = 0
        set(value) {
            field = if (value >= mMaxProgress) mMaxProgress else value
            invalidate()
        }

    private var mRectF: RectF? = null
    private lateinit var mPaint: Paint

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        var typeAttr = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        underColor = typeAttr.getColor(R.styleable.CircleProgress_underColor, Color.WHITE)
        progressColor = typeAttr.getColor(R.styleable.CircleProgress_progressColor, Color.WHITE)
        MediaManager.instance.mediaStyle?.let {
            progressColor = ContextCompat.getColor(getContext(), it.recordBtnColor)
        }
        progressWidth =
            typeAttr.getDimensionPixelSize(R.styleable.CircleProgress_progressWidth, 20)
        typeAttr.recycle()

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = progressWidth.toFloat()
        mPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var width = this.width
        var height = this.height
        if (width != height) {
            val min = width.coerceAtMost(height)
            width = min
            height = min
        }
        if (mRectF == null) {
            mRectF = RectF(
                (progressWidth / 2).toFloat(),
                (progressWidth / 2).toFloat(),
                (width - progressWidth / 2).toFloat(),
                (height - progressWidth / 2).toFloat()
            )
        }
        // 画背景
        mPaint.color = underColor
        canvas.drawArc(mRectF!!, -90f, 360f, false, mPaint)
        // 画进度
        mPaint.color = progressColor
        canvas.drawArc(
            mRectF!!,
            -90f,
            mProgress.toFloat() / mMaxProgress * 360,
            false,
            mPaint
        )
    }

}