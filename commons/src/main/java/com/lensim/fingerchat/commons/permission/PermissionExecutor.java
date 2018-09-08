package com.lensim.fingerchat.commons.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import com.lensim.fingerchat.commons.utils.L;

public class PermissionExecutor {

    private final Context _context;

    private final PermissionFactoryBase _permissionFactory;
    private final boolean _isSupportedOnFlyPermissions;

    public PermissionExecutor(Context context, PermissionFactoryBase factory) {
        _context = context;
        _isSupportedOnFlyPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        _permissionFactory = factory;
    }

    private Permission getPermission(EPermission permission) {
        return _permissionFactory.getPermission(permission);
    }

    private Permission[] getPermissions(EPermission[] permissions) {
        return _permissionFactory.getPermissions(permissions);
    }


    public boolean isPermissionGranted(EPermission permission) {
        return getPermission(permission).isPermissionGranted(_context);
    }

    public void checkPermission(Activity activity, EPermission permission,
        IPermissionListener listener) {
        if (_isSupportedOnFlyPermissions) {
            getPermission(permission).checkPermission(activity, listener);
        } else {
            if (listener != null) {
                listener.onPermissionChecked(permission, true, false);
            }
        }
    }

    public void checkMultiplyPermission(Activity activity, EPermission[] permission,
        IPermissionListener listener) {
        if (_isSupportedOnFlyPermissions) {
            Permission[] per = getPermissions(permission);
            if (per != null && per.length > 0) {
                per[0].checkMPermission(activity, per, listener);
            }
        } else {
            if (listener != null) {
                listener.onPermissionChecked(null, true, false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
        int[] grantResults) {
        try {
            EPermission permission = EPermission.fromInt(requestCode);
            getPermission(permission)
                .onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
        } catch (Exception exc) {
            L.e(exc);
            activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
