package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.User;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/9
 */
public class BindUserMessage extends BaseMessage {

    public User.BindMessage bindMessage;

    private BindUserMessage(Command cmd, Connection connection) {
        super(new Packet(cmd, genSessionId()), connection);
    }

    public static BindUserMessage buildApplyVerCode(Connection connection) {
        return new BindUserMessage(Command.OB_VER_CODE, connection);
    }

    public static BindUserMessage buildRegister(Connection connection) {
        return new BindUserMessage(Command.REGISTER, connection);
    }

    public static BindUserMessage buildBind(Connection connection) {
        return new BindUserMessage(Command.LOGIN, connection);
    }

    public static BindUserMessage buildUnbind(Connection connection) {
        return new BindUserMessage(Command.LOGOUT, connection);
    }

    public static BindUserMessage updateUserInfo(Connection connection) {
        return new BindUserMessage(Command.USER_UPDATE, connection);
    }

    public static BindUserMessage changePassword(Connection connection) {
        return new BindUserMessage(Command.CHANGE_PASS, connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            bindMessage = User.BindMessage.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public BindUserMessage setUserMessage(User.BindMessage bindMessage) {
        this.bindMessage = bindMessage;
        return this;
    }

    @Override
    protected byte[] encode() {
        return bindMessage.toByteArray();
    }

    @Override
    public String toString() {
        return "";
//        return "BindUserMessage{" +
//                "alias='" + userMessage.getAlias() + '\'' +
//                ", userId='" + userMessage.getUserid() + '\'' +
//                ", packet=" + packet +
//                '}';
    }

}
