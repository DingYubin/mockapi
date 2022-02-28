package com.yubin.medialibrary.photoPreview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @description:圆角图片
 *
 *
 * @author: 孙莹莹(A01266)
 *
 *
 * @date: 2021/3/2 10:58 AM
 */
class CircleImageView : AppCompatImageView {
    private var mPaint: Paint? = null
    private var mWidth = 0
    private val mHeight = 0
    private var mRadius = 0 //圆半径 = 0
    private var mRect //矩形凹行大小
            : RectF? = null
    private var mRoundRadius = 0 // 圆角大小 = 0
    private var mBitmapShader: BitmapShader? = null //图形渲染
    private var mMatrix: Matrix? = null
    private var mType = 0 // 记录是圆形还是圆角矩形 = 0

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mMatrix = Matrix()
        mRoundRadius = DEFAUT_ROUND_RADIUS
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // TODOAuto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 如果是绘制圆形，则强制宽高大小一致
        if (mType == TYPE_CIRCLE) {
            mWidth = Math.min(measuredWidth, measuredHeight)
            mRadius = mWidth / 2
            setMeasuredDimension(mWidth, mWidth)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (null == drawable) {
            return
        }
        setBitmapShader()
        if (mType == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mPaint!!)
        } else if (mType == TYPE_ROUND) {
            mPaint!!.color = Color.RED
            canvas.drawRoundRect(mRect!!, mRoundRadius.toFloat(), mRoundRadius.toFloat(), mPaint!!)
        } else if (mType == TYPE_OVAL) {
            canvas.drawOval(mRect!!, mPaint!!)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODOAuto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        mRect = RectF(0F, 0F, width.toFloat(), height.toFloat())
    }

    /**
     * 设置BitmapShader
     */
    private fun setBitmapShader() {
        val drawable = drawable ?: return
        val bitmap = drawableToBitmap(drawable)
        // 将bitmap作为着色器来创建一个BitmapShader
        mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        var scale = 1.0f
        if (mType == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            val bSize = Math.min(bitmap.width, bitmap.height)
            scale = mWidth * 1.0f / bSize
        } else if (mType == TYPE_ROUND || mType == TYPE_OVAL) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(width * 1.0f / bitmap.width, height * 1.0f / bitmap.height)
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix!!.setScale(scale, scale)
        // 设置变换矩阵
        mBitmapShader!!.setLocalMatrix(mMatrix)
        mPaint!!.shader = mBitmapShader
    }

    /**
     * drawable转bitmap
     *
     * @paramdrawable
     * @return
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }


    /**
     * 设置图片类型：圆形、圆角矩形、椭圆形
     * @param mType
     */
    var type: Int
        get() = mType
        set(mType) {
            if (this.mType != mType) {
                this.mType = mType
                invalidate()
            }
        }

    /**
     * 设置圆角大小
     * @parammRoundRadius
     */
    var roundRadius: Int
        get() = mRoundRadius
        set(mRoundRadius) {
            if (this.mRoundRadius != mRoundRadius) {
                this.mRoundRadius = mRoundRadius
                invalidate()
            }
        }

    companion object {
        const val TYPE_CIRCLE = 0 // 圆形
        const val TYPE_ROUND = 1 // 圆角矩形
        const val TYPE_OVAL = 2 //椭圆形
        const val DEFAUT_ROUND_RADIUS = 10 //默认圆角大小
    }
}