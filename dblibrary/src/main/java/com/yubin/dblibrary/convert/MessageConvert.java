package com.yubin.dblibrary.convert;

import androidx.room.TypeConverter;

import com.yubin.baselibrary.common.ChatType;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/01/24
 *     desc    : 消息转换器
 *     version : 1.0
 * </pre>
 */
public class MessageConvert {
    @TypeConverter
    public static ChatType parseChatType(int status) {
        return ChatType.valueOf(status);
    }

    @TypeConverter
    public static int parseChatType(ChatType chatType) {
        return chatType.getValue();
    }
}
