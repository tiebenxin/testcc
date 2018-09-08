package com.lensim.fingerchat.data.help_class;

import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.work_center.OATokenRepository;

/**
 * Created by LL130386 on 2018/8/3.
 * 判断token 是都有效
 */

public class TokenHelper {

    public static boolean isSSOTokenValid(String userId) {
        if (SSOTokenRepository.getInstance().getSSOToken() != null
            && SSOTokenRepository.getTokenValidTime(userId) > System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOATokenValid(String userId) {
        if (OATokenRepository.getInstance().getOAToken() != null &&
            OATokenRepository.getTokenValidTime(userId) > System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

}
