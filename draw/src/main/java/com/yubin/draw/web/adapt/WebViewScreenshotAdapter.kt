package com.yubin.draw.web.adapt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.yubin.draw.R
import com.yubin.draw.databinding.ViewItemWebScreenBinding

class WebViewScreenshotAdapter : RecyclerView.Adapter<WebViewScreenshotAdapter.ScreenshotViewHolder>() {
    val screenshots = mutableListOf<WebViewScreenshotItem>()

    data class WebViewScreenshotItem(
        val bitmap: Bitmap,
        val url: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    inner class ScreenshotViewHolder(
        private val binding: ViewItemWebScreenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WebViewScreenshotItem) {
            binding.apply {
                imageScreenshot.setImageBitmap(item.bitmap)
                // 可选：显示网页标题或URL
                textUrl.text = item.url

                // 设置圆角和阴影效果
                root.apply {
                    elevation = 8f
                    setBackgroundResource(R.drawable.bg_screenshot_item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val binding = ViewItemWebScreenBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScreenshotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        holder.bind(screenshots[position])
    }

    override fun getItemCount(): Int = screenshots.size

    fun addScreenshot(webView: WebView) {
        try {
            val bitmap = captureWebView(webView)
            val url = webView.url ?: ""
            val item = WebViewScreenshotItem(bitmap, url)
            screenshots.add(item)
            notifyItemInserted(screenshots.lastIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun captureWebView(webView: WebView): Bitmap {
        // 获取WebView的内容高度
        val scale = webView.scale
        val width = webView.width
        val height = (webView.contentHeight * scale).toInt()

        // 创建位图
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        webView.draw(canvas)

        // 压缩bitmap以节省内存
        return compressBitmap(bitmap)
    }

    private fun compressBitmap(source: Bitmap): Bitmap {
        val maxWidth = 340  // 与悬浮窗宽度匹配
        val maxHeight = 600 // 与悬浮窗高度匹配

        val ratio = minOf(
            maxWidth.toFloat() / source.width,
            maxHeight.toFloat() / source.height
        )

        val width = (source.width * ratio).toInt()
        val height = (source.height * ratio).toInt()

        val result = Bitmap.createScaledBitmap(source, width, height, true)
        if (result != source) {
            source.recycle()
        }
        return result
    }

    fun clear() {
        screenshots.forEach { it.bitmap.recycle() }
        screenshots.clear()
        notifyDataSetChanged()
    }
}