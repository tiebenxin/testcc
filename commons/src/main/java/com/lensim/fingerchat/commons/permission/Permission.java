package com.lensim.fingerchat.commons.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.lensim.fingerchat.commons.dialog.permission.DialogContainer;
import com.lensim.fingerchat.commons.dialog.permission.Dialogs;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/17.
 */

public abstract class Permission {

    private final List<IPermissionListener> _listeners;
    private DialogContainer _dialog;


    public Permission() {
        _listeners = new ArrayList<>();
    }

    protected abstract EPermission getType();

    protected abstract String getKey();

    protected abstract boolean isPermissionDoNotShowAgain();

    protected abstract void setPermissionDoNotShowAgain(boolean nsPermission);

    protected abstract String getText(EPermissionTexts text);


    boolean isPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, getKey())
            == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(Activity activity, IPermissionListener listener) {
        addListener(listener);
        activity.requestPermissions(new String[]{getKey()}, getType().value);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
        int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            boolean result = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            setPermissionDoNotShowAgain(
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, getKey()));
            if (!result) {
                showToast(getText(EPermissionTexts.TOAST_DENIED));
            }
            notifyListeners(result, true);
        } else {
            notifyListeners(false, true);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkMPermission(Activity activity, Permission[] permissions,
        IPermissionListener listener) {
        String[] grandPermission = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            if (activity != null) {
                if (permissions[i].isPermissionGranted(activity)) {
                    if (permissions[i].isPermissionDoNotShowAgain()) {
                        permissions[i].setPermissionDoNotShowAgain(false);
                    }
                } else {
                    grandPermission[i] = permissions[i].getKey();
                    if (isPermissionDoNotShowAgain()) {
                        showDialogNotShowAgain(activity, listener);
                    } else {
//                        showDialogRationale(activity, listener);
                        requestPermissions(activity, listener);
                    }
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(Activity activity, IPermissionListener listener) {
        if (activity != null) {
            if (isPermissionGranted(activity)) {
                if (isPermissionDoNotShowAgain()) {
                    setPermissionDoNotShowAgain(false);
                }
                if (listener != null) {
                    listener.onPermissionChecked(getType(), true, false);
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, getKey())) {
//                    showDialogRationale(activity, listener);
                    requestPermissions(activity, listener);

                } else {
                    if (isPermissionDoNotShowAgain()) {
                        showDialogNotShowAgain(activity, listener);
                    } else {
//                        showDialogRationale(activity, listener);
                        requestPermissions(activity, listener);

                    }
                }
            }
        } else {
            if (listener != null) {
                listener.onPermissionChecked(getType(), false, false);
            }
        }
    }


    private void addListener(IPermissionListener listener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    private void notifyListeners(boolean isGranted, boolean withAsk) {
        List<IPermissionListener> listeners = new ArrayList<>(_listeners);
        _listeners.clear();
        for (IPermissionListener listener : listeners) {
            if (listener != null) {
                listener.onPermissionChecked(getType(), isGranted, withAsk);
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(ContextHelper.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void showDialogRationale(final Activity activity, final IPermissionListener listener) {
        if (_dialog == null) {
            _dialog = Dialogs
                .showAlertDialog(activity, getText(EPermissionTexts.HEADER_RATIONALE),
                    getText(EPermissionTexts.MESSAGE_RATIONALE), getText(EPermissionTexts.ALLOW),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            _dialog.setTag(true);
                            requestPermissions(activity, listener);
                        }
                    }, null, null, getText(EPermissionTexts.DENY), null);
            _dialog.setCanceledOnTouchOutside(true);
            _dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (_dialog.getTag() == null) {
                        showToast(getText(EPermissionTexts.TOAST_DENIED));
                        addListener(listener);
                        notifyListeners(false, true);
                    }
                    _dialog = null;
                }
            });
        } else {
            addListener(listener);
        }
    }

    private void showDialogNotShowAgain(final Activity activity,
        final IPermissionListener listener) {
        if (_dialog == null) {
            _dialog = Dialogs.showAlertDialog(activity, getText(EPermissionTexts.HEADER_BLOCKED),
                getText(EPermissionTexts.MESSAGE_BLOCKED), getText(EPermissionTexts.APP_SETTINGS),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppPermissionsSettings(activity);
                    }
                }, null, null, getText(EPermissionTexts.NOT_NOW), null);
            _dialog.setCanceledOnTouchOutside(false);
            _dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showToast(getText(EPermissionTexts.TOAST_DENIED));
                    addListener(listener);
                    notifyListeners(false, true);
                    _dialog = null;
                }
            });
        } else {
            addListener(listener);
        }
    }

    private boolean openAppPermissionsSettings(Activity activity) {
        boolean result = false;

        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + ContextHelper.getApplication().getPackageName()));
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        if (isIntentAvailable(activity, i)) {
            activity.startActivity(i);
            result = true;
        }

        return result;
    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        return
            context.getPackageManager().resolveActivity(intent, PackageManager.PERMISSION_GRANTED)
                != null;
    }

}
