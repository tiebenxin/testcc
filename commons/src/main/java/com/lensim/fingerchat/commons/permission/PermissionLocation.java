package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionLocation extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.LOCATION;
    }

    @Override
    public String getKey() {
        return Manifest.permission.ACCESS_FINE_LOCATION;
    }
}
