package com.lensim.fingerchat.hexmeet.type;

import android.content.Context;


import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.db.DaoMaster;
import com.lensim.fingerchat.hexmeet.db.DaoMaster.DevOpenHelper;
import com.lensim.fingerchat.hexmeet.db.DaoSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DatabaseHelper {

  public enum DatabaseType {
    CONFERENCE_LIST, CONTACT_LIST, CALLRECORD_LIST, CALLROW_LIST
  }

  private static Map<DatabaseType, DevOpenHelper> map_type_helper = new ConcurrentHashMap<DatabaseType, DevOpenHelper>();
  private static Map<DatabaseType, DaoSession> map_type_session = new ConcurrentHashMap<DatabaseType, DaoSession>();

  public static DaoSession getSession(Context context, DatabaseType type) {
    if (map_type_session.get(type) == null) {
      DaoSession daoSession = new DaoMaster(getInstance(context, type).getWritableDatabase()).newSession();
      map_type_session.put(type, daoSession);
    }

    return map_type_session.get(type);
  }

  private static DevOpenHelper getInstance(Context context, DatabaseType type) {
    if (map_type_helper.get(type) == null) {
      DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(type), null);
      map_type_helper.put(type, helper);
    }

    return map_type_helper.get(type);
  }

  private static String getDatabaseName(DatabaseType type) {
    return type.toString() + "_" + RuntimeData.getUcmServer() + "_" + RuntimeData.getLogUser().getName();
  }

  public static void close() {
    for (DevOpenHelper helper : map_type_helper.values()) {
      helper.close();
    }
    for (DaoSession session : map_type_session.values()) {
      session.clear();
    }

    map_type_helper.clear();
    map_type_session.clear();
  }
}
