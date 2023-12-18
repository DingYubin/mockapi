package com.yubin.draw.widget.viewGroup.seek

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.litao.slider.NiftySlider
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.draw.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class SliderViewGroup : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var seekBar: NiftySlider

    //    private var distance: String? = null
    private lateinit var distanceTv: AppCompatTextView
    private lateinit var deliveryStatus: AppCompatTextView
    private lateinit var deliveryTime: AppCompatTextView


    init {
        View.inflate(context, R.layout.layout_slider_hourly_express_delivery_view, this)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        seekBar = findViewById(R.id.delivery_slider)
        distanceTv = findViewById(R.id.tv_distance_distance)
        deliveryStatus = findViewById(R.id.delivery_status)
        deliveryTime = findViewById(R.id.delivery_time)

        seekBar.apply {

            setThumbCustomDrawable(R.drawable.rider)
            setTrackTintList(
                AppCompatResources.getColorStateList(
                    context,
                    R.color.color_download_progress_start
                )
            )

            setTrackInactiveTintList(
                AppCompatResources.getColorStateList(
                    context,
                    R.color.color_download_progress_none
                )
            )
            //vertical offset of thumb
            setThumbVOffset((-8).dp)
            setThumbWithinTrackBounds(true)
        }
//        seekBar.setOnTouchListener { _, _ -> true }
//        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                onProgressChanged(progress, seekBar)
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//            }
//        })

    }

    private fun onProgressChanged(progress: Int, seekBar: SeekBar) {
        post {
            //进度百分比
            val progressRatio = progress.toFloat() / seekBar.max
            //文本宽度
            val indicatorW = distanceTv.measuredWidth / 2
            // thumb偏移量
            val thumbOffset = seekBar.thumbOffset
            // 拖动图片宽度
            val thumbW = seekBar.thumb.intrinsicWidth / 2
            val with = seekBar.measuredWidth - thumbW - thumbOffset - 44.dp
            val s = seekBar.measuredWidth - (progressRatio * seekBar.measuredWidth)
            val leftMargin = if (s >= distanceTv.measuredWidth) {
                val left = (progress * with) / seekBar.max + indicatorW + thumbW
                if (left < 0) 0 else left
            } else {
                val left = (progress * with) / seekBar.max - indicatorW - thumbW
                if (left < 0) 0 else left
            }

            val params = distanceTv.layoutParams as LayoutParams
            params.leftMargin = leftMargin
            distanceTv.layoutParams = params

//            if (progress == 0 || progress == 100) {
//                distanceTv.visibility = INVISIBLE
//            } else {
//                distanceTv.visibility = VISIBLE
//            }
        }
    }

    fun bindData(totalDistance: String?, currentDistance: String?, isComplete: Boolean) {

        val discount = String.format(
            ResourceUtil.getString(R.string.order_packages_delivery_status), "配送中"
        )
        val spannableString = SpannableString(discount)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FFFC6405")),
            1,
            discount.length - 1,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        deliveryStatus.text = spannableString

        deliveryTime.text = String.format(
            ResourceUtil.getString(R.string.order_packages_delivery_time),
            "15:30"
        )
//        seekBar.progress = getProgress(totalDistance, currentDistance)

        setDistance(currentDistance, isComplete)
    }

    private fun setDistance(currentDistance: String?, isComplete: Boolean) {
        val distance = currentDistance?.toDouble()?.div(1000)?.let { formatDistance(it) }
        if (distance.isNullOrEmpty() || isComplete) {
            distanceTv.visibility = INVISIBLE
        } else {
            distanceTv.visibility = VISIBLE
            val discount = String.format(
                ResourceUtil.getString(R.string.order_packages_delivery_distance),
                distance
            )
            val rmbIndex: Int = discount.indexOf(distance)
            val spannableString = SpannableString(discount)
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#FFE51E1E")),
                rmbIndex,
                discount.length - 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            distanceTv.text = spannableString
        }


    }

    private fun getProgress(totalDistance: String?, currentDistance: String?): Int {
        val totalD = totalDistance?.toDouble() ?: 0.00
        val currentD = currentDistance?.toDouble() ?: 0.00
        val progressRatio = if (totalD > 0.00) totalD.minus(currentD)
            .div(totalD) else 0.00
        return (progressRatio * 100).toInt()
    }

    private fun formatDistance(num: Double): String? {
        return if (num == 0.0) {
            "0.0"
        } else try {
            // 四舍五入
            val decimalFormat = DecimalFormat("#,##0.0", DecimalFormatSymbols(Locale.CHINA))
            decimalFormat.roundingMode = RoundingMode.HALF_UP
            decimalFormat.format(num)
        } catch (e: Exception) {
            LogUtil.e(e.message)
            "0.0"
        }
    }

}