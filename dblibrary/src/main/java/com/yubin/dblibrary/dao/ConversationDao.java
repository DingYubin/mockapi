package com.yubin.dblibrary.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yubin.baselibrary.common.ChatType;
import com.yubin.dblibrary.entity.Conversation;

import java.util.List;

import io.reactivex.Flowable;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/01/24
 *     desc    : 会话接口
 *     version : 1.0
 * </pre>
 */
@Dao
public interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Conversation conversation);

    @Delete
    void delete(Conversation conversation);

    @Query("delete from conversation")
    void deleteAll();

    @Query("update conversation set unread_num = 0 where session_id = :id and chat_type = :chatType")
    int read(String id, ChatType chatType);

    /**
     * 更新会话列表未读数
     *
     * @param unread_num 未读数
     * @param session_id 会话id
     */
    @Query("update conversation set unread_num = :unread_num where session_id = :session_id")
    void updateConversationUnReadNumber(int unread_num, String session_id);

    @Query("select * from conversation where chat_type = :chatType and session_id = :sessionId")
    Conversation queryConversation(String sessionId, ChatType chatType);

    @Query("select session_id from conversation where chat_type = 0 order by time desc ")
    Flowable<List<String>> listSingleChat();


}
