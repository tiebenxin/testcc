package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Resp;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/6
 */
public class RespMessage extends BaseMessage {

    public byte cmd;
    public Resp.Message response;

    public RespMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public RespMessage(byte cmd, Packet packet, Connection connection){
        super(packet,connection);
        this.cmd = cmd;
    }

    @Override
    protected void decode(byte[] body) {
        try {
            response = Resp.Message.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encode() {

        return response.toByteArray();
    }


    @Override
    public String toString() {
        return "RespMessage";
    }

}
