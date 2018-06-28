package com.lens.chatmodel.eventbus;

import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class RefreshEvent implements IEventProduct {

    private RefreshEntity entity;

    public RefreshEvent(RefreshEntity e) {
        entity = e;
    }

    public void setEntity(RefreshEntity e) {
        entity = e;
    }

    public RefreshEntity getEntity() {
        return entity;
    }

}
