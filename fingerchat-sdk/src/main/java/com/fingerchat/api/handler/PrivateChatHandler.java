package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.ChatListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.PrivateChatMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/10/6.
 */

public class PrivateChatHandler extends BaseMessageHandler<PrivateChatMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public PrivateChatMessage decode(Packet packet, Connection connection) {
        return new PrivateChatMessage(packet,connection);
    }

    @Override
    public void handle(PrivateChatMessage message) {
        logger.w(">>> received PrivateChatMessage : ",message.message.getContent());
        AckMessage ackMessage = AckMessage.from(message);
        BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
        builder.addId(message.message.getId());
        ackMessage.setAck(builder.build());
        ackMessage.sendRaw();
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);

        Collection<ChatListener> chatListeners = ClientConfig.I.getFGlistener(ChatListener.class);
        if(chatListeners!=null&&!chatListeners.isEmpty()){
            for (ChatListener chatListener : chatListeners) {
                chatListener.onPrivateChat(message);
            }
        }
    }
}
