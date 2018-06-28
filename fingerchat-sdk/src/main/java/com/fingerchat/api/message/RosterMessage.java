package com.fingerchat.api.message;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Roster;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author LY309313
 * @create 2017/9/13
 */
public class RosterMessage extends BaseMessage {

  public Roster.RosterMessage message;


  public RosterMessage(Packet packet, Connection connection) {
    super(packet, connection);
  }

  public RosterMessage(Packet packet) {
    super(packet);
  }

  @Override
  protected void decode(byte[] body) {
    try {
      message = Roster.RosterMessage.parseFrom(body);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected byte[] encode() {
    return message.toByteArray();
  }

  public void setMessage(Roster.RosterMessage message) {
    this.message = message;
  }

  @Override
  public String toString() {
//        return "account = " +
//                message.getItem(0).getAccount() +
//                "username" +
//                message.getItem(0).getUsernick();
    return "";
  }


}
