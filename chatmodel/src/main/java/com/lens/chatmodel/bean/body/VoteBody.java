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
    String mucNickName;//群聊备注名，私聊为昵称
    int secret;//是否密聊
    int bubbleWidth;
    int bubbleHeight;
    String groupName;//群聊专有字段，群名

    //投票消息
    int status;//发起投票，还是参与投票
    String voteid;
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
        return secret == 1;
    }


    public int getSecret() {
        return secret;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVoteId() {
        return voteid;
    }

    public void setVoteId(String voteId) {
        this.voteid = voteId;
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

            object.put("status", status);

            if (!TextUtils.isEmpty(voteid)) {
                object.put("voteid", voteid);
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

            if (!TextUtils.isEmpty(groupName)) {
                object.put("groupName", groupName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
