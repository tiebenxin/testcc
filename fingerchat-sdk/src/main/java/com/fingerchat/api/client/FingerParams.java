package com.fingerchat.api.client;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by LL130386 on 2017/12/2.
 */

public class FingerParams implements Serializable {

  public FingerParams() {
    if (maps == null) {
      maps = new LinkedHashMap<>();
    }
    maps.clear();
  }

  private LinkedHashMap<String, String> maps = new LinkedHashMap<>();

  public void put(String key, String value) {
    maps.put(key, value);
  }

  public void get(String key) {
    maps.get(key);
  }

  public LinkedHashMap getParams() {
    return maps;
  }

}
