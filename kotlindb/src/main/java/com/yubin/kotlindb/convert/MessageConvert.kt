package com.yubin.kotlindb.convert

import androidx.room.TypeConverter
import java.util.Date

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/01/24
 * desc    : 消息转换器
 * version : 1.0
</pre> *
 */
object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringArray(images: Array<String>?): String? {
        return images?.joinToString(",")
    }

    @TypeConverter
    fun toStringArray(imagesString: String?): Array<String>? {
        return imagesString?.split(",")?.toTypedArray()
    }
}