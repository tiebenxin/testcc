package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.ExcuteResultMessage;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LL130386 on 2017/7/9.
 */

public class ExcuteMessageHandler extends BaseMessageHandler<ExcuteResultMessage> {

    Logger logger = ClientConfig.I.getLogger();

    @Override
    public ExcuteResultMessage decode(Packet packet, Connection connection) {
        return new ExcuteResultMessage(packet, connection);
    }

    @Override
    public void handle(ExcuteResultMessage message) {
        logger.w(" >>> receive offlineMessages message=%s", message);
        if (message == null || message.message == null) {
            return;
        }
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onExcute(message);
    }
}
