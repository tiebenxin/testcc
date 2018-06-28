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

public class SecretChatSettingActivity extends BaseActivity implements OnClickListener {
    private View mRootView;
    private TextView secretTitle;
    private TextView close;
    private ImageView openLock;
    private RelativeLayout rlSetting;
    private boolean isLock = false;
    private TextView tvSet;
    @Override
    public void initView() {
        setContentView(mRootView = LayoutInflater.from(this).inflate(R.layout.activity_secretchat_setting, null));

        secretTitle = findViewById(R.id.secretTitle);
        close = findViewById(R.id.close);
        openLock = findViewById(R.id.openLock);
        rlSetting = findViewById(R.id.rlSetting);
        tvSet = findViewById(R.id.tvSet);

        secretTitle.setText(getText(R.string.open_pwd_set));

        close.setOnClickListener(this);
        openLock.setOnClickListener(this);
        tvSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.openLock:
                if (!isLock) {
                    rlSetting.setVisibility(View.VISIBLE);
                    openLock.setImageResource(R.drawable.pwd_unlock);
                } else {
                    rlSetting.setVisibility(View.GONE);
                    openLock.setImageResource(R.drawable.pwd_lock);
                }
                isLock = !isLock;
                break;
            case R.id.tvSet:
                Intent intent = new Intent(SecretChatSettingActivity.this, PassWordSettingActivity.class);
                intent.putExtra("pwdType", PassWordSettingActivity.SETTING_PWD);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
