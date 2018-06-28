package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.MessageAckMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;

/**
 * Created by LL130386 on 2018/4/16.
 * 系统消息二次回执
 */

public class MessageAckHandler extends BaseMessageHandler<MessageAckMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public MessageAckMessage decode(Packet packet, Connection connection) {
        return new MessageAckMessage(packet, connection);
    }

    @Override
    public void handle(MessageAckMessage message) {
        logger.w(">>> received MessageAckMessage : ", message.getPacket().cmd);
        if (message.message.getIdCount() > 0) {
            AckMessage ackMessage = AckMessage.from(message);
            BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
            builder.addId(message.message.getId(0));
            ackMessage.setAck(builder.build());
            ackMessage.sendRaw();
        }

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);
    }
}
