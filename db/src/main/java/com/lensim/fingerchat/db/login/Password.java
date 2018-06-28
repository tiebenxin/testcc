package com.lensim.fingerchat.db.login;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * date on 2018/1/19
 * author ll147996
 * describe
 */

@Entity
public class Password {

    @Id
    private long id;

    /**
     * 已加密密码
     */
    private String password;

    /**
     * 保存或更新密码的时间戳
     */
    private long time;

    /**
     * 已加密秘钥
     */
    private String secretkey;



    @Generated(hash = 1485877257)
    public Password(long id, String password, long time, String secretkey) {
        this.id = id;
        this.password = password;
        this.time = time;
        this.secretkey = secretkey;
    }

    @Generated(hash = 565943725)
    public Password() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSecretkey() {
        return this.secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

}
