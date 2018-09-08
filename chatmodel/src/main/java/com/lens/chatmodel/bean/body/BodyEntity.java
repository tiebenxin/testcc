package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/2/2.
 * 聊天内容body二次封装
 */

public class BodyEntity extends BaseJsonEntity {

    String senderAvatar;//发送者头像
    String body;//发送内容
    String mucNickName;//群聊备注名，私聊为昵称
    int secret;//是否密聊,1为密聊，0为非密聊
    int bubbleWidth;
    int bubbleHeight;
    int timeLength;
    String groupName;//群聊专有字段，群名


    public BodyEntity() {
    }

    public BodyEntity(String json) {
        initJson(json);
    }

    private void initJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                setBody(optS("body", object));
                setMucNickName(optS("mucNickName", object));
                setSenderAvatar(optS("senderAvatar", object));
                setSecret(optInt("secret", object));
                setBubbleWidth(optInt("bubbleWidth", object));
                setBubbleHeight(optInt("bubbleHeight", object));
                setTimeLength(optInt("timeLength", object));
                setGroupName(optS("groupName", object));
            }
        } catch (JSONException e) {
            setBody(json);
            setSecret(0);
            setBubbleWidth(0);
            setBubbleHeight(0);
            setTimeLength(0);
            setMucNickName("");
            setSenderAvatar("");
            setGroupName("");
        }
    }

    public BodyEntity fromJson(String json) {
        BodyEntity bodyEntity = new BodyEntity();
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                bodyEntity.setBody(optS("body", object));
                bodyEntity.setSecret(optInt("secret", object));
                bodyEntity.setBubbleWidth(optInt("bubbleWidth", object));
                bodyEntity.setBubbleHeight(optInt("bubbleHeight", object));
                bodyEntity.setTimeLength(optInt("timeLength", object));
                bodyEntity.setMucNickName(optS("mucNickName", object));
                bodyEntity.setSenderAvatar(optS("senderAvatar", object));
                bodyEntity.setGroupName(optS("groupName", object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bodyEntity;
    }

    public static String toJson(BodyEntity entity) {
        if (entity != null) {
            JSONObject object = new JSONObject();
            String text = checkString(entity.getBody());
            try {
                if (!TextUtils.isEmpty(text)) {
                    JSONObject ob = new JSONObject(text);
                    if (ob != null) {
                        object.put("body", ob.toString());
                    }
                } else {
                    object.put("body", "");

                }
                object.put("secret", entity.getSecret());
                object.put("bubbleWidth", checkInt(entity.getBubbleWidth()));
                object.put("bubbleHeight", checkInt(entity.getBubbleHeight()));
                if (entity.getTimeLength() > 0) {
                    object.put("timeLength", checkInt(entity.getTimeLength()));
                }
                if (!TextUtils.isEmpty(entity.getMucNickName())) {
                    object.put("mucNickName", entity.getMucNickName());
                }
                if (!TextUtils.isEmpty(entity.getSenderAvatar())) {
                    object.put("senderAvatar", entity.getSenderAvatar());
                }

                if (!TextUtils.isEmpty(entity.getGroupName())) {
                    object.put("groupName", entity.getGroupName());
                }

                return object.toString();
            } catch (JSONException e) {
                try {
                    object.put("body", text);
                    object.put("secret", entity.getSecret());
                    object.put("bubbleWidth", checkInt(entity.getBubbleWidth()));
                    object.put("bubbleHeight", checkInt(entity.getBubbleHeight()));
                    if (entity.getTimeLength() > 0) {
                        object.put("timeLength", checkInt(entity.getTimeLength()));
                    }
                    if (!TextUtils.isEmpty(entity.getMucNickName())) {
                        object.put("mucNickName", entity.getMucNickName());
                    }
                    if (!TextUtils.isEmpty(entity.getSenderAvatar())) {
                        object.put("senderAvatar", entity.getSenderAvatar());
                    }
                    if (!TextUtils.isEmpty(entity.getGroupName())) {
                        object.put("groupName", entity.getGroupName());
                    }
                    return object.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        }
        return "";
    }


    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
