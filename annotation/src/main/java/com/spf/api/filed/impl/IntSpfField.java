package com.spf.api.filed.impl;

import android.content.SharedPreferences;

import com.spf.api.filed.BaseSpfField;

public class IntSpfField extends BaseSpfField<Integer> {

    public IntSpfField(SharedPreferences sharedPreferences, String key) {
        super(sharedPreferences, key);
    }

    @Override
    public Integer get(Integer defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0;
        }
        return _sharedPreferences.getInt(_key, defaultValue);
    }

    @Override
    public void put(Integer value) {
        apply(edit().putInt(_key, value));
    }
}
