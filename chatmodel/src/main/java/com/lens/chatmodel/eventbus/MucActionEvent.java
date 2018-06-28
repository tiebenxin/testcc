package com.lens.chatmodel.eventbus;

import com.fingerchat.proto.message.Muc;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by xhdl0002 on 2018/1/18.
 */

public class MucActionEvent implements IEventProduct {
    private Muc.MucAction mPacket;
    private int type;

    public Muc.MucAction getmPacket() {
        return mPacket;
    }

    public void setmPacket(Muc.MucAction mPacket) {
        this.mPacket = mPacket;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
