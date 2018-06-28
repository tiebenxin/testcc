package com.lensim.fingerchat.fingerchat.ui.settings;

import android.os.Bundle;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.fingerchat.R;


/**
 * Created by LY305512 on 2017/12/22.
 */

public class MessageSettingActivity extends BaseActivity {

    private FGToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        if (isFinishing()) {
            return;
        }

        setContentView(R.layout.activity_global_settings);
        toolbar = findViewById(R.id.viewTitleBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("消息设置");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MessageSettingsFragment()).commit();
        }
    }


}
