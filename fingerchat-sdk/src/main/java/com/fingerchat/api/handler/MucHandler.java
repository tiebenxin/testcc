package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.MucListener;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.api.protocol.Packet;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/11/9.
 */

public class MucHandler extends BaseMessageHandler<MucMessage> {

    private final Logger logger = ClientConfig.I.getLogger();
    @Override
    public MucMessage decode(Packet packet, Connection connection) {
        return new MucMessage(packet,connection);
    }

    @Override
    public void handle(MucMessage message) {
        logger.w("received MucMessage ========>>>>>>" + message.message.getCode());
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);

        Collection<MucListener> mucListeners = ClientConfig.I.getFGlistener(MucListener.class);
        if(mucListeners!=null&&!mucListeners.isEmpty()){
            for (MucListener mucListener : mucListeners) {
                mucListener.onMuc(message);
            }
        }
    }
}
