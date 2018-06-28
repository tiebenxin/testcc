package com.spf.api.filed.impl;

import com.spf.api.filed.BaseEditorField;
import com.spf.api.filed.EditorHelper;

public class StringEditorField<E extends EditorHelper> extends BaseEditorField<String, E> {

    public StringEditorField(E editorHelper, String key) {
        super(editorHelper, key);
    }

    @Override
    public E put(String value) {
        _editorHelper.getEditor().putString(_key, value);
        return _editorHelper;
    }
}
