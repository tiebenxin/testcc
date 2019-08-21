package com.lens.spf.api.filed;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class SpfHelper {

    protected final SharedPreferences sharedPreferences;

    public SpfHelper(Context context, String suffixName) {
        this.sharedPreferences = context.getSharedPreferences(getSpfName() + suffixName, 0);
    }

    private String getSpfName() {
        return "FGSpf";
    }

    public final SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public final void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
