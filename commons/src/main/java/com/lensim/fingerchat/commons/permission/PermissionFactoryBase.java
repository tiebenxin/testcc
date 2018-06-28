package com.lensim.fingerchat.commons.permission;

import java.util.HashMap;
import java.util.Map;

public abstract class PermissionFactoryBase {
    private final Map<EPermission, Permission> _permissionMap;

    public PermissionFactoryBase() {
        _permissionMap = new HashMap<>();
    }

    public Permission getPermission(EPermission permission) {
        if (!_permissionMap.containsKey(permission)) {
            Permission perm = createPermission(permission);
            if (perm == null) {
                throw new IllegalArgumentException(String.format("[%s] is not implemented", permission.toString()));
            }
            _permissionMap.put(permission, perm);
        }
        return _permissionMap.get(permission);
    }

    public Permission[] getPermissions(EPermission[] permissions) {
        Permission[] per = new Permission[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            EPermission permission = permissions[i];
            if (!_permissionMap.containsKey(permission)) {
                Permission perm = createPermission(permission);
                if (perm == null) {
                    throw new IllegalArgumentException(String.format("[%s] is not implemented", permission.toString()));
                }
                _permissionMap.put(permission, perm);
                per[i] = _permissionMap.get(permission);
            }
        }
        return per;
    }

    private Permission createPermission(EPermission permission) {
        switch (permission) {
            case STORAGE:
                return createPermissionStorage();
            case CAMERA:
                return createPermissionCamera();
            case RECORD_AUDIO:
                return createPermissionRecordAudio();
            case ACCOUNTS:
                return createPermissionAccounts();
            case READ_CONTACTS:
                return createPermissionReadContacts();
            case SMS:
                return createPermissionSMS();
            case PHONE:
                return createPermissionPhone();
            case ACCESS_GPS:
                return createPermissionGPS();
            case LOCATION:
                return createPermissionLocation();
        }
        return null;
    }

    protected abstract Permission createPermissionPhone();


    protected abstract PermissionStorage createPermissionStorage();

    protected abstract PermissionCamera createPermissionCamera();

    protected abstract PermissionRecordAudio createPermissionRecordAudio();

    protected abstract PermissionAccounts createPermissionAccounts();

    protected abstract PermissionReadContacts createPermissionReadContacts();

    protected abstract PermissionSMS createPermissionSMS();

    protected abstract Permission createPermissionGPS();

    protected abstract PermissionLocation createPermissionLocation();
}
