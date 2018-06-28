package com.lensim.fingerchat.fingerchat.manager;

import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.cache.spf.Spf_User;

/**
 * SharedPreferences管理器
 * Created by zm on 2018/5/10.
 */
public class SPManager {
    // 用户信息管理
    private static Spf_User mSpfUser;

    public static Spf_User getUserSpf() {
        if (mSpfUser == null) {
            mSpfUser = Spf_User.create(ContextHelper.getContext());
        }
        return mSpfUser;
    }
}
