package com.lensim.fingerchat.commons.permission;

public enum EPermission {
  STORAGE(1001),
  CAMERA(1002),
  RECORD_AUDIO(1003),
  ACCOUNTS(1004),
  READ_CONTACTS(1005),
  SMS(1006),
  PHONE(1007),
  LOCATION(1008),
  ACCESS_GPS(2001),
  ACCESS_FINE_LOCATION(2002);

  public final int value;

  EPermission(int value) {
    this.value = value;
  }

  public static EPermission fromInt(int value) {
    EPermission result = null;
    for (EPermission item : EPermission.values()) {
      if (item.value == value) {
        result = item;
        break;
      }
    }
    if (result == null) {
      throw new IllegalArgumentException("EPermission - fromInt");
    }
    return result;
  }
}
