package com.lens.chatmodel.bean.message;

import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.interf.IChatRoomModel;

/**
 * Created by LL130386 on 2018/2/2.
 * 消息数据base类
 */

public abstract class BaseFGMessage implements IChatRoomModel {


    public abstract EMessageType getMsgType();

    public abstract BodyEntity getBodyEntity();

    public abstract String getBody();

    public abstract void setBody(String content);


    public abstract String getMsgId();


    @Override
    public EChatCellLayout getChatCellLayoutId() {
        EChatCellLayout layout = null;
        if (isSecret() && isIncoming()) {//接受到密聊消息
            layout = EChatCellLayout.SECRET;
        } else {
            if (getMsgType() == EMessageType.TEXT) {
                if (isIncoming()) {//接受者是自己，即为对方发送
                    layout = EChatCellLayout.TEXT_RECEIVED;
                } else {
                    layout = EChatCellLayout.TEXT_SEND;
                }
            } else if (getMsgType() == EMessageType.IMAGE) {
                if (isIncoming()) {
                    layout = EChatCellLayout.IMAGE_RECEIVED;
                } else {
                    layout = EChatCellLayout.IMAGE_SEND;
                }
            } else if (getMsgType() == EMessageType.FACE) {
                if (isIncoming()) {
                    layout = EChatCellLayout.EMOTICON_RECEIVED;
                } else {
                    layout = EChatCellLayout.EMOTICON_SEND;
                }
            } else if (getMsgType() == EMessageType.VOICE) {
                if (isIncoming()) {
                    layout = EChatCellLayout.VOICE_RECEIVED;
                } else {
                    layout = EChatCellLayout.VOICE_SEND;
                }
            } else if (getMsgType() == EMessageType.VIDEO) {
                if (isIncoming()) {
                    layout = EChatCellLayout.VIDEO_RECEIVED;
                } else {
                    layout = EChatCellLayout.VIDEO_SEND;
                }
            } else if (getMsgType() == EMessageType.CONTACT) {
                if (isIncoming()) {
                    layout = EChatCellLayout.BUSINESS_CARD_RECEIVED;
                } else {
                    layout = EChatCellLayout.BUSINESS_CARD_SEND;
                }
            } else if (getMsgType() == EMessageType.MAP) {
                if (isIncoming()) {
                    layout = EChatCellLayout.MAP_RECEIVED;
                } else {
                    layout = EChatCellLayout.MAP_SEND;
                }
            } else if (getMsgType() == EMessageType.VOTE) {
                if (isIncoming()) {
                    layout = EChatCellLayout.VOTE_RECEIVED;
                } else {
                    layout = EChatCellLayout.VOTE_SEND;
                }
            } else if (getMsgType() == EMessageType.CARD) {
                if (isIncoming()) {
                    layout = EChatCellLayout.WORK_LOGIN_RECEIVED;
                } else {
                    layout = EChatCellLayout.WORK_LOGIN_SEND;
                }
            } else if (getMsgType() == EMessageType.MULTIPLE) {
                if (isIncoming()) {
                    layout = EChatCellLayout.MULTI_RECEIVED;
                } else {
                    layout = EChatCellLayout.MULTI_SEND;
                }
            } else if (getMsgType() == EMessageType.ACTION) {
                layout = EChatCellLayout.CHAT_ACTION;
            } else if (getMsgType() == EMessageType.NOTICE) {
                layout = EChatCellLayout.NOTICE;
            } else if (getMsgType() == EMessageType.OA) {
                layout = EChatCellLayout.OA;
            } else if (getMsgType() == EMessageType.SYSTEM) {
                layout = EChatCellLayout.SYSTEM;
            } else {
                if (isIncoming()) {
                    layout = EChatCellLayout.TEXT_RECEIVED;
                } else {
                    layout = EChatCellLayout.TEXT_SEND;
                }
            }
        }
        return layout;
    }


    public abstract String getTo();

    public abstract String getFrom();

    public abstract String getContent();

    public abstract long getTime();

    public abstract int getCode();

    public abstract int getCancel();

    public abstract boolean isGroupChat();

    public abstract String getGroupName();

    public abstract EActionType getActionType();

    public abstract void setActionType(EActionType type);

    public abstract int getTimeLength();

    public abstract boolean isSecret();

    public abstract void setSecret(boolean secret);

}
