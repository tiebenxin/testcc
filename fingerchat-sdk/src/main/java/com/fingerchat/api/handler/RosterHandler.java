package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.RosterListener;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.api.protocol.Packet;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/11/9.
 */

public class RosterHandler extends BaseMessageHandler<RosterMessage> {

    private final Logger logger = ClientConfig.I.getLogger();
    @Override
    public RosterMessage decode(Packet packet, Connection connection) {
        return new RosterMessage(packet,connection);
    }

    @Override
    public void handle(RosterMessage message) {
        logger.w("received rostermessage ========>>>>>>" + message.message.getCode());
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);

        Collection<RosterListener> rosterListeners = ClientConfig.I.getFGlistener(RosterListener.class);
        if(rosterListeners!=null&&!rosterListeners.isEmpty()){
            for (RosterListener rosterListener : rosterListeners) {
                rosterListener.onReceivedRoster(message);
            }
        }
    }
}
