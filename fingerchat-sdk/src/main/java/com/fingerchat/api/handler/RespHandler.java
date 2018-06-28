package com.fingerchat.api.handler;

import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.Resp;

/**
 * Created by LY309313 on 2017/9/27.
 */

public class RespHandler extends BaseMessageHandler<RespMessage> {
    private final Logger logger = ClientConfig.I.getLogger();
    @Override
    public RespMessage decode(Packet packet, Connection connection) {
        return new RespMessage(packet,connection);
    }

    @Override
    public void handle(RespMessage message) {
        if(message.response.getType() == Resp.ResponseType.FASTCONNECT){
            ClientConfig.I.getSessionStorage().clearSession();
            message.getConnection().getClient().handshake();
        }/*else if(message.response.getType() == Resp.ResType.HANDSHAKE){
            message.getConnection().getClient().stop();
        }*//*else if(message.response.getType() == Resp.ResponseType.LOGIN){
            ClientConfig.I.getClientListener().onLogin(true, message.getConnection().getSessionContext().userid);
        }else if(m)*/

        else{
            ClientConfig.I.getClientListener().onResponse(message);
        }
    }
}
