package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.MucListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/11/9.
 */

public class MucActionHandler extends BaseMessageHandler<MucActionMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public MucActionMessage decode(Packet packet, Connection connection) {
        return new MucActionMessage(packet, connection);
    }

    @Override
    public void handle(MucActionMessage message) {
        logger.w("received MucActionMessage ========>>>>>>" + message.action.getMucid());

        AckMessage ackMessage = AckMessage.from(message);
        BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
        builder.addId(message.action.getId());
        ackMessage.setAck(builder.build());
        ackMessage.sendRaw();

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);

        Collection<MucListener> mucListeners = ClientConfig.I.getFGlistener(MucListener.class);
        if (mucListeners != null && !mucListeners.isEmpty()) {
            for (MucListener mucListener : mucListeners) {
                mucListener.onMucAction(message);
            }
        }
    }
}
