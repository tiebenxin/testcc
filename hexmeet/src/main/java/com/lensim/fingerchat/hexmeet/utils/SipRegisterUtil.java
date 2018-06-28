package com.lensim.fingerchat.hexmeet.utils;

import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetTransportType;
import com.lensim.fingerchat.hexmeet.App;


public class SipRegisterUtil {

  public static HexmeetRegistrationState getSipRegisterStatus() {
    return App.getHexmeetSdkInstance().getSipRegistrationState();
  }

  public static HexmeetTransportType getTransType(String type) {
    if (type == null || type.isEmpty()) {
      return HexmeetTransportType.TransportTcp;
    }

    if (type.equalsIgnoreCase("TLS")) {
      return HexmeetTransportType.TransportTls;
    } else if (type.equalsIgnoreCase("UDP")) {
      return HexmeetTransportType.TransportUdp;
    } else {
      return HexmeetTransportType.TransportTcp;
    }
  }
}
