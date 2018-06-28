package com.lensim.fingerchat.commons.base;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/19.
 * 解析base
 */

public class BaseJsonEntity {

    public String optS(String value, JSONObject object) {
        if (!TextUtils.isEmpty(object.optString(value))) {
            return object.optString(value);
        } else {
            return "";
        }
    }

    public int optInt(String value, JSONObject object) {
        return object.optInt(value);
    }

    public boolean optBoolean(String value, JSONObject object) {
        return object.optBoolean(value);
    }

    public double optDouble(String value, JSONObject object) {
        return object.optDouble(value);
    }

    public static int checkInt(int i) {
        if (i <= 0) {
            return 0;
        } else {
            return i;
        }
    }

    public static String checkString(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public String remove(String key, String json) {
        if (!TextUtils.isEmpty(json) && !TextUtils.isEmpty(key)) {
            try {
                JSONObject object = new JSONObject(json);
                if (object != null) {
                    if (object.has(key)) {
                        object.remove(key);
                        return object.toString();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }


}
