package com.fingerchat.api.listener;

import com.fingerchat.api.FGListener;
import com.fingerchat.api.message.UserInfoMessage;

/**
 * Created by LY309313 on 2017/12/26.
 */

public interface UserListener extends FGListener {

    void onReceivedUserinfo(UserInfoMessage message);
}
