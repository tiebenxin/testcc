package com.lens.chatmodel.bean.transfor;

import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/19.
 * 视频，语音，转发消息解析类
 */

public class VoiceEntity extends BaseTransforEntity {

    String voiceUrl;
    int voiceSize;

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public int getVoiceSize() {
        return voiceSize;
    }

    public void setVoiceSize(int voiceSize) {
        this.voiceSize = voiceSize;
    }

    public VoiceEntity fromObject(JSONObject o) {
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

        VoiceUploadEntity en = VoiceUploadEntity.fromJson(entity.getBody());
        if (en != null) {
            entity.setVoiceUrl(en.getVoiceUrl());
            entity.setVoiceSize(en.getTimeLength());
        }

        return entity;
    }
}
