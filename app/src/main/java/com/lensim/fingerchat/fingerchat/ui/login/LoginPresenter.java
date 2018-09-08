package com.lensim.fingerchat.fingerchat.ui.login;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.User;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.core.componet.log.DLog;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.BuildInfo;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.global.CommonEnum.ELogLevel;
import com.lensim.fingerchat.commons.global.CommonEnum.ELogType;
import com.lensim.fingerchat.commons.global.FGEnvironment;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.DeviceUtils;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.TokenHelper;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.db.DaoManager;
import com.lensim.fingerchat.fingerchat.api.SystemApi;
import com.lensim.fingerchat.fingerchat.model.bean.BaseRequestBody;
import java.util.Date;
import java.util.HashMap;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class LoginPresenter extends LoginContract.Presenter<LoginContract.View> implements
    UserListener {

    //登录超时时间
    private final static int OUT_TIME = 8 * 1000;
    private final static int TIME = 0x01;

    private boolean isOtherLogin = false;
    private boolean startTime = false;
    private String accout, password;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIME) {
                accout = null;
                password = null;
                startTime = false;
                if (getMvpView() != null) {
                    getMvpView().loginOutTime();
                }
            }
        }
    };

    @Override
    public void onAttachMvpView(LoginContract.View mvpView) {
        super.onAttachMvpView(mvpView);
        ClientConfig.I.registerListener(UserListener.class, this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachMvpView() {
        super.onDetachMvpView();
        mHandler.removeMessages(TIME);
        ClientConfig.I.removeListener(UserListener.class, this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onReceivedUserinfo(UserInfoMessage message) {
        System.out.println(LoginPresenter.class.getSimpleName() + "--onReceivedUserinfo");
        UserInfo info = getUserInfo(message.userInfo);
        if (info != null) {
            checkDaoInit(info);
            UserInfoRepository.getInstance().setUserInfo(info);
            if (!TextUtils.isEmpty(DaoManager.getUserID())) {
                ProviderUser.updateUser(ContextHelper.getContext(), info);
            }
            FGEnvironment.getInstance().initUserInfo(RosterManager.getInstance().createUser(info));
//            ssoLogin(info.getUserid(), AppConfig.INSTANCE.get(AppConfig.PASSWORD));
//            uploadLoginLog();
        }

        if (getMvpView() != null) {
            getMvpView().onReceivedUserinfo(message);
        }
    }

    private void checkDaoInit(UserInfo info) {
        String userId = info.getUserid();
        if (!TextUtils.isEmpty(userId)) {
            DaoManager.initUserId(userId);
        }
    }

    private UserInfo getUserInfo(User.UserInfo userInfo) {
        return new UserInfo(userInfo.getUserid(), userInfo.getUsernick(), userInfo.getPhoneNumber(),
            userInfo.getWorkAddress(),
            userInfo.getEmpName(), userInfo.getSex(), userInfo.getAvatar(), userInfo.getIsvalid(),
            userInfo.getJobname(),
            userInfo.getDptNo(), userInfo.getDptName(), userInfo.getEmpNo(), userInfo.getRight());
    }

    @Override
    public void login(String accout, String password, boolean isOther) {
        this.isOtherLogin = isOther;
        this.accout = accout;
        this.password = password;
        startTime = true;
        mHandler.sendEmptyMessageDelayed(TIME, OUT_TIME);
        login();
    }


    private void login() {
        if (TextUtils.isEmpty(accout) || TextUtils.isEmpty(password)) {
            return;
        }

        if (!FingerIM.I.hasStarted() && getMvpView() != null) {
            getMvpView().initIMClient();
        } else if (!FingerIM.I.isClientState()) {
            FingerIM.I.startFingerIM();
        } else if (!FingerIM.I.isConnected()) {
            FingerIM.I.manualReconnect();
        } else if (FingerIM.I.isHandOk() && startTime) {
            mHandler.removeMessages(TIME);
            if (isOtherLogin) {
                FingerIM.I.loginByPhone(accout, password);
            } else {
                FingerIM.I.login(accout, password);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onENetStatus(NetStatusEvent event) {
        //SUCCESS_ON_SERVICE(1),//握手成功
        if (event.getStatus() == ENetStatus.SUCCESS_ON_SERVICE) {
            login();
        }
    }


    @Override
    public void NetworkListener(NetStatusEvent event) {
        //网络连接正常
        if (event.getStatus() == ENetStatus.SUCCESS_ON_NET) {
            login();
        }
    }

    //客户端暂不上传登录日志，由服务端上传
    private void uploadLoginLog() {
        try {
            JSONObject object = new JSONObject();
            object.put("userId", UserInfoRepository.getUserName());
            object.put("userName", UserInfoRepository.getEmpName());
            object.put("logAppVer", BuildInfo.VERSION_NAME + "_" + BuildInfo.VERSION_CODE);
            object.put("logSysVer", Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);
            object.put("logMobileType", Build.MODEL);
            object.put("udid", TDevice.getIMEI());
            object.put("logIp", DeviceUtils.getIPAddress(true));
            object.put("logTime", StringUtils.formatDateTime(new Date()));
            HttpUtils.getInstance()
                .uploadLogger(object.toString(), ELogType.LOGIN, new IDataRequestListener() {
                    @Override
                    public void loadFailure(String reason) {
                        System.out.println(reason);
                        L.d(reason);
                    }

                    @Override
                    public void loadSuccess(Object object) {
                        L.d("登录日志上传成功");
                    }
                });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单点登录 拿到Token，存SP，Main界面在检查是否过期
     * 返回code ，12-ok，21-账号密码有误，30-服务器错误
     **/
    private void ssoLogin(final String userId, final String pwd) {
        HttpUtils.getInstance().ssoLogin(userId, pwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<SSOToken> response) {
                    if (response.code == 10) {
                        SSOToken token = response.result;
                        if (token != null) {
                            token.setUserId(userId);
                            token.setTokenValidTime(
                                System.currentTimeMillis() + token.getLifetime() * 1000);
                            SSOTokenRepository.getInstance().setSSOToken(token);
                        }
                    }
                }
            });
    }
}
