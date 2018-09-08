package com.fingerchat.api.client;

import com.fingerchat.api.Logger;
import com.fingerchat.api.MessageHandler;
import com.fingerchat.api.PacketReceiver;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.handler.AckHandler;
import com.fingerchat.api.handler.ExcuteMessageHandler;
import com.fingerchat.api.handler.FGPushHandler;
import com.fingerchat.api.handler.FastConnectOkHandler;
import com.fingerchat.api.handler.HandshakeOkHandler;
import com.fingerchat.api.handler.HeartbeatHandler;
import com.fingerchat.api.handler.LoginConflictHandler;
import com.fingerchat.api.handler.MessageAckHandler;
import com.fingerchat.api.handler.MucActionHandler;
import com.fingerchat.api.handler.MucChatHandler;
import com.fingerchat.api.handler.MucHandler;
import com.fingerchat.api.handler.MucMemberHandler;
import com.fingerchat.api.handler.OfflineMessageHandler;
import com.fingerchat.api.handler.PrivateChatHandler;
import com.fingerchat.api.handler.ReadMessageHandler;
import com.fingerchat.api.handler.RespHandler;
import com.fingerchat.api.handler.RosterHandler;
import com.fingerchat.api.handler.UserinfoHandler;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.util.thread.ExecutorManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by LY309313 on 2017/9/26.
 */

public class MessageDispatcher implements PacketReceiver {

    private final Executor executor = ExecutorManager.INSTANCE.getDispatchThread();
    private final Map<Byte, MessageHandler> handlers = new HashMap<>();
    private final Logger logger = ClientConfig.I.getLogger();
    private final AckRequestMgr ackRequestMgr;

    public MessageDispatcher() {
        register(Command.HEARTBEAT, new HeartbeatHandler());
        register(Command.FAST_CONNECT, new FastConnectOkHandler());
        register(Command.HANDSHAKE, new HandshakeOkHandler());
        register(Command.CONFLICT, new LoginConflictHandler());
        register(Command.GROUP_CHAT, new MucChatHandler());
        register(Command.RESPONSE, new RespHandler());
        register(Command.OFFLINE, new OfflineMessageHandler());
        register(Command.PRIVATE_CHAT, new PrivateChatHandler());
        register(Command.MUC, new MucHandler());
        register(Command.MUC_ACTION, new MucActionHandler());
        register(Command.MUC_MEMBER, new MucMemberHandler());
        register(Command.ROSTER, new RosterHandler());
        register(Command.ACK, new AckHandler());
        register(Command.USER_INFO, new UserinfoHandler());
        register(Command.MSG_ACK, new MessageAckHandler());
        register(Command.FIRST_HELLO, new PrivateChatHandler());
        register(Command.NOTIFY, new FGPushHandler());
        register(Command.EXCUTE, new ExcuteMessageHandler());
        register(Command.READED, new ReadMessageHandler());
        this.ackRequestMgr = AckRequestMgr.I();
    }

    public void register(Command command, MessageHandler handler) {
        handlers.put(command.cmd, handler);
    }

    @Override
    public void onReceive(final Packet packet, final Connection connection) {
        final MessageHandler handler = handlers.get(packet.cmd);
        if (handler != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        doAckResponse(packet);
                        handler.handle(packet, connection);
                    } catch (Throwable throwable) {
                        logger.e(throwable, "handle message error, packet=%s", packet);
                        connection.reconnect();
                    }
                }
            });
        } else {
            logger.w("<<< receive unsupported message, packet=%s", packet);
            //connection.reconnect();
        }
    }

    private void doAckResponse(Packet packet) {
        AckRequestMgr.RequestTask task = ackRequestMgr.getAndRemove(packet.sessionId);
        if (task != null) {
            task.success(packet);
        }
    }
}
