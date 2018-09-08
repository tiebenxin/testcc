package com.lensim.fingerchat.fingerchat.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lensim.fingerchat.commons.utils.AppHostUtil;
import com.lensim.fingerchat.fingerchat.BuildConfig;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by zm on 2018/5/22.
 */
public class _SplashActivity extends SplashActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initView() {
        super.initView();
        FrameLayout frameLayout = findViewById(R.id.splash_root);
        View view = LayoutInflater.from(this).inflate(R.layout.view_splash_point, null, false);
        frameLayout.addView(view);

        TextView tvServiceLink = view.findViewById(R.id.tv_service_link);
        TextView tvVersionName = view.findViewById(R.id.tv_version_name);
        TextView tvChannel = view.findViewById(R.id.tv_channel);

        tvServiceLink.setText(AppHostUtil.getHttpConnectHostApi());
        tvChannel.setText(BuildConfig.CHANNEL);
        tvVersionName.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void startActivity(boolean isLogin) {

        // 延迟3秒执行
        new Handler().postDelayed(() -> {
            if (!_SplashActivity.this.isFinishing()) {
                _SplashActivity.super.startActivity(isLogin);
            }
        }, 3000);
    }
}
