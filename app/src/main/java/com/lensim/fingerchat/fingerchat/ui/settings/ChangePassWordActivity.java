package com.lensim.fingerchat.fingerchat.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.fingerchat.R;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class ChangePassWordActivity extends BaseActivity {
    private EditText mOldpwd;
    private EditText mNewpwd;
    private EditText mCheckpwd;
    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_change_password);
        mOldpwd = findViewById(R.id.et_old_pwd);
        mNewpwd = findViewById(R.id.et_new_pwd);
        mCheckpwd = findViewById(R.id.et_check_pwd);
        //隐藏密码
        mOldpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        toolbar = findViewById(R.id.viewTitleBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("修改密码");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    public void submit(View view){
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

       HttpUtils.changePwd(UserInfoRepository.getUserId(), oldPwd, newPwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject>() {
                @Override
                public void onHandleSuccess(ResponseObject responseObject) {
                    if (responseObject.code == 10) {
                        T.show("修改密码成功");
                        PasswordRespository.setPassword(newPwd);
                        AppConfig.INSTANCE.set(AppConfig.PASSWORD, newPwd);
                        FingerIM.I.loginByPhone(AppConfig.INSTANCE.get(AppConfig.PHONE)
                            , AppConfig.INSTANCE.get(AppConfig.PASSWORD));
                    }else {
                        T.show(responseObject.msg);
                    }
                }

                @Override
                public void onNext(ResponseObject response) {
                    super.onNext(response);
                }
            });
    }

    /**
     * 获取旧密码输入
     *
     * @return
     */
    private String getOldPwdStr() {
        return mOldpwd.getText().toString().trim();
    }

    /**
     * 获取新密码输入
     *
     * @return
     */
    private String getNewPwdStr() {
        return mNewpwd.getText().toString().trim();
    }

    /**
     * 获取确定密码输入
     *
     * @return
     */
    private String getCheckPwdStr() {
        return mCheckpwd.getText().toString().trim();
    }

}
