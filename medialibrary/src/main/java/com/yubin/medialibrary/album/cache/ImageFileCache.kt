package com.yubin.medialibrary.album.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.internal.cache.DiskLruCache
import okio.Buffer
import okio.Sink
import okio.Source
import java.io.IOException
import java.io.InputStream

/**
 *description
 *
 *@author laiwei
 *@date create at 4/24/21 11:24 AM
 */
class ImageFileCache<E>(context: Context) {
    private var mDiskLruCache: DiskLruCache = CacheManager.getDiskLruCache(context)
    fun storeThumbnailIntoCacheFile(e: E, source: Bitmap) {
        if (mDiskLruCache.isClosed()) {
            return
        }
        var sink: Sink? = null
        var buffer: Buffer? = null
        try {
            val snapShot = mDiskLruCache[e.toString()]
            if (null == snapShot) {
                val editor = mDiskLruCache.edit(e.toString())
                if (null != editor) {
                    sink = editor.newSink(0)
                    buffer = Buffer()
                    val os = buffer.outputStream()
                    source.compress(Bitmap.CompressFormat.WEBP, 100, os)
                    sink?.write(buffer, buffer.size)
                    editor.commit()
                }
            }
        } catch (e1: java.lang.Exception) {
            e1.printStackTrace()
        } finally {
            try {
                if (null != sink) {
                    sink.flush()
                    sink.close()
                }
                if (null != buffer) {
                    buffer.clear()
                    buffer.close()
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }

    fun getBitmapFromFile(e: E): Bitmap? {
        if (mDiskLruCache.isClosed()) {
            return null
        }
        var source: Source? = null
        var inputStream: InputStream? = null
        var buffer: Buffer? = null
        var snapShot: DiskLruCache.Snapshot? = null
        try {
            snapShot = mDiskLruCache[e.toString()]
            if (null != snapShot) {
                source = snapShot.getSource(0)
                buffer = Buffer()
                var ret = source.read(buffer, 4L * 1024)
                while (ret != -1L) {
                    ret = source.read(buffer, 4L * 1024)
                }
                inputStream = buffer.inputStream()
                return BitmapFactory.decodeStream(inputStream)
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                if (null != buffer) {
                    buffer.clear()
                    buffer.close()
                }
                source?.close()
                snapShot?.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
        return null
    }

    fun release() {
        try {
            mDiskLruCache.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}