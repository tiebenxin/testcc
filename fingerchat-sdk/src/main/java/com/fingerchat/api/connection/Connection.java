package com.fingerchat.api.connection;

import com.fingerchat.api.IMClient;
import com.fingerchat.api.protocol.Packet;

import java.nio.channels.SocketChannel;

/**
 * Created by LY309313 on 2017/9/22.
 */

public interface Connection {



    void connect();

    SessionContext getSessionContext();

    void send(Packet packet);

    void close();

    boolean isConnected();

    void reconnect();

    boolean isReadTimeout();

    boolean isWriteTimeout();

    void setLastReadTime();

    void setLastWriteTime();

    long getLastReadTime();

    long getLastWriteTime();

    void resetTimeout();

    boolean isAutoConnect();

    SocketChannel getChannel();

    IMClient getClient();


}
