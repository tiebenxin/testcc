package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class SecretChatMessageActivity extends BaseActivity implements OnClickListener {

    private View mRootView;
    private TextView secretTitle;
    private TextView close;
    private RelativeLayout rlAdd;
    private ImageView setting;
    @Override
    public void initView() {
        setContentView(
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_secretchat_meaasge, null));
        secretTitle = findViewById(R.id.secretTitle);
        rlAdd = findViewById(R.id.rlAdd);
        close = findViewById(R.id.close);
        setting = findViewById(R.id.setting);

        secretTitle.setText(getText(R.string.secret_chat));
        rlAdd.setVisibility(View.VISIBLE);

        close.setOnClickListener(this);
        setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.setting:
                startActivity(new Intent(this,SecretChatSettingActivity.class));
                break;
                default:
                    break;
        }
    }
}
