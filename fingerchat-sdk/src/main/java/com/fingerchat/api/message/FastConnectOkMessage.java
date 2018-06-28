package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.FastConnect;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/11
 */
public class FastConnectOkMessage extends BaseMessage {

    public FastConnect.FastConnectOkMessage message;
    public FastConnectOkMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    public static FastConnectOkMessage from(BaseMessage src) {
        return new FastConnectOkMessage(src.createResponse(), src.connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = FastConnect.FastConnectOkMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    public FastConnectOkMessage setMessage(FastConnect.FastConnectOkMessage message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "FastConnectOkMessage{" +
                "heartbeat=" + message.getMinHeartbeat() +
                ", packet=" + packet +
                '}';
    }
}
