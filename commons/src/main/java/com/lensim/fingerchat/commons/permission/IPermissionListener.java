package com.lensim.fingerchat.commons.permission;

public interface IPermissionListener {
    void onPermissionChecked(EPermission permission, boolean isGranted, boolean withAsk);
}
