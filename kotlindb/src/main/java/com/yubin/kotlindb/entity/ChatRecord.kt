package com.yubin.kotlindb.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yubin.baselibrary.bean.BaseModel
import com.yubin.baselibrary.common.ChatType
import java.io.Serializable

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/01/24
 * desc    : 聊天记录
 * version : 1.0
</pre> *
 */
@Entity(
    tableName = "chat_record",
    indices = [Index("session_id"), Index("timestamp"), Index(value = arrayOf("msg_id"), unique = true)]
)
class ChatRecord : BaseModel(), Serializable {
    /**
     * 消息id
     */
    @NonNull
    @PrimaryKey
    var id: String? = null

    /**
     * 服务端返回的唯一id
     * 消息撤回使用
     */
    @ColumnInfo(name = "msg_id")
    var msgId: Long? = null

    /**
     * 联系人id
     */
    @ColumnInfo(name = "session_id")
    var sessionId: String? = null

    /**
     * 消息发送人id
     */
    @ColumnInfo(name = "from_id")
    var fromId: String? = null

    /**
     * 消息内容 格式文本，TEXT_IMG格式拆分为多条消息
     */
    var content: String? = null

    /**
     * 多媒体文件本地路径
     */
    @ColumnInfo(name = "local_uri")
    var localUri: String? = null

    /**
     * 消息时间
     */
    @ColumnInfo(name = "timestamp")
    var timeStamp: Long = 0

    /**
     * 时长(语音，视频)
     */
    var duration = 0

    @ColumnInfo(name = "chat_type")
    var chatType: ChatType? = null
    var exra: String? = null

    /**
     * 缩略图url
     */
    var smallImgUrl: String? = null

    /**
     * 缩略图尺寸
     */
    var smallImgSize: String? = null

    @ColumnInfo(name = "msg_seq")
    var seq: Long = 0
    var unRead = -1

    companion object {
        private const val serialVersionUID = 3731953708165173927L
    }
}