package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.PrivateChat;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/18
 */
public class PrivateChatMessage extends BaseMessage {


    public PrivateChat.PrivateMessage message;

    public PrivateChatMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }


    public PrivateChatMessage(Connection connection) {
        super(new Packet(Command.PRIVATE_CHAT,genSessionId()),connection);
    }


    public static PrivateChatMessage from(BaseMessage message){
        return new PrivateChatMessage(message.createResponse(),message.connection);
    }

    public PrivateChatMessage setMessage(PrivateChat.PrivateMessage message) {
        this.message = message;
        return this;
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = PrivateChat.PrivateMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    @Override
    public String toString() {
        return "message : " + message.getContent();
    }
}
