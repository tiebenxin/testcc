/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */


package com.lens.chatmodel.im_service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.Constants;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.api.util.DefaultLogger;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.User.BindMessage;
import com.lensim.fingerchat.commons.utils.L;

/**
 * 服务的启动，暂停，恢复，停止，用户绑定等方法
 * Created by LY309313 on 2017/11/9.
 */
public final class FingerIM {

    private static final String SP_FILE_NAME = "fingerhat.cfg";
    private static final String SP_KEY_CV = "clientVersion";
    private static final String SP_KEY_DI = "deviceId";
    private static final String SP_KEY_PK = "publicKey";
    private static final String SP_KEY_AS = "allotServer";
    private static final String SP_KEY_AT = "account";
    private static final String SP_KEY_PS = "password";
    private static final String SP_KEY_LG = "log";
    public static FingerIM I = I();
    private Context ctx;
    private ClientConfig clientConfig;
    private SharedPreferences sp;
    /*package*/ IMClient client;


    /**
     * 获取FingerIM实例
     */
    static /*package*/ FingerIM I() {
        if (I == null) {
            synchronized (FingerIM.class) {
                if (I == null) {
                    I = new FingerIM();
                }
            }
        }
        return I;
    }

    /**
     * 初始化FingerIM, 使用之前必须先初始化
     */
    public void init(Context context) {
        ctx = context.getApplicationContext();
        sp = ctx.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 是否已经初始化
     */
    public boolean hasInit() {
        return ctx != null;
    }

    /**
     * 是否已经初始化
     */
    public FingerIM checkInit(Context context) {
        if (ctx == null) {
            init(context);
        }
        return this;
    }

    /**
     * FingerIMService 是否已启动
     */
    public boolean hasStarted() {
        return client != null;
    }

    /**
     * FingerIMClient 是否正在运行
     */
    public boolean hasRunning() {
        return client != null && client.isRunning();
    }

    /**
     * FingerIMClient 是否已经登录
     */
    public boolean isLogin() {
        return client != null && client.isLogin();
    }

    /**
     * 设置client 配置项
     */
    public void setClientConfig(ClientConfig clientConfig) {
        if (clientConfig.getPublicKey() == null
            || clientConfig.getServerAddress() == null
//            || clientConfig.getAllotServer() == null
            || clientConfig.getClientVersion() == null) {
            throw new IllegalArgumentException("publicKey, allocServer can not be null");
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY_CV, clientConfig.getClientVersion())
            .putString(SP_KEY_DI, clientConfig.getDeviceId())
            .putString(SP_KEY_PK, clientConfig.getPublicKey())
            .putBoolean(SP_KEY_LG, clientConfig.isLogEnabled())
//            .putString(SP_KEY_AS, clientConfig.getAllotServer());
            .putString(SP_KEY_AS, clientConfig.getServerAddress().get(0));
        if (clientConfig.getUserId() != null) {
            editor.putString(SP_KEY_AT, clientConfig.getUserId());
        }

        editor.apply();
        this.clientConfig = clientConfig;
    }

    /**
     * 启动服务
     */
    public void startFingerIM() {
        if (hasInit()) {
            ctx.startService(new Intent(ctx, FingerIMService.class));
        }
    }

    /**
     * 停止推送服务
     */
    public void stopFingerIM() {
        if (hasInit()) {
            ctx.stopService(new Intent(ctx, FingerIMService.class));
        }
    }

    /**
     * 暂停推送服务
     */
    public void pauseFingerIM() {
        if (hasStarted()) {
            client.stop();
        }
    }

    /**
     * 恢复推送服务
     */
    public void resumeFingerIM() {
        if (hasStarted()) {
            client.start();
        }
    }

    /**
     * 设置网络状态推送服务
     */
    public void onNetStateChange(boolean isConnected) {
        if (hasStarted()) {
            client.onNetStateChange(isConnected);
        }
    }

    /*
    * 获取当前链接状态
    * */
    public boolean isConnected() {
        if (hasStarted()) {
            return client.getConnection().isConnected();
        }
        return false;
    }

    /*
    * 快速重连
    * */
    public void fastConnect() {
        if (hasStarted()) {
            client.fastConnect();
        }
    }

    /**
     * 绑定账号
     *
     * @param userId 要绑定的账号
     */
    public void login(String userId, String password) {
        if (hasInit()) {
            sp.edit().putString(SP_KEY_AT, userId).apply();
            sp.edit().putString(SP_KEY_PS, password).apply();
            if (hasRunning()) {
                client.login(userId, password);
            } else if (clientConfig != null) {
                clientConfig.setUserId(userId);
            }
        }
    }

    /**
     * 绑定手机号
     *
     * @param phone 要绑定的手机号
     */
    public void loginByPhone(String phone, String password) {
        if (hasInit()) {
            sp.edit().putString(SP_KEY_AT, phone).apply();
            sp.edit().putString(SP_KEY_PS, password).apply();
            if (hasRunning()) {
                client.loginByPhone(phone, password);
            } else if (clientConfig != null) {
                clientConfig.setUserId(phone);
            }
        }
    }

    public void loginError() {
        if (hasInit()) {
            client.loginError();
        }
    }

    public void register(String userid, String password, String phoneNumber, String verCode) {
        if (hasStarted() && client.isRunning()) {

            client.register(userid, password, phoneNumber, verCode);
        }
    }

    public void applyVerCode(String userid, String phoneNumber) {
        if (hasStarted() && client.isRunning()) {
            client.applyVerCode(userid, phoneNumber);
        }
    }

    /**
     * 解绑账号
     */
    public void unbindAccount() {
        if (hasInit()) {
            sp.edit().remove(SP_KEY_AT).apply();
            if (hasStarted() && client.isRunning()) {
                client.logout();
            } else {
                clientConfig.setUserId(null);
            }
        }
    }

    /**
     * 发送ACK
     *
     * @param messageId 要ACK的消息ID
     */
    public boolean ack(String messageId) {
        if (hasStarted() && client.isRunning()) {
            client.ack(messageId);
            return true;
        }
        return false;
    }


    public void sendMessage(Command cmd, MessageContext context) {
        if (hasStarted() && client.isRunning()) {
            client.send(cmd, context);
        }
    }

    public int sendMessageResult(Command cmd, MessageContext context) {
        if (hasStarted() && client.isRunning()) {
            return client.sendResult(cmd, context);
        }
        return 0;
    }

    /*
    * 获取所有联系人
    * */
    public void getRosters() {
        if (hasStarted() && client.isRunning()) {
            client.getRosters();
        }
    }

    /*
    * 添加好友
    * */
    public void inviteFriend(String user) {
        if (hasStarted() && client.isRunning()) {
            client.addFriend(user);
        }
    }

    /*
       * 删除好友
       * */
    public void deleFriend(String user) {
        if (hasStarted() && client.isRunning()) {
            client.deleFriend(user);
        }
    }

    /*
       * 更改好友信息
       * */
    public void updateFriendInfo(String user, RosterItem item) {
        if (hasStarted() && client.isRunning()) {
            client.updateFriendInfo(user, item);
        }
    }

    /*
       * 更改好友信息
       * */
    public void updateUserInfo(BindMessage message) {
        if (hasStarted() && client.isRunning()) {
            client.updateUserInfo(message);
        }
    }

    /*
    * 搜索好友
    * */
    public void searchFriend(String user) {
        if (hasStarted() && client.isRunning()) {
            client.searchFriend(user);
        }
    }


    public void enableLog(boolean enable) {
        if (clientConfig != null) {
            clientConfig.setLogEnabled(enable);
        }
    }

    @Nullable
    private ClientConfig getClientConfig() {
        if (clientConfig == null) {
            String clientVersion = sp.getString(SP_KEY_CV, null);
            String deviceId = sp.getString(SP_KEY_DI, null);
            String publicKey = sp.getString(SP_KEY_PK, null);
            String allocServer = sp.getString(SP_KEY_AS, null);
            boolean logEnabled = sp.getBoolean(SP_KEY_LG, false);
            clientConfig = ClientConfig.build()
                .setPublicKey(publicKey)
//                .setAllotServer(allocServer)
                .setServerAddress(allocServer)
                .setDeviceId(deviceId)
                .setOsName(Constants.DEF_OS_NAME)
                .setOsVersion(Build.VERSION.RELEASE)
                .setClientVersion(clientVersion)
                .setLogger(new IMLog())
                .setLogEnabled(logEnabled);
        }
        if (clientConfig.getClientVersion() == null
            || clientConfig.getPublicKey() == null
//            || clientConfig.getAllotServer() == null
            || clientConfig.getServerAddress() == null) {
            return null;
        }

        if (clientConfig.getSessionStorageDir() == null) {
            clientConfig.setSessionStorage(new SPSessionStorage(sp));
        }

        if (clientConfig.getOsVersion() == null) {
            clientConfig.setOsVersion(Build.VERSION.RELEASE);
        }

        if (clientConfig.getUserId() == null) {
            clientConfig.setUserId(sp.getString(SP_KEY_AT, null));
        }

        if (clientConfig.getLogger() instanceof DefaultLogger) {
            clientConfig.setLogger(new IMLog());
        }
        return clientConfig;
    }

    synchronized /*package*/ void create(ClientListener listener) {
        ClientConfig config = this.getClientConfig();
        if (config != null) {
            this.client = config.setClientListener(listener).create();
        }
    }

    synchronized /*package*/ void destroy() {
        if (client != null) {
            client.destroy();
        }
        I.client = null;
        I.clientConfig = null;
        I.sp = null;
        I.ctx = null;
        L.d(FingerIM.class.getSimpleName() + "--destroy");
    }

    public boolean isLoginConflicted() {
        if (clientConfig == null) {
            return false;
        }
        return clientConfig.isLoginConflicted();
    }

    public void setLoginConflicted(boolean var) {
        if (clientConfig != null) {
            clientConfig.setLoginConflicted(var);
        }
    }
}
