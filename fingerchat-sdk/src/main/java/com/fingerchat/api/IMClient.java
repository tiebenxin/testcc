package com.fingerchat.api;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.FingerProtocol;

/**
 * Created by LY309313 on 2017/9/23.
 */

public interface IMClient extends FingerProtocol {

    void start();

    void stop();

    void destroy();

    boolean isRunning();

    boolean isLogin();

    void onNetStateChange(boolean isConnected);


    Connection getConnection();


}
