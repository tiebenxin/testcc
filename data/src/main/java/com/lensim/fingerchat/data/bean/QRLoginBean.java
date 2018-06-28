package com.lensim.fingerchat.data.bean;

/**
 * Created by LL130386 on 2018/5/23.
 */

public class QRLoginBean {

    String appid;
    String appType;
    String appName;
    String homeUrl;
    String logoutUrl;
    String appToken;
    String appState;
    String qrtcodeId;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public String getQrtcodeId() {
        return qrtcodeId;
    }

    public void setQrtcodeId(String qrtcodeId) {
        this.qrtcodeId = qrtcodeId;
    }
}
