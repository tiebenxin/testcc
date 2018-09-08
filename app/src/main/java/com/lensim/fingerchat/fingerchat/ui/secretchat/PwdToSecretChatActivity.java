package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.manager.SPManager;
import com.lensim.fingerchat.fingerchat.ui.secretchat.fingerprint.FingerprintUtil;
import com.lensim.fingerchat.fingerchat.ui.secretchat.fingerprint.core.FingerprintCore;
import com.lensim.fingerchat.fingerchat.ui.secretchat.widget.NumberBlueKeyboardView;
import com.lensim.fingerchat.fingerchat.ui.secretchat.widget.NumberBlueKeyboardView.OnNumberClickListener;


/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class PwdToSecretChatActivity extends BaseActivity implements OnClickListener,
    OnNumberClickListener {
    private View mRootView;
    private RelativeLayout rlClose;
    private NumberBlueKeyboardView am_nkv_keyboard;
    private TextView chanceCount;
    private EditText etPwd;
    private TextView managerPwd;
    private TextView fingerPrint;
    private Dialog dialog;
    private String str = "";
    private int count = 4;
    private FingerprintCore mFingerprintCore;
    private boolean isBackground;

    @Override
    public void initView() {
        setContentView(mRootView = LayoutInflater.from(this).inflate(R.layout.activity_input_pwd, null));

        rlClose = findViewById(R.id.rlClose);
        am_nkv_keyboard = findViewById(R.id.am_nkv_keyboard);
        chanceCount = findViewById(R.id.chanceCount);
        etPwd = findViewById(R.id.etPwd);
        managerPwd = findViewById(R.id.managerPwd);
        fingerPrint = findViewById(R.id.fingerPrint);

        rlClose.setOnClickListener(this);
        managerPwd.setOnClickListener(this);
        fingerPrint.setOnClickListener(this);
        am_nkv_keyboard.setOnNumberClickListener(this);

        initFingerprintCore();

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        isBackground = getIntent().getBooleanExtra("background",false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlClose:
                finish();
                break;
            case R.id.managerPwd:
                startActivity(new Intent(this,PwdLockActivity.class));
                break;
            case R.id.fingerPrint:
                if (SPManager.getmSpfPassword().printLock().get(false)){
                    fingerPrintDialog();
                }else {
                    T.showShort(R.string.printlock_unopen);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNumberReturn(String number) {
        str += number;
        setTextContent(str);
    }

    @Override
    public void onNumberDelete() {
        if (str.length() <= 1) {
            str = "";
        } else {
            str = str.substring(0, str.length() - 1);
        }
    }
    private void setTextContent(String content){
        etPwd.setText(content);
        if (content.length() == 4){
            if (content.equals(SPManager.getmSpfPassword().secretchatPwd().get(""))){
                if (!isBackground){
                    Intent intent = new Intent(this, SecretChatMessageActivity.class);
                    startActivity(intent);
                }
                finish();
            }else {
                if (count == 0){
                    T.show(getString(R.string.freeze_account));
                    finish();
                }else {
                    chanceCount.setVisibility(View.VISIBLE);
                    chanceCount.setText("密码不正确，您还有"+ count-- +"次机会");
                    str = "";
                    onNumberDelete();
                }
            }
        }
    }
    private void fingerPrintDialog(){
        AlertDialog.Builder builder  = new AlertDialog.Builder(PwdToSecretChatActivity.this);
        View view = View.inflate(PwdToSecretChatActivity.this,R.layout.layout_finger_print,null);
        builder.setView(view);
        builder.setCancelable(true);
        final ImageView ivFinger = view.findViewById(R.id.ivFinger);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        final TextView input = view.findViewById(R.id.input);
        dialog = builder.create();
        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startFingerprintRecognition(ivFinger,input);
            }
        });
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 开始指纹识别
     */
    private void startFingerprintRecognition(ImageView ivFinger,TextView input){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mFingerprintCore.isSupport()) {
                if (!mFingerprintCore.isHasEnrolledFingerprints()) {
                    T.showShort(R.string.input_print);
                    FingerprintUtil.openFingerPrintSettingPage(this);
                    return;
                }
                T.showShort(R.string.long_press_to_open);
                ivFinger.setImageResource(R.drawable.fingerprint_guide);
                input.setText(R.string.long_press_to_open);
                if (mFingerprintCore.isAuthenticating()) {
                    T.showShort(R.string.long_press_open);
                } else {
                    mFingerprintCore.startAuthenticate();
                }
            } else {
                T.showShort(R.string.device_no_support);
                //mFingerGuideTxt.setText(R.string.fingerprint_recognition_tip);
            }
        }else {
            T.showShort(R.string.version_toolow);
        }
    }

    private FingerprintCore.IFingerprintResultListener mResultListener = new FingerprintCore.IFingerprintResultListener() {
        @Override
        public void onAuthenticateSuccess() {
            T.showShort(R.string.printlock_open_success);
            dialog.dismiss();
            Intent intent = new Intent(PwdToSecretChatActivity.this, SecretChatMessageActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onAuthenticateFailed(int helpId) {
            T.showShort(R.string.printlock_open_fail);
        }

        @Override
        public void onAuthenticateError(int errMsgId) {
            T.showShort(R.string.printlock_open_fail_wait);
        }

        @Override
        public void onStartAuthenticateResult(boolean isSuccess) {

        }
    };
    private void initFingerprintCore() {
        mFingerprintCore = new FingerprintCore(this);
        mFingerprintCore.setFingerprintManager(mResultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFingerprintCore != null) {
            mFingerprintCore.onDestroy();
            mFingerprintCore = null;
        }
        mResultListener = null;
    }
}
