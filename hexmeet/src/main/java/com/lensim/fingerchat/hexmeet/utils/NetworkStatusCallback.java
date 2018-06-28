package com.lensim.fingerchat.hexmeet.utils;

public interface NetworkStatusCallback {

  public void onConnected();

  public void onDisconnected();

  public void onChanged();

}
