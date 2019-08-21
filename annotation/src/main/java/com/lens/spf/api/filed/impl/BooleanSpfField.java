package com.lens.spf.api.filed.impl;

import android.content.SharedPreferences;

import com.lens.spf.api.filed.BaseSpfField;

public class BooleanSpfField extends BaseSpfField<Boolean> {

    public BooleanSpfField(SharedPreferences sharedPreferences, String key) {
        super(sharedPreferences, key);
    }

    @Override
    public Boolean get(Boolean defaultValue) {
        if (defaultValue == null) {
            defaultValue = false;
        }
        return _sharedPreferences.getBoolean(_key, defaultValue);
    }

    @Override
    public void put(Boolean value) {
        apply(edit().putBoolean(_key, value));
    }
}
