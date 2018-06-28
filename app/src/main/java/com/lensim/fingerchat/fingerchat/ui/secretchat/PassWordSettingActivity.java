package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;
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
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
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

    }

    @Override
    public void onNumberDelete() {

    }
}
