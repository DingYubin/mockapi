package com.yubin.dblibrary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.yubin.baselibrary.bean.BaseModel;
import com.yubin.baselibrary.common.ChatType;

import java.io.Serializable;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/1/24
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */
@Entity(tableName = "conversation",
        primaryKeys = {"session_id", "chat_type"},
        indices = {@Index("time")}
)
public class Conversation extends BaseModel implements Serializable {

    private static final long serialVersionUID = 4542253293693768051L;

    @NonNull
    @ColumnInfo(name = "session_id")
    private String sessionId;

    @ColumnInfo(name = "unread_num")
    private int unReadNum;

    private long time;

    /**
     * 会话类型
     */
    @NonNull
    @ColumnInfo(name = "chat_type")
    private ChatType chatType;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "sessionId='" + sessionId + '\'' +
                ", unReadNum=" + unReadNum +
                ", time=" + time +
                ", chatType=" + chatType +
                '}';
    }
}
