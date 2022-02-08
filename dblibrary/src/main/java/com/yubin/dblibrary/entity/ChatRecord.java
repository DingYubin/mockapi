package com.yubin.dblibrary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.yubin.baselibrary.bean.BaseModel;
import com.yubin.baselibrary.common.ChatType;

import java.io.Serializable;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/01/24
 *     desc    : 聊天记录
 *     version : 1.0
 * </pre>
 */
@Entity(tableName = "chat_record",
        indices = {@Index("session_id"), @Index("timestamp"), @Index(value = "msg_id", unique = true)})
public class ChatRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 3731953708165173927L;

    /**
     * 消息id
     */
    @PrimaryKey
    @NonNull
    private String id;

    /**
     * 服务端返回的唯一id
     * 消息撤回使用
     */
    @ColumnInfo(name = "msg_id")
    private Long msgId;

    /**
     * 联系人id
     */
    @ColumnInfo(name = "session_id")
    private String sessionId;

    /**
     * 消息发送人id
     */
    @ColumnInfo(name = "from_id")
    private String fromId;

    /**
     * 消息内容 格式文本，TEXT_IMG格式拆分为多条消息
     */
    private String content;

    /**
     * 多媒体文件本地路径
     */
    @ColumnInfo(name = "local_uri")
    private String localUri;

    /**
     * 消息时间
     */
    @ColumnInfo(name = "timestamp")
    private long timeStamp;

    /**
     * 时长(语音，视频)
     */
    private int duration;

    @ColumnInfo(name = "chat_type")
    @NonNull
    private ChatType chatType;

    private String exra;
    /**
     * 缩略图url
     */
    private String smallImgUrl;
    /**
     * 缩略图尺寸
     */
    private String smallImgSize;

    @ColumnInfo(name = "msg_seq")
    private long seq;

    public int unRead = -1;

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @NonNull
    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(@NonNull ChatType chatType) {
        this.chatType = chatType;
    }

    public String getExra() {
        return exra;
    }

    public void setExra(String exra) {
        this.exra = exra;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public String getSmallImgSize() {
        return smallImgSize;
    }

    public void setSmallImgSize(String smallImgSize) {
        this.smallImgSize = smallImgSize;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }
}
