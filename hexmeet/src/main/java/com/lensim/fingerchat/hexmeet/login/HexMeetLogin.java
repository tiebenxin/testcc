package com.lensim.fingerchat.hexmeet.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.data.hexmeet.LockUser;
import com.lensim.fingerchat.data.hexmeet.User;
import com.lensim.fingerchat.data.hexmeet.UserRepository;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.hexmeet.LockUser.DataBean;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.activity.HexMeetListActivity;
import com.lensim.fingerchat.hexmeet.utils.ProgressUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.List;
import java.util.Locale;
import io.reactivex.functions.Consumer;

/**
 * Created by LL117394 on 2017/11/04
 */
public class HexMeetLogin {

    private final String RCM_SERVER = "antkk.fingersystem.cn";
    private Context mContext;
    private User user;
    private String mMeetingID = "";
    private ProgressUtil progress;
    private HandlerLogin handlerLogin = new HandlerLogin();

    public HexMeetLogin(Context context) {
        this.mContext = context;
        ImageLoader.getInstance()
            .init(ImageLoaderConfiguration.createDefault(ContextHelper.getContext()));
        if (mContext instanceof Activity) {
            progress = new ProgressUtil((Activity) mContext, 10000, new Runnable() {
                @Override
                public void run() {
                }
            }, mContext.getString(R.string.refreshing));
        }
    }

    public void toHexMeetLogin() {
        if (mContext instanceof Activity) {
            new RxPermissions((Activity) mContext)
                .request(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (checkLogin()) {
                            openActivity(mContext, mMeetingID);
                        } else {
                            applyForCameras();
                        }
                    }
                });
        }
    }


    private void openActivity(Context context, String meetingID) {
        Intent intent = new Intent(context, HexMeetListActivity.class);
        intent.putExtra(HexMeetListActivity.MEETING_ID, meetingID);
        context.startActivity(intent);
    }

    /**
     * 登录10分钟内有效
     */
    public boolean checkLogin() {
        if (null != UserRepository.getInstance().getUser()
            && UserRepository.getInstance().getUser().isSuccess
            && System.currentTimeMillis() < UserRepository.getInstance().getUser().tiem + 10 * 60 * 1000) {
            return true;
        }
        return false;
    }


    /**
     * 申请视频会议帐号
     */
    public void applyForCameras() {
        if (progress != null) progress.show();
        Http.applyForCameras(1)
            .compose(RxSchedulers.<LockUser>compose())
            .subscribe(new BaseObserver<LockUser>() {
                @Override
                public void onNext(LockUser lockUser) {
                    if (lockUser.getResultCode() == 1) {
                        List<DataBean> mApplyedAccounts = lockUser.getData();
                        if (!mApplyedAccounts.isEmpty() && mApplyedAccounts.size() == 1) {
                            RuntimeData.setRcmServer(RCM_SERVER);
                            user = new User();
                            user.userCode = mApplyedAccounts.get(0).getUserCode();
                            user.userID = Integer.parseInt(mApplyedAccounts.get(0).getId());
                            LoginService.getInstance().login(user.userCode, "1234", handlerLogin);
                        }
                    } else {
                        Utils.showToast(mContext, lockUser.getErrMsg());
                    }
                }
            });
    }



    public void toHexMeetFromChat(String meetingID) {
        mMeetingID = meetingID;
        toHexMeetLogin();
    }


    class HandlerLogin extends Handler {
        @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progress != null) progress.dismiss();

            if (msg.what == 0) {
                setUser();
                openActivity(mContext, mMeetingID);
            } else if (msg.what == -1 && null != (String) msg.obj) {
                handlerErrorMsg((String) msg.obj);
            }
        }
    }

    private void setUser() {
        user.isSuccess = true;
        user.tiem = System.currentTimeMillis();
        UserRepository.getInstance().setUser(user);
    }

    private void handlerErrorMsg(String errorMsg) {
        if (!errorMsg.toLowerCase().contains("failed to connect to")) {
            String lower = errorMsg.toLowerCase(Locale.US);
            if (errorMsg.contains(mContext.getString(R.string.invalid_username_or_password))) {
                errorMsg = mContext.getString(R.string.error_account_or_password);
                Utils.showToastWithCustomLayout(mContext, errorMsg);
            } else if (lower.contains("handshake timed out")) {
                Utils.showToastWithCustomLayout(mContext,
                    mContext.getString(R.string.server_unavailable));
            } else if (lower.contains("timeout")) {
                Utils.showToastWithCustomLayout(mContext,
                    mContext.getString(R.string.server_unavailable));
            } else if (lower.contains("unable to resolve host")) {
                Utils.showToastWithCustomLayout(mContext,
                    mContext.getString(R.string.invalid_server_address));
            } else {
                Utils.showToastWithCustomLayout(mContext, errorMsg);
            }
        }
    }

}
