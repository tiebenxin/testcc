package com.lens.chatmodel.im_service;

import android.util.Log;

import com.fingerchat.api.Logger;


/**
 * Created by LY309313 on 2017/11/9.
 * Log类
 */
public final class IMLog implements Logger {
    private static final String sTag = "FINGER_CHAT";

    private boolean enable = false;

    @Override
    public void enable(boolean enabled) {
        this.enable = enabled;
    }

    @Override
    public void d(String s, Object... args) {
        if (enable) Log.d(sTag, String.format(s, args));
    }

    @Override
    public void i(String s, Object... args) {
        if (enable) Log.i(sTag, String.format(s, args));
    }

    @Override
    public void w(String s, Object... args) {
        if (enable) Log.w(sTag, String.format(s, args));
    }

    @Override
    public void e(Throwable e, String s, Object... args) {
        if (enable) Log.e(sTag, String.format(s, args), e);
    }
}