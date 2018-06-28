package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;

/**
 * Created by LY309313 on 2017/12/26.
 */

public interface MucListener extends FGListener {

    void onMucAction(MucActionMessage actionMessage);

    void onMuc(MucMessage mucMessage);

    void onMucMember(MucMemberMessage mucMemberMessage);
}
