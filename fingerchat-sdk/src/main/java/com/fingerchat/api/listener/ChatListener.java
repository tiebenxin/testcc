package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.MucChatMessage;
import com.fingerchat.api.message.PrivateChatMessage;

/**
 * Created by LY309313 on 2017/12/26.
 */

public interface ChatListener extends FGListener {

    void onPrivateChat(PrivateChatMessage privateChatMessage);

    void onMucChat(MucChatMessage mucChatMessage);
}
