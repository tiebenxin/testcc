package com.lens.chatmodel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lens.chatmodel.interf.IChatStateListener;

/**
 * Created by LL130386 on 2017/12/18.
 */

public class ChatUpdateReceiver extends BroadcastReceiver {

  private final IChatStateListener mListener;

  public ChatUpdateReceiver(IChatStateListener listener) {
    mListener = listener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {

  }
}
