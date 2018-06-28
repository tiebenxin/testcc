package com.fingerchat.api.handler;

import com.fingerchat.api.Logger;
import com.fingerchat.api.client.AckRequestMgr;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.protocol.Packet;

import java.util.Collection;

/**
 * Created by LY309313 on 2017/9/27.
 */

public class AckHandler extends BaseMessageHandler<AckMessage> {

    private final Logger logger;
    private final AckRequestMgr ackRequestMgr;

    public AckHandler() {
        this.logger = ClientConfig.I.getLogger();
        this.ackRequestMgr = AckRequestMgr.I();
    }
    @Override
    public AckMessage decode(Packet packet, Connection connection) {
        return new AckMessage(packet,connection);
    }

    @Override
    public void handle(AckMessage message) {
//        ProtocolStringList idList = message.ack.getIdList();
//        if(idList!=null && !idList.isEmpty())
//        for (String id : idList) {
//            AckRequestMgr.RequestTask task = ackRequestMgr.getAndRemove(id);
//            if (task != null) {
//                task.success(message.getPacket());
//            }
//        }


        Collection<AckListener> ackListeners = ClientConfig.I.getFGlistener(AckListener.class);
        if(ackListeners!=null&&!ackListeners.isEmpty()){
            for (AckListener ackListener : ackListeners) {
                ackListener.onAck(message);
            }
        }
    }
}
