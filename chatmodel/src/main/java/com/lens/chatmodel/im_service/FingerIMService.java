package com.lens.chatmodel.im_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.fingerchat.api.ClientListener;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.ConflictListener;
import com.fingerchat.api.listener.MucListener;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.message.ConflictMessage;
import com.fingerchat.api.message.FGPushMessage;
import com.fingerchat.api.message.MessageAckMessage;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucChatMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.api.message.PrivateChatMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.lens.chatmodel.ChatEnum.EIMType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.MucGroupMessageEvent;
import com.lens.chatmodel.eventbus.MucMemberMessageEvent;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.manager.SmartPingManager;
import org.greenrobot.eventbus.EventBus;


/**
 * Created by LY309313 on 2017/11/9.
 */
public final class FingerIMService extends Service implements ClientListener, ConflictListener {

    private int SERVICE_START_DELAYED = 5;

    private ResponseEvent responseEvent;
    private RosterEvent rosterEvent;
    private MucGroupMessageEvent mucGroupMessageEvent;
    private MucMemberMessageEvent mucMemberMessageEvent;
    private NetStatusEvent netStatusEvent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FingerIMService", "FingerIMService onCreate");
        //注册群action
        ClientConfig.I
            .registerListener(MucListener.class, MucManager.getInstance(getApplication()));
        ClientConfig.I.registerListener(ConflictListener.class, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!FingerIM.I.hasStarted()) {
            FingerIM.I.checkInit(this).create(this);
        }
        if (FingerIM.I.hasStarted()) {
            if (hasNetwork()) {
                FingerIM.I.client.start();
            }

            flags = START_FLAG_RETRY;
            SERVICE_START_DELAYED = 5;
            return super.onStartCommand(intent, flags, startId);
        } else {
            int ret = super.onStartCommand(intent, flags, startId);
            stopSelf();
            SERVICE_START_DELAYED += SERVICE_START_DELAYED;
            return ret;
        }
    }


    private boolean hasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
            Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        FingerIM.I.destroy();
        //注销群Action
        ClientConfig.I.registerListener(MucListener.class, MucManager.getInstance());
        ClientConfig.I.removeListener(ConflictListener.class, this);
    }

    @Override
    public void onKickUser(String deviceId, String userId) {
        System.out.println("登陆冲突");
        FingerIM.I.unbindAccount();
    }

    @Override
    public void onConnected(IMClient client) {
        System.out.println("链接成功");
        updateNetStatus(ENetStatus.SUCCESS_ON_SERVICE);
    }

    @Override
    public void onDisConnected(IMClient client) {
        SmartPingManager manager = SmartPingManager.getInstanceFor(client.getConnection());
        manager.maybeStopPingServerTask();
        System.out.println("断开链接");
        FingerIM.I.loginError();
        updateNetStatus(ENetStatus.ERROR_CONNECT);
    }

    @Override
    public void onHandshakeOk(IMClient client, int heartbeat) {
        System.out.println("握手成功");
        SmartPingManager manager = SmartPingManager.getInstanceFor(client.getConnection());
        manager.pingServerIfNecessary();
    }

    @Override
    public void onReceiveMessage(BaseMessage message) {
        if (message instanceof PrivateChatMessage) {
            System.out.println(
                "接收到PrivateChatMessage  :  " + ((PrivateChatMessage) message).message.getContent());
            FingerIM.I.ack(((PrivateChatMessage) message).message.getId());
            postEvent(EIMType.PRIVATE_MESSAGE, message);
        } else if (message instanceof MucChatMessage) {
            System.out
                .println(
                    "接收到MucChatMessage  :  " + ((MucChatMessage) message).message.getContent());
            FingerIM.I.ack(((MucChatMessage) message).message.getId());
            postEvent(EIMType.GROUP_MESSAGE, message);
        } else if (message instanceof RosterMessage) {
            System.out
                .println(
                    "接收到RosterMessage  :  " + ((RosterMessage) message).message.getItemCount());

            postEvent(EIMType.ROSTER_MESSAGE, message);
        } else if (message instanceof MucActionMessage) {
            System.out.println(
                "接收到MucActionMessage  :  " + ((MucActionMessage) message).action.getMucid());
        } else if (message instanceof MucMessage) {
            System.out
                .println(
                    "接收到MucMessage  :  " + ((MucMessage) message).message.getItemCount());
            postEvent(EIMType.MUC_GROUP_MESSAGE, message);
        } else if (message instanceof MucMemberMessage) {
            System.out
                .println(
                    "接收到MucMemberMessage  :  " + ((MucMemberMessage) message).message
                        .getItemCount());
            postEvent(EIMType.MUC_MEMBER_MESSAGE, message);
        } else if (message instanceof OfflineMessage) {//离线消息
            postEvent(EIMType.OFFLINE_MESSAGE, message);
        } else if (message instanceof MessageAckMessage) {
            FingerIM.I.ack(((MessageAckMessage) message).message.getId(0));
            postEvent(EIMType.MSG_ACK_MSG, message);
        } else if (message instanceof FGPushMessage) {
            System.out.println(
                "FGPushMessage:  " + ((FGPushMessage) message).message.getContent());
            postEvent(EIMType.FG_PUSH_MESSAGE, message);
        }
    }

    @Override
    public void onResponse(RespMessage response) {
        System.out.println("接收到响应消息：code == >" + response.response.getCode());
        if (responseEvent == null) {
            responseEvent = (ResponseEvent) EventFactory.INSTANCE
                .create(EventEnum.RESPONSE, response);
        } else {
            responseEvent.setPacket(response);
        }
        EventBus.getDefault().post(responseEvent);

    }

    private void postEvent(EIMType type, BaseMessage message) {
        switch (type) {
            case ROSTER_MESSAGE:
                if (rosterEvent == null) {
                    rosterEvent = (RosterEvent) EventFactory.INSTANCE
                        .create(EventEnum.ROSTER, message);
                } else {
                    rosterEvent.setPacket((RosterMessage) message);
                }
                EventBus.getDefault().post(rosterEvent);
                break;
            case MSG_ACK_MSG:
            case PRIVATE_MESSAGE:
            case GROUP_MESSAGE:
            case FG_PUSH_MESSAGE:
                MessageManager.getInstance().onReceive(message);
                break;
            case MUC_GROUP_MESSAGE:
                if (mucGroupMessageEvent == null) {
                    mucGroupMessageEvent = (MucGroupMessageEvent) EventFactory.INSTANCE
                        .create(EventEnum.MUC_GROUP_MESSAGE, message);
                } else {
                    mucGroupMessageEvent.setmPacket((MucMessage) message);
                }
                EventBus.getDefault().post(mucGroupMessageEvent);
                break;
            case MUC_MEMBER_MESSAGE:
                if (mucMemberMessageEvent == null) {
                    mucMemberMessageEvent = (MucMemberMessageEvent) EventFactory.INSTANCE
                        .create(EventEnum.MUC_MEMBER_MESSAGE, message);
                } else {
                    mucMemberMessageEvent.setmPacket((MucMemberMessage) message);
                }
                EventBus.getDefault().post(mucMemberMessageEvent);
                break;
            case OFFLINE_MESSAGE:
                MessageManager.getInstance().onReceive(message);
                break;

        }
    }

    private void updateNetStatus(ENetStatus status) {
        if (netStatusEvent == null) {
            netStatusEvent = new NetStatusEvent(status);
        } else {
            netStatusEvent.setStatus(status);
        }
        EventBus.getDefault().post(netStatusEvent);
    }

    @Override
    public void onReceivedConflictListener(ConflictMessage message) {
        updateNetStatus(ENetStatus.LOGIN_CONFLICTED);
    }
}
