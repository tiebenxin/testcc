package com.lensim.fingerchat.hexmeet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * Master of DAO (schema version 1000): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {

  public static final int SCHEMA_VERSION = 1000;

  /**
   * Creates underlying database table using DAOs.
   */
  public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
    RestMeeting_Dao.createTable(db, ifNotExists);
    RestUser_Dao.createTable(db, ifNotExists);
    RestContact_Dao.createTable(db, ifNotExists);
    RestEndpoint_Dao.createTable(db, ifNotExists);
    MeetingContact_Dao.createTable(db, ifNotExists);
    MeetingUser_Dao.createTable(db, ifNotExists);
    MeetingEndpoint_Dao.createTable(db, ifNotExists);
    RestCallRecord_Dao.createTable(db, ifNotExists);
    RestCallRow_Dao.createTable(db, ifNotExists);
  }

  /**
   * Drops underlying database table using DAOs.
   */
  public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
    RestMeeting_Dao.dropTable(db, ifExists);
    RestUser_Dao.dropTable(db, ifExists);
    RestContact_Dao.dropTable(db, ifExists);
    RestEndpoint_Dao.dropTable(db, ifExists);
    MeetingContact_Dao.dropTable(db, ifExists);
    MeetingUser_Dao.dropTable(db, ifExists);
    MeetingEndpoint_Dao.dropTable(db, ifExists);
    RestCallRecord_Dao.dropTable(db, ifExists);
    RestCallRow_Dao.dropTable(db, ifExists);
  }

  public static abstract class OpenHelper extends SQLiteOpenHelper {

    public OpenHelper(Context context, String name, CursorFactory factory) {
      super(context, name, factory, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
      createAllTables(db, false);
    }
  }

  /**
   * WARNING: Drops all table on Upgrade! Use only during development.
   */
  public static class DevOpenHelper extends OpenHelper {

    public DevOpenHelper(Context context, String name, CursorFactory factory) {
      super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
      dropAllTables(db, true);
      onCreate(db);
    }
  }

  public DaoMaster(SQLiteDatabase db) {
    super(db, SCHEMA_VERSION);
    registerDaoClass(RestMeeting_Dao.class);
    registerDaoClass(RestUser_Dao.class);
    registerDaoClass(RestContact_Dao.class);
    registerDaoClass(RestEndpoint_Dao.class);
    registerDaoClass(MeetingContact_Dao.class);
    registerDaoClass(MeetingUser_Dao.class);
    registerDaoClass(MeetingEndpoint_Dao.class);
    registerDaoClass(RestCallRecord_Dao.class);
    registerDaoClass(RestCallRow_Dao.class);
  }

  public DaoSession newSession() {
    return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
  }

  public DaoSession newSession(IdentityScopeType type) {
    return new DaoSession(db, type, daoConfigMap);
  }

}