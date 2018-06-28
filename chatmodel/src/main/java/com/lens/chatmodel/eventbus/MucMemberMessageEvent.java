package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by xhdl0002 on 2018/1/18.
 */

public class MucMemberMessageEvent implements IEventProduct {
    private MucMemberMessage mPacket;
    private int type;

    public void setmPacket(MucMemberMessage mPacket) {
        this.mPacket = mPacket;
    }

    public MucMemberMessage getmPacket() {
        return mPacket;
    }

    public MucMemberMessageEvent(MucMemberMessage mPacket) {

        this.mPacket = mPacket;
    }
}
