package com.lens.chatmodel.bean.transfor;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.helper.ChatHelper;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/18.
 * 合并转发消息解析类
 */

public class MultiMessageEntity extends BaseJsonEntity implements Parcelable {

    private String senderUserid;
    private String senderUserName;
    private String transitionTitle;
    private int type;//0 群聊groupchat 1.私聊chat
    private ArrayList<BaseTransforEntity> body;

    String senderAvatar;//发送者头像
    String mucNickName;//群聊备注名，私聊为昵称
    String groupName;//群聊专有字段，群名


    public MultiMessageEntity() {
    }

    public MultiMessageEntity(String json) {
        initJson(json);
    }

    protected MultiMessageEntity(Parcel in) {
        senderUserid = in.readString();
        senderUserName = in.readString();
        transitionTitle = in.readString();
        type = in.readInt();
        body = in.createTypedArrayList(BaseTransforEntity.CREATOR);
        senderAvatar = in.readString();
        mucNickName = in.readString();
        groupName = in.readString();
    }

    public static final Creator<MultiMessageEntity> CREATOR = new Creator<MultiMessageEntity>() {
        @Override
        public MultiMessageEntity createFromParcel(Parcel in) {
            return new MultiMessageEntity(in);
        }

        @Override
        public MultiMessageEntity[] newArray(int size) {
            return new MultiMessageEntity[size];
        }
    };

    private void initJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                setSenderUserid(optS("senderUserid", object));
                setSenderUserName(optS("senderUserName", object));
                setTransitionTitle(optS("transitionTitle", object));
                setType(optInt("type", object));

                ArrayList<BaseTransforEntity> list = new ArrayList<>();
                if (object.optJSONArray("body") != null) {
                    JSONArray array = object.optJSONArray("body");
                    int len = array.length();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            JSONObject o = array.getJSONObject(i);
                            int num = o.optInt("messageType");
                            MessageType msgType = MessageType.forNumber(num);
                            BaseTransforEntity baseEntity = createEntity(
                                ChatHelper.getMessageType(msgType), o);
                            if (baseEntity != null) {
                                list.add(baseEntity);
                            }
                        }
                    }
                } else {
                    if (object.has("body")) {
                        String body = object.optString("body");
                        if (body != null) {
                            JSONArray array = new JSONArray(body);
                            if (array != null) {
                                int len = array.length();
                                if (len > 0) {
                                    for (int i = 0; i < len; i++) {
                                        JSONObject o = array.getJSONObject(i);
                                        int num = o.optInt("messageType");
                                        MessageType msgType = MessageType.forNumber(num);
                                        BaseTransforEntity baseEntity = createEntity(
                                            ChatHelper.getMessageType(msgType), o);
                                        if (baseEntity != null) {
                                            list.add(baseEntity);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                setBody(list);
                setMucNickName(optS("mucNickName", object));
                setSenderAvatar(optS("senderAvatar", object));
                setGroupName(optS("groupName", object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MultiMessageEntity fromJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject object = new JSONObject(json);
                if (object != null) {
                    MultiMessageEntity entity = new MultiMessageEntity();
                    entity.setSenderUserid(optS("senderUserid", object));
                    entity.setSenderUserName(optS("senderUserName", object));
                    entity.setTransitionTitle(optS("transitionTitle", object));
                    entity.setType(optInt("type", object));

                    ArrayList<BaseTransforEntity> list = new ArrayList<>();
                    if (object.optJSONArray("body") != null) {
                        JSONArray array = object.optJSONArray("body");
                        int len = array.length();
                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                JSONObject o = array.getJSONObject(i);
                                int num = o.optInt("messageType");
                                MessageType msgType = MessageType.forNumber(num);
                                BaseTransforEntity baseEntity = createEntity(
                                    ChatHelper.getMessageType(msgType), o);
                                if (baseEntity != null) {
                                    list.add(baseEntity);
                                }
                            }
                        }
                    }
                    entity.setBody(list);
                    entity.setSenderAvatar(optS("senderAvatar", object));
                    entity.setMucNickName(optS("mucNickName", object));
                    entity.setGroupName(optS("groupName", object));
                    return entity;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String toJson(MultiMessageEntity entity) {
        if (entity == null) {
            return "";
        }
        try {
            JSONObject object = new JSONObject();
            object.put("senderUserid", entity.getSenderUserid());
            object.put("senderUserName", entity.getSenderUserName());
            object.put("transitionTitle", entity.getTransitionTitle());
            object.put("type", entity.getType());
            List<BaseTransforEntity> list = entity.getBody();
            if (list != null && !list.isEmpty()) {
                JSONArray array = new JSONArray();
                int len = list.size();
                for (int i = 0; i < len; i++) {
                    BaseTransforEntity en = list.get(i);
                    if (en != null) {
                        array.put(en.toJson(en));
                    }
                    object.put("body", array.toString());
                }
            } else {
                object.put("body", "");
            }
            object.put("senderAvatar", entity.getSenderAvatar());
            object.put("mucNickName", entity.getMucNickName());
            object.put("groupName", entity.getGroupName());
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getTransitionTitle() {
        return transitionTitle;
    }

    public void setTransitionTitle(String transitionTitle) {
        this.transitionTitle = transitionTitle;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getSenderUserid() {
        return senderUserid;
    }

    public void setSenderUserid(String senderUserid) {
        this.senderUserid = senderUserid;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public ArrayList<BaseTransforEntity> getBody() {
        return body;
    }

    public void setBody(ArrayList<BaseTransforEntity> body) {
        this.body = body;
    }

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private BaseTransforEntity createEntity(EMessageType type, JSONObject o) {
        BaseTransforEntity entity = null;
        if (o == null) {
            return entity;
        }

        BaseTransforEntity image = new BaseTransforEntity();
        entity = image.fromObject(o);
//        switch (type) {
//            case TEXT:
//                BaseTransforEntity text = new BaseTransforEntity();
//                entity = text.fromObject(o);
//                break;
//            case IMAGE:
//                BaseTransforEntity image = new BaseTransforEntity();
//                entity = image.fromObject(o);
//                break;
//            case VOICE:
//                BaseTransforEntity image = new BaseTransforEntity();
//                entity = image.fromObject(o);
//                break;
//            case VIDEO:
//                BaseTransforEntity image = new BaseTransforEntity();
//                entity = image.fromObject(o);
//                break;
//            case CARD:
//                BaseTransforEntity image = new BaseTransforEntity();
//                entity = image.fromObject(o);
//                break;
//            case MAP:
//                BaseTransforEntity image = new BaseTransforEntity();
//                entity = image.fromObject(o);
//                break;
//            case FACE:
//                BaseTransforEntity face = new BaseTransforEntity();
//                entity = face.fromObject(o);
//                break;
//        }

        return entity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderUserid);
        dest.writeString(senderUserName);
        dest.writeString(transitionTitle);
        dest.writeInt(type);
        dest.writeTypedList(body);
        dest.writeString(senderAvatar);
        dest.writeString(mucNickName);
        dest.writeString(groupName);
    }
}
