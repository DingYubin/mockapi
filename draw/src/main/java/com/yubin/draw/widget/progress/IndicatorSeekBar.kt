package com.yubin.draw.widget.progress

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar


class IndicatorSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle
) : AppCompatSeekBar(context!!, attrs, defStyleAttr) {
    // 画笔
    private var mPaint: Paint? = null

    // 进度文字位置信息
    private val mProgressTextRect = Rect()

    // 滑块按钮宽度
    private val mThumbWidth = dp2px(50f)

    // 进度指示器宽度
    private val mIndicatorWidth = dp2px(50f)

    // 进度监听
    private var mIndicatorSeekBarChangeListener: OnIndicatorSeekBarChangeListener? = null

    init {
        init()
    }

    private fun init() {
        mPaint = TextPaint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.parseColor("#00574B")
        mPaint?.textSize = sp2px(16f).toFloat()
        // 如果不设置padding，当滑动到最左边或最右边时，滑块会显示不全
        setPadding(mThumbWidth / 2, 0, mThumbWidth / 2, 0)
        // 设置滑动监听
        this.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener!!.onStartTrackingTouch(seekBar)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener!!.onStopTrackingTouch(seekBar)
                }
            }
        })
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progressText = "$progress%"
        mPaint!!.getTextBounds(progressText, 0, progressText.length, mProgressTextRect)
        // 进度百分比
        val progressRatio = progress.toFloat() / max
        // thumb偏移量
        val thumbOffset =
            (mThumbWidth - mProgressTextRect.width()) / 2 - mThumbWidth * progressRatio
        val thumbX = width * progressRatio + thumbOffset
        val thumbY = height / 2f + mProgressTextRect.height() / 2f
        canvas.drawText(progressText, thumbX, thumbY, mPaint!!)
        if (mIndicatorSeekBarChangeListener != null) {
            val indicatorOffset =
                width * progressRatio - (mIndicatorWidth - mThumbWidth) / 2 - mThumbWidth * progressRatio
            mIndicatorSeekBarChangeListener!!.onProgressChanged(this, progress, indicatorOffset)
        }
    }

    /**
     * 设置进度监听
     *
     * @param listener OnIndicatorSeekBarChangeListener
     */
    fun setOnSeekBarChangeListener(listener: OnIndicatorSeekBarChangeListener?) {
        mIndicatorSeekBarChangeListener = listener
    }

    /**
     * 进度监听
     */
    interface OnIndicatorSeekBarChangeListener {
        /**
         * 进度监听回调
         *
         * @param seekBar SeekBar
         * @param progress 进度
         * @param indicatorOffset 指示器偏移量
         */
        fun onProgressChanged(seekBar: SeekBar?, progress: Int, indicatorOffset: Float)

        /**
         * 开始拖动
         *
         * @param seekBar SeekBar
         */
        fun onStartTrackingTouch(seekBar: SeekBar?)

        /**
         * 停止拖动
         *
         * @param seekBar SeekBar
         */
        fun onStopTrackingTouch(seekBar: SeekBar?)
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        ).toInt()
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return px值
     */
    private fun sp2px(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp,
            resources.displayMetrics
        ).toInt()
    }
}