package com.lens.chatmodel.eventbus;

/**
 * Created by LL130386 on 2017/11/25.
 */

public enum EventEnum {
    RESPONSE(0), //登陆注册等响应消息
    ROSTER(1), //联系人消息
    CHAT_MESSAGE(2),//消息,包括群聊和私聊
    MUC_MESSAGE(3),//群聊消息，？
    MAIN_REFRESH(4),//activity main 中刷新消息
    EXCUTE(5),//系统执行消息

    MUC_GROUP_MESSAGE(8),//群相关消息
    MUC_MEMBER_MESSAGE(9),//群成员
    MUC_ACTION_MESSAGE(10),//群action

    MUC_REFRESH_MESSAGE(11);//muc刷新


    public final int value;

    EventEnum(int value) {
        this.value = value;
    }

    public static EventEnum fromInt(int value) {
        EventEnum result = null;
        for (EventEnum item : EventEnum.values()) {
            if (item.value == value) {
                result = item;
                break;
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("EventEnum - fromInt");
        }
        return result;
    }
}
