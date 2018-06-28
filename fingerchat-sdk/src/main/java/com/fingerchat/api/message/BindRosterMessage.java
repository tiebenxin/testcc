package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Roster;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/9
 */
public class BindRosterMessage extends BaseMessage {

    public Roster.RosterOption rosterOption;

    private BindRosterMessage(Command cmd, Connection connection) {
        super(new Packet(cmd, genSessionId()), connection);
    }


    public static BindRosterMessage buildOption(Connection connection) {
        return new BindRosterMessage(Command.ROSTER_OPTION, connection);
    }


    @Override
    protected void decode(byte[] body) {
        try {
            rosterOption = Roster.RosterOption.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public BindRosterMessage setRosterMessage(Roster.RosterOption roster) {
        this.rosterOption = roster;
        return this;
    }

    @Override
    protected byte[] encode() {
        return rosterOption.toByteArray();
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
