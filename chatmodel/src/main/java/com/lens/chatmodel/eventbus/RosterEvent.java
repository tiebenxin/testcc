package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.RosterMessage;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class RosterEvent implements IEventProduct {

  public static final int TYPE_ALL = 0;
  public static final int TYPE_GROUP = 1;

  private RosterMessage mPacket;
  private int type;

  public RosterEvent(RosterMessage packet) {
    mPacket = packet;
  }

  public void setPacket(RosterMessage packet) {
    mPacket = packet;
  }

  public RosterMessage getPacket() {
    return mPacket;
  }

}
