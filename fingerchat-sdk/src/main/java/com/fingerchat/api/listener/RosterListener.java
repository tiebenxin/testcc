package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.RosterMessage;

/**
 * Created by LY309313 on 2017/12/26.
 */

public interface RosterListener extends FGListener {

    void onReceivedRoster(RosterMessage message);
}
