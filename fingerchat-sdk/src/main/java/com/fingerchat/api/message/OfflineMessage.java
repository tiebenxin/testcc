package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Offline;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/20
 */
public class OfflineMessage extends BaseMessage{

    public Offline.OfflineMessage offlineMessage;

    public OfflineMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            offlineMessage = Offline.OfflineMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return offlineMessage.toByteArray();
    }

    @Override
    public String toString() {
        return "";
//        return "private chat message: " + offlineMessage.getPrivateMessageList() +
//                "muc chat message: " + offlineMessage.getRoomMessageList() +
//                "roster message: " + offlineMessage.getRosterMessageList();
    }
}
