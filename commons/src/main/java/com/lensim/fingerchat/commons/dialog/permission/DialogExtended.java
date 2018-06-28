package com.lensim.fingerchat.commons.dialog.permission;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import com.lensim.fingerchat.commons.utils.L;

public class DialogExtended extends Dialog {

    public IOnKeyDownListener _onKeyDownListener;

    public DialogExtended(Context context) {
        super(context);
    }

    public DialogExtended(Context context, int theme) {
        super(context, theme);
    }

    protected DialogExtended(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setOnKeyDownListener(IOnKeyDownListener onKeyDownListener) {
        _onKeyDownListener = onKeyDownListener;
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            try {
                super.dismiss();
            } catch (Exception exc) {
                L.e(exc);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return _onKeyDownListener != null && _onKeyDownListener.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public interface IOnKeyDownListener {
        boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
