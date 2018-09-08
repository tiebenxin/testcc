package com.lensim.fingerchat.data.work_center;

import java.io.Serializable;

/**
 * date on 2017/12/27
 * author ll147996
 * describe
 */

public class OAToken implements Serializable {
    public String oaToken;
    public long lifetime;//有效时长 60*60*24 s

    String userId;

    public long tokenValidTime;//token有效的终止时间毫秒值

    public String getOaToken() {
        return oaToken;
    }

    public void setOaToken(String oaToken) {
        this.oaToken = oaToken;
    }

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    public long getTokenValidTime() {
        return tokenValidTime;
    }

    public void setTokenValidTime(long tokenValidTime) {
        this.tokenValidTime = tokenValidTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
