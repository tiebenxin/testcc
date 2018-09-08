package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.ReadAck;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by LL130386 on 2018/9/5.
 */
public class BindReadAckMessage extends BaseMessage {

    public ReadAck.ReadedMessageList bindMessage;

    private BindReadAckMessage(Command cmd, Connection connection) {
        super(new Packet(cmd, genSessionId()), connection);
    }

    public static BindReadAckMessage buildReaded(Connection connection) {
        return new BindReadAckMessage(Command.READED, connection);
    }


    @Override
    protected void decode(byte[] body) {
        try {
            bindMessage = ReadAck.ReadedMessageList.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public BindReadAckMessage setReadedMessageList(ReadAck.ReadedMessageList bindMessage) {
        this.bindMessage = bindMessage;
        return this;
    }

    @Override
    protected byte[] encode() {
        return bindMessage.toByteArray();
    }

    @Override
    public String toString() {
        return "";
//        return "BindUserMessage{" +
//                "alias='" + userMessage.getAlias() + '\'' +
//                ", userId='" + userMessage.getUserid() + '\'' +
//                ", packet=" + packet +
//                '}';
    }

}
