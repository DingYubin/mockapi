package com.yubin.kotlindb.convert

import androidx.room.TypeConverter
import com.yubin.baselibrary.common.ChatType

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/01/24
 * desc    : 消息转换器
 * version : 1.0
</pre> *
 */
object MessageConvert {
    @TypeConverter
    fun parseChatType(status: Int): ChatType {
        return ChatType.valueOf(status)
    }

    @TypeConverter
    fun parseChatType(chatType: ChatType): Int {
        return chatType.value
    }
}