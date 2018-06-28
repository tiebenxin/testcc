package com.lens.chatmodel.eventbus;

import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 * 网络状态Event
 */

public class NetStatusEvent implements IEventProduct {

    private ENetStatus status;

    public NetStatusEvent(ENetStatus s) {
        status = s;
    }

    public void setStatus(ENetStatus s) {
        status = s;
    }

    public ENetStatus getStatus() {
        return status;
    }

}
