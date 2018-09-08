package com.lensim.fingerchat.db;

import android.text.TextUtils;

/**
 * Created by LL130386 on 2017/12/13.
 */

public class DaoManager {

    private static String userID = "";

    public static void initUserId(String user) {
        if (!TextUtils.isEmpty(user)) {
            userID = user;
        }
    }

    public static String getUserID() {
        return userID;
    }

    public static void clearUserId() {
        userID = "";
    }

}
