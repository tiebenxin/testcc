package com.lensim.fingerchat.data.login;

import java.io.Serializable;

/**
 * date on 2017/12/27
 * author ll147996
 * describe
 */

public class SSOToken implements Serializable {

    /**
     * userid : zzr
     * client : android
     * appid :
     * fxToken :
     * tokenValidTime : 86400
     */

    private String userid;
    private String client;
    private String appid;
    private String fxToken;
    private long lifetime;//有效时长 60*60*24 s
    private long tokenValidTime;//token有效的终止时间毫秒值

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getFxToken() {
        return fxToken;
    }

    public void setFxToken(String fxToken) {
        this.fxToken = fxToken;
    }

    public long getTokenValidTime() {
        return tokenValidTime;
    }

    public void setTokenValidTime(long tokenValidTime) {
        this.tokenValidTime = tokenValidTime;
    }

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }
}
