package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.ConflictMessage;

/**
 * date on 2018/4/25
 * author ll147996
 * describe
 */

public interface ConflictListener extends FGListener {

    void onReceivedConflictListener(ConflictMessage message);
}
