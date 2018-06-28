package com.fingerchat.api.client;

import static com.fingerchat.api.Constants.MAX_HB_TIMEOUT_COUNT;

import com.fingerchat.api.Constants;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.Logger;
import com.fingerchat.api.ack.AckCallback;
import com.fingerchat.api.ack.AckContext;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.connection.SessionContext;
import com.fingerchat.api.connection.SessionStorage;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.BindRosterMessage;
import com.fingerchat.api.message.BindUserMessage;
import com.fingerchat.api.message.FastConnectMessage;
import com.fingerchat.api.message.HandshakeMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.protocol.Payload;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.api.security.AesCipher;
import com.fingerchat.api.security.CipherBox;
import com.fingerchat.api.session.PersistentSession;
import com.fingerchat.api.util.Strings;
import com.fingerchat.api.util.thread.ExecutorManager;
import com.fingerchat.proto.message.BaseChat;
import com.fingerchat.proto.message.FastConnect;
import com.fingerchat.proto.message.HandShake;
import com.fingerchat.proto.message.Roster;
import com.fingerchat.proto.message.Roster.ROption;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.User;
import com.fingerchat.proto.message.User.BindMessage;
import com.google.protobuf.ByteString;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by LY309313 on 2017/9/26.
 */

public final class FingerClient implements IMClient, AckCallback {


    private enum State {Started, Shutdown, Destroyed}

    private final AtomicReference<State> clientState = new AtomicReference<>(State.Shutdown);

    private final TcpConnection connection;
    private final ClientConfig config;
    private final Logger logger;
    private int hbTimeoutTimes;

    private AckRequestMgr ackRequestMgr;


    /*package*/ FingerClient(ClientConfig config) {
        this.config = config;
        this.logger = config.getLogger();

        MessageDispatcher receiver = new MessageDispatcher();

//        if (config.isEnableHttpProxy()) {
//            this.httpRequestMgr = HttpRequestMgr.I();
//            receiver.register(Command.HTTP_PROXY, new HttpProxyHandler());
//        }

        this.ackRequestMgr = AckRequestMgr.I();
        this.connection = new TcpConnection(this, receiver);
        this.ackRequestMgr.setConnection(this.connection);
    }

    @Override
    public void start() {
        if (clientState.compareAndSet(State.Shutdown, State.Started)) {
            connection.setAutoConnect(true);
            connection.connect();
            logger.w("do start client ...");
        }
    }

    @Override
    public void stop() {
        logger.w("client shutdown !!!, state=%s", clientState.get());
        if (clientState.compareAndSet(State.Started, State.Shutdown)) {
            connection.setAutoConnect(false);
            connection.close();
        }
    }

    @Override
    public void destroy() {
        if (clientState.get() != State.Destroyed) {
            this.stop();
            logger.w("client destroy !!!");
            ExecutorManager.INSTANCE.shutdown();
            ClientConfig.I.destroy();
            clientState.set(State.Destroyed);
        }
    }

    @Override
    public boolean isRunning() {
        return clientState.get() == State.Started && connection.isConnected();
    }

    @Override
    public boolean isLogin() {
        if (config != null) {
            if (!Strings.isBlank(config.getUserId()) && !Strings.isBlank(config.getPassword())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 这个方法主要用于解决网络十分不稳定的场景：
     * 正常情况如果网络断开，就应该关闭连接，反之则应去建立连接
     * 但是在网络抖动厉害时就会发生连接频繁的建立／断开。
     * <p>
     * 处理这种场景的其中一个方案是：
     * 当网络断开时不主动关闭连接，而是尝试发送一次心跳检测，
     * 如果能收到响应，说明网络短时间内又恢复了，
     * 否则就断开连接，等待网络恢复并重建连接。
     *
     * @param isConnected true/false
     */
    @Override
    public void onNetStateChange(boolean isConnected) {
        connection.setAutoConnect(isConnected);
        logger.i("network state change, isConnected=%b, connection=%s", isConnected, connection);
        if (isConnected) { //当有网络时，去尝试重连
            connection.connect();
        } else if (connection.isConnected()) { //无网络，如果连接没有断开，尝试发送一次心跳检测，用于快速校验网络状况
            connection.resetTimeout();//心跳检测前，重置上次读写数据包的时间戳
            hbTimeoutTimes =
                Constants.MAX_HB_TIMEOUT_COUNT - 2;//总共要调用两次healthCheck，第一次用于发送心跳，第二次用于检测是否超时
            final ScheduledExecutorService timer = ExecutorManager.INSTANCE.getTimerThread();

            //隔3s后发送一次心跳检测，看能不能收到服务端的响应
            timer.schedule(new Runnable() {
                int checkCount = 0;

                @Override
                public void run() {
                    logger
                        .w("network disconnected, try test tcp connection checkCount=%d, connection=%s",
                            checkCount, connection);
                    //如果期间连接状态发生变化，取消任务
                    if (connection.isAutoConnect() || !connection.isConnected()) {
                        return;
                    }

                    if (++checkCount <= 2) {
                        if (healthCheck() && checkCount < 2) {
                            timer.schedule(this, 3, TimeUnit.SECONDS);
                        }
                    }
                }

            }, 3, TimeUnit.SECONDS);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }


    @Override
    public boolean healthCheck() {

        if (connection.isReadTimeout()) {
            hbTimeoutTimes++;
            logger.w("heartbeat timeout times=%s", hbTimeoutTimes);
        } else {
            hbTimeoutTimes = 0;
        }

        if (hbTimeoutTimes >= MAX_HB_TIMEOUT_COUNT) {
            logger.w("heartbeat timeout times=%d over limit=%d, client restart", hbTimeoutTimes,
                MAX_HB_TIMEOUT_COUNT);
            hbTimeoutTimes = 0;
            connection.reconnect();
            return false;
        }

        if (connection.isWriteTimeout()) {
            logger.d("<<< send heartbeat ping...");
            connection.send(Packet.HB_PACKET);
        }

        return true;
    }

    @Override
    public void fastConnect() {
        SessionStorage storage = config.getSessionStorage();
        if (storage == null) {
            handshake();
            return;
        }

        String ss = storage.getSession();
        if (Strings.isBlank(ss)) {
            handshake();
            return;
        }

        PersistentSession session = PersistentSession.decode(ss);
        if (session == null || session.isExpired()) {
            storage.clearSession();
            logger.w("fast connect failure session expired, session=%s", session);
            handshake();
            return;
        }

        FastConnectMessage message = new FastConnectMessage(connection);
        FastConnect.FastConnectMessage.Builder builder = FastConnect.FastConnectMessage
            .newBuilder();
        if (null == config.getDeviceId() || "".equals(config.getDeviceId())) {
            config.setDeviceId("123qazwsx");
        }
        builder.setDeviceid(config.getDeviceId());
        builder.setSessionid(session.sessionId);
        builder.setMaxHeartbeat(config.getMaxHeartbeat());
        builder.setMinHeartbeat(config.getMinHeartbeat());
//        String id = UUID.randomUUID().toString();
//        builder.setId(id);
//        message.deviceId = config.getDeviceId();
//        message.sessionId = session.sessionId;
//        message.maxHeartbeat = config.getMaxHeartbeat();
//        message.minHeartbeat = config.getMinHeartbeat();
        message.setMessage(builder.build());
        message.encodeBody();
        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setRequest(message.getPacket())
            .setTimeout(4000)
            .setRetryCount(3)
        );
        logger.w("<<< do fast connect, message=%s", message);
        System.out.println(FingerClient.class.getSimpleName() + "--fastConnect");

        message.sendRaw();
        connection.getSessionContext().changeCipher(session.cipher);
    }

    @Override
    public void handshake() {
        SessionContext context = connection.getSessionContext();
        context.changeCipher(CipherBox.INSTANCE.getRsaCipher());
        HandshakeMessage message = new HandshakeMessage(connection);
        HandShake.HandShakeMessage.Builder builder = HandShake.HandShakeMessage.newBuilder();
        builder.setClientKey(ByteString.copyFrom(CipherBox.INSTANCE.randomAESKey()));
        builder.setIv(ByteString.copyFrom(CipherBox.INSTANCE.randomAESIV()));
        builder.setDeviceId(config.getDeviceId());
        builder.setOsName(config.getOsName());
        builder.setOsVersion(config.getOsVersion());
        builder.setClientVersion(config.getClientVersion());
        builder.setMaxHeartbeat(config.getMaxHeartbeat());
        builder.setMinHeartbeat(config.getMinHeartbeat());
//        String id = UUID.randomUUID().toString();
//        builder.setId(id);
        message.setHandShakeMessage(builder.build());

//        message.clientKey = CipherBox.INSTANCE.randomAESKey();
//        message.iv = CipherBox.INSTANCE.randomAESIV();
//        message.deviceId = config.getDeviceId();
//        message.osName = config.getOsName();
//        message.osVersion = config.getOsVersion();
//        message.clientVersion = config.getClientVersion();
//        message.maxHeartbeat = config.getMaxHeartbeat();
//        message.minHeartbeat = config.getMinHeartbeat();
        message.encodeBody();

        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setTimeout(4000)
            .setRequest(message.getPacket())
            .setRetryCount(3)
        );
        logger.w("<<< do handshake, message=%s", message);
        message.send();
        context.changeCipher(new AesCipher(message.handShakeMessage.getClientKey().toByteArray(),
            message.handShakeMessage.getIv().toByteArray()));
    }

    @Override
    public void applyVerCode(String userid, String phoneNumber) {
        if (Strings.isBlank(userid) || Strings.isBlank(phoneNumber)) {
            logger.w("user is null or phoneNumber is null");
            return;
        }
        User.BindMessage.Builder builder = User.BindMessage.newBuilder();
        builder.setUsername(userid).setPhoneNumber(phoneNumber);
//        String id = UUID.randomUUID().toString();
//        builder.setId(id);
        BindUserMessage message = BindUserMessage
            .buildApplyVerCode(connection)
            .setUserMessage(builder.build());
        message.encodeBody();
        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setTimeout(20000)
            .setRequest(message.getPacket())
            .setRetryCount(5)
        );
        logger.w("<<< do bind user, userId=%s", userid);
        message.send();

    }

    @Override
    public void register(String userId, String password, String phoneNumber, String verCode) {
        if (Strings.isBlank(userId)) {
            logger.w("bind user is null");
            return;
        }
        SessionContext context = connection.getSessionContext();
        if (context.userid != null) {
            if (userId.equals(context.userid)) {//已经绑定
                if (password != null && password.equals(context.password)) {
                    return;
                }
            } else {
                logout();//切换用户，要先解绑老用户
            }
        }
        context.setBindUser(userId).setPassword(password);
        config.setUserId(userId).setPassword(password);

        User.BindMessage.Builder builder = User.BindMessage.newBuilder();
        builder.setUsername(userId).setPassword(password).setPhoneNumber(phoneNumber)
            .setVerCode(verCode);
//        String id = UUID.randomUUID().toString();
//        builder.setId(id);
        BindUserMessage message = BindUserMessage
            .buildRegister(connection)
            .setUserMessage(builder.build());
        message.encodeBody();
        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setTimeout(20000)
            .setRequest(message.getPacket())
            .setRetryCount(1)
        );
        logger.w("<<< do bind user, userId=%s", userId);
        message.send();
    }

    @Override
    public void login(final String userId, final String password) {
        if (Strings.isBlank(userId)) {
            logger.w("bind user is null");
            return;
        }
        SessionContext context = connection.getSessionContext();
        if (context.userid != null) {
            if (userId.equals(context.userid)) {//已经绑定
                if (password != null && password.equals(context.password)) {
                    return;
                }
            } else {
                logout();//切换用户，要先解绑老用户
            }
        }
        context.setBindUser(userId).setPassword(password);
        config.setUserId(userId).setPassword(password);

        User.BindMessage.Builder builder = User.BindMessage.newBuilder();
        builder.setUsername(userId).setPassword(password).setNeedInfo(1);//1 需要user info,0 不需要
        BindUserMessage message = BindUserMessage
            .buildBind(connection)
            .setUserMessage(builder.build());
        message.encodeBody();
        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setTimeout(20000)
            .setRequest(message.getPacket())
            .setRetryCount(1)
        );
        logger.w("<<< do bind user, userId=%s", userId);
        System.out.println(FingerClient.class.getSimpleName() + "--login");
        message.send();

    }

    @Override
    public void loginByPhone(String phone, String password) {
        if (Strings.isBlank(phone)) {
            logger.w("bind user is null");
            return;
        }
        SessionContext context = connection.getSessionContext();
        if (context.userid != null) {
            if (phone.equals(context.userid)) {//已经绑定
                if (password != null && password.equals(context.password)) {
                    return;
                }
            } else {
                logout();//切换用户，要先解绑老用户
            }
        }
        context.setBindUser(phone).setPassword(password);
        config.setUserId(phone).setPassword(password);

        User.BindMessage.Builder builder = User.BindMessage.newBuilder();
        builder.setPhoneNumber(phone).setPassword(password).setNeedInfo(1);//1 需要user info,0 不需要
        BindUserMessage message = BindUserMessage
            .buildBind(connection)
            .setUserMessage(builder.build());
        message.encodeBody();
        // TODO: 2017/9/27 等待服务器回应，回应不及时则发起重连
        ackRequestMgr.add(message.getSessionId(), AckContext
            .build(this)
            .setTimeout(20000)
            .setRequest(message.getPacket())
            .setRetryCount(1)
        );
        logger.w("<<< do bind user, userId=%s", phone);
        message.send();
    }

    @Override
    public void loginError() {
        String userId = config.getUserId();
        if (Strings.isBlank(userId)) {
            logger.w("unbind user is null");
            return;
        }
        config.setUserId(null).setPassword(null);
        connection.getSessionContext().setBindUser(null).setPassword(null);
    }

    @Override
    public void logout() {
        String userId = config.getUserId();
        if (Strings.isBlank(userId)) {
            logger.w("unbind user is null");
            return;
        }
        config.setUserId(null).setPassword(null);
        connection.getSessionContext().setBindUser(null).setPassword(null);
        User.BindMessage.Builder builder = User.BindMessage.newBuilder();
        builder.setUsername(userId);
        BindUserMessage
            .buildUnbind(connection)
            .setUserMessage(builder.build())
            .send();
        stop();
        logger.w("<<< do unbind user, userId=%s", userId);
    }

    @Override
    public void ack(String messageId) {
        if (messageId != null) {
            BaseChat.SysAck.Builder ack = BaseChat.SysAck.newBuilder();
            ack.addId(messageId);
            AckMessage ackMessage = new AckMessage(connection);
            ackMessage.setAck(ack.build());
            ackMessage.sendRaw();
            logger.d("<<< send ack for push messageId=%s", messageId);
        }
    }

    @Override
    public void getRosters() {
        Roster.RosterOption.Builder roster = Roster.RosterOption.newBuilder();
        roster.setOption(ROption.RQuery).setCondition(2);  //condition 2 表示查询所有
        BindRosterMessage
            .buildOption(connection)
            .setRosterMessage(roster.build())
            .send();
    }

    @Override
    public void addFriend(String user) {
        Roster.RosterOption.Builder roster = Roster.RosterOption.newBuilder();
        roster.setOption(ROption.Apply).setTo(user);
        BindRosterMessage
            .buildOption(connection)
            .setRosterMessage(roster.build())
            .send();
    }

    @Override
    public void searchFriend(String user) {
        Roster.RosterOption.Builder roster = Roster.RosterOption.newBuilder();
        roster.setOption(ROption.RQuery).setTo(user).setCondition(3);  //condition 1 表示查询好友，3 查询陌生人
        BindRosterMessage
            .buildOption(connection)
            .setRosterMessage(roster.build())
            .send();
    }

    @Override
    public void deleFriend(String user) {
        Roster.RosterOption.Builder roster = Roster.RosterOption.newBuilder();
        roster.setOption(ROption.Delete).setTo(user);
        BindRosterMessage
            .buildOption(connection)
            .setRosterMessage(roster.build())
            .send();
    }

    @Override
    public void updateFriendInfo(String user, RosterItem item) {
        Roster.RosterOption.Builder roster = Roster.RosterOption.newBuilder();
        roster.setOption(ROption.Update).setTo(user).setItem(item);
        BindRosterMessage
            .buildOption(connection)
            .setRosterMessage(roster.build())
            .send();
    }

    @Override
    public void updateUserInfo(BindMessage message) {
        BindUserMessage
            .updateUserInfo(connection)
            .setUserMessage(message)
            .send();
    }


    @Override
    public int sendResult(Command cmd, MessageContext context) {
        if (connection.getSessionContext().handshakeOk()) {
            Payload message = new Payload(cmd, context.content, connection);
            message.encodeBody();
            context.setRequest(message.getPacket());
            Future<Boolean> future = ackRequestMgr.add(message.getSessionId(), context);
            message.send();
            logger.d("<<< send push message=%s", message);
            return message.getSessionId();
        }
        logger.d("神奇");
        return 0;
    }

    @Override
    public Future<Boolean> send(Command cmd, MessageContext context) {
        if (connection.getSessionContext().handshakeOk()) {
            Payload message = new Payload(cmd, context.content, connection);
            message.encodeBody();
            context.setRequest(message.getPacket());
            Future<Boolean> future = ackRequestMgr.add(message.getSessionId(), context);
            message.send();
            logger.d("<<< send push message=%s", message);
            return future;
        }
        logger.d("神奇");
        return null;
    }


    @Override
    public void onSuccess(Packet response) {

    }

    @Override
    public void onTimeout(Packet request) {
        this.connection.reconnect();
    }

}
