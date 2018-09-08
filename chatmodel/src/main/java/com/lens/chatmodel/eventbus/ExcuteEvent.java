package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.ExcuteResultMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class ExcuteEvent implements IEventProduct {

    private ExcuteResultMessage mPacket;
    private String msgId;

    public ExcuteEvent() {

    }

    public ExcuteEvent(ExcuteResultMessage packet) {
        mPacket = packet;
    }

    public void setPacket(ExcuteResultMessage packet) {
        mPacket = packet;
    }

    public ExcuteResultMessage getPacket() {
        return mPacket;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
