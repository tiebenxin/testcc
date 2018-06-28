package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.User;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/11/9
 */
public class ConflictMessage extends BaseMessage {

    public User.loginConflict message;
    public ConflictMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            message = User.loginConflict.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return message.toByteArray();
    }

    public void setMessage(User.loginConflict message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "";
    }
}
