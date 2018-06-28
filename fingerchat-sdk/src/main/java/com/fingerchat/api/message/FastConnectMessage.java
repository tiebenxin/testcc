package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.FastConnect;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/11
 */
public final class FastConnectMessage extends BaseMessage {

    public FastConnect.FastConnectMessage message;

    public FastConnectMessage(Connection connection) {
        super(new Packet(Command.FAST_CONNECT, genSessionId()), connection);
    }

    public FastConnectMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = FastConnect.FastConnectMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    public void setMessage(FastConnect.FastConnectMessage message) {
        this.message = message;
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    @Override
    public String toString() {
        return "FastConnectMessage{" +
                "deviceId='" + message.getDeviceid() + '\'' +
                ", sessionId='" + message.getSessionid() + '\'' +
                ", minHeartbeat=" + message.getMinHeartbeat() +
                ", maxHeartbeat=" + message.getMaxHeartbeat() +
                ", packet=" + packet +
                '}';
    }
}
