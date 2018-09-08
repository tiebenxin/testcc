package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/4/24.
 */

public class MapBody extends BaseJsonEntity {

    String senderAvatar;//发送者头像
    String mucNickName;//群聊备注名，非群聊则无
    int secret;//是否密聊
    int bubbleWidth;
    int bubbleHeight;

    //map消息字段
    String locationAddress;
    String locationName;
    double latitude;
    double longitude;

    String groupName;

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


    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

            if (!TextUtils.isEmpty(locationAddress)) {
                object.put("locationAddress", locationAddress);
            }
            if (!TextUtils.isEmpty(locationName)) {
                object.put("locationName", locationName);
            }

            object.put("latitude", latitude);
            object.put("longitude", longitude);

            if (!TextUtils.isEmpty(groupName)) {
                object.put("groupName", groupName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
