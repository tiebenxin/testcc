package com.fingerchat.api.handler;

import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.ConflictListener;
import com.fingerchat.api.message.ConflictMessage;
import com.fingerchat.api.protocol.Packet;
import java.util.Collection;

/**
 * Created by LY309313 on 2017/11/10.
 */

public class LoginConflictHandler extends BaseMessageHandler<ConflictMessage> {

    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public ConflictMessage decode(Packet packet, Connection connection) {
        return new ConflictMessage(packet, connection);
    }

    @Override
    public void handle(ConflictMessage message) {
        System.out.println(LoginConflictHandler.class.getSimpleName() + "--LoginConflict");
        Collection<ConflictListener> conflictListeners = ClientConfig.I
            .getFGlistener(ConflictListener.class);
        if (conflictListeners != null && !conflictListeners.isEmpty()) {
            for (ConflictListener conflictListener : conflictListeners) {
                conflictListener.onReceivedConflictListener(message);
                System.out.println(LoginConflictHandler.class.getSimpleName() + "--onReceivedConflictListener");

            }
        }
    }
}
