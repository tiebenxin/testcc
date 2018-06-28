package com.fingerchat.api.ack;

import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/23.
 */

public class AckContext {


    public AckCallback callback;

    public int timeout = 1000;
    public Packet request;
    public int retryCount;

    public static AckContext build(AckCallback callback) {
        AckContext context = new AckContext();
        context.setCallback(callback);
        return context;
    }

    public AckCallback getCallback() {
        return callback;
    }

    public AckContext setCallback(AckCallback callback) {
        this.callback = callback;
        return this;
    }
    public int getTimeout() {
        return timeout;
    }

    public AckContext setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Packet getRequest() {
        return request;
    }

    public AckContext setRequest(Packet request) {
        this.request = request;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public AckContext setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

}
