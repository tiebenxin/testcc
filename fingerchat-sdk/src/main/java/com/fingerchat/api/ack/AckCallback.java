package com.fingerchat.api.ack;

import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/23.
 */

public interface AckCallback {

    void onSuccess(Packet response);

    void onTimeout(Packet request);
}
