package com.fingerchat.api;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/22.
 */

public interface IMessage {

    Connection getConnection();

    void setConnection(Connection connection);

    void decodeBody();

    void encodeBody();

    void send();

    void sendRaw();

    Packet getPacket();
}
