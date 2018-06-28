package com.spf.api.filed.impl;

import android.content.SharedPreferences;

import com.spf.api.filed.BaseSpfField;

public class StringSpfField extends BaseSpfField<String> {

    public StringSpfField(SharedPreferences sharedPreferences, String key) {
        super(sharedPreferences, key);
    }

    @Override
    public String get(String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        return _sharedPreferences.getString(_key, defaultValue);
    }

    @Override
    public void put(String value) {
        apply(edit().putString(_key, value));
    }
}
