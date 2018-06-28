package com.fingerchat.api.util;


import com.fingerchat.api.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LY309313 on 2017/9/23.
 */

public class DefaultLogger implements Logger {

    private static final String TAG = "[fingerchat] ";
    private final DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

    private boolean enable = false;

    @Override
    public void enable(boolean enabled) {
        this.enable = enabled;
    }

    @Override
    public void d(String s, Object... args) {
        if (enable) {
            System.out.printf(format.format(new Date()) + " [D] " + TAG + s + '\n', args);
        }
    }

    @Override
    public void i(String s, Object... args) {
        if (enable) {
            System.out.printf(format.format(new Date()) + " [I] " + TAG + s + '\n', args);
        }
    }

    @Override
    public void w(String s, Object... args) {
        if (enable) {
            System.err.printf(format.format(new Date()) + " [W] " + TAG + s + '\n', args);
        }
    }

    @Override
    public void e(Throwable e, String s, Object... args) {
        if (enable) {
            System.err.printf(format.format(new Date()) + " [E] " + TAG + s + '\n', args);
            e.printStackTrace();
        }
    }


}
