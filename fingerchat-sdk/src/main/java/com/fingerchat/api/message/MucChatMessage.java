package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.MucChat;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/19
 */
public class MucChatMessage extends BaseMessage{

    public MucChat.RoomMessage message;

    public MucChatMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public MucChatMessage(Connection connection) {
        super(new Packet(Command.GROUP_CHAT,genSessionId()), connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = MucChat.RoomMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    public static MucChatMessage from(BaseMessage message){
        return new MucChatMessage(message.createResponse(),message.connection);
    }

    public MucChatMessage setMessage(MucChat.RoomMessage message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "message : " + message.getContent();
    }

}
