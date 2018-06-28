package com.fingerchat.api.handler;

import com.fingerchat.api.Logger;
import com.fingerchat.api.MessageHandler;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/23.
 */

public class HeartbeatHandler implements MessageHandler {
    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public void handle(Packet packet, Connection connection) {
        logger.d(">>> receive heartbeat pong...");
    }


}
