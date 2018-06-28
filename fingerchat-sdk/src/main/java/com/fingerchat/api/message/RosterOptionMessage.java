package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Roster;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/12
 */
public class RosterOptionMessage extends BaseMessage {

    public Roster.RosterOption option;
    public RosterOptionMessage(Packet packet, Connection connection) {
        super(new Packet(Command.ROSTER_OPTION,genSessionId()), connection);
    }

    public RosterOptionMessage(Packet packet){
        super(packet);
    }
    @Override
    protected void decode(byte[] body) {
        try {
            option = Roster.RosterOption.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void setOption(Roster.RosterOption option) {
        this.option = option;
    }

    @Override
    protected byte[] encode() {
        return option.toByteArray();
    }

    @Override
    public String toString() {
        return "roster option message";
    }
}
