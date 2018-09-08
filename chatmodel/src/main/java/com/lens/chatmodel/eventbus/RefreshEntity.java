package com.lens.chatmodel.eventbus;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/2.
 */

public class RefreshEntity {

    private int activity;
    private int fragment;
    private int type;//0为普通刷新，1为离线刷新


    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getFragment() {
        return fragment;
    }

    public void setFragment(int fragment) {
        this.fragment = fragment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static RefreshEntity fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                RefreshEntity entity = new RefreshEntity();
                entity.setActivity(object.optInt("activity"));
                entity.setFragment(object.optInt("fragment"));
                return entity;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toJson(RefreshEntity entity) {
        if (entity == null) {
            return null;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("activity", entity.getActivity());
            object.put("fragment", entity.getFragment());
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
