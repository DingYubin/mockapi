package com.yubin.medialibrary.album.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.yubin.medialibrary.R
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.util.MD5Util
import com.yubin.medialibrary.util.MediaUtil
import com.yubin.medialibrary.util.UnitHelper
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 *description
 *
 *@author laiwei
 *@date create at 4/24/21 11:25 AM
 */
class ImageLoaderManager {
    private var mMemoryCache: ImageMemoryCache<String>? = null
    private var pool: ThreadPoolExecutor? = null
    private var mDefaultResId = MediaManager.instance.mediaStyle?.defaultImage
        ?: R.drawable.icon_cec_place_holder_square
    private var targetWidth = 0f

    companion object {
        val INSTANCE: ImageLoaderManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ImageLoaderManager()
        }
    }

    /**
     * 初始化线程池相关的参数
     */
    fun initThreadPoolExecutor(blockingDequeCapacity: Int, context: Context) {
        mMemoryCache =
            ImageMemoryCache(context)
        val corePoolSize =
            Runtime.getRuntime().availableProcessors().coerceAtMost(4)
        val blockingDeque: BlockingDeque<Runnable> =
            LinkedBlockingDeque(blockingDequeCapacity)
        pool = ThreadPoolExecutor(
            corePoolSize,
            corePoolSize,
            60,
            TimeUnit.SECONDS,
            blockingDeque,
            ThreadPoolExecutor.DiscardOldestPolicy()
        )
        targetWidth = UnitHelper.dp2px(context, 91f)
    }

    fun release() {
        if (null != mMemoryCache) {
            mMemoryCache?.evictAll()
        }
        if (null != pool) {
            pool?.shutdownNow()
        }
    }


    fun intoTarget(key: String?, target: ImageView?) {
        if (key == null || target == null) {
            target?.setImageResource(mDefaultResId)
            return
        }
        val md5 = MD5Util.toMd5(key)
        target.tag = md5
        val bitmap = mMemoryCache?.get(md5)
        if (null != bitmap) {
            target.setImageBitmap(bitmap)
        } else {
            target.setImageResource(mDefaultResId)
            pool?.execute(DecodeTask(key, md5, target))
        }
    }

    fun intoTarget(key: String?, videoPath: String?, target: ImageView?) {
        if ((key == null && videoPath == null) || target == null) {
            target?.setImageResource(mDefaultResId)
            return
        }
        if (key?.isNotEmpty() == true) {
            val md5 = MD5Util.toMd5(key)
            target.tag = md5
            val bitmap = mMemoryCache?.get(md5)
            if (null != bitmap) {
                target.setImageBitmap(bitmap)
            } else {
                target.setImageResource(mDefaultResId)
                pool?.execute(DecodeTask(key, md5, target))
            }
        } else {
            val md5 = MD5Util.toMd5(videoPath)
            target.tag = md5
            val bitmap = mMemoryCache?.get(md5)
            if (null != bitmap) {
                target.setImageBitmap(bitmap)
            } else {
                target.setImageResource(mDefaultResId)
                videoPath?.let {
                    pool?.execute(DecodeVideoTask(videoPath, md5, target))
                }
            }
        }

    }

    fun decodeBitmapFromFile(name: String?): Bitmap? {
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

    inner class DecodeTask(
        private val name: String?,
        private val md5: String,
        private val target: ImageView?
    ) : Runnable {
        override fun run() {
            val bitmap: Bitmap?
            if (mMemoryCache?.getFromFile(md5) == null) {
                bitmap = decodeBitmapFromFile(name)
                mMemoryCache?.putToFile(md5, bitmap)
            } else {
                bitmap = mMemoryCache?.getFromFile(md5)
            }
            mMemoryCache?.put(md5, bitmap)
            if (md5 == target?.tag) {
                mHandler.post {
                    target.setImageBitmap(bitmap)
                }
            }
        }
    }

    inner class DecodeVideoTask(
        private val name: String,
        private val md5: String,
        private val target: ImageView?
    ) : Runnable {
        override fun run() {
            val bitmap: Bitmap?
            if (mMemoryCache?.getFromFile(md5) == null) {
                bitmap = MediaUtil.decodeVideoThumb(name)
                mMemoryCache?.putToFile(md5, bitmap)
            } else {
                bitmap = mMemoryCache?.getFromFile(md5)
            }
            mMemoryCache?.put(md5, bitmap)
            if (md5 == target?.tag) {
                mHandler.post {
                    target.setImageBitmap(bitmap)
                }
            }
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())


}