package com.yubin.draw.widget.viewGroup.seek

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.draw.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class SeekBarViewGifGroup : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var seekBar: AppCompatSeekBar

    //    private var distance: String? = null
    private lateinit var distanceTv: AppCompatTextView
    private lateinit var deliveryStatus: AppCompatTextView
    private lateinit var deliveryTime: AppCompatTextView
    private lateinit var rider: AppCompatImageView


    init {
        View.inflate(context, R.layout.layout_gif_hourly_express_delivery_view, this)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        seekBar = findViewById(R.id.delivery_seek)
        rider = findViewById(R.id.rider_image)
        distanceTv = findViewById(R.id.tv_distance_distance)
        deliveryStatus = findViewById(R.id.delivery_status)
        deliveryTime = findViewById(R.id.delivery_time)

//        seekBar.setOnTouchListener { _, _ -> true }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress, seekBar)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        Glide.with(context).asGif().load(R.raw.ride_begin).centerCrop()
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(1)

                    resource?.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            show2RiderGif()
                        }
                    })

                    return false
                }
            })
            .placeholder(R.drawable.ride)
            .into(rider)
    }

    private fun show2RiderGif() {
        Glide.with(context).asGif()
            .apply(RequestOptions().skipMemoryCache(true))
            .load(R.raw.riding).centerCrop()
            .error(R.drawable.ride).into(rider)
    }

    private fun onProgressChanged(progress: Int, seekBar: SeekBar) {
        post {
            //进度百分比
            val progressRatio = progress.toFloat() / seekBar.max

            val with = seekBar.measuredWidth - rider.measuredWidth
            val s = seekBar.measuredWidth - (progressRatio * seekBar.measuredWidth)

            val riderLeftMargin = if (progress > 0) {
                (progress * with) / seekBar.max
            } else {
                (-3).dp
            }
            val riderParams = rider.layoutParams as LayoutParams
            riderParams.leftMargin = riderLeftMargin
            rider.layoutParams = riderParams

            val tvLeftMargin = if (s >= distanceTv.measuredWidth) {
                (-6).dp
            } else {
                -rider.measuredWidth - distanceTv.measuredWidth - 6.dp
            }

            val tvParams = distanceTv.layoutParams as LayoutParams
            tvParams.leftMargin = tvLeftMargin
            distanceTv.layoutParams = tvParams
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
        seekBar.progress = getProgress(totalDistance, currentDistance)

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