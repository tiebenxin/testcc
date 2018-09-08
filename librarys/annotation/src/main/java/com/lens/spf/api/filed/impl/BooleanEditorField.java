package com.lens.spf.api.filed.impl;

import com.lens.spf.api.filed.BaseEditorField;
import com.lens.spf.api.filed.EditorHelper;

public class BooleanEditorField<E extends EditorHelper> extends BaseEditorField<Boolean, E> {

    public BooleanEditorField(E editorHelper, String key) {
        super(editorHelper, key);
    }

    @Override
    public E put(Boolean value) {
        _editorHelper.getEditor().putBoolean(_key, value);
        return _editorHelper;
    }
}
