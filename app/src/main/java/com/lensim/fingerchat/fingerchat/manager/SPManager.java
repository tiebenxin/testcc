package com.lensim.fingerchat.fingerchat.manager;

import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.cache.spf.Spf_Password;
import com.lensim.fingerchat.fingerchat.cache.spf.Spf_User;

/**
 * SharedPreferences管理器
 * Created by zm on 2018/5/10.
 */
public class SPManager {
    // 用户信息管理
    private static Spf_User mSpfUser;
    private static Spf_Password mSpfPassword; //密聊信息管理

    public static Spf_User getUserSpf() {
        if (mSpfUser == null) {
            mSpfUser = Spf_User.create(ContextHelper.getContext());
        }
        return mSpfUser;
    }

    public static Spf_Password getmSpfPassword(){
        if (null == mSpfPassword){
            mSpfPassword = Spf_Password.create(ContextHelper.getContext());
        }
        return mSpfPassword;
    }
}
