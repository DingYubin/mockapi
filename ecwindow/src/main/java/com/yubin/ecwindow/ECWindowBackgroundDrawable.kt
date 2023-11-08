package com.yubin.ecwindow

import android.graphics.*
import android.graphics.drawable.Drawable

class ECWindowBackgroundDrawable(
    val triangleHeight: Float,
    cornerPathEffect: Float,
    private val style: Paint.Style,
    val strokeWidth: Float,
    private val strokeColor: Int,
    private val fillColor: Int,
) : Drawable() {

    companion object {
        const val TRIANGLE_DIRECTION_UP = 0
        const val TRIANGLE_DIRECTION_DOWN = 1
    }

    private val paint: Paint = Paint()
    private val path = Path()

    var triangleOffset = 0f
    var triangleDirection = TRIANGLE_DIRECTION_UP

    init {
        paint.color = strokeColor
        paint.strokeCap = Paint.Cap.ROUND
        paint.pathEffect = CornerPathEffect(cornerPathEffect)
        paint.isAntiAlias = true
//        paint.setShadowLayer(3f, 3f, 3f, Color.GRAY)
    }

    override fun draw(canvas: Canvas) {
        when (style) {
            Paint.Style.FILL_AND_STROKE -> {
                this.setFillPath()
                paint.style = Paint.Style.FILL
                paint.color = fillColor
                canvas.drawPath(path, paint)

                this.setPath()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = strokeColor
                canvas.drawPath(path, paint)
            }
            Paint.Style.STROKE -> {
                this.setPath()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = strokeColor
                canvas.drawPath(path, paint)
            }
            Paint.Style.FILL -> {
                this.setPath()
                paint.style = Paint.Style.FILL
                paint.color = fillColor
                canvas.drawPath(path, paint)
            }
        }
    }

    private fun setPath() {
        val w = bounds.right - bounds.left
        val h = bounds.bottom - bounds.top

        val triangleLocX = w / 2f - triangleOffset

        path.reset()
        val cohesion = this.strokeWidth / 2
        if (triangleDirection == TRIANGLE_DIRECTION_UP) {
            path.moveTo(cohesion, this.triangleHeight + cohesion)
            path.lineTo(triangleLocX - this.triangleHeight, this.triangleHeight + cohesion)
            path.lineTo(triangleLocX, cohesion)
            path.lineTo(triangleLocX + this.triangleHeight, this.triangleHeight + cohesion)
            path.lineTo(w.toFloat() - cohesion, this.triangleHeight + cohesion)
            path.lineTo(w.toFloat() - cohesion, h.toFloat() - cohesion)
            path.lineTo(cohesion, h.toFloat() - cohesion)
            path.lineTo(cohesion, this.triangleHeight + cohesion)
        } else {
            path.moveTo(cohesion, cohesion)
            path.lineTo(w.toFloat() - cohesion, cohesion)
            path.lineTo(w.toFloat() - cohesion, h - this.triangleHeight - cohesion)
            path.lineTo(triangleLocX + triangleHeight, h - this.triangleHeight - cohesion)
            path.lineTo(triangleLocX, h.toFloat() - cohesion)
            path.lineTo(triangleLocX - triangleHeight, h - this.triangleHeight - cohesion)
            path.lineTo(cohesion, h - this.triangleHeight - cohesion)
            path.lineTo(cohesion, cohesion)
        }
        path.close()
    }

    private fun setFillPath() {
        val w = bounds.right - bounds.left
        val h = bounds.bottom - bounds.top

        val triangleLocX = w / 2f - triangleOffset

        path.reset()
        val cohesion = this.strokeWidth
        if (triangleDirection == TRIANGLE_DIRECTION_UP) {
            path.moveTo(cohesion, this.triangleHeight + cohesion)
            path.lineTo(triangleLocX - this.triangleHeight, this.triangleHeight + cohesion)
            path.lineTo(triangleLocX, cohesion)
            path.lineTo(triangleLocX + this.triangleHeight, this.triangleHeight + cohesion)
            path.lineTo(w.toFloat() - cohesion, this.triangleHeight + cohesion)
            path.lineTo(w.toFloat() - cohesion, h.toFloat() - cohesion)
            path.lineTo(cohesion, h.toFloat() - cohesion)
            path.lineTo(cohesion, this.triangleHeight + cohesion)
        } else {
            path.moveTo(cohesion, cohesion)
            path.lineTo(w.toFloat() - cohesion, cohesion)
            path.lineTo(w.toFloat() - cohesion, h - this.triangleHeight - cohesion)
            path.lineTo(triangleLocX + triangleHeight, h - this.triangleHeight - cohesion)
            path.lineTo(triangleLocX, h.toFloat() - cohesion)
            path.lineTo(triangleLocX - triangleHeight, h - this.triangleHeight - cohesion)
            path.lineTo(cohesion, h - this.triangleHeight - cohesion)
            path.lineTo(cohesion, cohesion)
        }
        path.close()
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

}