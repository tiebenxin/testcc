package com.fingerchat.api.handler;


import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.FastConnectOkMessage;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/27.
 */

public final class FastConnectOkHandler extends BaseMessageHandler<FastConnectOkMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public FastConnectOkMessage decode(Packet packet, Connection connection) {
        return new FastConnectOkMessage(packet,connection);
    }

    @Override
    public void handle(FastConnectOkMessage message) {
        logger.w(">>> fast connect ok, message=%s", message);
        message.getConnection().getSessionContext().setHeartbeat(message.message.getMinHeartbeat());
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onHandshakeOk(message.getConnection().getClient(), message.message.getMinHeartbeat());
    }
}
