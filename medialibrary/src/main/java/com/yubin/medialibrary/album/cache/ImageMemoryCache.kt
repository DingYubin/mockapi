package com.yubin.medialibrary.album.cache

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache

/**
 * description 内存缓存类使用LruCache
 *
 * @author laiwei
 * @date create at 2019-07-16 10:48
 */
class ImageMemoryCache<E>(context: Context) {
    private var fileCache: ImageFileCache<E>? = null
    private val memoryCache: LruCache<E, Bitmap>
    private val memoryCacheSize: Int
    operator fun get(e: E): Bitmap? {
        return memoryCache[e]
    }

    fun getFromFile(e: E): Bitmap? {
        return if (null != fileCache) {
            fileCache?.getBitmapFromFile(e)
        } else null
    }

    fun put(e: E?, bitmap: Bitmap?) {
        if (null == e || null == bitmap) {
            return
        }
        memoryCache.put(e, bitmap)
    }

    fun putToFile(e: E, bitmap: Bitmap?) {
        if (null != fileCache && null != bitmap) {
            fileCache?.storeThumbnailIntoCacheFile(e, bitmap)
        }
    }

    fun evictAll() {
        memoryCache.evictAll()
        if (null != fileCache) {
            fileCache?.release()
        }
    }

    init {
        val file = context.getExternalFilesDir("Thumbnail")
        if (null != file) {
            fileCache = ImageFileCache(context)
        }
        memoryCacheSize = (Runtime.getRuntime().maxMemory() / 8).toInt()
        memoryCache = object : LruCache<E, Bitmap>(memoryCacheSize) {
            override fun sizeOf(key: E, value: Bitmap): Int {
                return value.allocationByteCount
            }

            override fun entryRemoved(
                evicted: Boolean,
                key: E,
                oldValue: Bitmap,
                newValue: Bitmap?
            ) {
                super.entryRemoved(evicted, key, oldValue, newValue)
                if (evicted) {
                    oldValue.recycle()
                }
            }
        }
    }
}