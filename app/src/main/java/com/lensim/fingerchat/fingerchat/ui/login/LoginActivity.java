package com.lensim.fingerchat.fingerchat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.Resp.Message;
import com.lens.chatmodel.eventbus.ExcuteEvent;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.im_service.IMLog;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.net.network.NetworkReceiver;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lensim.fingerchat.commons.BuildConfig;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseMvpActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.AppHostUtil;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.db.DaoManager;
import com.lensim.fingerchat.fingerchat.FGApplication;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.SystemApi;
import com.lensim.fingerchat.fingerchat.model.bean.VersionInfoBean;
import com.lensim.fingerchat.fingerchat.model.result.GetVersionInfoResult;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentForgetPsw.IFragmentForgetListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentLogin.IFragmentLoginListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentOtherLogin.IFragmentLoginOtherListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentRegister.IFragmentRegisterListener;
import com.lensim.fingerchat.fingerchat.ui.settings.ChangePasswordActivity;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2017/11/14.
 */

@CreatePresenter(LoginPresenter.class)
public class LoginActivity extends BaseMvpActivity<LoginContract.View, LoginPresenter> implements
    LoginContract.View {

    private ELoginTabs mCurrentTab;
    private View mRootView;
    private Fragment mCurrentFragment;
    private FGToolbar toolbar;

    private boolean isReceivedUserinfo = false;
    private boolean isResponseEvent = false;
    private NetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTab = ELoginTabs.LOGIN;
        showFragment(mCurrentTab);
        checkVersion();

    }

    @Override
    public void initView() {
        setContentView(
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_login, null));
        toolbar = findViewById(R.id.viewTitleBar);
        doRegisterNetReceiver();
    }


    private void doRegisterNetReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new NetworkReceiver();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
    }


    private void showFragment(ELoginTabs tab) {
        hideSoftKeyboard();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tab.toString());
        if (newFragment == null) {
            newFragment = createFragment(tab);
        }

        prepareFragment(newFragment);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.fl_content, newFragment, tab.toString());
        ft.attach(newFragment);
        ft.commitAllowingStateLoss();
        mCurrentTab = tab;
        setCurrentFragment(newFragment);
        initTitleBar(mCurrentTab);
        if (mCurrentTab != ELoginTabs.LOGIN) {
            FingerIM.I.setBannedAutoLogin(false);
//            checkConnection();
        }
    }

    private void initTitleBar(ELoginTabs tab) {
        if (tab == ELoginTabs.LOGIN) {
            toolbar.setTitleText(ContextHelper.getString(R.string.login));
            initBackButton(toolbar, false);
        } else if (tab == ELoginTabs.REGISTER) {
            toolbar.setTitleText(ContextHelper.getString(R.string.register));
            initBackButton(toolbar, false);
        } else if (tab == ELoginTabs.FORGET_PSW) {
            toolbar.setTitleText(ContextHelper.getString(R.string.identify_now));
            initBackButton(toolbar, true);
        } else if (tab == ELoginTabs.OTHER_LOGIN) {
            toolbar.setTitleText(ContextHelper.getString(R.string.login_by_phone_num));
            initBackButton(toolbar, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrentFragent() != null) {
            if (getCurrentFragent() instanceof FragmentRegister) {
                ((FragmentRegister) getCurrentFragent())
                    .notifyActivityResult(requestCode, resultCode, data);
            } else if (getCurrentFragent() instanceof FragmentForgetPsw) {
                if (resultCode == RESULT_OK) {
                    showFragment(ELoginTabs.LOGIN);
                }
            }
        }


    }

    @Override
    public void backPressed() {
        if (mCurrentTab == ELoginTabs.LOGIN) {
            LoginActivity.this.finish();
        } else {
            showFragment(ELoginTabs.LOGIN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isProgressShowing()) {
            dismissProgress();
        }
        if (mCurrentFragment instanceof FragmentRegister) {
            ((FragmentRegister) mCurrentFragment).notifyDestroy();
        }
        if (mCurrentFragment instanceof FragmentForgetPsw) {
            ((FragmentForgetPsw) mCurrentFragment).notifyDestroy();
        }
    }


    private void prepareFragment(Fragment fragment) {
        if (fragment instanceof FragmentLogin) {
            prepareFragmentLogin((FragmentLogin) fragment);

        } else if (fragment instanceof FragmentRegister) {
            prepareFragmentRegister((FragmentRegister) fragment);

        } else if (fragment instanceof FragmentForgetPsw) {
            prepareFragmentForgetPsw((FragmentForgetPsw) fragment);

        } else if (fragment instanceof FragmentOtherLogin) {
            prepareFragmentOtherLogin((FragmentOtherLogin) fragment);

        }

    }


    private void prepareFragmentLogin(FragmentLogin fragment) {
        fragment.setListener(new IFragmentLoginListener() {
            @Override
            public void clickLogin(String accout, String password) {
                if (!NetworkUtils.isNetAvaliale()) {
                    T.showShort(R.string.no_network_connection);
                    return;
                }
                showProgress(ContextHelper.getString(R.string.login_ing), false);
                login(accout, password);
//                ssoLogin(accout, password);//sso登录成功后，再登录IM服务器

            }

            @Override
            public void clickRegister() {
                showFragment(ELoginTabs.REGISTER);
            }

            @Override
            public void clickForget() {
                showFragment(ELoginTabs.FORGET_PSW);
            }

            @Override
            public void clickMoreLogin() {
                showFragment(ELoginTabs.OTHER_LOGIN);
            }
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }

    private Fragment getCurrentFragent() {
        return mCurrentFragment;
    }

    private void prepareFragmentRegister(FragmentRegister fragment) {
        fragment.setListener(new IFragmentRegisterListener() {
            @Override
            public void clickRegister(String accout, String password, String nick, String phone,
                String identifyCode, String avatar) {
                showProgress(ContextHelper.getString(R.string.deal_with_ing), true);
                FingerIM.I.register(accout, password, nick, phone, identifyCode, avatar);
                AppConfig.INSTANCE.set(AppConfig.ACCOUT, accout);
                AppConfig.INSTANCE.set(AppConfig.PASSWORD, password);
                AppConfig.INSTANCE.set(AppConfig.PHONE, phone);
            }

            @Override
            public void clickBack() {
                showFragment(ELoginTabs.LOGIN);
            }
        });
    }

    private void prepareFragmentForgetPsw(FragmentForgetPsw fragment) {
        fragment.setListener(new IFragmentForgetListener() {
            @Override
            public void clickIdentify(String accout, String identifyCode) {
                Intent intent = ChangePasswordActivity
                    .newIntent(LoginActivity.this, 1, accout, identifyCode);
                startActivityForResult(intent, 1);
            }

            @Override
            public void clickBack() {
                showFragment(ELoginTabs.LOGIN);
            }
        });
    }

    private void prepareFragmentOtherLogin(FragmentOtherLogin fragment) {
        fragment.setListener(new IFragmentLoginOtherListener() {
            @Override
            public void clickLogin(String accout, String password) {
                if (!NetworkUtils.isNetAvaliale()) {
                    T.showShort(R.string.no_network_connection);
                    return;
                }
                showProgress(ContextHelper.getString(R.string.login_ing), false);

                getMvpPresenter().login(accout, password, true);

                AppConfig.INSTANCE.set(AppConfig.PHONE, accout);
                AppConfig.INSTANCE.set(AppConfig.PASSWORD, password);
            }

            @Override
            public void clickBack() {
                showFragment(ELoginTabs.LOGIN);
            }
        });

    }

    private Fragment createFragment(ELoginTabs tab) {
        switch (tab) {
            case LOGIN:
                return new FragmentLogin();
            case REGISTER:
                return new FragmentRegister();
            case FORGET_PSW:
                return new FragmentForgetPsw();
            case OTHER_LOGIN:
                return new FragmentOtherLogin();
        }

        return null;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            isResponseEvent = true;
            dealWithRequest(event);
        }
    }


    /**
     * “UserInfoMessage”消息 和“ResponseEvent”消息
     * 都收到了在调用“dstartMainActivity()“ 方法
     */
    private void isReceived() {
        if (isReceivedUserinfo && isResponseEvent) {
            startMainActivity();
        }
    }


    private void dealWithRequest(IEventProduct e) {
        if (e instanceof NetStatusEvent) {//链接状态有变化
            NetStatusEvent event = (NetStatusEvent) e;
            getMvpPresenter().NetworkListener(event);
            if (mCurrentFragment instanceof FragmentForgetPsw) {
                ((FragmentForgetPsw) mCurrentFragment).notifyRequestResult(e);
            }
        } else if (e instanceof ResponseEvent) {
            ResponseEvent event = (ResponseEvent) e;
            if (mCurrentFragment instanceof FragmentRegister) {
                if (event.getPacket() != null && event.getPacket().response != null) {
                    Message msg = event.getPacket().response;
                    dismissProgress();
                    if (msg.getCode() == Common.REG_REGISTER_OK
                        || msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                        T.show("注册成功，快去登录吧");
                        FingerIM.I.loginError();//注册成功.解绑用户数据，避免登录不上
                        savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                        showFragment(ELoginTabs.LOGIN);
                    } else {
                        FingerIM.I.loginError();//注册错误.解绑用户数据
                        if (msg.getCode() == Common.REG_SMS_OK) {
                            T.show("获取验证码成功");
                        } else if (msg.getCode() == Common.REG_SMS_ERROR
                            || msg.getCode() == Common.USERNAME_INVALIDE
                            || msg.getCode() == Common.USER_NOT_FOUND
                            || msg.getCode() == Common.PHONE_INVALID) {
                            T.show("获取验证码失败");
                        } else if (msg.getCode() == Common.OBTAIN_CODE_EXIST) {
                            T.show("已获取验证码未超时");
                        } else if (msg.getCode() == Common.PASSWORD_LOW_INTENSITY) {
                            T.show("密码强度不够，必须包含8-16位数字和大小写字母");
                        } else if (msg.getCode() == Common.RATE_LIMIT) {
                            T.show("发送短信频率过快");
                        } else if (msg.getCode() == Common.ACCOUNT_DUMPLICATED) {
                            T.show("该账号或者该手机号已经注册，请重新注册");
                        } else if (msg.getCode() == Common.OBTAIN_CODE_ERROR) {
                            T.show("服务器验证用户和手机号失败");
                        } else {//获取验证码失败
                            T.show("注册失败");
                        }
                    }
                }
            } else if (mCurrentFragment instanceof FragmentLogin) {
                if (event.getPacket() != null && event.getPacket().response != null) {
                    Message msg = event.getPacket().response;
                    dismissProgress();
                    if (msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                        isReceived();
                        savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                    } else if (msg.getCode() == Common.PASSWORD_DEFERRED_80DAYS) {//密码即将过期
                        showPsdGoOldDialog();
                    } else if (msg.getCode() == Common.PASSWORD_DEFERRED_90DAYS) {//密码已经过期
                        showPsdExpiredDialog();
                    } else {
                        FingerIM.I.loginError();//登录错误解绑用户数据
                        if (msg.getCode() == Common.LOGIN_ACCOUNT_INEXIST) {
                            T.showShort(R.string.accout_inexist);
                        } else if (msg.getCode() == Common.PASSWORD_ERROR) {
                            T.showShort(R.string.accout_or_psd_error);
                        } else if (msg.getCode() == Common.LOGIN_FORBIDDON_LOGIN) {
                            T.showShort(R.string.accout_was_banned);
                        } else if (msg.getCode() == Common.WITHOUT_PERMISSION) {
                            T.show("该账号无登录权限");
                        } else if (msg.getCode() == Common.LOGIN_VERYFY_ERROR) {
                            T.show("登录失败");
                        } else if (msg.getCode() == Common.DUPLICATE_PASSWORD_IN5TIMES) {
                            T.show("不能使用最近5次已使用过的密码");
                        } else if (msg.getCode() == Common.WRONG_PASSWORD_LOCKED) {
                            T.show("登录密码错误次数超过上限，账户已经被锁定10分钟");
                        }
                    }
                }
            } else if (mCurrentFragment instanceof FragmentForgetPsw) {
                ((FragmentForgetPsw) mCurrentFragment).notifyRequestResult(e);
            } else if (mCurrentFragment instanceof FragmentOtherLogin) {
                if (event.getPacket() != null && event.getPacket().response != null) {
                    Message msg = event.getPacket().response;
                    dismissProgress();
                    if (msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                        isReceived();
                        savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                    } else {
                        FingerIM.I.loginError();//登录错误解绑用户数据
                        if (msg.getCode() == Common.LOGIN_ACCOUNT_INEXIST
                            || msg.getCode() == Common.PARAM_INVALID) {
                            T.showShort(R.string.accout_inexist);
                        } else if (msg.getCode() == Common.LOGIN_VERYFY_ERROR) {
                            T.showShort(R.string.accout_or_psd_error);
                        } else if (msg.getCode() == Common.PASSWORD_ERROR) {
                            T.showShort(R.string.psd_error);
                        } else if (msg.getCode() == Common.LOGIN_FORBIDDON_LOGIN) {
                            T.showShort(R.string.accout_was_banned);
                        }
                    }
                }

            }
        } else if (e instanceof ExcuteEvent) {
            if (mCurrentFragment instanceof FragmentForgetPsw) {
                ((FragmentForgetPsw) mCurrentFragment).notifyRequestResult(e);
            }
        }
    }

    private void savePassWord(String psw) {
        if (!TextUtils.isEmpty(psw)) {
            PasswordRespository.setPassword(psw);
        }
    }

    private void startMainActivity() {
        //登录成功后加载群列表
        FingerIM.I.setBannedAutoLogin(true);
        AppManager.getInstance().setLoginStatus(true);
        initDao();
        //保存密码
        PasswordRespository.setPassword(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        this.finish();
    }

    private void initDao() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        if (!TextUtils.isEmpty(userId)) {
            DaoManager.initUserId(userId);
            System.out.println(LoginActivity.class.getSimpleName() + "--initDao");
        }

    }

    @Override
    public void onReceivedUserinfo(UserInfoMessage msg) {
        isReceivedUserinfo = true;
        isReceived();
    }

    @Override
    public void initIMClient() {
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

    @Override
    public void loginOutTime() {
        T.showShort(R.string.connection_out_time);
        dismissProgress();
    }


    public enum ELoginTabs {
        LOGIN(0),
        REGISTER(1),
        FORGET_PSW(2),
        OTHER_LOGIN(3);

        public final int value;

        ELoginTabs(int value) {
            this.value = value;
        }

        public static ELoginTabs fromInt(int value) {
            ELoginTabs result = null;
            for (ELoginTabs item : ELoginTabs.values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ELoginTabs - fromInt");
            }
            return result;
        }
    }

    private void checkVersion() {
        if (!SPHelper.getBoolean(AppConfig.VERSION_REMIND, false)) {  //wsq  用户没有点击过不再提醒
            new SystemApi().getVersionInfo(new FXRxSubscriberHelper<GetVersionInfoResult>() {
                @Override
                public void _onNext(GetVersionInfoResult getVersionInfoResult) {
                    VersionInfoBean bean = getVersionInfoResult.getContent();
                    if (bean == null) {
                        return;
                    }
                    if (!bean.getAppVersion().equals(getVersionName())) {
                        showUpdateDialog(bean);
                    }
                }
            });
        }
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

    /**
     * 单点登录 拿到Token，存SP，Main界面在检查是否过期
     * 返回code ，12-ok，21-账号密码有误，30-服务器错误
     **/
    private void ssoLogin(final String userid, final String pwd) {
        HttpUtils.getInstance().ssoLogin(userid, pwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<SSOToken> response) {
                    if (response.code == 10) {
                        SSOToken token = response.result;
                        if (token != null) {
                            token.setUserId(userid);
                            token.setTokenValidTime(
                                System.currentTimeMillis() + token.getLifetime() * 1000);
                            SSOTokenRepository.getInstance().setSSOToken(token);
                            login(userid, pwd);
                        }
                    } else if (response.code == 21) {
                        dismissProgress();
                        T.show("账号或密码错误");
                    } else {
                        dismissProgress();
                        T.show("获取token失败");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    dismissProgress();
                    T.show("获取token失败");
                }

                @Override
                public void onHandleError(ResponseObject<SSOToken> ssoTokenResponseObject) {
                    dismissProgress();
                    T.show("获取token失败");
                }
            });
    }

    private void login(String userid, String pwd) {
        getMvpPresenter().login(userid, pwd, false);
        AppConfig.INSTANCE.set(AppConfig.ACCOUT, userid);
        AppConfig.INSTANCE.set(AppConfig.PASSWORD, pwd);
    }

    //密码即将过期
    public void showPsdGoOldDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("提示")
            .withMessage("你的密码即将过期，请重设密码")
            .withDuration(300)
            .withIcon(R.mipmap.ic_logo)
            .withButton1Text("暂不设置")
            .withButton2Text("立即设置")
            .isCancelableOnTouchOutside(false)
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    startMainActivity();
                }
            })
            .setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent intent = ChangePasswordActivity.newIntent(LoginActivity.this, 0,
                        AppConfig.INSTANCE.get(AppConfig.ACCOUT), "");
                    startActivity(intent);
                }
            }).show();
    }

    //密码已经过期
    public void showPsdExpiredDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("提示")
            .withMessage("你的密码已经过期，请立即重设密码")
            .withDuration(300)
            .withIcon(R.mipmap.ic_logo)
            .withButton2Text("确定")
            .isCancelableOnTouchOutside(false)
            .setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent intent = ChangePasswordActivity.newIntent(LoginActivity.this, 0,
                        AppConfig.INSTANCE.get(AppConfig.ACCOUT), "");
                    startActivity(intent);
                }
            }).show();
    }
}