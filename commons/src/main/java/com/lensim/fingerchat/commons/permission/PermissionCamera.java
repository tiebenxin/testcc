package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionCamera extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.CAMERA;
    }

    @Override
    public String getKey() {
        return Manifest.permission.CAMERA;
    }
}
