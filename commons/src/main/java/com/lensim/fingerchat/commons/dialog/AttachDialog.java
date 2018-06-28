package com.lensim.fingerchat.commons.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;

/**
 * Created by LY309313 on 2017/6/24.
 */

public class AttachDialog extends BaseDialog {

    private EditText mAttachTitle;
    private TextView mAttachCancel;
    private TextView mAttachConfirm;
    OnConfrimListener onConfrimListener;
    public AttachDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    public AttachDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_attach);
        mAttachTitle = (EditText) findViewById(R.id.mAttachTitle);
        mAttachCancel = (TextView) findViewById(R.id.mAttachCancel);
        mAttachConfirm = (TextView) findViewById(R.id.mAttachConfirm);
    }

    @Override
    public void initEvent() {
        mAttachCancel.setOnClickListener(this);
        mAttachConfirm.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int i = view.getId();
        if (i == R.id.mAttachCancel) {
            dismiss();

        } else if (i == R.id.mAttachConfirm) {
            String title = mAttachTitle.getText().toString();
            if (onConfrimListener != null) {
                onConfrimListener.onConfirm(title);
            }

        }
    }

    public void setOnConfrimListener(OnConfrimListener onConfrimListener) {
        this.onConfrimListener = onConfrimListener;
    }

    public interface OnConfrimListener{

        void onConfirm(String title);
    }
}
