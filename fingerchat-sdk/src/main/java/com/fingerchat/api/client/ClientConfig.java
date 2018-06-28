package com.fingerchat.api.client;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Constants;
import com.fingerchat.api.FGListener;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.Logger;
import com.fingerchat.api.connection.SessionStorage;
import com.fingerchat.api.session.FileSessionStorage;
import com.fingerchat.api.util.DefaultLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by LY309313 on 2017/9/23.
 */

public final class ClientConfig {

    private final DefaultClientListener clientListener = new DefaultClientListener();

    public static ClientConfig I = new ClientConfig();
    //    private String allotServer;
//    private String serverHost;
//    private int serverPort;
    private List<String> serverAddress;
    private String publicKey;
    private String deviceId;
    private String osName = Constants.DEF_OS_NAME;
    private String osVersion;
    private String clientVersion;
    private String userId;
    private String password;
    private boolean register;
    private int maxHeartbeat = Constants.DEF_HEARTBEAT;
    private int minHeartbeat = Constants.DEF_HEARTBEAT;
    private int aesKeyLength = 16;
    private int compressLimit = Constants.DEF_COMPRESS_LIMIT;
    private SessionStorage sessionStorage;
    private String sessionStorageDir;
    private Logger logger;
    private boolean logEnabled;
    private boolean enableHttpProxy = true;
    private boolean isLoginConflicted = false;

    private final Map<Class<? extends FGListener>, Collection<? extends FGListener>> listeners = new HashMap<>();

    public static ClientConfig build() {
        return I = new ClientConfig();
    }

    public IMClient create() {
        return new FingerClient(this);
    }

    /*package*/ void destroy() {
        clientListener.setListener(null);
        I = new ClientConfig();
    }

    public SessionStorage getSessionStorage() {
        if (sessionStorage == null) {
            sessionStorage = new FileSessionStorage(sessionStorageDir);
        }
        return sessionStorage;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = new DefaultLogger();
        }
        return logger;
    }

    public ClientConfig setLogger(Logger logger) {
        this.logger = logger;
        this.getLogger().enable(logEnabled);
        return this;
    }

    public String getSessionStorageDir() {
        return sessionStorageDir;
    }

    public ClientConfig setSessionStorage(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
        return this;
    }

    public ClientConfig setSessionStorageDir(String sessionStorageDir) {
        this.sessionStorageDir = sessionStorageDir;
        return this;
    }

//    public String getAllotServer() {
//        return allotServer;
//    }
//
//    public ClientConfig setAllotServer(String allotServer) {
//        this.allotServer = allotServer;
//        return this;
//    }

    public String getDeviceId() {
        return deviceId;
    }

    public ClientConfig setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getOsName() {
        return osName;
    }

    public ClientConfig setOsName(String osName) {
        this.osName = osName;
        return this;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public ClientConfig setOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return this;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public ClientConfig setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
        return this;
    }

    public int getMaxHeartbeat() {
        return maxHeartbeat;
    }

    public ClientConfig setMaxHeartbeat(int maxHeartbeat) {
        this.maxHeartbeat = maxHeartbeat;
        return this;
    }

    public int getMinHeartbeat() {
        return minHeartbeat;
    }

    public ClientConfig setMinHeartbeat(int minHeartbeat) {
        this.minHeartbeat = minHeartbeat;
        return this;
    }

    public int getAesKeyLength() {
        return aesKeyLength;
    }

    public ClientConfig setAesKeyLength(int aesKeyLength) {
        this.aesKeyLength = aesKeyLength;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public ClientConfig setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public int getCompressLimit() {
        return compressLimit;
    }

    public ClientConfig setCompressLimit(int compressLimit) {
        this.compressLimit = compressLimit;
        return this;
    }

    public ClientListener getClientListener() {
        return clientListener;
    }

    public ClientConfig setClientListener(ClientListener clientListener) {
        this.clientListener.setListener(clientListener);
        return this;
    }


    public String getUserId() {
        return userId;
    }

    public ClientConfig setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public ClientConfig setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
        this.logger.enable(logEnabled);
        return this;
    }

    public boolean isEnableHttpProxy() {
        return enableHttpProxy;
    }

    public ClientConfig setEnableHttpProxy(boolean enableHttpProxy) {
        this.enableHttpProxy = enableHttpProxy;
        return this;
    }

//    public String getServerHost() {
//        return serverHost;
//    }
//
//    public ClientConfig setServerHost(String serverHost) {
//        this.serverHost = serverHost;
//        return this;
//    }
//
//    public int getServerPort() {
//        return serverPort;
//    }
//
//    public ClientConfig setServerPort(int serverPort) {
//        this.serverPort = serverPort;
//        return this;
//    }

    public String getPassword() {
        return password;
    }

    public ClientConfig setPassword(String password) {
        this.password = password;
        return this;
    }


    public boolean isRegister() {
        return register;
    }

    public ClientConfig setRegister(boolean register) {
        this.register = register;
        return this;
    }


    /**
     * 注册监听
     */
    public <T extends FGListener> void registerListener(Class<T> clazz, T listener) {
        getOrCreateListener(clazz).add(listener);
    }

    /**
     * 获取该类型所有接口
     */
    private <T extends FGListener> Collection<T> getOrCreateListener(Class<T> clazz) {
        Collection<T> collection = ((Collection<T>) listeners.get(clazz));
        if (collection == null) {
            collection = new ArrayList<>();
            listeners.put(clazz, collection);
        }
        return collection;
    }

    /**
     * 获取该类型所有接口
     */
    public <T extends FGListener> Collection<T> getFGlistener(Class<T> clazz) {
        return Collections.unmodifiableCollection(getOrCreateListener(clazz));
    }

    /**
     * 移除该接口
     */
    public <T extends FGListener> void removeListener(Class<T> clazz, T listener) {
        getOrCreateListener(clazz).remove(listener);
    }

    /**
     * 设置服务器地址（host:port）
     */
    public ClientConfig setServerAddress(String address) {
        if (this.serverAddress == null) {
            serverAddress = new ArrayList<>();
        }
        serverAddress.add(address);
        return this;
    }

    public ClientConfig setServerAddresses(List<String> addresses) {
        this.serverAddress = addresses;
        return this;
    }


    public List<String> getServerAddress() {
        return serverAddress;
    }

    public void setLoginConflicted(boolean flag) {
        isLoginConflicted = flag;
    }

    public boolean isLoginConflicted() {
        return isLoginConflicted;
    }
}
