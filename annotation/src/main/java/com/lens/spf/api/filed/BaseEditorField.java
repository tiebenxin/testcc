package com.lens.spf.api.filed;

public abstract class BaseEditorField<T, E extends EditorHelper> {
    protected final E _editorHelper;
    protected final String _key;


    public BaseEditorField(E editorHelper, String key) {
        this._editorHelper = editorHelper;
        this._key = key;
    }


    public final E remove() {
        _editorHelper.getEditor().remove(_key);
        return _editorHelper;
    }

    public abstract E put(T value);
}
