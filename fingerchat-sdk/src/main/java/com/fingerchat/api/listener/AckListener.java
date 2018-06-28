package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.AckMessage;

/**
 * Created by LY309313 on 2017/12/26.
 */

public interface AckListener extends FGListener {

    void onAck(AckMessage message);
}
