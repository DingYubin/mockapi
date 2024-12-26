package com.yubin.draw.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.yubin.baselibrary.impl.DebounceOnClickListener
import com.yubin.baselibrary.util.CMUnitHelper
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.ResourceUtil
import com.yubin.baselibrary.util.ScreenUtils
import com.yubin.draw.R

@SuppressLint("ViewConstructor")
class GuideLayout (context: Context, anchorView: View) : FrameLayout(context) {
    private val mDensity: Float

    /**
     * 半透明背景
     */
    private var mRectF: RectF
    private var mPaint: Paint
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null

    /**
     * 定位区域
     */
    private var mAnchorPaint: Paint
    private var mAnchorRectF: RectF

    var onConfirmClickListener: DebounceOnClickListener? = null

    init {
        setWillNotDraw(false)
        mDensity = Resources.getSystem().displayMetrics.density

        mPaint = Paint().apply { color = ResourceUtil.getColor(R.color.color_cec_mask_70) }
        mRectF = RectF(
            0f,
            0f,
            CMUnitHelper.getRealScreenSize(context).x.toFloat(),
            CMUnitHelper.getRealScreenSize(context).y.toFloat()
        )

        mAnchorPaint = Paint().apply {
            color = -0x1
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            flags = Paint.ANTI_ALIAS_FLAG
        }

        val mPosition: Point = anchorView.locationOnScreen
        mAnchorRectF = RectF((mPosition.x).toFloat(),
            (mPosition.y).toFloat(),
            (mPosition.x + anchorView.width).toFloat(),
            (mPosition.y + anchorView.height).toFloat())

        LogUtil.d("mPosition x = ${mPosition.x} y = ${mPosition.y}, anchorView width = ${anchorView.width} anchorView height = ${anchorView.height}")

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap!!, 0f, 0f, null)
        } else {
            canvas.also {
                mBitmap = Bitmap.createBitmap(mRectF.width().toInt(), mRectF.height().toInt(), Bitmap.Config.ARGB_8888)
                mBitmap?.let {
                    mCanvas = Canvas(it)
                }
                mBitmap?.also { bitmap ->
                    bitmap.eraseColor(Color.TRANSPARENT)
                    mCanvas?.also { _canvas ->
                        _canvas.drawColor(ResourceUtil.getColor(R.color.color_cec_mask_70))
        //                        val roundedCornerRadiusPx = CMUnitHelper.dp2px(15f)
        //                        _canvas.drawRect(rect, mAnchorPaint)
        //                        val cx = mAnchorRectF.right - roundedCornerRadiusPx
        //                        val cy = (mAnchorRectF.bottom + mAnchorRectF.top) / 2
        //                        val cRadius = (mAnchorRectF.bottom - mAnchorRectF.top) / 2
        //                        _canvas.drawCircle(cx, cy, cRadius, mAnchorPaint)
                        val rect = Rect(mAnchorRectF.left.toInt(), mAnchorRectF.top.toInt() - ScreenUtils.getStatusBarHeight(), mAnchorRectF.right.toInt(), mAnchorRectF.bottom.toInt()- ScreenUtils.getStatusBarHeight())

                        _canvas.drawRect(rect, mAnchorPaint)
                        LogUtil.d("mPosition x = ${mAnchorRectF.left.toInt()} y = ${mAnchorRectF.top.toInt()}, anchorView width = ${mAnchorRectF.right - mAnchorRectF.left.toInt()} anchorView height = ${mAnchorRectF.bottom-mAnchorRectF.top.toInt()}")
                        canvas.drawBitmap(bitmap, 0f, 0f, null)
                    }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmap?.recycle()
        mBitmap = null
    }

    private val View.locationOnScreen: Point
        get() = IntArray(2).let {
            getLocationOnScreen(it)
            Point(it[0], it[1])
        }
}