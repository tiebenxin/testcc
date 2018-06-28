package com.lensim.fingerchat.commons.helper;

import com.google.gson.Gson;

/**
 * Created by LL130386 on 2018/1/18.
 */

public class GsonHelper {
    public static <T extends Object> T getObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }


    }


    public static <T extends Object> String optObject(T t) {
        if (t == null) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.toJson(t);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
