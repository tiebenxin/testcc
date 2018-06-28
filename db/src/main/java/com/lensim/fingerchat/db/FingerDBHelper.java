package com.lensim.fingerchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lensim.fingerchat.commons.utils.L;

/**
 * Created by LL130386 on 2017/12/13.
 */

public class FingerDBHelper extends SQLiteOpenHelper {

    //    try {
//      db.beginTransaction();
//
//      db.setTransactionSuccessful();
//    } catch (Exception e) {
//      L.e(e);
//    } finally {
//      db.endTransaction();
//      closeDatabase(db, null);
//    }


    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "lens.db";
    private static String userID;


    String CREATE_ROSTER_TABLE = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
        DBHelper.TABLE_ROSTER, DBHelper.ACCOUT, DBHelper.USER_NICK, DBHelper.WORK_ADDRESS,
        DBHelper.GROUP, DBHelper.EMP_NAME, DBHelper.REMARK_NAME, DBHelper.SEX, DBHelper.IMAGE,
        DBHelper.IS_VALID, DBHelper.JOB_NAME, DBHelper.DPT_NO, DBHelper.DPT_NAME,
        DBHelper.EMP_NO, DBHelper.IS_BLOCK, DBHelper.SHORT, DBHelper.PINYIN, DBHelper.STATUS,
        DBHelper.TIME, DBHelper.HAS_READED, DBHelper.NEW_STATUS, DBHelper.IS_STAR,
        DBHelper.CHAT_BG, DBHelper.IS_QUIT);


    String CREATE_MESSAGE_TABLE = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
        DBHelper.TABLE_MESSAGE, DBHelper.TYPE, DBHelper.TO, DBHelper.CONTENT, DBHelper.ID,
        DBHelper.TIME, DBHelper.CODE, DBHelper.CANCLE, DBHelper.FROM, DBHelper.SEND_TYPE,
        DBHelper.CHAT_TAG, DBHelper.UPLOAD_URL, DBHelper.IS_SECRET, DBHelper.PLAY_STATUS,
        DBHelper.ACTION_TYPE, DBHelper.USER_AVATAR, DBHelper.NICK, DBHelper.CHAT_TYPE);

    String CREATE_RECENT_MSG_TABLE = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
        DBHelper.TABLE_RECENT, DBHelper.MSG, DBHelper.NICK, DBHelper.GROUP_NAME, DBHelper.MSG_TYPE,
        DBHelper.USER_ID, DBHelper.TOP_FLAG, DBHelper.UNREAD_COUNT, DBHelper.NOT_DISTURB,
        DBHelper.TIME, DBHelper.IS_AT, DBHelper.CHAT_ID, DBHelper.AVATAR_URL, DBHelper.CHAT_TYPE,
        DBHelper.HINT, DBHelper.BG_ID);

    String CREATE_MUC_INFO = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(10) not null,%s varchar(10),%s varchar(10),"
            + "%s integer,%s integer,%s integer,%s integer,%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
        DBHelper.TABLE_MUC_INFO, DBHelper.MUC_ID, DBHelper.MUC_NAME, DBHelper.SUBJECT,
        DBHelper.AUTOENTER, DBHelper.MEMBERCOUNT, DBHelper.ROLE, DBHelper.NOTDISTURB,
        DBHelper.CHATBG, DBHelper.MUC_USERNICK, DBHelper.CREATION_TIME, DBHelper.CREATOR);

    String CREATE_MUC_USER = String
        .format("create table %s (_id integer primary key autoincrement," +
                "%s varchar(10),%s integer,%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
            DBHelper.TABLE_PRIVATE_MCU_USER, DBHelper.MUC_ID, DBHelper.GROUP_ROLE,
            DBHelper.GROUP_USERNAME, DBHelper.GROUP_USERNICK,
            DBHelper.GROUP_MUC_USERNICK, DBHelper.GROUP_INVITER, DBHelper.GROUP_AVATAR);


    public FingerDBHelper(Context context, String userId) {
        super(context, userId + DB_NAME, null, DB_VERSION);
    }

    static private FingerDBHelper _instance;

    static public FingerDBHelper getInstance(Context context, String userId) {
        if (userID != null) {
            if (userId.equals(userID)) {
                if (_instance == null) {
                    _instance = new FingerDBHelper(context, userId);
                }

            } else {
                _instance = new FingerDBHelper(context, userId);
                userID = userId;
            }
        } else {
            _instance = new FingerDBHelper(context, userId);
            userID = userId;
        }
        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createRosterTable(db);
            createPrivateMsgTable(db);
            createRecentMsgTable(db);
            createMucInfoTable(db);
            createMucUserTable(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.beginTransaction();
            try {
                createRosterTable(db);
                createPrivateMsgTable(db);
                createRecentMsgTable(db);
                createMucInfoTable(db);
                createMucUserTable(db);
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
            }
        }

    }

    public void createRosterTable(SQLiteDatabase db) {
        db.execSQL(CREATE_ROSTER_TABLE);
    }

    public void createPrivateMsgTable(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    public void createRecentMsgTable(SQLiteDatabase db) {
        db.execSQL(CREATE_RECENT_MSG_TABLE);
    }

    public void createMucInfoTable(SQLiteDatabase db) {
        db.execSQL(CREATE_MUC_INFO);
    }

    public void createMucUserTable(SQLiteDatabase db) {
        db.execSQL(CREATE_MUC_USER);
    }
}
