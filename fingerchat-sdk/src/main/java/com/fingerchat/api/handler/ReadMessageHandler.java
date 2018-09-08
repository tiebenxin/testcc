package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.MessageAckMessage;
import com.fingerchat.api.message.MucChatMessage;
import com.fingerchat.api.message.ReadAckMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;

/**
 * Created by LL130386 on 2017/7/9.
 */

public class ReadMessageHandler extends BaseMessageHandler<ReadAckMessage> {

    Logger logger = ClientConfig.I.getLogger();

    @Override
    public ReadAckMessage decode(Packet packet, Connection connection) {
        return new ReadAckMessage(packet, connection);
    }

    @Override
    public void handle(ReadAckMessage message) {
        if (message == null || message.message == null) {
            return;
        }
        System.out.println("收到ReadAckMessage--" + message.message.getId());
        AckMessage ackMessage = AckMessage.from(message);
        BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
        builder.addId(message.message.getId());
        ackMessage.setAck(builder.build());
        ackMessage.sendRaw();

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onRead(message);
    }
}
