package com.lensim.fingerchat.fingerchat.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.Resp.Message;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.ResponseType;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.im_service.IMLog;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.net.network.NetworkReceiver;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lensim.fingerchat.commons.BuildConfig;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseMvpActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.db.DaoManager;
import com.lensim.fingerchat.fingerchat.FGApplication;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentForgetPsw.IFragmentForgetListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentLogin.IFragmentLoginListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentOtherLogin.IFragmentLoginOtherListener;
import com.lensim.fingerchat.fingerchat.ui.login.FragmentRegister.IFragmentRegisterListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2017/11/14.
 */

@CreatePresenter(LoginPresenter.class)
public class LoginActivity extends BaseMvpActivity<LoginView, LoginPresenter> implements LoginView,
    UserListener {

    private final int LOGIN = 0x01;

    private ELoginTabs mCurrentTab;
    private View mRootView;
    private Fragment mCurrentFragment;
    private FGToolbar toolbar;
    private boolean isReceivedUserinfo = false;
    private boolean isResponseEvent = false;
    private NetworkReceiver mNetworkReceiver;
    private String account;
    private String psd;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == LOGIN) {
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(psd)) {
                    if (mCurrentFragment instanceof FragmentOtherLogin) {
                        doLogin(account, psd, true);
                    } else {
                        doLogin(account, psd, false);
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTab = ELoginTabs.LOGIN;
        showFragment(mCurrentTab);
        checkClientContent();
    }

    @Override
    public void initView() {
        setContentView(
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_login, null));
        toolbar = findViewById(R.id.viewTitleBar);
        ClientConfig.I.registerListener(UserListener.class, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
    }

    /*
    * 是否监听网络状态，要先判断IM服务是否起来
    * */
    private void checkClientContent() {
        if (!FingerIM.I.hasStarted()) {//服务没起来
            if (NetworkUtils.isNetAvaliale()) {
                initIMClient();
            } else {
                doRegisterNetReceiver(ENetStatus.ERROR_NET);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientConfig.I.removeListener(UserListener.class, this);
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
                showProgress(ContextHelper.getString(R.string.login_ing), true);
                savePassWord(password);
                account = accout;
                psd = password;
                doLogin(accout, password, false);
                AppConfig.INSTANCE.set(AppConfig.ACCOUT, accout);
                AppConfig.INSTANCE.set(AppConfig.PASSWORD, password);
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
            public void clickRegister(String accout, String password, String phone,
                String identifyCode) {
                showProgress(ContextHelper.getString(R.string.deal_with_ing), true);
                FingerIM.I.register(accout, password, phone, identifyCode);
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

            }

            @Override
            public void clickBack() {

            }
        });
    }

    private void prepareFragmentOtherLogin(FragmentOtherLogin fragment) {
        fragment.setListener(new IFragmentLoginOtherListener() {
            @Override
            public void clickLogin(String accout, String password) {
                showProgress(ContextHelper.getString(R.string.login_ing), true);
                savePassWord(password);
                account = accout;
                psd = password;
                doLogin(accout, password, true);
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

    @Override
    public void onReceivedUserinfo(UserInfoMessage message) {
        isReceivedUserinfo = true;
        isReceived();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            isResponseEvent = true;
            dealWithRequest(event);
        }
    }

    private void doLogin(String accout, String password, boolean isOther) {
        if (FingerIM.I.hasStarted()) {
            if (FingerIM.I.hasRunning()) {
                if (FingerIM.I.isConnected()) {
                    mHandler.removeMessages(LOGIN);
                    if (isOther) {
                        FingerIM.I.loginByPhone(accout, password);
                    } else {
                        FingerIM.I.login(accout, password);
                    }
                } else {
                    FingerIM.I.fastConnect();
                    mHandler.sendEmptyMessage(LOGIN);
                }
            } else {
                FingerIM.I.startFingerIM();
                mHandler.sendEmptyMessage(LOGIN);
            }
        } else {
            initIMClient();
            mHandler.sendEmptyMessage(LOGIN);
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
            if (event.getStatus() == ENetStatus.SUCCESS_ON_NET) {//网络连接正常
                granted();//检测是否服务已经启动
            } else if (event.getStatus() == ENetStatus.ERROR_CONNECT) {//连接断开
                if (!NetworkUtils.isNetAvaliale()) {
                    doRegisterNetReceiver(ENetStatus.ERROR_NET);
                }
            }
        } else if (e instanceof ResponseEvent) {
            ResponseEvent event = (ResponseEvent) e;
            if (mCurrentFragment instanceof FragmentRegister
                && event.getType() == ResponseType.REGISTER) {
                if (event.getPacket() != null && event.getPacket().response != null) {
                    Message msg = event.getPacket().response;
                    dismissProgress();
                    if (msg.getCode() == Common.REG_REGISTER_OK
                        || msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                        L.d("register_event_success");
                        isReceived();
                        savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                    } else {
                        FingerIM.I.loginError();//注册错误解绑用户数据
                        if (msg.getCode() == Common.REG_SMS_OK) {
                            T.show("获取验证码成功");
                        } else if (msg.getCode() == Common.REG_SMS_ERROR) {
                            T.show("获取验证码失败");
                        } else if (msg.getCode() == Common.ACCOUNT_DUMPLICATED) {
                            T.show("该账号或者该手机号已经注册，请重新注册");
                        } else {//获取验证码失败
                            T.show("注册失败");
                        }
                    }
                }
            } else if (mCurrentFragment instanceof FragmentLogin
                && event.getType() == ResponseType.LOGIN) {
                if (event.getPacket() != null && event.getPacket().response != null) {
                    Message msg = event.getPacket().response;
                    dismissProgress();
                    if (msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                        isReceived();
                        savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                    } else {
                        FingerIM.I.loginError();//登录错误解绑用户数据
                        if (msg.getCode() == Common.LOGIN_ACCOUNT_INEXIST) {
                            T.showShort(R.string.accout_inexist);
                        } else if (msg.getCode() == Common.LOGIN_VERYFY_ERROR) {
                            T.showShort(R.string.accout_or_psd_error);
                        } else if (msg.getCode() == Common.LOGIN_FORBIDDON_LOGIN) {
                            T.showShort(R.string.accout_was_banned);
                        }

                    }
                }
            } else if (mCurrentFragment instanceof FragmentForgetPsw) {
                if (event.getType() == ResponseType.LOGIN) {
                    if (event.getType() == ResponseType.REGISTER) {
                        if (event.getPacket() != null && event.getPacket().response != null) {
                            Message msg = event.getPacket().response;
                            if (msg.getCode() == Common.REG_SMS_OK) {

                            } else {

                            }
                        }
                    }
                }
            } else if (mCurrentFragment instanceof FragmentOtherLogin) {
                if (event.getType() == ResponseType.LOGIN) {
                    if (event.getPacket() != null && event.getPacket().response != null) {
                        Message msg = event.getPacket().response;
                        dismissProgress();
                        if (msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                            isReceived();
                            savePassWord(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                        } else {
                            FingerIM.I.loginError();//登录错误解绑用户数据
                            if (msg.getCode() == Common.LOGIN_ACCOUNT_INEXIST) {
                                T.showShort(R.string.accout_inexist);
                            } else if (msg.getCode() == Common.LOGIN_VERYFY_ERROR) {
                                T.showShort(R.string.accout_or_psd_error);
                            } else if (msg.getCode() == Common.LOGIN_FORBIDDON_LOGIN) {
                                T.showShort(R.string.accout_was_banned);
                            }

                        }
                    }
                }

            }
        }
    }

        /*
     * 断网后，注册网络状态的广播接受者
     * */

    private void doRegisterNetReceiver(ENetStatus status) {
        if (status == ENetStatus.ERROR_NET) {
            if (mNetworkReceiver == null) {
                mNetworkReceiver = new NetworkReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(mNetworkReceiver, filter);
            }
        }
    }

    /*
    * 检测是否服务已经启动
    * */
    private void granted() {
        if (!FingerIM.I.hasStarted()) {
            initIMClient();
        } else if (!FingerIM.I.hasRunning()) {
            if (NetworkUtils.isNetAvaliale()) {
                FingerIM.I.startFingerIM();
            }
        } else if (!FingerIM.I.isConnected()) {
            FingerIM.I.onNetStateChange(true);

        }
    }

    private void initIMClient() {
        //公钥有服务端提供和私钥对应
        ClientConfig cc = ClientConfig.build()
            .setPublicKey(FGApplication.PUBLIC_KEY)
            .setServerAddress(FGApplication.TEST_SERVER)
//            .setServerAddress(FGApplication.LOCAL_SERVER)
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


    private void savePassWord(String psw) {
        if (!TextUtils.isEmpty(psw)) {
            PasswordRespository.setPassword(psw);
        }
    }

    private void startMainActivity() {
        //登录成功后加载群列表
        AppManager.getInstance().setLoginStatus(true);
        initDao();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        this.finish();
    }

    private void initDao() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        if (!TextUtils.isEmpty(userId)) {
            DaoManager.initUserId(userId);
            //保存密码
            PasswordRespository.setPassword(AppConfig.INSTANCE.get(AppConfig.PASSWORD));
        }
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
}