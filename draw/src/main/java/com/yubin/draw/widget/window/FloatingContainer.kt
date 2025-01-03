package com.yubin.draw.widget.window

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.FrameLayout
import com.yubin.baselibrary.util.LogUtil
import kotlin.math.abs

class FloatingContainer(context: Context) : FrameLayout(context) {
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var startX = 0f
    private var startY = 0f
    private var isMove = false
    private var isDraggable = true

    var onSingleItemClick: (() -> Unit)? = null
    var onExpandClick: (() -> Unit)? = null
    var onMove: ((Float, Float) -> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isMove = false
                touchStartX = ev.rawX
                touchStartY = ev.rawY
                startX = ev.x
                startY = ev.y
                LogUtil.d("web view MotionEvent.ACTION_DOWN")
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDraggable) return false

                val deltaX = abs(ev.x - startX)
                val deltaY = abs(ev.y - startY)
                LogUtil.d("web view MotionEvent.ACTION_MOVE")
                // 如果移动距离超过阈值，拦截事件
                if (deltaX > 5 || deltaY > 5) {
                    isMove = true
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                LogUtil.d("web view MotionEvent.ACTION_UP")
                if (isDraggable) {
                    onSingleItemClick?.invoke()
                } else {
                    onExpandClick?.invoke()
                }
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!isDraggable) return false

                val deltaX = event.rawX - touchStartX
                val deltaY = event.rawY - touchStartY

                onMove?.invoke(deltaX, deltaY)
                LogUtil.d("onTouchEvent view MotionEvent.ACTION_MOVE")
                touchStartX = event.rawX
                touchStartY = event.rawY
            }

            MotionEvent.ACTION_UP -> {
                LogUtil.d("onTouchEvent view MotionEvent.ACTION_UP")
                if (!isMove) {
                    if (isDraggable) {
                        onSingleItemClick?.invoke()
                    } else {
                        onExpandClick?.invoke()
                    }
                }
            }
        }
        return true
    }

    fun setDraggable(draggable: Boolean) {
        isDraggable = draggable
    }
}