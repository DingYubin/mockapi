package com.yubin.kotlindb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yubin.baselibrary.common.ChatType
import com.yubin.kotlindb.entity.ChatRecord
import io.reactivex.Maybe

@Dao
interface ChatRecordDao {
    @Query("delete from chat_record where id = :id")
    fun delChat(id: String?)

    @Query("delete from chat_record")
    fun delAllChat()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(chatRecord: ChatRecord?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(chatRecord: List<ChatRecord?>?)

    @Query("update chat_record set msg_id = :msgId, timestamp = :timestamp  where id = :id")
    fun updateMsgId(id: String?, msgId: Long, timestamp: Long): Long

    /**
     * 修改未读个数
     */
    @Query("update chat_record set unRead = :unRead where msg_id = :msgId")
    fun updateUnread(msgId: Long, unRead: Int)

    /**
     * 同一个对话里聊天内容（模糊查询）
     *
     * @param sessionId 对话
     * @param chatType  会话类型
     * @param content   聊天内容
     */
    @Query("select * from chat_record where session_id = :sessionId and chat_type  = :chatType and content like '%' || :content || '%' order by msg_seq ASC")
    fun listConversationDimContentByChatType(
        sessionId: String?,
        chatType: ChatType?, content: String?
    ): Maybe<List<ChatRecord?>?>?
}