package com.spf.api.filed.impl;

import android.content.SharedPreferences;

import com.spf.api.filed.BaseSpfField;

public class LongSpfField extends BaseSpfField<Long> {

    public LongSpfField(SharedPreferences sharedPreferences, String key) {
        super(sharedPreferences, key);
    }

    @Override
    public Long get(Long defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0L;
        }
        return _sharedPreferences.getLong(_key, defaultValue);
    }

    @Override
    public void put(Long value) {
        apply(edit().putLong(_key, value));
    }
}
