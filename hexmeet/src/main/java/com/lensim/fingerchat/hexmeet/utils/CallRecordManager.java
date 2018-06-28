package com.lensim.fingerchat.hexmeet.utils;



import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.db.DaoSession;
import com.lensim.fingerchat.hexmeet.db.RestCallRecord_;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_Dao;
import com.lensim.fingerchat.hexmeet.type.DatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition.StringCondition;

public class CallRecordManager {

  private static boolean isDataTranfered = false;

  private static DaoSession getSession() {
    return DatabaseHelper.getSession(App.getContext(), DatabaseHelper.DatabaseType.CALLROW_LIST);
  }

  public static List<RestCallRow_> getLatestCallRecordPerPeerSip(String selfSip) {
    if (!isDataTranfered) {
      DaoSession session = DatabaseHelper.getSession(App.getContext(), DatabaseHelper.DatabaseType.CALLRECORD_LIST);
      List<RestCallRecord_> records = session.queryBuilder(RestCallRecord_.class).list();
      if (records.size() > 0) {
        for (RestCallRecord_ record : records) {
          RestCallRow_ callRow = new RestCallRow_();
          boolean isOutgoing = selfSip.equals(record.getFromSipNum() + "");
          callRow.setPeerSipNum(isOutgoing ? record.getToSipNum() + "" : record.getFromSipNum() + "");
          callRow.setIsOutgoing(isOutgoing);
          callRow.setIsVideoCall(record.getIsVideoCall());
          callRow.setStartTime(record.getStartTime());
          callRow.setDuration(record.getDuration());
          getSession().insert(callRow);
        }

        for (RestCallRecord_ record : records) {
          session.delete(record);
        }
      }
      isDataTranfered = true;
    }

    QueryBuilder<RestCallRow_> qb = getSession().queryBuilder(RestCallRow_.class);
    qb.where(new StringCondition("NOT EXISTS (SELECT 1 FROM REST_CALL_ROW_ " +
        "WHERE T.PEER_SIP_NUM=PEER_SIP_NUM AND T.START_TIME<START_TIME) ORDER BY START_TIME DESC"));
    return qb.list();
  }

  public static List<RestCallRow_> getCallRecords(String peerSip) {
    QueryBuilder<RestCallRow_> qb = getSession().queryBuilder(RestCallRow_.class);
    qb.where(RestCallRow_Dao.Properties.PeerSipNum.eq(peerSip));
    qb.orderDesc(RestCallRow_Dao.Properties.StartTime);
    qb.limit(200);
    return qb.list();
  }

  public static Map<String, Integer> getMap_peerSip_missedCount() {
    QueryBuilder<RestCallRow_> qb = getSession().queryBuilder(RestCallRow_.class);
    qb.where(new StringCondition("T.DURATION=0 AND NOT EXISTS (SELECT 1 FROM REST_CALL_ROW_ " +
        "WHERE T.PEER_SIP_NUM=PEER_SIP_NUM AND T.START_TIME<START_TIME AND DURATION>0)"));

    Map<String, Integer> map = new HashMap<String, Integer>();
    for (RestCallRow_ record : qb.list()) {
      Integer missCount = map.get(record.getPeerSipNum());
      if (missCount == null) {
        map.put(record.getPeerSipNum(), 1);
      } else {
        map.put(record.getPeerSipNum(), missCount + 1);
      }
    }
    return map;
  }

  public static long insert(RestCallRow_ callRow) {
    return getSession().insert(callRow);
  }

  public static void delete(String peerSip) {
    QueryBuilder<RestCallRow_> qb = getSession().queryBuilder(RestCallRow_.class);
    qb.where(RestCallRow_Dao.Properties.PeerSipNum.eq(peerSip));
    List<RestCallRow_> records = qb.list();
    for (RestCallRow_ record : records) {
      getSession().delete(record);
    }
  }

  public static void delete(RestCallRow_ callRow) {
    getSession().delete(callRow);
  }

  public static void update(RestCallRow_ callRow) {
    getSession().update(callRow);
  }

  public static void reset() {
    isDataTranfered = false;
  }
}
