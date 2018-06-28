package com.spf.api.filed.impl;

import com.spf.api.filed.BaseEditorField;
import com.spf.api.filed.EditorHelper;

public class FloatEditorField<E extends EditorHelper> extends BaseEditorField<Float, E> {

    public FloatEditorField(E editorHelper, String key) {
        super(editorHelper, key);
    }

    @Override
    public E put(Float value) {
        _editorHelper.getEditor().putFloat(_key, value);
        return _editorHelper;
    }
}
