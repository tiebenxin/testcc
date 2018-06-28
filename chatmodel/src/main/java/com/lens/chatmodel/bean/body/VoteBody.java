package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/4/24.
 * 投票消息body
 */

public class VoteBody extends BaseJsonEntity {

    String senderAvatar;//发送者头像
    String mucNickName;//群聊备注名，非群聊则无
    boolean secret;//是否密聊
    int bubbleWidth;
    int bubbleHeight;

    //投票消息
    int status;
    String voteId;
    String title;
    String option1;
    String option2;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
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

            object.put("status", status);

            if (!TextUtils.isEmpty(voteId)) {
                object.put("voteid", voteId);
            }

            if (!TextUtils.isEmpty(title)) {
                object.put("title", title);
            }
            if (!TextUtils.isEmpty(option1)) {
                object.put("option1", option1);
            }
            if (!TextUtils.isEmpty(option2)) {
                object.put("option2", option2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
