package com.yubin.ecwindow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import android.widget.PopupWindow

class ECPopWindow(context: Context) : PopupWindow(context) {

    init {
        setDismissListener()
    }

    private var mContext: Context = context
    private lateinit var anchor: View
    private var windowBackgroundAlpha = 0.9f
    private var isTouchOutsideDismiss = true

    /**
     * 弹窗离屏幕左右的边距
     */
    private var offsetEdge = 0
    private var locateTriangleOffset = 0
    private var onWindowStateLister: OnWindowStateLister? = null
    var windowBg: ECWindowBackgroundDrawable? = null

    private var anchorRectF: RectF? = null

    /**
     * 默认居中显示
     */
    fun show(anchor: View) {
        this.anchor = anchor
        this.anchorRectF = null
        this.touchOutsideDismiss(isTouchOutsideDismiss)
        this.setWindowBg(true, 0f)
        this.setWindowBackgroundAlpha(windowBackgroundAlpha)
        contentView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val offset = calculateOffset(null)
                contentView.viewTreeObserver.removeOnPreDrawListener(this)
                setWindowBg(offset[1] == 0, offset[2].toFloat())
                update(anchor, offset[0], offset[1], -1, -1)
                onWindowStateLister?.onWindowShow(offset[0], offset[1])
                return false
            }
        })
        showAtLocation(anchor, Gravity.START xor Gravity.TOP, 0, 0)
    }

    fun showInScreenLocation(parent: View, gravity: Int, x: Int, y: Int) {
        this.anchor = parent
        this.touchOutsideDismiss(isTouchOutsideDismiss)
        super.showAtLocation(parent, gravity, x, y)
        this.setWindowBackgroundAlpha(windowBackgroundAlpha)
    }


    /**
     * 在view中的某个区域显示popupWindow
     * @param parent View
     * @param rectF RectF 屏幕中位置的矩形框
     * @param isAtBelow Boolean? 是否局下
     */
    fun show(parent: View, rectF: RectF, isAtBelow: Boolean? = null) {
        // 找到在屏幕中的位置
        val locationOnScreen = intArrayOf(0, 0)
        parent.getLocationOnScreen(locationOnScreen)
        rectF.left = rectF.left + locationOnScreen[0]
        rectF.top = rectF.top + locationOnScreen[1]
        rectF.right = rectF.right + locationOnScreen[0]
        rectF.bottom = rectF.bottom + locationOnScreen[1]

        this.anchorRectF = rectF
        this.anchor = parent
        this.touchOutsideDismiss(isTouchOutsideDismiss)
        this.setWindowBg(true, 0f)
        this.setWindowBackgroundAlpha(windowBackgroundAlpha)
        contentView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val offset = calculateOffset(isAtBelow)
                contentView.viewTreeObserver.removeOnPreDrawListener(this)
                setWindowBg(offset[1] == 0, offset[2].toFloat())
                val offsetX = (rectF.left + offset[0]).toInt()
                val offsetY = (rectF.bottom + offset[1]).toInt()
                dismiss()
                showAtLocation(anchor, Gravity.START or Gravity.TOP, offsetX, offsetY)
                onWindowStateLister?.onWindowShow(offset[0], offset[1])
                return false
            }
        })
        showAtLocation(anchor, Gravity.START or Gravity.TOP, 0, 0)
    }

    private fun setWindowBg(isAtBelow: Boolean, triangleOffset: Float) {
        windowBg?.let {
            it.triangleOffset = triangleOffset
            if (isAtBelow) {
                it.triangleDirection = ECWindowBackgroundDrawable.TRIANGLE_DIRECTION_UP
                contentView.setPadding(
                    it.strokeWidth.toInt(),
                    (it.triangleHeight.toInt() + it.strokeWidth.toInt()),
                    it.strokeWidth.toInt(),
                    it.strokeWidth.toInt()
                )
            } else {
                it.triangleDirection = ECWindowBackgroundDrawable.TRIANGLE_DIRECTION_DOWN
                contentView.setPadding(
                    it.strokeWidth.toInt(),
                    it.strokeWidth.toInt(),
                    it.strokeWidth.toInt(),
                    (it.triangleHeight.toInt() + it.strokeWidth.toInt())
                )
            }
            contentView.background = it
        }
    }

    fun addOnWindowStateLister(onWindowStateLister: OnWindowStateLister?) {
        this.onWindowStateLister = onWindowStateLister
    }

    /**
     * 获取popWindow指向的矩形区域
     */
    private fun getAnchorRectF(): RectF {
        return if (anchorRectF != null) {
            anchorRectF!!
        } else {
            //弹窗定位View宽高
            val anchorWidth = this.anchor.width.toFloat()
            val anchorHeight = this.anchor.height.toFloat()
            //弹窗定位View在屏幕中的位置
            val anchorLoc = IntArray(2)
            this.anchor.getLocationOnScreen(anchorLoc)
            RectF(
                anchorLoc[0].toFloat(),
                anchorLoc[1].toFloat(),
                anchorLoc[0] + anchorWidth,
                anchorLoc[1] + anchorHeight
            )
        }
    }

    /**
     * 计算偏移量，使弹窗居中
     */
    private fun calculateOffset(isBelow: Boolean?): IntArray {
        //弹窗宽高
        val w = contentView.width
        val h = contentView.height
        val rectF = getAnchorRectF()
        //屏幕宽高
        val screenWidth = this.anchor.resources.displayMetrics.widthPixels
        val screenHeight = this.anchor.resources.displayMetrics.heightPixels
        //计算偏移量
        val offset = IntArray(3)
        //y轴偏移计算逻辑，默认定位在View下方，y轴偏移量为0。当下方不够显示时，则y轴偏移量为(-anchorHeight - h)
        offset[1] = if (isBelow == null) {
            //y轴偏移计算逻辑，默认定位在View下方，y轴偏移量为0。当下方不够显示时，则y轴偏移量为(-anchorHeight - h)
            if (rectF.top + rectF.height() + h > screenHeight) {
                (-rectF.height() - h).toInt()
            } else {
                0
            }
        } else if (isBelow) {
            0
        } else {
            (-rectF.height() - h).toInt()
        }
        //x轴偏移量计算逻辑，默认应和定位View居中显示，需处理边界问题
        if (locateTriangleOffset == 0) {
            locateTriangleOffset = (rectF.width() / 2).toInt()
        }
        /**
         * 弹窗相对定位View偏移量
         */
        val xStart = locateTriangleOffset - w / 2
        when {
            (xStart + rectF.left) < offsetEdge -> {
                /**
                 * 如果弹窗在屏幕内的坐标小于左屏幕边距，需重新计算偏移量
                 */
                offset[0] = (xStart + (offsetEdge - (xStart + rectF.left))).toInt()
            }

            (xStart + rectF.left + w) > screenWidth - offsetEdge -> {
                /**
                 * 如果弹窗在屏幕内的坐标大于右屏幕边距，需重新计算偏移量
                 */
                offset[0] = (xStart - (xStart + rectF.left + w - screenWidth + offsetEdge)).toInt()
            }

            else -> {
                offset[0] = xStart
            }
        }
        //----------<弹   窗   中  心   点   坐   标  >----<实 际 三 角 标 要 显 示 的 坐 标>
        offset[2] = ((rectF.left + offset[0] + w / 2) - (rectF.left + locateTriangleOffset)).toInt()
        return offset
    }

    /**
     * 控制窗口背景的不透明度
     */
    private fun setWindowBackgroundAlpha(alpha: Float) {
        if (mContext is Activity) {
            val window = (mContext as Activity).window
            val layoutParams = window.attributes
            layoutParams.alpha = alpha
            window.attributes = layoutParams
        }
    }

    /**
     * 设置关闭监听，设置背景透明度还原为1f
     */
    private fun setDismissListener() {
        super.setOnDismissListener {
            onWindowStateLister?.onDismiss()
            setWindowBackgroundAlpha(1.0f)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchOutsideDismiss(isTouchOutsideDismiss: Boolean) {
        if (!isTouchOutsideDismiss) {
            isFocusable = true
            isOutsideTouchable = false
            setBackgroundDrawable(null)
            contentView ?: return
            contentView.isFocusable = true
            contentView.isFocusableInTouchMode = true
            contentView.setOnKeyListener { _: View?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            //在Android 6.0以上 ，只能通过拦截事件来解决
            setTouchInterceptor(View.OnTouchListener { _, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()
                val mWidth = contentView.width
                val mHeight = contentView.height
                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= mWidth || y < 0 || y >= mHeight)) {
                    //outside
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    //outside
                    return@OnTouchListener true
                }
                false
            })
        } else {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    class Builder(context: Context) {

        private var ecPopWindow: ECPopWindow = ECPopWindow(context)

        init {
            ecPopWindow.isOutsideTouchable = false
        }

        fun setContentView(view: View): Builder {
            ecPopWindow.contentView = FrameLayout(view.context).apply {
                addView(view)
            }
            return this
        }

        fun setSize(width: Int, height: Int): Builder {
            ecPopWindow.width = width
            ecPopWindow.height = height
            return this
        }

        /**
         * 设置弹窗后背景
         */
        fun setActivityBackgroundAlpha(windowBackgroundAlpha: Float): Builder {
            ecPopWindow.windowBackgroundAlpha = windowBackgroundAlpha
            return this
        }

        /**
         * 设置特定背景，带有三角箭头
         */
        fun setWindowBackground(windowBg: ECWindowBackgroundDrawable?): Builder {
            ecPopWindow.windowBg = windowBg
            return this
        }

        fun setOffsetEdge(offsetEdge: Int): Builder {
            ecPopWindow.offsetEdge = offsetEdge
            return this
        }

        fun setLocateTriangleOffset(locateTriangleOffset: Int): Builder {
            ecPopWindow.locateTriangleOffset = locateTriangleOffset
            return this
        }

        fun setTouchOutsideDismiss(isTouchOutsideDismiss: Boolean) : Builder {
            ecPopWindow.isTouchOutsideDismiss = isTouchOutsideDismiss
            return this
        }

        fun createWindow(): ECPopWindow {
            return ecPopWindow
        }

        companion object {
            fun build(context: Context): Builder {
                return Builder(context)
            }
        }
    }

    interface OnWindowStateLister {

        fun onWindowShow(xOff: Int, yOff: Int)

        fun onDismiss()
    }
}


