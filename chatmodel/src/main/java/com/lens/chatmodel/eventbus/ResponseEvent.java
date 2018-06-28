package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.RespMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class ResponseEvent implements IEventProduct {

    private RespMessage mPacket;
    private int type;

    public ResponseEvent(RespMessage packet) {
        mPacket = packet;
        type = ResponseType.getType(packet);
    }

    public void setPacket(RespMessage packet) {
        mPacket = packet;
    }

    public RespMessage getPacket() {
        return mPacket;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return mPacket.response.getCode();
    }
}
