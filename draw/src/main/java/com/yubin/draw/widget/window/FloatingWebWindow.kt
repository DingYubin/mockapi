package com.yubin.draw.widget.window

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yubin.baselibrary.util.LogUtil
import kotlin.math.abs

/**
 * 悬浮窗
 */
class FloatingWebWindow private constructor() {
    private var windowManager: WindowManager? = null
    private var containerView: FloatingContainer? = null
    private var recyclerView: RecyclerView? = null
    private var showView: View? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isExpanded = false
    private var isWindowShowing = false  // 添加标志位跟踪窗口状态
    private var onItemClickListener: ((String) -> Unit)? = null

    companion object {
        private const val COLLAPSED_WIDTH = 340
        private const val COLLAPSED_HEIGHT = 600
        private const val DEFAULT_X = 600
        private const val DEFAULT_Y = 500
        private const val TOUCH_SLOP = 5
        private const val ANIMATION_DURATION = 300L

        @Volatile
        private var instance: FloatingWebWindow? = null

        fun getInstance(): FloatingWebWindow {
            return instance ?: synchronized(this) {
                instance ?: FloatingWebWindow().also { instance = it }
            }
        }
    }

    fun showFloatingWindow(context: Context, recyclerView: RecyclerView) {
//        if (isWindowShowing) {
//            // 如果窗口已经显示，直接返回
//            return
//        }

        try {
            // 确保清理旧的窗口
//            dismiss()

            this.windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            this.recyclerView = recyclerView

            // 创建容器视图
            val containerView = FloatingContainer(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    COLLAPSED_WIDTH,
                    COLLAPSED_HEIGHT
                )

                // 设置点击和移动回调
                onSingleItemClick = {
                    if (recyclerView.adapter?.itemCount == 1) {
                        LogUtil.d("web view 单机事件")
//                        (recyclerView.adapter as? WebViewScreenshotAdapter)?.getItem(0)?.url?.let { url ->
//                            onItemClickListener?.invoke(url)
//                        }
                    }
                }

                onExpandClick = {
                    LogUtil.d("单机事件")
                    if (recyclerView.adapter?.itemCount == 1) {
                        LogUtil.d("web view 单机事件")
                    } else {
                        toggleExpandCollapse(context)
                    }

                }

                onMove = { deltaX, deltaY ->
                    updatePosition(deltaX.toInt(), deltaY.toInt())
                }

                // 根据item数量设置是否可拖动
                setDraggable(recyclerView.adapter?.itemCount == 1)
            }

            containerView.addView(recyclerView, FrameLayout.LayoutParams(
                COLLAPSED_WIDTH,
                COLLAPSED_HEIGHT
            ))

            this.layoutParams = createLayoutParams()

            // 添加窗口
            windowManager?.addView(containerView, layoutParams)
            this.showView = containerView
            isWindowShowing = true  // 设置窗口显示标志

            // 初始状态设置为收缩状态
            updateRecyclerViewState(false)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePosition(deltaX: Int, deltaY: Int) {
        try {
            layoutParams?.let { params ->
                params.x += deltaX
                params.y += deltaY

                val view = showView?: return
                val wm = windowManager?: return

                if (view.isAttachedToWindow) {
                    wm.updateViewLayout(view, params)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleExpandCollapse(context: Context) {
        try {
//            isExpanded = !isExpanded

            // 获取当前窗口的位置
            val currentX = layoutParams?.x ?: DEFAULT_X
            val currentY = layoutParams?.y ?: DEFAULT_Y

            // 更新布局参数
            layoutParams?.apply {
//                if (isExpanded) {
                    // 展开时使用屏幕宽度
//                    val metrics = context.resources.displayMetrics
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    // 保持在原位置
                    x = currentX
                    y = currentY
//                } else {
//                    // 收缩时恢复原始宽度
//                    width = COLLAPSED_WIDTH
//                    // 保持在原位置
//                    x = currentX
//                    y = currentY
//                }

                // 更新窗口布局
                containerView?.let { container ->
                    windowManager?.updateViewLayout(container, this)
//                    if (container.isAttachedToWindow) {
//                        windowManager?.updateViewLayout(container, this)
//                    }
                }
            }

            // 更新RecyclerView状态
            updateRecyclerViewState(isExpanded)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateRecyclerViewState(expanded: Boolean) {
        recyclerView?.let { rv ->
            val adapter = rv.adapter
            val itemCount = adapter?.itemCount ?: 0

            if (itemCount > 1) {
                // 收缩状态：滚动到最后一个项目并禁用滑动
                if (!expanded) {
                    rv.scrollToPosition(itemCount - 1)
                    disableRecyclerViewScroll(rv)
                } else {
                    // 展开状态：启用滑动
                    enableRecyclerViewScroll(rv)
                }
            }
        }
    }

    private fun disableRecyclerViewScroll(recyclerView: RecyclerView) {
        // 禁用所有触摸事件
        recyclerView.setOnTouchListener { _, _ -> true }

        // 禁用滑动
        recyclerView.layoutManager = object : LinearLayoutManager(
            recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        ) {
            override fun canScrollHorizontally(): Boolean = false
            override fun canScrollVertically(): Boolean = false
        }
    }

    private fun enableRecyclerViewScroll(recyclerView: RecyclerView) {
        // 移除触摸事件拦截
        recyclerView.setOnTouchListener(null)

        // 启用滑动
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    // 单个item时的触摸监听器
    private inner class SingleItemTouchListener : View.OnTouchListener {
        private var touchStartX = 0f
        private var touchStartY = 0f
        private var startX = 0f
        private var startY = 0f
        private var isMove = false

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMove = false
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    startX = event.x
                    startY = event.y
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - touchStartX
                    val deltaY = event.rawY - touchStartY

                    try {
                        layoutParams?.let { params ->
                            params.x = (params.x + deltaX).toInt()
                            params.y = (params.y + deltaY).toInt()

                            val currentView = showView?: return@let
                            val wm = windowManager?: return@let

                            if (currentView.isAttachedToWindow) {
                                wm.updateViewLayout(currentView, params)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    touchStartX = event.rawX
                    touchStartY = event.rawY

                    if (abs(event.x - startX) >= 5 || abs(event.y - startY) >= 5) {
                        isMove = true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (!isMove) {
                        // 单击时触发回调
                        recyclerView?.let { rv ->
                            val adapter = rv.adapter
                            if (adapter?.itemCount == 1) {
                                LogUtil.d("SingleItemTouchListener 单机事件")
//                                (adapter as? WebViewScreenshotAdapter)?.getItem(0)?.url?.let { url ->
//                                    onItemClickListener?.invoke(url)
//                                }
                            }
                        }
                    }
                }
            }
            return true
        }
    }

    // 多个item时的触摸监听器
    private inner class MultiItemTouchListener : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    // 直接触发展开/收缩
                    LogUtil.d("MultiItemTouchListener 单机事件")
//                    toggleExpandCollapse()
                }
            }
            return true
        }
    }

    private fun createLayoutParams() = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        format = PixelFormat.TRANSLUCENT
        width = COLLAPSED_WIDTH
        height = COLLAPSED_HEIGHT
        x = DEFAULT_X
        y = DEFAULT_Y
    }

    private fun createLayoutParams2() = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        format = PixelFormat.TRANSLUCENT
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = COLLAPSED_HEIGHT
        x = DEFAULT_X
        y = DEFAULT_Y
    }

    fun dismiss() {
        try {
            if (isWindowShowing && containerView?.isAttachedToWindow == true) {
                windowManager?.removeView(containerView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 清理所有资源
            containerView = null
            windowManager = null
            recyclerView = null
            isExpanded = false
            isWindowShowing = false  // 重置窗口显示标志
        }
    }

    fun isShowing(): Boolean = isWindowShowing



    // 添加位置限制
    private fun constrainPosition(x: Int, y: Int): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        val maxX = displayMetrics.widthPixels - COLLAPSED_WIDTH
        val maxY = displayMetrics.heightPixels - COLLAPSED_HEIGHT

        return Pair(
            x.coerceIn(0, maxX),
            y.coerceIn(0, maxY)
        )
    }

    // 添加动画效果
    private fun animateView(view: View, toAlpha: Float) {
        view.animate()
            .alpha(toAlpha)
            .setDuration(200)
            .start()
    }

    // 添加大小调整功能
    fun updateSize(width: Int, height: Int) {
        layoutParams?.let { params ->
            params.width = width
            params.height = height
            windowManager?.updateViewLayout(showView, params)
        }
    }

}

