package com.lensim.fingerchat.data.login;

import android.support.annotation.NonNull;
import com.lensim.fingerchat.data.repository.SPDataRepository;

/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class UserInfoRepository {

    private static String USER_NULL = "";
    private UserInfo userInfo;
    private SPDataRepository<UserInfo> spDataRepository;


    private UserInfoRepository() {
        spDataRepository = new SPDataRepository<>();
    }

    public static UserInfoRepository getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        private static final UserInfoRepository INSTANCE = new UserInfoRepository();
    }


    public UserInfo getUserInfo() {
        if (userInfo != null) {
            return userInfo;
        } else {
            return spDataRepository.getData(UserInfo.class);
        }
    }

    public void setUserInfo(@NonNull UserInfo userInfo) {
        this.userInfo = userInfo;
        spDataRepository.saveData(userInfo);
    }

    public static String getUserName() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getUserid();
        } else {
            return USER_NULL;
        }
    }

    public static String getUsernick() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getUsernick();
        } else {
            return USER_NULL;
        }
    }

    public static String getUserId() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getUserid();
        }else {
            return USER_NULL;
        }
    }

    public static String getPhoneNumber() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getPhoneNumber();
        } else {
            return USER_NULL;
        }
    }

    public static String getWorkAddress() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getWorkAddress();
        } else {
            return USER_NULL;
        }
    }

    public static String getEmpName() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getEmpName();
        } else {
            return USER_NULL;
        }
    }

    public static String getSex() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getSex();
        } else {
            return USER_NULL;
        }
    }

    public static String getImage() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getImage();
        } else {
            return USER_NULL;
        }
    }

    public static int getIsvalid() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getIsvalid();
        } else {
            return 0;
        }
    }

    public static String getJobname() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getJobname();
        } else {
            return USER_NULL;
        }
    }

    public static String getDptNo() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getDptNo();
        } else {
            return USER_NULL;
        }
    }

    public static String getDptName() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getDptName();
        } else {
            return USER_NULL;
        }
    }

    public static String getEmpNo() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getEmpNo();
        } else {
            return USER_NULL;
        }
    }

    public static String getRight() {
        if (getInstance().getUserInfo() != null) {
            return getInstance().getUserInfo().getRight();
        } else {
            return USER_NULL;
        }
    }
}
