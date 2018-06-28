package com.spf.api.filed;

import android.content.SharedPreferences;

public class EditorHelper {
    protected final SharedPreferences.Editor editor;

    public EditorHelper(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public final void clear() {
        editor.clear().apply();
    }

    public final void apply() {
        editor.apply();
    }

    public final boolean commit() {
        return editor.commit();
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }
}
