package com.yubin.medialibrary.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


/**
 * description 文件压缩工具
 *
 * @author yubin
 * @date create at 2019-07-17 14:49
 */
object BitmapCompressUtil {
    const val TAG = "BitmapCompressUtil"
    private const val CRITICAL_VALUE = (2 * 1024 * 1024).toLong()

    fun compressData2Bitmap(data: ByteArray?, degree: Int): Bitmap? {
        if (null == data) {
            return null
        }
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = 2
        options.inPreferredConfig = Bitmap.Config.RGB_565
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(data, 0, data.size, options)
        if (null == bitmap) {
            return null
        }
        if (degree != 0) {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        }
        return bitmap
    }

    fun compressBitmap2File(
        bitmap: Bitmap?,
        desPath: String,
        compressFormat: Bitmap.CompressFormat
    ): String? {
        try {
            val fos = FileOutputStream(File(desPath))
            bitmap?.compress(compressFormat, 100, fos)
            fos.flush()
            fos.close()
            return desPath
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return null
    }

    fun compressBitmapFile(
        path: String?,
        desPath: String,
        format: String?,
        maxWidth: Int,
        maxHeight: Int,
        quality: Float
    ): String? {
        if (path.isNullOrEmpty()) {
            return null
        }
        var sourceBitmap: Bitmap? = null
        var bitmap: Bitmap? = null
        return try {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            val w: Int = options.outWidth
            val h: Int = options.outHeight
            if (w * h > 3656 * 3656) {
                options.inSampleSize = (w.coerceAtLeast(h) / 3656f).roundToInt()
            }
            options.inJustDecodeBounds = false
            sourceBitmap = BitmapFactory.decodeFile(path, options)
            if (null == sourceBitmap) {
                return null
            }
            val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
            val desFile = File(desPath)
            var fos = FileOutputStream(desFile)
            sourceBitmap.compress(compressFormat, 80, fos)
            fos.flush()
            fos.close()
            val totalSpace = desFile.length()
            if (totalSpace > CRITICAL_VALUE) {
                val w: Int = sourceBitmap.width
                val h: Int = sourceBitmap.height
                val scale = calcScaleSize(w, h)
                val matrix = Matrix()
                matrix.setScale(scale, scale)
                bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, w, h, matrix, true)
                fos = FileOutputStream(desFile)
                bitmap.compress(compressFormat, 80, fos)
                fos.flush()
                fos.close()
            }
            desPath
        } catch (e: IOException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        } catch (e: OutOfMemoryError) {
            compressBitmapFileFirstSize(path, desPath, format, maxWidth, maxHeight, quality)
        } finally {
            sourceBitmap?.recycle()
            bitmap?.recycle()
        }
    }

    private fun compressBitmapFileFirstSize(
        path: String?,
        desPath: String?,
        format: String?,
        maxWidth: Int,
        maxHeight: Int,
        quality: Float
    ): String? {
        if (path.isNullOrEmpty()) {
            return null
        }
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val w: Int = options.outWidth
        val h: Int = options.outHeight
        val scale = calcScaleSize(w, h)
        options.inJustDecodeBounds = false
        var sourceBitmap: Bitmap? = null
        var bitmap: Bitmap
        try {
            sourceBitmap = BitmapFactory.decodeFile(path, options)
            val matrix = Matrix()
            matrix.setScale(scale, scale)
            bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, w, h, matrix, true)
        } catch (e: Exception) {
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inSampleSize = Math.ceil((1 / scale).toDouble()).toInt()
            bitmap = BitmapFactory.decodeFile(path, options)
        }
        val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        try {
            val fos = FileOutputStream(File(desPath))
            bitmap.compress(compressFormat, 70, fos)
            fos.flush()
            fos.close()
            return desPath
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            sourceBitmap?.recycle()
            bitmap.recycle()
        }
        return null
    }

    private fun calcScaleSize(srcWidth: Int, srcHeight: Int): Float {
        val longSide: Int
        val shortSide: Int
        if (srcHeight >= srcWidth) {
            longSide = srcHeight
            shortSide = srcWidth
        } else {
            longSide = srcWidth
            shortSide = srcHeight
        }
        val scale = longSide.toFloat() / shortSide
        if (shortSide <= 1280) {
            return 1F
        }
        return if (scale <= 2.5) {
            if (shortSide <= 3840) {
                1440f / shortSide
            } else 1920f / shortSide
        } else {
            1280f / shortSide
        }
    }

    fun decodeBitmapFromFile(name: String?, targetWidth: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(name, options)
        val w = options.outWidth
        val h = options.outHeight
        val sw = w / targetWidth
        val sh = h / targetWidth
        var s: Int = if (sw > sh) sw.toInt() else sh.toInt()
        s = if (s > 1) s else 1
        options.inJustDecodeBounds = false
        options.inSampleSize = s
        options.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(name, options)
    }

    //生成圆角图片
    fun getRoundedCornerBitmap(bitmap: Bitmap, roundPx: Float): Bitmap? {
        return try {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            val rectF = RectF(
                Rect(
                    0, 0, bitmap.width,
                    bitmap.height
                )
            )
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.BLACK
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            canvas.drawBitmap(bitmap, src, rect, paint)
            bitmap.recycle()
            output
        } catch (e: java.lang.Exception) {
            bitmap
        }
    }


}