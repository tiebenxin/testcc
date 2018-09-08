package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.ReadAck;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by LL130386 on 2018/9/5.
 */
public class ReadAckMessage extends BaseMessage {

    public ReadAck.ReadedMessageList message;


    public ReadAckMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public ReadAckMessage(Packet packet) {
        super(packet);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = ReadAck.ReadedMessageList.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    public void setMessage(ReadAck.ReadedMessageList message) {
        this.message = message;
    }

    @Override
    public String toString() {
//        return "account = " +
//                message.getItem(0).getAccount() +
//                "username" +
//                message.getItem(0).getUsernick();
        return "";
    }


}
