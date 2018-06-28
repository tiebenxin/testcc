package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.MucChatMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class MucMessageEvent implements IEventProduct {


  private MucChatMessage mPacket;
  private int type;

  public MucMessageEvent(MucChatMessage packet) {
    mPacket = packet;
  }

  public void setPacket(MucChatMessage packet) {
    mPacket = packet;
  }

  public MucChatMessage getPacket() {
    return mPacket;
  }

}
