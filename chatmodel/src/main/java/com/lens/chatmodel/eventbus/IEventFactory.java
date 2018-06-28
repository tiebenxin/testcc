package com.lens.chatmodel.eventbus;

import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/25.
 */

public interface IEventFactory {

  IEventProduct createResponseEvent(Object object);

  IEventProduct createRosterEvent(Object object);

  IEventProduct createMessageEvent(Object object);

  IEventProduct createGroupMessage(Object object);

  IEventProduct createMemberMessage(Object object);

  IEventProduct createMucActionMessage(Object object);

}
