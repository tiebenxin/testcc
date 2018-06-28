package com.lensim.fingerchat.commons.global;

import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEnvironment;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.io.File;

/**
 * Created by LL130386 on 2017/12/19.
 */

public class FGEnvironment implements IEnvironment {

    private IChatUser user;
    private static IEnvironment environment;

    public FGEnvironment() {
    }

    public static IEnvironment getInstance() {
        if (environment == null) {
            environment = new FGEnvironment();
        }
        return environment;
    }

    public void initUserInfo(IChatUser user) {
        this.user = user;
    }


    @Override
    public IEnvironment getEnvironment() {
        return null;
    }

    @Override
    public String getUserId() {
        if (user == null) {
            return AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        }
        return user.getUserId();
    }

    @Override
    public String getAcodePath(String userId) {
        if (!StringUtils.isEmpty(userId)) {
            String QRcode = AppConfig.QR_CODE_PATH + File.separator + userId + ".qr";
            if (FileUtil.checkFilePathExists(QRcode)) {
                return QRcode;
            }
        }
        return "";
    }

    @Override
    public String getAvatarUrl() {
        if (user == null) {
            return "";
        }
        return user.getAvatarUrl();
    }

    @Override
    public String getUserNick() {
        if (user == null) {
            return "";
        }
        return user.getUserNick();
    }

}
