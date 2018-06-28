package com.fingerchat.api;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/23.
 */
public interface PacketReceiver {

    void onReceive(Packet packet, Connection connection);
}
