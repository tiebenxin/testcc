package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat.MsgAck;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/18
 */
public class MessageAckMessage extends BaseMessage {


    public MsgAck message;

    public MessageAckMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }


    public MessageAckMessage(Connection connection) {
        super(new Packet(Command.MSG_ACK, genSessionId()), connection);
    }


    public static MessageAckMessage from(BaseMessage message) {
        return new MessageAckMessage(message.createResponse(), message.connection);
    }

    public MessageAckMessage setMessage(MsgAck message) {
        this.message = message;
        return this;
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = MsgAck.parseFrom(body);
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
        return "message : " + message.getId(0);
    }
}
