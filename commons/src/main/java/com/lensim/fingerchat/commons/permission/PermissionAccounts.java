package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionAccounts extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.ACCOUNTS;
    }

    @Override
    public String getKey() {
        return Manifest.permission.GET_ACCOUNTS;
    }
}
