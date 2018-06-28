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
    String mucNickName;//群聊备注名，非群聊则无
    boolean secret;//是否密聊
    int bubbleWidth;
    int bubbleHeight;

    //名片消息
    String friendName;
    String friendHeader;
    boolean isValid;
    boolean isEnable;
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
        return secret;
    }

    public void setSecret(boolean secret) {
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
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
