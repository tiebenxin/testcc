package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionPhone extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.PHONE;
    }

    @Override
    public String getKey() {
        return Manifest.permission.CALL_PHONE;
    }
}
