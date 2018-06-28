package com.lens.chatmodel.bean.message;

import android.text.TextUtils;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.helper.ChatHelper;

/**
 * Created by LL130386 on 2018/4/18.
 * 消息数据类
 */

public class MessageBean extends BaseFGMessage {

    private EMessageType messageType;
    private String nick;
    private ESendType sendType;
    boolean isIncoming;
    private String content;
    private String uploadUrl;
    private int uploadProgress;
    private EPlayType playStatus;
    boolean isSecret;
    boolean isGroupChat;
    String groupName;
    private EActionType actionType;
    private String from;
    private String to;
    private int timeLength;
    private String msgId;
    private String hint;
    private long time;
    private int code;
    private int cancel;
    private String avatarUrl;
    private BodyEntity bodyEntity;


    @Override
    public EMessageType getMsgType() {
        return messageType;
    }

    public void setMessageType(EMessageType type) {
        messageType = type;
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public void setNick(String n) {
        nick = n;
    }

    @Override
    public ESendType getSendType() {
        return sendType;
    }

    @Override
    public void setSendType(ESendType type) {
        sendType = type;
    }


    @Override
    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }


    @Override
    public String getUploadUrl() {
        return uploadUrl;
    }

    @Override
    public void setUploadUrl(String url) {
        uploadUrl = url;
    }

    @Override
    public int getUploadProgress() {
        return uploadProgress;
    }

    @Override
    public void setUploadProgress(int progress) {
        uploadProgress = progress;
    }

    @Override
    public boolean isSecret() {
        if (getBodyEntity() != null) {
            return getBodyEntity().isSecret();
        } else {
            return false;
        }
    }

    @Override
    public void setSecret(boolean secret) {
        if (getBodyEntity() != null) {
            getBodyEntity().setSecret(secret);
        }
    }

    @Override
    public EPlayType getPlayStatus() {
        return playStatus;
    }

    @Override
    public void setPlayStatus(EPlayType status) {
        playStatus = status;
    }

    @Override
    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String name) {
        groupName = name;
    }

    @Override
    public EActionType getActionType() {
        return actionType;
    }

    @Override
    public void setActionType(EActionType type) {
        actionType = type;
    }

    @Override
    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int len) {
        timeLength = len;
    }

    @Override
    public String getTo() {
        return to;
    }

    public void setTo(String value) {
        to = value;
    }

    @Override
    public String getFrom() {
        return from;
    }

    public void setFrom(String value) {
        from = value;
    }

    @Override
    public String getContent() {
        if (messageType == EMessageType.CONTACT || messageType == EMessageType.MAP
            || messageType == EMessageType.VOTE) {
            return content;
        } else {
            if (getBodyEntity() != null) {
                return getBodyEntity().getBody();
            } else {
                return content;
            }
        }

    }

    public void setContent(String value) {
        content = value;
        BodyEntity bodyEntity = getBodyEntity();
        if (bodyEntity != null) {
            timeLength = bodyEntity.getTimeLength();
            isSecret = bodyEntity.isSecret();
        }
    }

    @Override
    public String getHint() {
        if (TextUtils.isEmpty(hint)) {
            String s = ChatHelper.getHint(getMsgType(), getBodyEntity().getBody(), isSecret);
            if (TextUtils.isEmpty(s)) {
                return hint;
            }
            return s;
        }
        return hint;
    }


    @Override
    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int c) {
        code = c;
    }

    @Override
    public int getCancel() {
        return cancel;
    }

    public void setCancel(int c) {
        cancel = c;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public void setAvatarUrl(String url) {
        avatarUrl = url;
    }

    @Override
    public BodyEntity getBodyEntity() {
        if (bodyEntity == null) {
            bodyEntity = new BodyEntity(getBody());
        }
        if (!TextUtils.isEmpty(bodyEntity.getBody())) {
            return bodyEntity;
        } else {
            if (bodyEntity.isSecret() || messageType == EMessageType.CONTACT
                || messageType == EMessageType.MAP || messageType == EMessageType.VOTE) {
                return bodyEntity;
            } else {
                return null;
            }
        }
    }

    @Override
    public String getBody() {
        return content;
    }

    @Override
    public void setBody(String text) {
        content = text;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String id) {
        msgId = id;
    }
}
