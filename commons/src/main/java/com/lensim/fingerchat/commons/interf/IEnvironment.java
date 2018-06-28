package com.lensim.fingerchat.commons.interf;

/**
 * Created by LL130386 on 2017/12/19.
 */

public interface IEnvironment {

    IEnvironment getEnvironment();


    void initUserInfo(IChatUser user);

    String getUserId();

    String getAcodePath(String userId);

    String getAvatarUrl();

    String getUserNick();

}
