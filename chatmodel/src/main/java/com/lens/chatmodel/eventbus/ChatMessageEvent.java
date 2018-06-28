package com.lens.chatmodel.eventbus;

import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class ChatMessageEvent implements IEventProduct {

    public static final int RECEIVE = 0;
    public static final int ERROR = 1;
    public static final int CANCEL = 2;


    private IChatRoomModel mPacket;
    private int type;
    private String msgId;

    public ChatMessageEvent(){

    }

    public ChatMessageEvent(IChatRoomModel packet) {
        mPacket = packet;
    }

    public void setPacket(IChatRoomModel packet) {
        mPacket = packet;
    }

    public IChatRoomModel getPacket() {
        return mPacket;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
