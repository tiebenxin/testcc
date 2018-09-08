package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.FGPushMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;

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
        AckMessage ackMessage = AckMessage.from(message);
        BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
        builder.addId(message.message.getMessageId());
        ackMessage.setAck(builder.build());
        ackMessage.sendRaw();//发送回执
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);
    }
}
