package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionStorage extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.STORAGE;
    }

    @Override
    public String getKey() {
        return Manifest.permission.READ_EXTERNAL_STORAGE;
    }
}
