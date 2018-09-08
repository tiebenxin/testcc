package com.lensim.fingerchat.commons.http;

import android.text.TextUtils;

/**
 * Created by zm on 2018/4/11.
 */

public class TokenManager {
    public static final String TOKEN_KEY = "Authorization";

    /**
     * 全局token
     */
    public static String sToken = "";

    public static String getToken() {
        if(TextUtils.isEmpty(sToken)) {
            initToken();
        }
        return sToken;
    }

    /**
     * 初始化token
     */
    public static String initToken() {
        // 从本地读取
//        sToken = SPManager.getInstance().getToken();

        if(TextUtils.isEmpty(sToken)) {
//            if(AccountManager.getInstance().getAccount() != null) {
//                sToken = AccountManager.getInstance().getAccount().getToken();
//                if(!TextUtils.isEmpty(sToken)) {
//                    updateToken(sToken);
//                }
//            }
        }
        return sToken;
    }

    /**
     * 更新token
     */
    public static void updateToken(String token) {

        // 写到本地
//        SPManager.getInstance().saveToken(token);

        // 写到内存
        sToken = token;
    }

    public static void clearToken() {
        sToken = null;
//        SPManager.getInstance().clearToken();
    }
}
