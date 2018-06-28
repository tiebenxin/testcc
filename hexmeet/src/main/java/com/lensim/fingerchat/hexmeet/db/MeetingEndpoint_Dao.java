package com.lensim.fingerchat.hexmeet.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "MEETING_ENDPOINT_".
 */
public class MeetingEndpoint_Dao extends AbstractDao<MeetingEndpoint_, Long> {

  public static final String TABLENAME = "MEETING_ENDPOINT_";

  /**
   * Properties of entity MeetingEndpoint_.<br/>
   * Can be used for QueryBuilder and for referencing column names.
   */
  public static class Properties {

    public final static Property Id = new Property(0, Long.class, "id", true, "_id");
    public final static Property MeetingId = new Property(1, Long.class, "meetingId", false, "MEETING_ID");
    public final static Property EndpointId = new Property(2, Long.class, "endpointId", false, "ENDPOINT_ID");
  }

  ;

  private DaoSession daoSession;

  private Query<MeetingEndpoint_> restMeeting__MeetingEndpointsQuery;

  public MeetingEndpoint_Dao(DaoConfig config) {
    super(config);
  }

  public MeetingEndpoint_Dao(DaoConfig config, DaoSession daoSession) {
    super(config, daoSession);
    this.daoSession = daoSession;
  }

  /**
   * Creates the underlying database table.
   */
  public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
    String constraint = ifNotExists ? "IF NOT EXISTS " : "";
    db.execSQL("CREATE TABLE " + constraint + "\"MEETING_ENDPOINT_\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
        "\"MEETING_ID\" INTEGER," + // 1: meetingId
        "\"ENDPOINT_ID\" INTEGER);"); // 2: endpointId
  }

  /**
   * Drops the underlying database table.
   */
  public static void dropTable(SQLiteDatabase db, boolean ifExists) {
    String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEETING_ENDPOINT_\"";
    db.execSQL(sql);
  }

  /**
   * @inheritdoc
   */
  @Override
  protected void bindValues(SQLiteStatement stmt, MeetingEndpoint_ entity) {
    stmt.clearBindings();

    Long id = entity.getId();
    if (id != null) {
      stmt.bindLong(1, id);
    }

    Long meetingId = entity.getMeetingId();
    if (meetingId != null) {
      stmt.bindLong(2, meetingId);
    }

    Long endpointId = entity.getEndpointId();
    if (endpointId != null) {
      stmt.bindLong(3, endpointId);
    }
  }

  @Override
  protected void attachEntity(MeetingEndpoint_ entity) {
    super.attachEntity(entity);
    entity.__setDaoSession(daoSession);
  }

  /**
   * @inheritdoc
   */
  @Override
  public Long readKey(Cursor cursor, int offset) {
    return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
  }

  /**
   * @inheritdoc
   */
  @Override
  public MeetingEndpoint_ readEntity(Cursor cursor, int offset) {
    MeetingEndpoint_ entity = new MeetingEndpoint_( //
        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
        cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // meetingId
        cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // endpointId
    );
    return entity;
  }

  /**
   * @inheritdoc
   */
  @Override
  public void readEntity(Cursor cursor, MeetingEndpoint_ entity, int offset) {
    entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
    entity.setMeetingId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
    entity.setEndpointId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
  }

  /**
   * @inheritdoc
   */
  @Override
  protected Long updateKeyAfterInsert(MeetingEndpoint_ entity, long rowId) {
    entity.setId(rowId);
    return rowId;
  }

  /**
   * @inheritdoc
   */
  @Override
  public Long getKey(MeetingEndpoint_ entity) {
    if (entity != null) {
      return entity.getId();
    } else {
      return null;
    }
  }

  /**
   * @inheritdoc
   */
  @Override
  protected boolean isEntityUpdateable() {
    return true;
  }

  /**
   * Internal query to resolve the "meetingEndpoints" to-many relationship of RestMeeting_.
   */
  public List<MeetingEndpoint_> _queryRestMeeting__MeetingEndpoints(Long meetingId) {
    synchronized (this) {
      if (restMeeting__MeetingEndpointsQuery == null) {
        QueryBuilder<MeetingEndpoint_> queryBuilder = queryBuilder();
        queryBuilder.where(Properties.MeetingId.eq(null));
        restMeeting__MeetingEndpointsQuery = queryBuilder.build();
      }
    }
    Query<MeetingEndpoint_> query = restMeeting__MeetingEndpointsQuery.forCurrentThread();
    query.setParameter(0, meetingId);
    return query.list();
  }

  private String selectDeep;

  protected String getSelectDeep() {
    if (selectDeep == null) {
      StringBuilder builder = new StringBuilder("SELECT ");
      SqlUtils.appendColumns(builder, "T", getAllColumns());
      builder.append(',');
      SqlUtils.appendColumns(builder, "T0", daoSession.getRestEndpoint_Dao().getAllColumns());
      builder.append(',');
      SqlUtils.appendColumns(builder, "T1", daoSession.getRestMeeting_Dao().getAllColumns());
      builder.append(" FROM MEETING_ENDPOINT_ T");
      builder.append(" LEFT JOIN REST_ENDPOINT_ T0 ON T.\"ENDPOINT_ID\"=T0.\"_id\"");
      builder.append(" LEFT JOIN REST_MEETING_ T1 ON T.\"MEETING_ID\"=T1.\"_id\"");
      builder.append(' ');
      selectDeep = builder.toString();
    }
    return selectDeep;
  }

  protected MeetingEndpoint_ loadCurrentDeep(Cursor cursor, boolean lock) {
    MeetingEndpoint_ entity = loadCurrent(cursor, 0, lock);
    int offset = getAllColumns().length;

    RestEndpoint_ endpoint = loadCurrentOther(daoSession.getRestEndpoint_Dao(), cursor, offset);
    entity.setEndpoint(endpoint);
    offset += daoSession.getRestEndpoint_Dao().getAllColumns().length;

    RestMeeting_ meeting = loadCurrentOther(daoSession.getRestMeeting_Dao(), cursor, offset);
    entity.setMeeting(meeting);

    return entity;
  }

  public MeetingEndpoint_ loadDeep(Long key) {
    assertSinglePk();
    if (key == null) {
      return null;
    }

    StringBuilder builder = new StringBuilder(getSelectDeep());
    builder.append("WHERE ");
    SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
    String sql = builder.toString();

    String[] keyArray = new String[]{key.toString()};
    Cursor cursor = db.rawQuery(sql, keyArray);

    try {
      boolean available = cursor.moveToFirst();
      if (!available) {
        return null;
      } else if (!cursor.isLast()) {
        throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
      }
      return loadCurrentDeep(cursor, true);
    } finally {
      cursor.close();
    }
  }

  /**
   * Reads all available rows from the given cursor and returns a list of new ImageTO objects.
   */
  public List<MeetingEndpoint_> loadAllDeepFromCursor(Cursor cursor) {
    int count = cursor.getCount();
    List<MeetingEndpoint_> list = new ArrayList<MeetingEndpoint_>(count);

    if (cursor.moveToFirst()) {
      if (identityScope != null) {
        identityScope.lock();
        identityScope.reserveRoom(count);
      }
      try {
        do {
          list.add(loadCurrentDeep(cursor, false));
        } while (cursor.moveToNext());
      } finally {
        if (identityScope != null) {
          identityScope.unlock();
        }
      }
    }
    return list;
  }

  protected List<MeetingEndpoint_> loadDeepAllAndCloseCursor(Cursor cursor) {
    try {
      return loadAllDeepFromCursor(cursor);
    } finally {
      cursor.close();
    }
  }


  /**
   * A raw-style query where you can pass any WHERE clause and arguments.
   */
  public List<MeetingEndpoint_> queryDeep(String where, String... selectionArg) {
    Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
    return loadDeepAllAndCloseCursor(cursor);
  }

}