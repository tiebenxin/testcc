package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.User;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by LY309313 on 2017/12/12.
 */

public class UserInfoMessage extends BaseMessage {

    public User.UserInfo userInfo;

    public UserInfoMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            userInfo = User.UserInfo.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return userInfo.toByteArray();
    }
}
