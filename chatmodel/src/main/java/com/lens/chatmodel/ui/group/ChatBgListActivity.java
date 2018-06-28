package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;

/**
 * Created by xhdl0002 on 2018/2/9.
 */

public class ChatBgListActivity extends BaseActivity {

    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_chatbg_list);
        toolbar = findViewById(R.id.viewTitleBar);
        toolbar.setTitleText("聊天背景");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        findViewById(R.id.chatbg_local_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent() == null) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ChatBgLocalSelActivity.class);
                intent.putExtra("mucId", getIntent().getStringExtra("mucId"));
                intent.putExtra("chatType", getIntent().getIntExtra("chatType", 0));
                startActivityForResult(intent, AppConfig.REQUEST_CHANGE_CONFIG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_CHANGE_CONFIG) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK,data);
                finish();
            }
        }
    }
}
