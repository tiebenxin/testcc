/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.lens.chatmodel.net.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import org.greenrobot.eventbus.EventBus;


/**
 * 网络状态变化广播接受者.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private NetStatusEvent netStatusEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            return;
        }
        NetworkInfo networkInfo = intent
            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null && networkInfo.isConnected()) {
            updateNetStatus(ENetStatus.SUCCESS_ON_NET);
            System.out.println("NetworkReceiver" + "有网络了");
            
        } else {
            updateNetStatus(ENetStatus.ERROR_NET);
            System.out.println("NetworkReceiver" + "无网络了");
        }
    }

    private void updateNetStatus(ENetStatus status) {
        if (netStatusEvent == null) {
            netStatusEvent = new NetStatusEvent(status);
        } else {
            netStatusEvent.setStatus(status);
        }
        EventBus.getDefault().post(netStatusEvent);
    }


}