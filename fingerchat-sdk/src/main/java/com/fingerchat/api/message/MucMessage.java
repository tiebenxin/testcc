package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Muc;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/19
 */
public class MucMessage extends BaseMessage{

    public Muc.MucMessage message;

    public MucMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = Muc.MucMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }



    public MucMessage setMessage(Muc.MucMessage message) {
        this.message = message;
        return this;
    }



    @Override
    public String toString() {
        return "";
    }

}
