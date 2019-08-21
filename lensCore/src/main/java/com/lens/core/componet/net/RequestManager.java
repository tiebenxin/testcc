package com.lens.core.componet.net;

import java.util.HashMap;

public class RequestManager {

    protected static HashMap<Class, Object> sRequestManager = new HashMap<>();

    public static <T> T getRequest(Class<T> clazz, String baseUrl) {
        T t = (T) sRequestManager.get(clazz);
        if (t == null) {
            t = RetrofitClient.createApi(clazz, baseUrl);
            sRequestManager.put(clazz, t);
        }
        return t;
    }
}
