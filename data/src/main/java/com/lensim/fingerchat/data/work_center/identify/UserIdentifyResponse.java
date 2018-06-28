package com.lensim.fingerchat.data.work_center.identify;

import com.lensim.fingerchat.data.repository.SPDataRepository;


/**
 * date on 2018/1/15
 * author ll147996
 * describe
 */

public class UserIdentifyResponse {


    private UserIdentify userIdentify;
    private SPDataRepository<UserIdentify> spDataRepository;

    private UserIdentifyResponse() {
        spDataRepository = new SPDataRepository<>();
    }

    public static UserIdentifyResponse getInstance(){
        return UserIdentifyResponse.Singleton.INSTANCE;
    }

    private static class Singleton{
        private static final UserIdentifyResponse INSTANCE = new UserIdentifyResponse();
    }

    public UserIdentify getUserIdentify() {
        if (userIdentify != null) {
            return userIdentify;
        } else {
            return spDataRepository.getData(UserIdentify.class);
        }

    }

    public void setUserIdentify(UserIdentify userIdentify) {
        this.userIdentify = userIdentify;
        spDataRepository.saveData(userIdentify);
    }

}
