package com.fingerchat.api.client;

import com.fingerchat.api.ClientListener;
import com.fingerchat.api.IMClient;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.util.thread.ExecutorManager;

import java.util.concurrent.Executor;

/**
 * Created by LY309313 on 2017/9/23.
 */

public class DefaultClientListener implements ClientListener {


    private final Executor executor = ExecutorManager.INSTANCE.getDispatchThread();
    private ClientListener listener;

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnected(final IMClient client) {
        if (listener != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onConnected(client);
                }
            });
        }
        client.fastConnect();
    }

    @Override
    public void onDisConnected(final IMClient client) {
        if (listener != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onDisConnected(client);
                }
            });
        }
        AckRequestMgr.I().clear();
    }

    @Override
    public void onHandshakeOk(final IMClient client, final int heartbeat) {
        if (listener != null) {//dispatcher已经使用了Executor，此处直接同步调用
            listener.onHandshakeOk(client, heartbeat);
        }

    }

    @Override
    public void onReceiveMessage( BaseMessage message) {
        if(listener != null){
            listener.onReceiveMessage(message);
        }
    }

    @Override
    public void onResponse(RespMessage response) {
        if(listener!=null){
            listener.onResponse(response);
        }
    }

    @Override
    public void onKickUser(String deviceId, String userId) {
        if (listener != null) {//dispatcher已经使用了Executor，此处直接同步调用
            listener.onKickUser(deviceId, userId);
        }
    }

}
