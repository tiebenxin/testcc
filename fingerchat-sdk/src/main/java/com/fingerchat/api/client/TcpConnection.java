package com.fingerchat.api.client;


import static com.fingerchat.api.Constants.MAX_RESTART_COUNT;
import static com.fingerchat.api.client.TcpConnection.State.connected;
import static com.fingerchat.api.client.TcpConnection.State.connecting;
import static com.fingerchat.api.client.TcpConnection.State.disconnected;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Constants;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.Logger;
import com.fingerchat.api.PacketReader;
import com.fingerchat.api.PacketReceiver;
import com.fingerchat.api.PacketWriter;
import com.fingerchat.api.codec.AsyncPacketReader;
import com.fingerchat.api.codec.AsyncPacketWriter;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.connection.SessionContext;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.util.IOUtils;
import com.fingerchat.api.util.Strings;
import com.fingerchat.api.util.thread.EventLock;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by LY309313 on 2017/9/23.
 */

public final class TcpConnection implements Connection {

    public enum State {connecting, connected, disconnecting, disconnected}

    private final AtomicReference<State> state = new AtomicReference<>(disconnected);
    private final EventLock connLock = new EventLock();
    private final Logger logger;
    private final ClientListener listener;
    private final FingerClient client;
    private final PacketWriter writer;
    private final PacketReader reader;
    //    private final AllotClient allotClient;
    private SocketChannel channel;
    private SessionContext context;
    private long lastReadTime;
    private long lastWriteTime;
    private ConnectThread connectThread;
    private int totalReconnectCount;
    private volatile int reconnectCount = 0;
    private volatile boolean autoConnect = true;

    public TcpConnection(FingerClient client, PacketReceiver receiver) {
        ClientConfig config = ClientConfig.I;
        this.client = client;
        this.logger = config.getLogger();
        this.listener = config.getClientListener();
        //this.allotClient = new AllotClient();
        this.reader = new AsyncPacketReader(this, receiver);
        this.writer = new AsyncPacketWriter(this, connLock);
    }

    private void onConnected(SocketChannel channel) {
        this.reconnectCount = 0;
        this.channel = channel;
        this.context = new SessionContext();
        this.state.set(connected);
        this.reader.startRead();
        logger.w("connection connected !!!");
        listener.onConnected(client);
    }

    @Override
    public void close() {
        if (state.compareAndSet(State.connected, State.disconnecting)) {
            reader.stopRead();
            if (connectThread != null) {
                connectThread.shutdown();
            }
            doClose();
            logger.w("connection closed !!!");
        }
    }

    private void doClose() {
        connLock.lock();
        try {
            Channel channel = this.channel;
            if (channel != null) {
//                if (channel.isOpen()) {
//                    IOUtils.close(channel);
//                    listener.onDisConnected(client);
//                    logger.w("channel closed !!!");
//                }
                IOUtils.close(channel);
                listener.onDisConnected(client);
                this.channel = null;
            }
        } finally {
            state.set(disconnected);
            connLock.unlock();
        }
    }

    @Override
    public void connect() {
        if (state.compareAndSet(State.disconnected, connecting)) {
            if ((connectThread == null) || !connectThread.isAlive()) {
                connectThread = new ConnectThread(connLock);
            }
            connectThread.addConnectTask(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return doReconnect();
                }
            });
        }
    }

    @Override
    public void reconnect() {
        close();
        connect();
    }

    @Override
    public void manualReconnect() {
        reader.stopRead();
        if (connectThread != null) {
            connectThread.shutdown();
        }
        doClose();
        logger.w("connection closed !!!");

        if (state.compareAndSet(State.disconnected, connecting)) {
            if ((connectThread == null) || !connectThread.isAlive()) {
                connectThread = new ConnectThread(connLock);
            }
            connectThread.addConnectTask(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return doConnect();
                }
            });
        }

    }

    private boolean doReconnect() {
        if (totalReconnectCount > Constants.MAX_TOTAL_RESTART_COUNT || !autoConnect) {// 过载保护
            logger
                .w("doReconnect failure reconnect count over limit or autoConnect off, total=%d, state=%s, autoConnect=%b"
                    , totalReconnectCount, state.get(), autoConnect);
            state.set(disconnected);
            return true;
        }

        reconnectCount++;    // 记录重连次数
        totalReconnectCount++;

        logger.d("try doReconnect, count=%d, total=%d, autoConnect=%b, state=%s", reconnectCount,
            totalReconnectCount, autoConnect, state.get());

        if (reconnectCount > MAX_RESTART_COUNT) {    // 超过此值 sleep 10min
            if (connLock.await(TimeUnit.MINUTES.toMillis(10))) {
                state.set(disconnected);
                return true;
            }
            reconnectCount = 0;
        } else if (reconnectCount > 2) {             // 第二次重连时开始按秒sleep，然后重试
            if (connLock.await(TimeUnit.SECONDS.toMillis(reconnectCount))) {
                state.set(disconnected);
                return true;
            }
        }

        if (Thread.currentThread().isInterrupted() || state.get() != State.connecting
            || !autoConnect) {
            logger.w("doReconnect failure, count=%d, total=%d, autoConnect=%b, state=%s",
                reconnectCount, totalReconnectCount, autoConnect, state.get());
            state.set(disconnected);
            return true;
        }

        logger.w("doReconnect, count=%d, total=%d, autoConnect=%b, state=%s", reconnectCount,
            totalReconnectCount, autoConnect, state.get());
        return doConnect();
    }

    private boolean doConnect() {
        List<String> address = ClientConfig.I.getServerAddress();
        if (address != null && address.size() > 0) {
            for (int i = 0; i < address.size(); i++) {
                String[] host_port = address.get(i).split(":");
                if (host_port.length == 2) {

                    String host = host_port[0];
                    int port = Strings.toInt(host_port[1], 0);
                    logger.w("doConnect, host=%s, port=%s", host, port);
                    if (doConnect(host, port)) {
                        return true;
                    }
                }
//                address.remove(i--);
            }
        }
        return false;
    }


    private boolean doConnect(String host, int port) {
        connLock.lock();
        logger.w("try connect server [%s:%s]", host, port);
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.socket().setTcpNoDelay(true);
            channel.connect(new InetSocketAddress(host, port));
            logger.w("connect server ok [%s:%s]", host, port);
            onConnected(channel);
            connLock.signalAll();
            connLock.unlock();
            return true;
        } catch (Throwable t) {
            IOUtils.close(channel);
            connLock.unlock();
            logger.e(t, "connect server ex, [%s:%s]", host, port);
        }
        return false;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.connLock.lock();
        this.autoConnect = autoConnect;
        this.connLock.signalAll();
        this.connLock.unlock();
    }

    @Override
    public void send(Packet packet) {
        writer.write(packet);
    }


    @Override
    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public IMClient getClient() {
        return client;
    }

    @Override
    public boolean isConnected() {
        return state.get() == connected;
    }

    @Override
    public void setLastReadTime() {
        lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void setLastWriteTime() {
        lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public long getLastReadTime() {
        return lastReadTime;
    }

    @Override
    public long getLastWriteTime() {
        return lastWriteTime;
    }

    @Override
    public boolean isReadTimeout() {
        return System.currentTimeMillis() - lastReadTime > context.heartbeat + 1000;
    }

    @Override
    public void resetTimeout() {
        lastReadTime = lastWriteTime = 0;
    }

    @Override
    public boolean isAutoConnect() {
        return autoConnect;
    }

    @Override
    public SessionContext getSessionContext() {
        return context;
    }

    @Override
    public boolean isWriteTimeout() {
        return System.currentTimeMillis() - lastWriteTime > context.heartbeat - 1000;
    }

    @Override
    public String toString() {
        return "TcpConnection{" +
            "state=" + state +
            ", channel=" + channel +
            ", lastReadTime=" + lastReadTime +
            ", lastWriteTime=" + lastWriteTime +
            ", totalReconnectCount=" + totalReconnectCount +
            ", reconnectCount=" + reconnectCount +
            ", autoConnect=" + autoConnect +
            '}';
    }
}
