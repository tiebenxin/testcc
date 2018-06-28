package com.spf.api.filed.impl;

import com.spf.api.filed.BaseEditorField;
import com.spf.api.filed.EditorHelper;

public class LongEditorField<E extends EditorHelper> extends BaseEditorField<Long, E> {

    public LongEditorField(E editorHelper, String key) {
        super(editorHelper, key);
    }

    @Override
    public E put(Long value) {
        _editorHelper.getEditor().putLong(_key, value);
        return _editorHelper;
    }
}
