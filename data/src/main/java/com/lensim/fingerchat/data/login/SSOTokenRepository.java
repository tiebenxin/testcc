package com.lensim.fingerchat.data.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.lensim.fingerchat.data.repository.SPDataRepository;


/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class SSOTokenRepository {

    private SSOToken ssoToken;
    private SPDataRepository<SSOToken> spDataRepository;

    private SSOTokenRepository() {
        spDataRepository = new SPDataRepository<>();
    }

    public static SSOTokenRepository getInstance() {
        return SSOTokenRepository.Singleton.INSTANCE;
    }

    private static class Singleton {

        private static final SSOTokenRepository INSTANCE = new SSOTokenRepository();
    }

    public SSOToken getSSOToken() {
        if (ssoToken != null) {
            return ssoToken;
        } else {
            return spDataRepository.getData(SSOToken.class);
        }
    }

    public void setSSOToken(@NonNull SSOToken ssoToken) {
        this.ssoToken = ssoToken;
        spDataRepository.saveData(ssoToken);
    }

    public static String getToken() {
        if (getInstance().getSSOToken() != null) {
            return getInstance().getSSOToken().getFxToken();
        } else {
            return "";
        }
    }

    public static String getUserName() {
        if (getInstance().getSSOToken() != null) {
            return getInstance().getSSOToken().getUserId();
        } else {
            return "";
        }
    }

    public static long getTokenValidTime(String userId) {
        if (getInstance().getSSOToken() != null && !TextUtils.isEmpty(userId) && userId
            .equalsIgnoreCase(getInstance().getUserId())) {
            return getInstance().getSSOToken().getTokenValidTime();
        } else {
            return 0;
        }
    }

    public String getUserId() {
        if (getInstance().getSSOToken() != null) {
            return getInstance().getSSOToken().getUserId();
        }
        return "";
    }


    public void clearSSOToken() {
        if (getInstance().getSSOToken() != null && spDataRepository != null) {
            spDataRepository.removeData(SSOToken.class);
        }
    }
}
