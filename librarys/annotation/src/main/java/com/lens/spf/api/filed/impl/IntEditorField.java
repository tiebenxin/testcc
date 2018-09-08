package com.lens.spf.api.filed.impl;

import com.lens.spf.api.filed.BaseEditorField;
import com.lens.spf.api.filed.EditorHelper;

public class IntEditorField<E extends EditorHelper> extends BaseEditorField<Integer, E> {

    public IntEditorField(E editorHelper, String key) {
        super(editorHelper, key);
    }

    @Override
    public E put(Integer value) {
        _editorHelper.getEditor().putInt(_key, value);
        return _editorHelper;
    }
}
