package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionGPS extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.ACCESS_GPS;
    }

    @Override
    public String getKey() {
        return Manifest.permission.ACCESS_FINE_LOCATION;
    }
}
