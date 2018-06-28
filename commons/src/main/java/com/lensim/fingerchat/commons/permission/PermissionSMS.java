package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionSMS extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.SMS;
    }

    @Override
    public String getKey() {
        return Manifest.permission.RECEIVE_SMS;
    }
}
