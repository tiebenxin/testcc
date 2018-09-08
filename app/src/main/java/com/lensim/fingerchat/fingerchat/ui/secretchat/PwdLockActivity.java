package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.manager.SPManager;
import com.lensim.fingerchat.fingerchat.ui.secretchat.widget.SlideSwitch;
import com.lensim.fingerchat.fingerchat.ui.secretchat.widget.SlideSwitch.SlideListener;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class PwdLockActivity extends BaseActivity implements OnClickListener {
    private static final int CHANGE_PWD = 1;
    private static final int FORGET_PWD = 2;
    private TextView close;
    private View mRootView;
    private TextView secretTitle;
    private SlideSwitch safeLock;
    private SlideSwitch fingerprintLock;
    private RelativeLayout rlChange;
    private RelativeLayout rlForget;
    private SlideSwitch screenLock;

    @Override
    public void initView() {
        setContentView(
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_pwd_lock, null));

        close = findViewById(R.id.close);
        secretTitle = findViewById(R.id.secretTitle);
        safeLock = findViewById(R.id.safeLock);
        rlChange = findViewById(R.id.rlChange);
        rlForget = findViewById(R.id.rlForget);
        fingerprintLock = findViewById(R.id.fingerprintLock);
        screenLock = findViewById(R.id.screenLock);

        secretTitle.setText(getText(R.string.safe_pwd_lock));

        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.rlChange:
                loginPwdDialog(CHANGE_PWD);
                break;
            case R.id.rlForget:
                loginPwdDialog(FORGET_PWD);
                break;
                default:
                    break;
        }
    }
    private void initListener(){
        safeLock.setState(SPManager.getmSpfPassword().hasPwd().get(false));
        fingerprintLock.setState(SPManager.getmSpfPassword().printLock().get(false));
        screenLock.setState(SPManager.getmSpfPassword().screenLock().get(false));
        close.setOnClickListener(this);
        rlChange.setOnClickListener(this);
        rlForget.setOnClickListener(this);
        safeLock.setSlideListener(new SlideListener() {
            @Override
            public void open() {
                boolean type = SPManager.getmSpfPassword().type().get(true);
                if (type){
                    Intent intent = new Intent(PwdLockActivity.this, PassWordSettingActivity.class);
                    intent.putExtra("pwdType", PassWordSettingActivity.SETTING_PWD);
                    startActivityForResult(intent, 0);
                }else {
                    SPManager.getmSpfPassword().edit().type().put(true).commit();
                }

            }
            @Override
            public void close() {
                boolean type = SPManager.getmSpfPassword().type().get(true);
                if (type){
                    Intent intent = new Intent(PwdLockActivity.this, PassWordSettingActivity.class);
                    intent.putExtra("pwdType", PassWordSettingActivity.CLOSE_LOCK);
                    startActivityForResult(intent, 1);
                }else {
                    SPManager.getmSpfPassword().edit().type().put(true).commit();
                }

            }
        });

        fingerprintLock.setSlideListener(new SlideListener() {
            @Override
            public void open() {
                SPManager.getmSpfPassword().edit().printLock().put(true).commit();
            }

            @Override
            public void close() {
                SPManager.getmSpfPassword().edit().printLock().put(false).commit();
            }
        });
        screenLock.setSlideListener(new SlideListener() {
            @Override
            public void open() {
                if (!SPManager.getmSpfPassword().hasPwd().get(false)){
                    T.showShort(PwdLockActivity.this, "请先开启密码锁");
                    screenLock.setState(false);
                }else {
                    SPManager.getmSpfPassword().edit().screenLock().put(true).commit();
                }
            }

            @Override
            public void close() {
                SPManager.getmSpfPassword().edit().screenLock().put(false).commit();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SPManager.getmSpfPassword().edit().type().put(false).commit();
        boolean hasPwd = SPManager.getmSpfPassword().hasPwd().get(false);
        safeLock.setState(hasPwd);
    }
    private void loginPwdDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PwdLockActivity.this);
        View view = View.inflate(PwdLockActivity.this, R.layout.layout_login_pwd_confirm, null);
        builder.setView(view);
        builder.setCancelable(true);
        final EditText etPwd = view.findViewById(R.id.etPwd);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        final Dialog dialog = builder.create();
        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd = PasswordRespository.getPassword();
                String etPassWord = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(etPassWord)) {
                    T.showShort(PwdLockActivity.this, "密码不能为空");
                    return;
                }
                if (pwd.equals(etPassWord)) {
                    switch (i) {
                        case CHANGE_PWD:
                            Intent intent = new Intent(PwdLockActivity.this, PassWordSettingActivity.class);
                            intent.putExtra("pwdType", PassWordSettingActivity.CHANGE_PWD);
                            startActivity(intent);
                            dialog.dismiss();
                            break;
                        case FORGET_PWD:
                            Intent i = new Intent(PwdLockActivity.this, PassWordSettingActivity.class);
                            i.putExtra("pwdType", PassWordSettingActivity.SETTING_PWD);
                            startActivity(i);
                            dialog.dismiss();
                            break;
                        default:
                            break;
                    }

                } else {
                    T.showShort(PwdLockActivity.this, "密码不正确");
                }

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

}
