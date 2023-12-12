package com.yubin.draw.widget.viewGroup.seek

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.draw.R

class SeekBarViewGroup : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mInflater = LayoutInflater.from(context)

    private lateinit var seekBar: AppCompatSeekBar
    private lateinit var hourlyLayout: ConstraintLayout

    init {
        View.inflate(context, R.layout.layout_hourly_express_delivery_view, this)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        seekBar = findViewById(R.id.hourly_seek)
        val indicator: AppCompatTextView = findViewById(R.id.tv_indicator)
        hourlyLayout = findViewById(R.id.hourly_layout)

        seekBar.setOnTouchListener { _, _ -> true }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                post {
                    //进度百分比
                    val progressRatio = progress.toFloat() / seekBar.max
                    //文本宽度
                    val indicatorW = indicator.measuredWidth / 2
                    // thumb偏移量
                    val thumbOffset = seekBar.thumbOffset
                    // 拖动图片宽度
                    val thumbW = seekBar.thumb.intrinsicWidth / 2
                    val with = seekBar.measuredWidth - thumbW - thumbOffset - 44.dp
                    val s = seekBar.measuredWidth - (progressRatio * seekBar.measuredWidth)
                    val leftMargin = if (s >= indicator.measuredWidth) {
                        val left = (progress * with) / seekBar.max + indicatorW + thumbW
                        if (left < 0) 0 else left
                    } else {
                        val left = (progress * with) / seekBar.max - indicatorW - thumbW
                        if (left < 0) 0 else left
                    }

                    val params = indicator.layoutParams as LayoutParams
                    params.leftMargin = leftMargin
                    indicator.layoutParams = params
//                val tv = "$progress%"
//                indicator.text = tv
//                hourlyLayout.addView(tag)
                    if (progress == seekBar.min) {
                        indicator.visibility = INVISIBLE
                    } else {
                        indicator.visibility = VISIBLE
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })


//        seekBar.progress = 20
    }

    fun setProgress(progress: Int) {
        seekBar.progress = progress
    }

}