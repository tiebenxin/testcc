package com.lensim.fingerchat.fingerchat.ui.secretchat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.manager.SPManager;

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
    private ImageView addSecretContast;
    @Override
    public void initView() {
        setContentView(
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_secretchat_meaasge, null));
        secretTitle = findViewById(R.id.secretTitle);
        rlAdd = findViewById(R.id.rlAdd);
        close = findViewById(R.id.close);
        setting = findViewById(R.id.setting);
        addSecretContast = findViewById(R.id.addSecretContast);

        secretTitle.setText(getText(R.string.secret_chat));
        rlAdd.setVisibility(View.VISIBLE);

        close.setOnClickListener(this);
        setting.setOnClickListener(this);
        addSecretContast.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.setting:
                if (SPManager.getmSpfPassword().firstSet().get(false) ||
                    SPManager.getmSpfPassword().hasPwd().get(false)) {
                    startActivity(new Intent(this, PwdLockActivity.class));
                } else {
                    startActivity(new Intent(this, SecretChatSettingActivity.class));
                }
                break;
            case R.id.addSecretContast:
                Intent intent = new Intent(this, GroupSelectListActivity.class);
                intent.putExtra(Constant.KEY_OPERATION, Constant.SECRETCHAT_ADD);
                startActivity(intent);
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPManager.getmSpfPassword().edit().backgroundTime().put(System.currentTimeMillis()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPManager.getmSpfPassword().edit().backgroundTime().put((long) 0).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        long h = SPManager.getmSpfPassword().backgroundTime().get((long) 0);   //密码锁屏保护，届时要放到聊天页面
        if (h > 0) {
            long htime = System.currentTimeMillis() - h;
            if (htime > 30 * 1000 && SPManager.getmSpfPassword().hasPwd().get(false)
                && SPManager.getmSpfPassword().screenLock().get(false)) {
                SPManager.getmSpfPassword().edit().backgroundTime().put((long) 0).commit();
                Intent intent = new Intent(this, PwdToSecretChatActivity.class);
                intent.putExtra("background",true);
                startActivity(intent);
            }
        }
    }
}
