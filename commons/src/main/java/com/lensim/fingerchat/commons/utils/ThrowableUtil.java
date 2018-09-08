package com.lensim.fingerchat.commons.utils;

public class ThrowableUtil {
    private ThrowableUtil() {}

    public static String getMessage(Throwable t) {
        return t == null ? "" : t.getMessage();
    }

}
