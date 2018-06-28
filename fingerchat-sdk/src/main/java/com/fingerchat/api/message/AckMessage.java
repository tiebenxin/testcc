package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/19
 */
public class AckMessage extends BaseMessage{

    public BaseChat.SysAck ack;
    public AckMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public AckMessage(Connection connection) {
        super(new Packet(Command.ACK), connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            ack = BaseChat.SysAck.parseFrom(body);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public static AckMessage from(BaseMessage message){
        return new AckMessage(new Packet(Command.ACK,message.getSessionId()),message.connection);
    }

    public AckMessage setAck(BaseChat.SysAck ack) {
        this.ack = ack;
        return this;
    }

    @Override
    protected byte[] encode() {
        return ack.toByteArray();
    }

    @Override
    public String toString() {
        return "";
    }
}
