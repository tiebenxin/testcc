package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LL130386
 * @create 2018/7/9
 */
public class ExcuteResultMessage extends BaseMessage{

    public ExcuteMessage message;

    public ExcuteResultMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = ExcuteMessage.parseFrom(body);
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
        return "";
    }
}
