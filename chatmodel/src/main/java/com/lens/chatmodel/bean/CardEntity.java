package com.lens.chatmodel.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/28.
 * 个人名片数据
 */

public class CardEntity {

  String friendName;
  String friendHeader;
  boolean isValid;
  boolean isEnable;
  String friendId;

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

  public static String toJson(CardEntity entity) {
    if (entity != null) {
      JSONObject object = new JSONObject();
      try {
        object.put("friendName", entity.getFriendName());
        object.put("friendHeader", entity.getFriendHeader());
        object.put("isValid", entity.isValid());
        object.put("isEnable", entity.isEnable());
        object.put("friendId", entity.getFriendId());
        return object.toString();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public static CardEntity fromJson(String json) {
    try {
      JSONObject object = new JSONObject(json);
      if (object != null) {
        CardEntity entity = new CardEntity();
        if (object.has("friendName")) {
          entity.setFriendName(object.optString("friendName"));
        }
        if (object.has("friendHeader")) {
          entity.setFriendHeader(object.optString("friendHeader"));
        }
        if (object.has("friendId")) {
          entity.setFriendId(object.optString("friendId"));
        }
        if (object.has("isValid")) {
          entity.setValid(object.optBoolean("isValid"));
        }
        if (object.has("isEnable")) {
          entity.setEnable(object.optBoolean("isEnable"));
        }
        return entity;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
