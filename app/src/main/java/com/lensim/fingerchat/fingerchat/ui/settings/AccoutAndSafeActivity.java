package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseActivity;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class AccoutAndSafeActivity extends BaseActivity {

    private TextView tv_change_pwd;

    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_accoutandsafe);
        tv_change_pwd = findViewById(R.id.tv_change_pwd);
        toolbar = findViewById(R.id.viewTitleBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("账号与安全");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        setListener();
    }

    /**
     * 修改密码
     */
    private void setListener() {
        tv_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码，不需要code
                Intent intent = ChangePasswordActivity
                    .newIntent(AccoutAndSafeActivity.this, 0, UserInfoRepository.getUserId(), "");
                startActivityForResult(intent,0);
                finish();
            }
        });
    }
}
