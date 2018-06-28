package com.lensim.fingerchat.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.lensim.fingerchat.commons.helper.ContextHelper;

/**
 * date on 2018/1/6
 * author ll147996
 * describe 存储基本类型 以及String
 */

public class SPSaveHelper {

    public final static String DEFAULT_NAME = "fingerchat.client";

    public static double getFloatValue(String fileName, String key, float defValue) {
        SharedPreferences sharedPreferences = ContextHelper.getContext().getSharedPreferences(
            fileName, Context.MODE_PRIVATE);
        float f = sharedPreferences.getFloat(key, defValue);
        return f;
    }

    public static int getFloatValue(String fileName, String key, int defValue) {
        SharedPreferences sharedPreferences = ContextHelper.getContext().getSharedPreferences(
            fileName, Context.MODE_PRIVATE);
        int i = sharedPreferences.getInt(key, defValue);
        return i;
    }

    public static String getStringValue(String key, String defValue) {
        return getStringValue(DEFAULT_NAME, key, defValue);
    }

    public static String getStringValue(String fileName, String key, String defValue) {
        SharedPreferences sharedPreferences = ContextHelper.getContext().getSharedPreferences(
            fileName, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(key, defValue);
        return s;
    }


    public static int getIntValue(String key, int defValue) {
        return getIntValue(DEFAULT_NAME, key, defValue);
    }

    public static int getIntValue(String fileName, String key, int defValue) {
        SharedPreferences sharedPreferences = ContextHelper.getContext().getSharedPreferences(
            fileName, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(key, defValue);
    }

    /**
     * 只限基本类型 以及String
     */
    public static void setValue(String key, Object value) {
        setValue(DEFAULT_NAME, key, value);
    }


    /**
     * 只限基本类型 以及String
     */
    public static void setValue(String fileName, String key, Object value) {
        SharedPreferences sharedPreferences = ContextHelper.getContext().getSharedPreferences(
            fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            throw new ClassCastException("value 只能是基本类型 以及String");
        }
        editor.commit();
    }

}
