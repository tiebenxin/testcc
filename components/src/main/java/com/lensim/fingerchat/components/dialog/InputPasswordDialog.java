package com.lensim.fingerchat.components.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.R;


/**
 * Created by LY309313 on 2016/12/19.
 *
 */

public class InputPasswordDialog extends BaseDialog {


    private EditText mPassword;
    private TextView mPwdCancel;
    private TextView mPwdConfrim;
    private OnPwdConfrimListener onPwdConfrimListener;
    public InputPasswordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public InputPasswordDialog(Context context) {
        super(context);
    }

    public InputPasswordDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_input_password);
        mPassword = (EditText) findViewById(R.id.mPassword);
        mPwdCancel = (TextView) findViewById(R.id.mPwdCancel);
        mPwdConfrim = (TextView) findViewById(R.id.mPwdConfrim);

    }

    @Override
    public void initEvent() {
        mPwdConfrim.setOnClickListener(this);
        mPwdCancel.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        if (view.getId() == R.id.mPwdConfrim) {
            String content = mPassword.getText().toString();
            if(onPwdConfrimListener!=null && !StringUtils.isEmpty(content)){
                onPwdConfrimListener.oncomfirm(content);
            }
        } else if (view.getId() == R.id.mPwdCancel) {
            dismiss();
        }
    }

    public void setOnPwdConfrimListener(OnPwdConfrimListener onPwdConfrimListener) {
        this.onPwdConfrimListener = onPwdConfrimListener;
    }

    public interface OnPwdConfrimListener{

        void oncomfirm(String content);

    }
}
