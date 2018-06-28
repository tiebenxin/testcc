package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by xhdl0002 on 2018/1/18.
 */

public class MucActionMessageEvent implements IEventProduct {
    private MucActionMessage mPacket;
    private int type;

    public MucActionMessage getPacket() {
        return mPacket;
    }

    public void setPacket(MucActionMessage mPacket) {
        this.mPacket = mPacket;
    }

    public MucActionMessageEvent(MucActionMessage mPacket) {
        this.mPacket = mPacket;
    }
}
