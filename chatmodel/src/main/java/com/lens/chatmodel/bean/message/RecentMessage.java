package com.lens.chatmodel.bean.message;

import android.text.TextUtils;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.helper.ChatHelper;

/**
 * Created by LL130386 on 2017/12/30.
 * 消息列表数据类
 */

public class RecentMessage implements Comparable<RecentMessage> {

    private String chatId;
    private String msg;
    private String nick;
    private String groupName;
    private EMessageType msgType;
    private String userId;//发送者的userId
    private int topFlag;
    private int unreadCount;
    private int notDisturb;
    private long time;
    private boolean isAt;
    private int chatType;
    private String hint;
    private String avatarUrl;
    private boolean isNew;
    private int backgroundId;//聊天背景图片


    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }


    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int type) {
        chatType = type;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String id) {
        this.chatId = id;
    }

    public boolean isAt() {
        return isAt;
    }

    public void setAt(boolean at) {
        isAt = at;
    }

    private ChatEnum.ESureType sendType;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public EMessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(EMessageType msgType) {
        this.msgType = msgType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(int topFlag) {
        this.topFlag = topFlag;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getNotDisturb() {
        return notDisturb;
    }

    public void setNotDisturb(int notDisturb) {
        this.notDisturb = notDisturb;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ChatEnum.ESureType getSendType() {
        return sendType;
    }

    public void setSendType(ChatEnum.ESureType sendType) {
        this.sendType = sendType;
    }

    public String getHint() {
        if (TextUtils.isEmpty(hint)) {
            if (!TextUtils.isEmpty(msg)) {
                String s = ChatHelper.getHint(getMsgType(), msg, false);
                if (TextUtils.isEmpty(s)) {
                    hint = msg;
                }
                hint = s;
            }
        }
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public int compareTo(RecentMessage recentMessage) {
        return ((Long) recentMessage.getTime()).compareTo(this.getTime());
    }
}
