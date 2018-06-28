package com.lensim.fingerchat.commons.dialog.permission;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import com.lensim.fingerchat.commons.utils.L;

public class DialogContainer {

    private final Dialog _dialog;
    private Object _tag;

    private DialogInterface.OnDismissListener _ownDismissListener;

    public DialogContainer(Dialog dialog) {
        _dialog = dialog;
        _dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DialogsStatistic.INSTANCE.stopShowDialog(_dialog);
                if (_ownDismissListener != null) {
                    _ownDismissListener.onDismiss(dialog);
                }
            }
        });
    }

    public Object getTag() {
        return _tag;
    }

    public void setTag(Object tag) {
        _tag = tag;
    }

    public Window getWindow() {
        return _dialog.getWindow();
    }

    public void dismiss() {
        try {
            if (_dialog != null && _dialog.isShowing()) {
                _dialog.dismiss();
            }
        } catch (Exception exc) {
            L.e(exc);
        }
    }

    public DialogContainer show() {
        try {
            _dialog.show();
            DialogsStatistic.INSTANCE.startShowDialog(_dialog);
        } catch (Exception exc) {
            L.e(exc);
        }
        return this;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        _ownDismissListener = listener;
    }

    public void setCanceledOnTouchOutside(boolean isCanceled) {
        _dialog.setCanceledOnTouchOutside(isCanceled);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        _dialog.setOnCancelListener(listener);
    }

    public boolean isShowing() {
        return _dialog.isShowing();
    }

    public Dialog getDialog() {
        return _dialog;
    }

    public static class Builder extends AlertDialog.Builder {

        public Builder(Context context) {
            super(context);
        }

        public Builder(Context context, int theme) {
            super(context, theme);
        }

        public DialogContainer showDialog() {
            return new DialogContainer(super.create()).show();
        }
    }
}
