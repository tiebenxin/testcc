package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.HandShake;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/7
 */
public class HandshakeOkMessage extends BaseMessage {

    public HandShake.HandshakeOkMessage handshakeOkMessage;
    public HandshakeOkMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            handshakeOkMessage = HandShake.HandshakeOkMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return handshakeOkMessage.toByteArray();
    }
    public static HandshakeOkMessage from(BaseMessage src) {
        return new HandshakeOkMessage(src.createResponse(), src.connection);
    }

    public HandshakeOkMessage setHandshakeOkMessage(HandShake.HandshakeOkMessage handshakeOkMessage) {
        this.handshakeOkMessage = handshakeOkMessage;
        return this;
    }

    @Override
    public String toString() {
        return "HandshakeOkMessage{" +
                "expireTime=" + handshakeOkMessage.getExpireTime() +
                ", serverKey=" +handshakeOkMessage.getServerKey() +
                ", heartbeat=" + handshakeOkMessage.getHeartbeat() +
                ", sessionId='" + handshakeOkMessage.getSessionId() + '\'' +
                ", packet=" + packet +
                '}';
    }
}
