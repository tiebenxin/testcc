package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.HandShake;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/7
 */
public class HandshakeMessage extends BaseMessage {

    public HandShake.HandShakeMessage handShakeMessage;

    public HandshakeMessage(Connection connection) {
        super(new Packet(Command.HANDSHAKE, genSessionId()), connection);
    }

    public HandshakeMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            handShakeMessage = HandShake.HandShakeMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return handShakeMessage.toByteArray();
    }

    public void setHandShakeMessage(HandShake.HandShakeMessage handShakeMessage) {
        this.handShakeMessage = handShakeMessage;
    }

    @Override
    public String toString() {
        return "";
//        return "HandshakeMessage{" +
//                "clientKey=" + handShakeMessage.getClientKey() +
//                ", deviceId='" + handShakeMessage.getDeviceId() + '\'' +
//                ", osName='" + handShakeMessage.getOsName() + '\'' +
//                ", osVersion='" + handShakeMessage.getOsVersion() + '\'' +
//                ", clientVersion='" + handShakeMessage.getClientVersion() + '\'' +
//                ", iv=" + handShakeMessage.getIv() +
//                ", minHeartbeat=" + handShakeMessage.getMinHeartbeat() +
//                ", maxHeartbeat=" + handShakeMessage.getMaxHeartbeat() +
//                ", timestamp=" + handShakeMessage.getTimestamp() +
//                ", packet=" + packet +
//                '}';
    }
}
