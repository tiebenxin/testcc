package com.lensim.fingerchat.fingerchat.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.fingerchat.api.message.RespMessage;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.fingerchat.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class ChangePasswordActivity extends BaseActivity {

    private EditText mOldpwd;
    private EditText mNewpwd;
    private EditText mCheckpwd;
    private FGToolbar toolbar;
    private String userId;
    private String vertifyCode;
    private TextView tv_old_title;
    private int type;

    /*
    * @type 0修改密码，1表示忘记密码
    * */
    public static Intent newIntent(Context context, int type, String userId, String code) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("code", code);
        intent.putExtra("type", type);
        return intent;
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_change_password);
        tv_old_title = findViewById(R.id.tv_old_title);
        mOldpwd = findViewById(R.id.et_old_pwd);
        mNewpwd = findViewById(R.id.et_new_pwd);
        mCheckpwd = findViewById(R.id.et_check_pwd);
        //隐藏密码
        mOldpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        toolbar = findViewById(R.id.viewTitleBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("修改密码");
        initBackButton(toolbar, true);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        vertifyCode = intent.getStringExtra("code");
        type = intent.getIntExtra("type", 0);

        if (type == 1) {
            tv_old_title.setVisibility(View.GONE);
            mOldpwd.setVisibility(View.GONE);
        } else {
            tv_old_title.setVisibility(View.VISIBLE);
            mOldpwd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    public void submit(View view) {

        if (type == 1) {
            //忘记密码，重置
            resetPassword();
        } else {
            changePassword();
        }
    }

    private void resetPassword() {
        // 新密码
        String newPwd = getNewPwdStr();
        if (TextUtils.isEmpty(newPwd)) {
            T.show("新密码不能为空");
            return;
        } else if (StringUtils.isEmpty(newPwd)) {
            T.show("新密码不能包含非法字符");
            return;
        } else if (newPwd.length() < 6) {
            T.show("密码长度至少为6位数");
            return;
        }

        // 核准新密码
        String checkPwd = getCheckPwdStr();
        if (TextUtils.isEmpty(checkPwd)) {
            T.show("确定新密码不能为空");
            return;
        } else if (!newPwd.equals(checkPwd)) {
            T.show("两次输入不一致");
            return;
        }

        if (TextUtils.isEmpty(vertifyCode)) {
            T.show("验证码不能为空");
            return;
        }
        FingerIM.I.changePassword(userId, newPwd, vertifyCode, true);
    }

    /**
     * 获取旧密码输入
     */
    private String getOldPwdStr() {
        return mOldpwd.getText().toString().trim();
    }

    /**
     * 获取新密码输入
     */
    private String getNewPwdStr() {
        return mNewpwd.getText().toString().trim();
    }

    /**
     * 获取确定密码输入
     */
    private String getCheckPwdStr() {
        return mCheckpwd.getText().toString().trim();
    }

    private void changePassword() {
        // 旧密码
        String oldPwd = getOldPwdStr();
        if (TextUtils.isEmpty(oldPwd)) {
            T.show("旧密码不为空");
            return;
        } else if (!AppConfig.INSTANCE.get(AppConfig.PASSWORD).equals(oldPwd)) {
            T.show("旧密码输入错误");
            return;
        }

        // 新密码
        String newPwd = getNewPwdStr();
        if (TextUtils.isEmpty(newPwd)) {
            T.show("新密码不能为空");
            return;
        } else if (StringUtils.isEmpty(newPwd)) {
            T.show("新密码不能包含非法字符");
            return;
        } else if (newPwd.length() < 6) {
            T.show("密码长度至少为6位数");
            return;
        }

        // 核准新密码
        String checkPwd = getCheckPwdStr();
        if (TextUtils.isEmpty(checkPwd)) {
            T.show("确定新密码不能为空");
            return;
        } else if (!newPwd.equals(checkPwd)) {
            T.show("两次输入不一致");
            return;
        }
        FingerIM.I.changePassword(userId, oldPwd, newPwd, false);

//        HttpUtils.changePwd(UserInfoRepository.getUserId(), oldPwd, newPwd)
//            .compose(RxSchedulers.compose())
//            .subscribe(new FGObserver<ResponseObject>() {
//                @Override
//                public void onHandleSuccess(ResponseObject responseObject) {
//                    if (responseObject.code == 10) {
//                        T.show("修改密码成功");
//                        PasswordRespository.setPassword(newPwd);
//                        AppConfig.INSTANCE.set(AppConfig.PASSWORD, newPwd);
//                        FingerIM.I.login(userId, newPwd);
//                        setResult(RESULT_OK);
//                        finish();
//                    } else {
//                        T.show(responseObject.msg);
//                    }
//                }
//
//                @Override
//                public void onNext(ResponseObject response) {
//                    super.onNext(response);
//                }
//            });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithRequest(event);
        }
    }

    private void dealWithRequest(IEventProduct e) {
        if (e instanceof ResponseEvent) {
            ResponseEvent event = (ResponseEvent) e;
            RespMessage message = event.getPacket();
            int code = message.response.getCode();
            if (code == Common.UPDATE_SUCCESS) {//修改密码成功
                T.show("修改密码成功");
                setResult(RESULT_OK);
                finish();
            } else if (code == Common.PASSWORD_LOW_INTENSITY) {
                T.show("密码强度不够，必须包含8-16位数字和大小写字母");
            } else if (code == Common.DUPLICATE_PASSWORD_IN5TIMES) {
                T.show("密码强度不够，必须包含8-16位数字和大小写字母");
            } else if (code == Common.DUPLICATE_PASSWORD_IN5TIMES) {
                T.show("不能使用最近5次已使用过的密码");
            } else if (code == Common.UPDATE_FAILURE) {
                T.show("修改密码失败");
            }
        }
    }

}
