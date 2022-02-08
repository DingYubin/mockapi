package com.yubin.dblibrary.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yubin.baselibrary.common.ChatType;
import com.yubin.dblibrary.entity.ChatRecord;

import java.util.List;

import io.reactivex.Maybe;
@Dao
public interface ChatRecordDao {

    @Query("delete from chat_record where id = :id")
    void delChat(String id);

    @Query("delete from chat_record")
    void delAllChat();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(ChatRecord chatRecord);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<ChatRecord> chatRecord);

    @Query("update chat_record set msg_id = :msgId, timestamp = :timestamp  where id = :id")
    long updateMsgId(String id, long msgId, long timestamp);

    /**
     * 修改未读个数
     */
    @Query("update chat_record set unRead = :unRead where msg_id = :msgId")
    void updateUnread(long msgId, int unRead);

    /**
     * 同一个对话里聊天内容（模糊查询）
     *
     * @param sessionId 对话
     * @param chatType  会话类型
     * @param content   聊天内容
     */
    @Query("select * from chat_record where session_id = :sessionId and chat_type  = :chatType and content like '%' || :content || '%' order by msg_seq ASC")
    Maybe<List<ChatRecord>> listConversationDimContentByChatType(String sessionId,
                                                                 ChatType chatType, String content);
}
