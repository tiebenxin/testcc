package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.manager.SPManager;
import com.lensim.fingerchat.fingerchat.ui.secretchat.widget.NumberKeyboardView;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class PassWordSettingActivity extends BaseActivity implements OnClickListener ,NumberKeyboardView.OnNumberClickListener{
    public static final int SETTING_PWD = 1;
    public static final int CLOSE_LOCK = 2;
    public static final int CHANGE_PWD = 3;
    private View mRootView;
    private TextView secretTitle;
    private TextView close;
    private EditText etPwd;
    private TextView chanceCount;
    private NumberKeyboardView am_nkv_keyboard;
    private TextView confirmPwd;
    private TextView input;
    private int type;
    private int settingFirst = 1;  //设置新密码
    private int changeFirst = 1;  //修改密码
    private String str = "";
    private int count = 4;
    @Override
    public void initView() {
        setContentView(mRootView = LayoutInflater
            .from(this).inflate(R.layout.activity_pwd_set, null));

        secretTitle = findViewById(R.id.secretTitle);
        close = findViewById(R.id.close);
        etPwd = findViewById(R.id.etPwd);
        chanceCount = findViewById(R.id.chanceCount);
        am_nkv_keyboard = findViewById(R.id.am_nkv_keyboard);
        confirmPwd = findViewById(R.id.confirmPwd);
        input = findViewById(R.id.input);

        secretTitle.setText(getText(R.string.pwd_set));
        close.setOnClickListener(this);
        am_nkv_keyboard.setOnNumberClickListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        type = getIntent().getIntExtra("pwdType", 0);

        switch (type) {
            case SETTING_PWD:
                secretTitle.setText(getText(R.string.new_pwd_set));
                break;
            case CLOSE_LOCK:
                secretTitle.setText(getText(R.string.close_pwd_lock));
                input.setText(getText(R.string.old_pwd_to_close));
                break;
            case CHANGE_PWD:
                secretTitle.setText(getText(R.string.change_safe_pwd));
                input.setText(getText(R.string.old_pwd));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                finish();
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
        setTextContent(str);
    }

    private void setTextContent(String content){
        etPwd.setText(content);
        if (content.length() == 4){
            switch (type){
                case SETTING_PWD:
                    if (settingFirst == 1){
                        SPManager.getmSpfPassword().edit().secretchatPwd().put(content).commit();
                        etPwd.setText("");
                        confirmPwd.setVisibility(View.VISIBLE);
                        settingFirst = 2;
                        str = "";
                        onNumberDelete();
                    }else {
                        if (content.equals(SPManager.getmSpfPassword().secretchatPwd().get(""))) {
                            T.showShort(R.string.pwd_set_success);
                            SPManager.getmSpfPassword().edit().firstSet().put(true).commit();
                            SPManager.getmSpfPassword().edit().hasPwd().put(true).commit();
                            finish();
                            setResult(0);
                        } else {
                            confirmPwd.setVisibility(View.VISIBLE);
                            confirmPwd.setText(getText(R.string.confirem_pwd));
                            str = "";
                            onNumberDelete();
                        }
                    }
                    break;
                case CLOSE_LOCK:
                    if (content.equals(SPManager.getmSpfPassword().secretchatPwd().get(""))){
                        SPManager.getmSpfPassword().edit().hasPwd().put(false).commit();
                        SPManager.getmSpfPassword().edit().secretchatPwd().put("").commit();
                        SPManager.getmSpfPassword().edit().printLock().put(false).commit();
                        T.show(getString(R.string.pwd_closed));
                        finish();
                        setResult(1);
                    }else {
                        if (count == 0){
                            T.show(getString(R.string.freeze_account));
                            finish();
                            setResult(1);
                        }else {
                            chanceCount.setVisibility(View.VISIBLE);
                            chanceCount.setText("密码不正确，您还有" + count-- + "次机会");
                            str = "";
                            onNumberDelete();
                        }
                    }
                    break;
                case CHANGE_PWD:
                    if (changeFirst == 1) {
                        if (content.equals(SPManager.getmSpfPassword().secretchatPwd().get(""))) {
                            confirmPwd.setVisibility(View.VISIBLE);
                            str = "";
                            onNumberDelete();
                            confirmPwd.setText("");
                            input.setText(getText(R.string.please_input_new_pwd));
                            changeFirst = 2;
                        } else {
                            confirmPwd.setVisibility(View.VISIBLE);
                            confirmPwd.setText(getText(R.string.old_pwd_wrong));
                            str = "";
                            onNumberDelete();
                        }
                    } else if (changeFirst == 2) {

                        SPManager.getmSpfPassword().edit().secretchatPwd().put(content).commit();
                        etPwd.setText("");
                        confirmPwd.setVisibility(View.GONE);
                        input.setText(getText(R.string.input_new_pwd_again));
                        changeFirst = 3;
                        str = "";
                        onNumberDelete();
                    } else {
                        if (content.equals(SPManager.getmSpfPassword().secretchatPwd().get(""))) {

                            T.showShort(R.string.safe_pwd_change_success);
                            SPManager.getmSpfPassword().edit().hasPwd().put(true).commit();
                            this.finish();
                        } else {
                            confirmPwd.setVisibility(View.VISIBLE);
                            confirmPwd.setText(getText(R.string.confirem_pwd));
                            str = "";
                            onNumberDelete();
                        }
                    }

                    break;
                    default:
                        break;
            }
        }
    }

}
