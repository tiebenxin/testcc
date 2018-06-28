package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Muc;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/19
 */
public class MucActionMessage extends BaseMessage{

    public Muc.MucAction action;
    public MucActionMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            action = Muc.MucAction.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public static MucActionMessage from(BaseMessage message){
        return new MucActionMessage(message.createResponse(),message.connection);
    }

    @Override
    protected byte[] encode() {
        return action.toByteArray();
    }

    @Override
    public String toString() {
        return "";
    }

}
