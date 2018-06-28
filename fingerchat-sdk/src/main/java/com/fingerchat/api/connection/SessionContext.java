package com.fingerchat.api.connection;

/**
 * Created by LY309313 on 2017/9/22.
 */

public final class SessionContext {


    public int heartbeat;
    public Cipher cipher;
    public String userid;
    public String password;

    public void changeCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public SessionContext setBindUser(String userid) {
        this.userid = userid;
        return this;
    }

    public SessionContext setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean handshakeOk() {
        return heartbeat > 0;
    }

    @Override
    public String toString() {
        return "SessionContext{" +
                "heartbeat=" + heartbeat +
                ", cipher=" + cipher +
                ", userid='" + userid + '\'' +
                '}';
    }

}
