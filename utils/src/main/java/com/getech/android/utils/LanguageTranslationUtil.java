package com.getech.android.utils;

import android.content.Context;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by xiao.q
 * Date: 2019/11/25 17:03
 * 语言翻译，用于IM长连接的消息    比如 xxx发起了群聊，把这些特殊的字符串翻译成英文
 */
public class LanguageTranslationUtil implements Serializable {

    private static LanguageTranslationUtil INSTANCE;

    private LanguageTranslationUtil() {

    }

    public static LanguageTranslationUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (LanguageTranslationUtil.class) {
                if (INSTANCE == null) {
                    return new LanguageTranslationUtil();
                }
            }
        }
        return INSTANCE;
    }

    public String LanguageTranslationString(Context context, String content) {
        if (null == content) {
            return "";
        }
        if (MultiLanguageUtils.isEn(context)) {
            if (content.contains("退出了群聊")) {
                return content.replace("退出了群聊", " exit group chat");
            }
            if (content.contains("已将群主转让给")) {
                return content.replace("已将群主转让给", " transfered Group Owner to ");
            }
            if (content.contains("移出群聊") && content.contains("将")) {
                return content.replace("将", " moves ").replace("移出群聊", " out of the group chat");
            }
            if (content.contains("通过扫描二维码加入群聊")) {
                return content.replace("通过扫描二维码加入群聊", " joined the group chat by QR-Code Scanning");
            }
            if (content.contains("邀请") && content.contains("加入群聊")) {
                return content.replace("邀请", " invited ").replace("加入群聊", " to join the group chat");
            }
            if (content.contains("修改群名称为")) {
                return content.replace("修改群名称为", " modified group name");
            }
            if (content.contains("您已加入部门群")) {
                return content.replace("您已加入部门群", "You have joined the department group");
            }
            if (content.contains("欢迎加入群聊")) {
                return content.replace("欢迎加入群聊", "Welcome to group chat");
            }
            if (content.contains("发起了群聊")) {
                return content.replace("发起了群聊", " created a group chat");
            }
            if (content.contains("你撤回一条消息")) {
                return content.replace("你撤回一条消息", "You wthdrew a message");
            }
            if (content.contains("撤回一条消息")) {
                return content.replace("撤回一条消息", " wthdrew a message");
            }
            if (content.contains("[名片]")) {
                return content.replace("[名片]", "[Contact Card]");
            }
            if (content.contains("[会议邀请]")) {
                return content.replace("[会议邀请]", "[meeting invitation]");
            }
            if (content.contains("[图片]")) {
                return content.replace("[图片]", "[Image]");
            }
            if (content.contains("[视频]")) {
                return content.replace("[视频]", "[Video]");
            }
            if (content.contains("[语音]")) {
                return content.replace("[语音]", "[Voice]");
            }
            if (content.contains("[视频通话]")) {
                return content.replace("[视频通话]", "[Video call]");
            }
            if (content.contains("[文件]")) {
                return content.replace("[文件]", "[File]");
            }
            if (content.contains("[位置]")) {
                return content.replace("[位置]", "[Location]");
            }
            if (content.contains("[链接]")) {
                return content.replace("[链接]", "[Link]");
            }
            if (content.contains("[公告]")) {
                return content.replace("[公告]", "[Group Notice]");
            }
            if (content.equals("文件传输助手")) {
                return content.replace("文件传输助手", "File Transfer");
            }
            if (content.equals("业务沟通")) {
                return "Business communication";
            }
            if (content.equals("机器人")) {
                return "Robot";
            }
            if (content.equals("考勤打卡")) {
                return "Time and attendance";
            }
            if (content.contains("[应用卡片]")) {
                return content.replace("[应用卡片]", "[Application Card]");
            }
            if (content.contains("[服务号文章]")) {
                return content.replace("[服务号文章]", "[ServiceNo Article]");
            }
            if (content.contains("已开启仅部分人可发言")) {
                return content.replace("已开启仅部分人可发言", "Enabled Only some members can speak");
            }
            if (content.contains("已将") && content.contains("设为管理员")) {
                return content.replace("已将", " has asigned ").replace("设为管理员", " as Group Admin");
            }
            return content;
        }
        return content;
    }

    public String LanguageTranslationNotifyString(Context context, String content) {
        if (null == content) {
            return "";
        }
        if (MultiLanguageUtils.isEn(context)) {
            if (content.contains("[名片]")) {
                return content.replace("[名片]", "[Contact Card]");
            }
            if (content.contains("[图片]")) {
                return content.replace("[图片]", "[Image]");
            }
            if (content.contains("[视频]")) {
                return content.replace("[视频]", "[Video]");
            }
            if (content.contains("[语音]")) {
                return content.replace("[语音]", "[Voice]");
            }
            if (content.contains("[视频通话]")) {
                return content.replace("[视频通话]", "[Video call]");
            }
            if (content.contains("[文件]")) {
                return content.replace("[文件]", "[File]");
            }
            if (content.contains("[位置]")) {
                return content.replace("[位置]", "[Location]");
            }
            if (content.contains("[链接]")) {
                return content.replace("[链接]", "[Link]");
            }
            if (content.contains("[公告]")) {
                return content.replace("[公告]", "[Group Notice]");
            }
            if (content.contains("[会议邀请]")) {
                return content.replace("[会议邀请]", "[meeting invitation]");
            }
            if (content.contains("[应用卡片]")) {
                return content.replace("[应用卡片]", "[Application Card]");
            }
            if (content.contains("[服务号文章]")) {
                return content.replace("[服务号文章]", "[ServiceNo Article]");
            }
            return content;
        }
        return content;
    }

    private Object readResolve() throws ObjectStreamException {
        // instead of the object we're on,
        // return the class variable INSTANCE
        return INSTANCE;
    }

}
