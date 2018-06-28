package com.fingerchat.api.handler;

import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.api.protocol.Packet;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/12/12.
 */

public class UserinfoHandler extends BaseMessageHandler<UserInfoMessage> {
    @Override
    public UserInfoMessage decode(Packet packet, Connection connection) {
        return new UserInfoMessage(packet,connection);
    }

    @Override
    public void handle(UserInfoMessage message) {
        System.out.println("接收到个人信息:" + message.userInfo.getUserid());

        Collection<UserListener> userListeners = ClientConfig.I.getFGlistener(UserListener.class);
        if(userListeners!=null&&!userListeners.isEmpty()){
            for (UserListener userListener : userListeners) {
                userListener.onReceivedUserinfo(message);
            }
        }
    }
}
