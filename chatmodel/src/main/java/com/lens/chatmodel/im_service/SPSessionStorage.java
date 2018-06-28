package com.lens.chatmodel.im_service;

import android.content.SharedPreferences;

import com.fingerchat.api.connection.SessionStorage;


/**
 * Created by LY309313 on 2017/11/9.
 */
public final class SPSessionStorage implements SessionStorage {
    private final SharedPreferences sp;

    public SPSessionStorage(SharedPreferences sp) {
        this.sp = sp;
    }

    @Override
    public void saveSession(String sessionContext) {
        sp.edit().putString("session", sessionContext).apply();
    }

    @Override
    public String getSession() {
        return sp.getString("session", null);
    }

    @Override
    public void clearSession() {
        sp.edit().remove("session").apply();
    }
}
