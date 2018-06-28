package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.proto.message.BaseChat;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.MucChat;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.fingerchat.proto.message.PrivateChat;
import com.fingerchat.proto.message.Roster;

import java.util.List;

/**
 * Created by LY309313 on 2017/9/28.
 */

public class OfflineMessageHandler extends BaseMessageHandler<OfflineMessage> {

    Logger logger = ClientConfig.I.getLogger();

    @Override
    public OfflineMessage decode(Packet packet, Connection connection) {
        return new OfflineMessage(packet, connection);
    }

    @Override
    public void handle(OfflineMessage message) {
        logger.w(" >>> receive offlineMessages message=%s", message);
        if (message == null || message.offlineMessage == null) {
            return;
        }
        AckMessage ackMessage = AckMessage.from(message);
        BaseChat.SysAck.Builder builder = BaseChat.SysAck.newBuilder();
        List<PrivateChat.PrivateMessage> privateMessageList = message.offlineMessage
            .getPrivateMessageList();
        for (PrivateChat.PrivateMessage privateMessage : privateMessageList) {
            builder.addId(privateMessage.getId());
        }
        List<MucChat.RoomMessage> roomMessageList = message.offlineMessage.getRoomMessageList();
        for (MucChat.RoomMessage roomMessage : roomMessageList) {
            builder.addId(roomMessage.getId());
        }
        List<Roster.RosterMessage> rosterMessageList = message.offlineMessage
            .getRosterMessageList();
        for (Roster.RosterMessage rosterMessage : rosterMessageList) {
            builder.addId(rosterMessage.getId());
        }
        List<MucAction> actionList = message.offlineMessage.getActionList();
        for (MucAction action : actionList) {
            builder.addId(action.getId());
        }
        List<PushMessage> pushMessageList = message.offlineMessage.getNotifyList();
        for (PushMessage push : pushMessageList) {
            builder.addId(push.getMessageId());
        }

        builder.addId(message.offlineMessage.getId() +"");//离线包ID

        ackMessage.setAck(builder.build());
        ackMessage.sendRaw();
        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onReceiveMessage(message);
    }
}
