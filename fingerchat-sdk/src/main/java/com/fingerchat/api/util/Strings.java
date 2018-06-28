package com.fingerchat.api.util;

/**
 * Created by LY309313 on 2017/9/26.
 */

public class Strings {

    public static final String EMPTY = "";

    public static boolean isBlank(CharSequence text) {
        if (text == null || text.length() == 0) return true;
        for (int i = 0, L = text.length(); i < L; i++) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }

    public static long toLong(String text, long defaultVal) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
        }
        return defaultVal;
    }

    public static int toInt(String text, int defaultVal) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
        }
        return defaultVal;
    }

}
