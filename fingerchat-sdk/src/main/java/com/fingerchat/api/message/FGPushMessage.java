package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/13
 */
public class FGPushMessage extends BaseMessage {

    public PushMessage message;

    public FGPushMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public FGPushMessage(Packet packet) {
        super(packet);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = PushMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    public void setMessage(PushMessage message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "";
    }


}
