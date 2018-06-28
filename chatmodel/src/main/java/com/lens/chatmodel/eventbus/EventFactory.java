package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/25.
 */

public class EventFactory implements IEventFactory {

    public static EventFactory INSTANCE = new EventFactory();

    public IEventProduct create(EventEnum eventEnum, Object object) {
        switch (eventEnum) {
            case RESPONSE:
                return createResponseEvent(object);
            case ROSTER:
                return createRosterEvent(object);
            case CHAT_MESSAGE:
                return createMessageEvent(object);
            case MAIN_REFRESH:
                return createMainRefreshEvent(object);
            case MUC_GROUP_MESSAGE:
                return createGroupMessage(object);
            case MUC_MEMBER_MESSAGE:
                return createMemberMessage(object);
            case MUC_ACTION_MESSAGE:
                return createMucActionMessage(object);
            case MUC_REFRESH_MESSAGE:
                return createMucRefreshEvent(object);
            default:
                return null;
        }

    }

    private IEventProduct createMainRefreshEvent(Object object) {
        if (object instanceof RefreshEntity) {
            return new RefreshEvent((RefreshEntity) object);
        }
        return null;
    }


    @Override
    public IEventProduct createResponseEvent(Object object) {
        if (object instanceof RespMessage) {
            return new ResponseEvent((RespMessage) object);
        }
        return null;
    }

    @Override
    public IEventProduct createRosterEvent(Object object) {
        if (object instanceof RosterMessage) {
            return new RosterEvent((RosterMessage) object);
        }
        return null;
    }

    @Override
    public IEventProduct createMessageEvent(Object object) {
        if (object instanceof MessageBean) {
            return new ChatMessageEvent((MessageBean) object);
        } else {
            return new ChatMessageEvent();
        }
    }

    @Override
    public IEventProduct createGroupMessage(Object object) {
        if (object instanceof MucMessage) {
            return new MucGroupMessageEvent((MucMessage) object);
        }
        return null;
    }

    @Override
    public IEventProduct createMemberMessage(Object object) {
        if (object instanceof MucMemberMessage) {
            return new MucMemberMessageEvent((MucMemberMessage) object);
        }
        return null;
    }

    @Override
    public IEventProduct createMucActionMessage(Object object) {
        if (object instanceof MucActionMessage) {
            return new MucActionMessageEvent((MucActionMessage) object);
        }
        return null;
    }

    public IEventProduct createMucRefreshEvent(Object object) {
        if (object instanceof Integer) {
            return new MucRefreshEvent((Integer) object);
        }
        return null;
    }
}
