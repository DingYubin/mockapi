package com.yubin.kotlindb.dao

import androidx.room.*
import com.yubin.baselibrary.common.ChatType
import com.yubin.kotlindb.entity.Conversation
import io.reactivex.Flowable

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/01/24
 * desc    : 会话接口
 * version : 1.0
</pre> *
 */
@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(conversation: Conversation?): Long

    @Delete
    fun delete(conversation: Conversation?)

    @Query("delete from conversation")
    fun deleteAll()

    @Query("update conversation set unread_num = 0 where session_id = :id and chat_type = :chatType")
    fun read(id: String?, chatType: ChatType?): Int

    /**
     * 更新会话列表未读数
     *
     * @param unread_num 未读数
     * @param session_id 会话id
     */
    @Query("update conversation set unread_num = :unread_num where session_id = :session_id")
    fun updateConversationUnReadNumber(unread_num: Int, session_id: String?)

    @Query("select * from conversation where chat_type = :chatType and session_id = :sessionId")
    fun queryConversation(sessionId: String?, chatType: ChatType?): Conversation?

    @Query("select session_id from conversation where chat_type = 0 order by time desc ")
    fun listSingleChat(): Flowable<List<String?>?>?
}