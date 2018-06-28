package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionReadContacts extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.READ_CONTACTS;
    }

    @Override
    public String getKey() {
        return Manifest.permission.READ_CONTACTS;
    }
}
