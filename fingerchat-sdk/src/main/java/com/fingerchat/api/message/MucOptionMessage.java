package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Muc;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/18
 */
public class MucOptionMessage extends BaseMessage {

    public Muc.MucOption option;

    public MucOptionMessage(Connection connection) {
        super(new Packet(Command.MUC_OPTION,genSessionId()), connection);
    }

    @Override
    protected void decode(byte[] body) {
        try {
            option = Muc.MucOption.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {
        return option.toByteArray();
    }

    @Override
    public String toString() {
        return "";
    }
}
