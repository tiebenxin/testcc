package com.lens.chatmodel.interf;

import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.bean.body.BodyEntity;

/**
 * Created by LL130386 on 2017/12/6.
 */

public interface IChatRoomModel {

    EMessageType getMsgType();


    String getNick();


    void setNick(String n);

    ESendType getSendType();

    boolean isIncoming();

    EChatCellLayout getChatCellLayoutId();


    String getUploadUrl();

    void setUploadUrl(String url);


    int getUploadProgress();

    void setUploadProgress(int progress);

    boolean isSecret();

    EPlayType getPlayStatus();

    void setPlayStatus(EPlayType status);

    void setSendType(ESendType type);

    boolean isGroupChat();

    String getGroupName();

    EActionType getActionType();

    void setActionType(EActionType type);

    int getTimeLength();


    String getTo();

    String getFrom();

    String getContent();

    String getHint();


    long getTime();

    int getCode();

    int getCancel();

    String getAvatarUrl();

    void setAvatarUrl(String avatarUrl);

    BodyEntity getBodyEntity();

    String getBody();

    void setBody(String content);

    String getMsgId();

}
