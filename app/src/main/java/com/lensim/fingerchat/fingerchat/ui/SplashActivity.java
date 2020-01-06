package com.lensim.fingerchat.fingerchat.ui;


import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;

import com.fingerchat.api.client.ClientConfig;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.im_service.IMLog;
import com.lens.chatmodel.net.network.NetworkReceiver;
import com.lensim.fingerchat.commons.BuildConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.FGApplication;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.login.LoginActivity;
import com.lensim.fingerchat.commons.utils.AppHostUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;


/**
 * Created by LL130386 on 2017/11/14.
 */


public class SplashActivity extends BaseActivity {

    private boolean isGranted = true;
    private boolean isFirstTime = true;
    private String password;
    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_splash);
        if (isFirstTime) {
            requestPermission();
            isFirstTime = false;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstTime && !isGranted && !isPermissionDialogShow()) {
            requestPermission();
        }
    }



    @SuppressLint("CheckResult")
    private void requestPermission() {
        //权限获取
        new RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
//                permission.RECORD_AUDIO,
//                permission.CAMERA
            )
            .subscribe((bool) -> {
                if (bool) {
                    granted();
                } else if (!isPermissionDialogShow()) {
                    showMissingPermissionDialog();
                }
                isGranted = bool;
            });
    }


    private void granted() {
        if (checkIsLogin()) {
            if (FingerIM.I.hasStarted()) {
                FingerIM.I.login(userId, password);
            } else {
                initIMClient();
            }
            startActivity(true);
        } else {
            if (!FingerIM.I.hasStarted()) {
                initIMClient();
            }
            startActivity(false);
        }
    }

    private boolean checkIsLogin() {
        password = PasswordRespository.getPassword();
        userId = UserInfoRepository.getUserName();
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(userId)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 显示提示信息
     */
    NiftyDialogBuilder builder;

    protected void showMissingPermissionDialog() {
        builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle(getString(com.lensim.fingerchat.commons.R.string.notifyTitle))
            .withMessage(getString(com.lensim.fingerchat.commons.R.string.notifyMsg))
            .withDuration(200)
            .withButton1Text(getString(com.lensim.fingerchat.commons.R.string.cancel))
            .withButton2Text(getString(com.lensim.fingerchat.commons.R.string.setting))
            .setButton1Click(v -> {
                builder.dismiss();
                finish();
            })
            .setButton2Click(v -> {
                builder.dismiss();
                startAppSettings();
            })
            .show();
    }

    protected boolean isPermissionDialogShow() {
        if (builder != null) {
            return builder.isShowing();
        } else {
            return false;
        }
    }

    /**
     * 启动应用的设置
     */
    protected void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }


    private void initIMClient() {
        //公钥有服务端提供和私钥对应
        ClientConfig cc = ClientConfig.build()
            .setPublicKey(FGApplication.PUBLIC_KEY)
            .setServerAddress(AppHostUtil.getTcpConnectHostApi())
            .setDeviceId(TDevice.getDeviceId(this))
            .setClientVersion(BuildConfig.VERSION_NAME)
            .setLogger(new IMLog())
            .setLogEnabled(BuildConfig.DEBUG)
            .setMaxHeartbeat(270)
            .setMinHeartbeat(30)
            .setEnableHttpProxy(true);
        FingerIM.I.checkInit(getApplicationContext()).setClientConfig(cc);
        FingerIM.I.checkInit(this).startFingerIM();
    }


    protected void startActivity(boolean isLogin) {
        Intent intent;
        if (isLogin) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        // 提前将全屏切换为非全屏状态，解决从全屏进入非全屏标题栏闪动的问题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        startActivity(intent);
        this.finish();
    }
}
