package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/9
 */
public class BindExcuteMessage extends BaseMessage {

    public ExcuteMessage message;

    private BindExcuteMessage(Command cmd, Connection connection) {
        super(new Packet(cmd, genSessionId()), connection);
    }

    public static BindExcuteMessage buildExcute(Connection connection) {
        return new BindExcuteMessage(Command.EXCUTE, connection);
    }


    @Override
    protected void decode(byte[] body) {
        try {
            message = ExcuteMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public BindExcuteMessage setExcuteMessage(ExcuteMessage bindMessage) {
        this.message = bindMessage;
        return this;
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
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
