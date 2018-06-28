package com.fingerchat.api;

/**
 * Created by LY309313 on 2017/9/23.
 */

public interface Logger {

    void enable(boolean enabled);

    void d(String s, Object... args);

    void i(String s, Object... args);

    void w(String s, Object... args);

    void e(Throwable e, String s, Object... args);

}
