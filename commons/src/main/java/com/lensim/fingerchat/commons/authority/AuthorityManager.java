package com.lensim.fingerchat.commons.authority;


import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;

/**
 * Created by LY309313 on 2016/12/27.
 */

public class AuthorityManager {

    private String copyContent;

    private static final AuthorityManager instance;

    static {
        instance = new AuthorityManager();
    }

    public static AuthorityManager getInstance() {
        return instance;
    }

    private AuthorityManager() {

    }

    /**
     * 内复制
     */
    public void copy(String content) {
        L.i("AuthorityManager", "复制的什么:" + content);
        this.copyContent = content;
    }

    /**
     * 是否有复制权限
     */
    public boolean copyOutside() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        String authority = SPHelper.getString(userId + AppConfig.AUTHORITY_SETTED, "");
        L.i("AuthorityManager", "取到权限列表:" + authority);
        if (authority.equals("")) {
            return false;
        }
        if (authority.contains("001002")) {
            return true;
        }
        return true;
    }

    /**
     * 是否能够保存图片
     */
    public boolean copyPicOutsize() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        String authority = SPHelper.getString(userId + AppConfig.AUTHORITY_SETTED, "");
        L.i("AuthorityManager", "取到权限列表:" + authority);
        if (authority.equals("")) {
            return false;
        }
        if (authority.contains("001004")) {
            return true;
        }
        return true;
    }

    /**
     * 获取内复制的内容
     */
    public String getCopyContent() {
        return copyContent;
    }

    /**
     * 是否有内复制的权限
     */
    public boolean copyInside() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        String authority = SPHelper.getString(userId + AppConfig.AUTHORITY_SETTED, "");
        L.i("AuthorityManager", "取到权限列表:" + authority);
        if (authority.equals("")) {
            return false;
        }
        if (authority.contains("001001")) {
            return true;
        }
        return true;
    }

    /**
     * 是否有截图权限
     */
    public boolean screenShot() {
        String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
        String screenShot = SPHelper.getString(userId + AppConfig.AUTHORITY_SETTED, "");
        if (screenShot.equals("")) {
            return false;
        }
        if (screenShot.contains("003002")) {
            return true;
        }
        return true;
    }
}
