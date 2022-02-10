package com.yubin.kotlindb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.yubin.baselibrary.bean.BaseModel
import com.yubin.baselibrary.common.ChatType
import java.io.Serializable

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/1/24
 * desc    : 会话
 * version : 1.0
</pre> *
 */
@Entity(
    tableName = "conversation",
    primaryKeys = ["session_id", "chat_type"],
    indices = [Index("time")]
)
data class Conversation(
    @ColumnInfo(name = "session_id") val sessionId: String?,
    @ColumnInfo(name = "unread_num") var unReadNum: Int = 0,
    @ColumnInfo(name = "chat_type") private var chatType: ChatType? = null,
    var time: Long = 0
) : BaseModel(), Serializable