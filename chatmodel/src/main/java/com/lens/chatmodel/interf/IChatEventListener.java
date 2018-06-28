package com.lens.chatmodel.interf;

import com.lens.chatmodel.ChatEnum.ECellEventType;

/**
 * Created by LL130386 on 2017/12/19.
 */

public interface IChatEventListener {

    void onEvent(ECellEventType type, Object o1, Object o2);

}
