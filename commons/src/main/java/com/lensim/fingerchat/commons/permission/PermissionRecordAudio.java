package com.lensim.fingerchat.commons.permission;

import android.Manifest;

public abstract class PermissionRecordAudio extends Permission {

    @Override
    public EPermission getType() {
        return EPermission.RECORD_AUDIO;
    }

    @Override
    public String getKey() {
        return Manifest.permission.RECORD_AUDIO;
    }
}
