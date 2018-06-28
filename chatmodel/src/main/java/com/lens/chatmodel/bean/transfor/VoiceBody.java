package com.lens.chatmodel.bean.transfor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/6/14.
 */

public class VoiceBody {

    String body;
    int timeLength;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String toJson() {
        try {
            JSONObject object = new JSONObject();
            object.put("body", body);
            object.put("timeLength", timeLength);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
