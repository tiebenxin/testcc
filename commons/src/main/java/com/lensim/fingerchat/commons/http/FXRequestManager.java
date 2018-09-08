package com.lensim.fingerchat.commons.http;

import com.lens.core.componet.net.RequestManager;
import com.lensim.fingerchat.commons.utils.AppHostUtil;

/**
 * Created by zm on 2018/4/11
 */
public class FXRequestManager extends RequestManager {

    public static <T> T getRequest(Class<T> clazz) {
        T t = (T) sRequestManager.get(clazz);
        if (t == null) {
            t = FXRetrofitClient.createApi(clazz, AppHostUtil.getHttpConnectHostApi());
            sRequestManager.put(clazz, t);
        }
        return t;
    }
}
