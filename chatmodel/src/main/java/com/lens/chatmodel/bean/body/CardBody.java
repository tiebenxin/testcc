package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/4/24.
 * 名片消息body
 */

public class CardBody extends BaseJsonEntity {

    String senderAvatar;//发送者头像
    String mucNickName;//群聊备注名，私聊为昵称
    int secret;//是否密聊
    int bubbleWidth;
    int bubbleHeight;
    String groupName;//群聊专有字段，群名


    //名片消息
    String friendName;
    String friendHeader;
    int isValid;
    int isEnable;
    String friendId;

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }


    public String getMucNickName() {
        return mucNickName;
    }

    public void setMucNickName(String mucNickName) {
        this.mucNickName = mucNickName;
    }

    public boolean isSecret() {
        return secret == 1;
    }

    public void setSecret(int secret) {
        this.secret = secret;
    }

    public int getBubbleWidth() {
        return bubbleWidth;
    }

    public void setBubbleWidth(int bubbleWidth) {
        this.bubbleWidth = bubbleWidth;
    }

    public int getBubbleHeight() {
        return bubbleHeight;
    }

    public void setBubbleHeight(int bubbleHeight) {
        this.bubbleHeight = bubbleHeight;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendHeader() {
        return friendHeader;
    }

    public void setFriendHeader(String friendHeader) {
        this.friendHeader = friendHeader;
    }

    public boolean isValid() {
        return isValid == 1;
    }

    public void setValid(int valid) {
        isValid = valid;
    }

    public boolean isEnable() {
        return isEnable == 1;
    }

    public void setEnable(int enable) {
        isEnable = enable;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        try {
            if (!TextUtils.isEmpty(senderAvatar)) {
                object.put("senderAvatar", senderAvatar);
            }

            if (!TextUtils.isEmpty(mucNickName)) {
                object.put("mucNickName", mucNickName);
            }
            if (!TextUtils.isEmpty(mucNickName)) {
                object.put("mucNickName", mucNickName);
            }

            object.put("bubbleWidth", bubbleWidth);
            object.put("bubbleHeight", bubbleHeight);
            object.put("secret", secret);

            if (!TextUtils.isEmpty(friendHeader)) {
                object.put("friendHeader", friendHeader);
            }
            if (!TextUtils.isEmpty(friendName)) {
                object.put("friendName", friendName);
            }
            if (!TextUtils.isEmpty(friendId)) {
                object.put("friendId", friendId);
            }
            object.put("isValid", isValid);
            object.put("isEnable", isEnable);
            if (!TextUtils.isEmpty(groupName)) {
                object.put("groupName", groupName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
