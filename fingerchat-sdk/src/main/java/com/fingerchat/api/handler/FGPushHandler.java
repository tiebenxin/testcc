package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.RosterListener;
import com.fingerchat.api.message.FGPushMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.api.protocol.Packet;
import java.util.Collection;

/**
 * Created by LY309313 on 2017/11/9.
 * FG小秘书推送消息
 */

public class FGPushHandler extends BaseMessageHandler<FGPushMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public FGPushMessage decode(Packet packet, Connection connection) {
        return new FGPushMessage(packet, connection);
    }

    @Override
    public void handle(FGPushMessage message) {
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);
    }
}
