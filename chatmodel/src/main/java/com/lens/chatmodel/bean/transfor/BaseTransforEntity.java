package com.lens.chatmodel.bean.transfor;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/18.
 * 合并转发消息数据base
 */

public class BaseTransforEntity extends BaseJsonEntity implements Parcelable {

    String insertTime;
    int messageType;
    String senderUserid;
    String senderUserName;
    String friendHeader;
    String msgId; //"id_p"
    String body; //消息内容

    public BaseTransforEntity() {
    }


    protected BaseTransforEntity(Parcel in) {
        insertTime = in.readString();
        messageType = in.readInt();
        senderUserid = in.readString();
        senderUserName = in.readString();
        friendHeader = in.readString();
        msgId = in.readString();
        body = in.readString();
    }

    public static final Creator<BaseTransforEntity> CREATOR = new Creator<BaseTransforEntity>() {
        @Override
        public BaseTransforEntity createFromParcel(Parcel in) {
            return new BaseTransforEntity(in);
        }

        @Override
        public BaseTransforEntity[] newArray(int size) {
            return new BaseTransforEntity[size];
        }
    };

    public long getInsertTime() {
        return Long.valueOf(insertTime);
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
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

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFriendHeader() {
        return friendHeader;
    }

    public void setFriendHeader(String friendHeader) {
        this.friendHeader = friendHeader;
    }

    public JSONObject toJson(BaseTransforEntity bean) {
        if (bean != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("id_p", bean.getMsgId());
                object.put("body", bean.getBody());
                object.put("insertTime", bean.getInsertTime());
                object.put("senderUserid", bean.getSenderUserid());
                object.put("senderUserName", bean.getSenderUserName());
                object.put("messageType", bean.getMessageType());
                object.put("friendHeader", bean.getFriendHeader());
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public BaseTransforEntity fromObject(JSONObject o) {
        if (o == null) {
            return null;
        }
        VoiceEntity entity = new VoiceEntity();
        entity.setMsgId(optS("id_p", o));
        entity.setBody(optS("body", o));
        entity.setInsertTime(optS("insertTime", o));
        entity.setSenderUserid(optS("senderUserid", o));
        entity.setSenderUserName(optS("senderUserName", o));
        entity.setMessageType(optInt("messageType", o));
        entity.setFriendHeader(optS("friendHeader", o));
        return entity;
    }

    public static BaseTransforEntity createEntity(IChatRoomModel model) {
        if (model == null) {
            return null;
        }
        BaseTransforEntity entity = new BaseTransforEntity();
        entity.setMsgId(model.getMsgId());
        if (model.getMsgType() == EMessageType.VOICE) {
            if (!model.isIncoming()) {
                String uploadUrl = model.getUploadUrl();
                if (!TextUtils.isEmpty(uploadUrl)) {
                    VoiceUploadEntity voice = VoiceUploadEntity.fromJson(uploadUrl);
                    if (entity != null) {
                        VoiceBody body = new VoiceBody();
                        body.setBody(voice.getVoiceUrl());
                        body.setTimeLength(voice.getTimeLength());
                        entity.setBody(body.toJson());
                    }
                } else {
                    entity.setBody(model.getBody());
                }
            } else {
                entity.setBody(model.getBody());
            }
        } else {
            if (!model.isIncoming() && (model.getMsgType() == EMessageType.IMAGE
                || model.getMsgType() == EMessageType.FACE
                || model.getMsgType() == EMessageType.VIDEO)) {
                if (!TextUtils
                    .isEmpty(model.getUploadUrl())) {//经过一次转发的消息，虽然是incoming，但是uploadUrl并没有数据
                    entity.setBody(model.getUploadUrl());
                } else {
                    entity.setBody(model.getContent());
                }
            } else {
                entity.setBody(model.getContent());
            }
        }
        if (model.isGroupChat()) {
            entity.setSenderUserid(model.getFrom());
            entity.setSenderUserName(ChatHelper.getUserNick(model.getNick(), model.getFrom()));
            if (!TextUtils.isEmpty(model.getAvatarUrl())) {
                entity.setFriendHeader(model.getAvatarUrl());
            } else {
                if (model.getBodyEntity() != null) {
                    entity.setFriendHeader(model.getBodyEntity().getSenderAvatar());
                }
            }

        } else {
            if (model.isIncoming()) {
                entity.setSenderUserid(model.getTo());
                entity.setSenderUserName(ChatHelper.getUserNick(model.getNick(), model.getTo()));
            } else {
                entity.setSenderUserid(model.getFrom());
                entity.setSenderUserName(ChatHelper.getUserNick(model.getNick(), model.getFrom()));
            }
            entity.setFriendHeader(model.getAvatarUrl());
        }
        entity.setMessageType(model.getMsgType().value);
        entity.setInsertTime(model.getTime() + "");
        return entity;
    }


    public EMultiCellLayout getCellLayoutId() {
        EMultiCellLayout layout = null;
        EMessageType type = EMessageType.fromInt(getMessageType());
        switch (type) {
            case TEXT:
                layout = EMultiCellLayout.TEXT;
                break;
            case IMAGE:
                layout = EMultiCellLayout.IMAGE;
                break;
            case VOICE:
                layout = EMultiCellLayout.VOICE;
                break;
            case VIDEO:
                layout = EMultiCellLayout.VIDEO;
                break;
            case FACE:
                layout = EMultiCellLayout.EMOTICON;
                break;
            case VOTE:
                layout = EMultiCellLayout.VOTE;
                break;
            case MAP:
                layout = EMultiCellLayout.MAP;
                break;
            case CARD:
                layout = EMultiCellLayout.WORK_LOGIN;
                break;
            case MULTIPLE:
                layout = EMultiCellLayout.MULTI;
                break;
            case CONTACT:
                layout = EMultiCellLayout.BUSINESS_CARD;
                break;
            default:
                layout = EMultiCellLayout.TEXT;
        }
        return layout;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(insertTime);
        dest.writeInt(messageType);
        dest.writeString(senderUserid);
        dest.writeString(senderUserName);
        dest.writeString(friendHeader);
        dest.writeString(msgId);
        dest.writeString(body);
    }
}
