package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/26.
 */

public class VoiceUploadEntity extends BaseJsonEntity {

    String VoiceUrl;
    int timeLength;


    public static VoiceUploadEntity fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                VoiceUploadEntity entry = new VoiceUploadEntity();
                if (object.has("VoiceUrl") && !TextUtils.isEmpty(object.optString("VoiceUrl"))) {
                    entry.setVoiceUrl(object.optString("VoiceUrl"));
                } else {
                    entry.setVoiceUrl("");
                }
                if (object.has("timeLength") && object.optInt("timeLength") >= 0) {
                    entry.setTimeLength(object.optInt("timeLength"));
                } else {
                    entry.setTimeLength(0);
                }

                return entry;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(VoiceUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("VoiceUrl", entry.getVoiceUrl());
                object.put("timeLength", entry.getTimeLength());
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static JSONObject toObject(VoiceUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("VoiceUrl", entry.getVoiceUrl());
                object.put("timeLength", entry.getTimeLength());
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getVoiceUrl() {
        return VoiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        VoiceUrl = voiceUrl;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }
}
