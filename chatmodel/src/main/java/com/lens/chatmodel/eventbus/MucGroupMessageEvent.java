package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.MucMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by xhdl0002 on 2018/1/18.
 */

public class MucGroupMessageEvent implements IEventProduct {
    private MucMessage mPacket;
    private int type;

    public MucMessage getmPacket() {
        return mPacket;
    }

    public MucGroupMessageEvent(MucMessage mPacket) {
        this.mPacket = mPacket;
    }

    public void setmPacket(MucMessage mPacket) {
        this.mPacket = mPacket;
    }

    public MucMessage getPacket() {
        return mPacket;
    }
}
