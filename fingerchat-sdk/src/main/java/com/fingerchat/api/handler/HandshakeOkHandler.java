package com.fingerchat.api.handler;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Logger;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.connection.SessionContext;
import com.fingerchat.api.connection.SessionStorage;
import com.fingerchat.api.message.HandshakeOkMessage;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.security.AesCipher;
import com.fingerchat.api.security.CipherBox;
import com.fingerchat.api.session.PersistentSession;

/**
 * Created by LY309313 on 2017/9/27.
 */

public final class HandshakeOkHandler extends BaseMessageHandler<HandshakeOkMessage> {

    private final Logger logger = ClientConfig.I.getLogger();
    @Override
    public HandshakeOkMessage decode(Packet packet, Connection connection) {
        return new HandshakeOkMessage(packet,connection);
    }

    @Override
    public void handle(HandshakeOkMessage message) {
        logger.w(">>> handshake ok message=%s", message);

        Connection connection = message.getConnection();
        SessionContext context = connection.getSessionContext();
        byte[] serverKey = message.handshakeOkMessage.getServerKey().toByteArray();
        if (serverKey.length != CipherBox.INSTANCE.getAesKeyLength()) {
            logger.w("handshake error serverKey invalid message=%s", message);
            connection.reconnect();
            return;
        }
        //设置心跳
        context.setHeartbeat(message.handshakeOkMessage.getHeartbeat());

        //更换密钥
        AesCipher cipher = (AesCipher) context.cipher;
        byte[] sessionKey = CipherBox.INSTANCE.mixKey(cipher.key, serverKey);
        context.changeCipher(new AesCipher(sessionKey, cipher.iv));

        //触发握手成功事件

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onHandshakeOk(connection.getClient(), message.handshakeOkMessage.getHeartbeat());

        //保存token
        saveToken(message, context);
    }


    private void saveToken(HandshakeOkMessage message, SessionContext context) {
        SessionStorage storage = ClientConfig.I.getSessionStorage();
        if (storage == null || message.handshakeOkMessage.getSessionId() == null) return;
        PersistentSession session = new PersistentSession();
        session.sessionId = message.handshakeOkMessage.getSessionId();
        session.expireTime =message.handshakeOkMessage.getExpireTime();
        session.cipher = context.cipher;
        storage.saveSession(PersistentSession.encode(session));
    }
}
