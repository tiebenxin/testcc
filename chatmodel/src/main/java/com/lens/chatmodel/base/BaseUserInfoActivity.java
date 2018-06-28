package com.lens.chatmodel.base;

import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;

/**
 * Created by LL130386 on 2018/1/31.
 * 可以便捷获取登录账号用户信息
 */

public class BaseUserInfoActivity extends FGActivity {

    private UserInfo mUserInfo;

    @Override
    public void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();

    }

    private void initUserInfo() {
        mUserInfo = UserInfoRepository.getInstance().getUserInfo();
    }

    public UserInfo getUserInfo() {
        if (mUserInfo == null) {
            initUserInfo();
        }
        return mUserInfo;
    }

    public String getUserId() {
        return getUserInfo().getUserid();
    }

    public String getUserNick() {
        return getUserInfo().getUsernick();
    }

    public String getUserAvatar() {
        return getUserInfo().getImage();
    }
}
