package com.fingerchat.api;

import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.message.RespMessage;

/**
 * Created by LY309313 on 2017/9/23.
 */

public interface ClientListener {

    void onConnected(IMClient client);

    void onDisConnected(IMClient client);

    void onHandshakeOk(IMClient client, int heartbeat);

    //void onReceiveMessage(IMClient client, byte[] content, String messageId);
    void onReceiveMessage(BaseMessage message);

    void onResponse(RespMessage response);

    void onKickUser(String deviceId, String userId);

//    void onLogin(boolean success, String userId);
//
//    void onLogout(boolean success, String userId);
}
