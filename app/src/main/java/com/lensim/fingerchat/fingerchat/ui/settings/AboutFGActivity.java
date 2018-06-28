package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseActivity;

import butterknife.InjectView;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class AboutFGActivity extends BaseActivity {
    @InjectView(R.id.tv_version)
    TextView tv_version;
    @InjectView(R.id.tv_inspect_version)
    TextView tv_inspect_version;
    private int mLocalVersionCode;
    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_about_fg);
        tv_inspect_version = findViewById(R.id.tv_inspect_version);
        tv_version = findViewById(R.id.tv_version);
        toolbar = findViewById(R.id.viewTitleBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("关于飞鸽");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        tv_version.setText("飞鸽 " + getVersionName());
        setListener();
    }

    private void setListener() {
        tv_inspect_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalVersionCode = getVersionCode();
                boolean isUpdateVersion = checkVersion();
                if (isUpdateVersion) {
                    //存在就弹出对话框，是否更新下载


                } else {
                    //已经是最新版本弹个土司
                    Toast.makeText(getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //检查是否存在新版本
    private boolean checkVersion() {


        return false;
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取当前版本号
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
