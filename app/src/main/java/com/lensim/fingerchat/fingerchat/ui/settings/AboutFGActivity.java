package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.SystemApi;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.fingerchat.model.bean.VersionInfoBean;
import com.lensim.fingerchat.fingerchat.model.result.GetVersionInfoResult;

import butterknife.InjectView;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class AboutFGActivity extends BaseActivity {
    @InjectView(R.id.tv_version)
    TextView tv_version;
    @InjectView(R.id.tv_inspect_version)
    TextView tv_inspect_version;
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
                new SystemApi().getVersionInfo(new FXRxSubscriberHelper<GetVersionInfoResult>() {
                    @Override
                    public void _onNext(GetVersionInfoResult getVersionInfoResult) {
                        VersionInfoBean bean = getVersionInfoResult.getContent();
                        if (bean == null) {
                            return;
                        }
                        if (bean.getAppVersion().equals(getVersionName())) {
                            Toast.makeText(getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT)
                                .show();
                        } else {
                            showUpdateDialog(bean);
                        }
                    }
                });
            }
        });
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

    public void showUpdateDialog(VersionInfoBean mAppVersion) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("有新版本")
            .withMessage(mAppVersion.getAppMsg())
            .withDuration(300)
            .withIcon(R.mipmap.ic_logo)
            .withButton1Text("不再提醒")
            .withButton2Text("更新")
            .isCancelableOnTouchOutside(false)
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    SPHelper.saveValue(AppConfig.VERSION_CODE, mAppVersion.getAppVersion());
                    SPHelper.saveValue(AppConfig.VERSION_REMIND, true);
                }
            })
            .setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    try {
                        Uri uri = Uri.parse(mAppVersion.getAppUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        T.show("apk下载链接异常");
                    }
                }
            }).show();
    }

}
