package com.lensim.fingerchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lensim.fingerchat.commons.utils.ExecutorHolder;
import com.lensim.fingerchat.commons.utils.L;
import java.util.concurrent.ExecutorService;

/**
 * Created by LL130386 on 2017/12/13.
 */

public class FingerDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 3;
    public static final String DB_NAME = "lens.db";
    private static String userID;
    private ExecutorService pool;
    private Object mLock;


    String CREATE_ROSTER_TABLE = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),"
            + "%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s long(10),%s integer(10),%s integer(10),%s integer(10),%s varchar(10),%s integer(10))",
        DBHelper.TABLE_ROSTER, DBHelper.ACCOUT, DBHelper.USER_NICK, DBHelper.WORK_ADDRESS,
        DBHelper.GROUP, DBHelper.EMP_NAME, DBHelper.REMARK_NAME, DBHelper.SEX, DBHelper.IMAGE,
        DBHelper.IS_VALID, DBHelper.JOB_NAME, DBHelper.DPT_NO, DBHelper.DPT_NAME,
        DBHelper.EMP_NO, DBHelper.IS_BLOCK, DBHelper.SHORT, DBHelper.PINYIN, DBHelper.STATUS,
        DBHelper.TIME, DBHelper.HAS_READED, DBHelper.NEW_STATUS, DBHelper.IS_STAR,
        DBHelper.CHAT_BG, DBHelper.IS_QUIT);

    String CREATE_MESSAGE_TABLE_V2 = String.format(
        "create table %s (_id integer primary key autoincrement,%s integer(2),%s varchar(10),%s varchar(10),%s varchar(10),%s long(10),"
            + "%s varchar(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10),"
            + "%s integer(10),%s varchar(10),%s varchar(10),%s integer(10),%s integer(10))",
        DBHelper.TABLE_MESSAGE, DBHelper.TYPE, DBHelper.TO, DBHelper.CONTENT, DBHelper.ID,
        DBHelper.TIME, DBHelper.CODE, DBHelper.CANCLE, DBHelper.FROM, DBHelper.SEND_TYPE,
        DBHelper.CHAT_TAG, DBHelper.UPLOAD_URL, DBHelper.IS_SECRET, DBHelper.PLAY_STATUS,
        DBHelper.ACTION_TYPE, DBHelper.USER_AVATAR, DBHelper.NICK, DBHelper.CHAT_TYPE,
        DBHelper.HAS_READED);

    String CREATE_MESSAGE_TABLE_V3 = String.format(
        "create table %s (_id integer primary key autoincrement,%s integer(2),%s varchar(10),%s varchar(10),%s varchar(10),%s long(10),"
            + "%s varchar(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10),"
            + "%s integer(10),%s varchar(10),%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s integer(10))",
        DBHelper.TABLE_MESSAGE, DBHelper.TYPE, DBHelper.TO, DBHelper.CONTENT, DBHelper.ID,
        DBHelper.TIME, DBHelper.CODE, DBHelper.CANCLE, DBHelper.FROM, DBHelper.SEND_TYPE,
        DBHelper.CHAT_TAG, DBHelper.UPLOAD_URL, DBHelper.IS_SECRET, DBHelper.PLAY_STATUS,
        DBHelper.ACTION_TYPE, DBHelper.USER_AVATAR, DBHelper.NICK, DBHelper.CHAT_TYPE,
        DBHelper.HAS_READED, DBHelper.READED_MEMBERS, DBHelper.SERVER_READED);

    String CREATE_MESSAGE_TABLE_V1 = String.format(
        "create table %s (_id integer primary key autoincrement,%s integer(2),%s varchar(10),%s varchar(10),%s varchar(10),%s long(10),%s varchar(10),%s integer(10),"
            + "%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10),%s integer(10),%s varchar(10),%s varchar(10),%s integer(10))",
        DBHelper.TABLE_MESSAGE, DBHelper.TYPE, DBHelper.TO, DBHelper.CONTENT, DBHelper.ID,
        DBHelper.TIME, DBHelper.CODE, DBHelper.CANCLE, DBHelper.FROM, DBHelper.SEND_TYPE,
        DBHelper.CHAT_TAG, DBHelper.UPLOAD_URL, DBHelper.IS_SECRET, DBHelper.PLAY_STATUS,
        DBHelper.ACTION_TYPE, DBHelper.USER_AVATAR, DBHelper.NICK, DBHelper.CHAT_TYPE);

    String CREATE_RECENT_MSG_TABLE_V1 = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s varchar(10),"
            + "%s long(10),%s integer(10),%s varchar(10),%s integer(10),%s varchar(10),%s varchar(10),%s varchar(10),%s integer(10),%s integer(10))",
        DBHelper.TABLE_RECENT, DBHelper.NICK, DBHelper.GROUP_NAME, DBHelper.USER_ID,
        DBHelper.TOP_FLAG, DBHelper.NOT_DISTURB, DBHelper.TIME, DBHelper.IS_AT, DBHelper.CHAT_ID,
        DBHelper.AVATAR_URL, DBHelper.CHAT_TYPE, DBHelper.BG_ID, DBHelper.MSG, DBHelper.HINT,
        DBHelper.UNREAD_COUNT, DBHelper.MSG_TYPE);

    String CREATE_RECENT_MSG_TABLE_V2 = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(2),%s varchar(10),%s integer(10),%s integer(10),%s varchar(10),%s varchar(10),"
            + "%s long(10),%s integer(10),%s varchar(10),%s integer(10),%s integer(10))",
        DBHelper.TABLE_RECENT, DBHelper.NICK, DBHelper.GROUP_NAME, DBHelper.USER_ID,
        DBHelper.TOP_FLAG, DBHelper.NOT_DISTURB, DBHelper.TIME, DBHelper.IS_AT,
        DBHelper.CHAT_ID, DBHelper.AVATAR_URL, DBHelper.CHAT_TYPE, DBHelper.BG_ID);

    String CREATE_MUC_INFO = String.format(
        "create table %s (_id integer primary key autoincrement,%s varchar(10) not null,%s varchar(10),%s varchar(10),"
            + "%s integer,%s integer,%s integer,%s integer,%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
        DBHelper.TABLE_MUC_INFO, DBHelper.MUC_ID, DBHelper.MUC_NAME, DBHelper.SUBJECT,
        DBHelper.AUTOENTER, DBHelper.MEMBERCOUNT, DBHelper.ROLE, DBHelper.NOTDISTURB,
        DBHelper.CHATBG, DBHelper.MUC_USERNICK, DBHelper.CREATION_TIME, DBHelper.CREATOR);

    String CREATE_MUC_USER = String
        .format("create table %s (_id integer primary key autoincrement," +
                "%s varchar(10),%s integer,%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10),%s varchar(10))",
            DBHelper.TABLE_MUC_USER, DBHelper.MUC_ID, DBHelper.GROUP_ROLE,
            DBHelper.GROUP_USERNAME, DBHelper.GROUP_USERNICK,
            DBHelper.GROUP_MUC_USERNICK, DBHelper.GROUP_INVITER, DBHelper.GROUP_AVATAR);

    String recent_fields_v1 = String
        .format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", DBHelper.NICK, DBHelper.GROUP_NAME,
            DBHelper.USER_ID, DBHelper.TOP_FLAG, DBHelper.NOT_DISTURB, DBHelper.TIME,
            DBHelper.IS_AT, DBHelper.CHAT_ID, DBHelper.AVATAR_URL, DBHelper.CHAT_TYPE,
            DBHelper.BG_ID);

    String recent_fields_v2 = String
        .format("(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)", DBHelper.NICK,
            DBHelper.GROUP_NAME, DBHelper.USER_ID, DBHelper.TOP_FLAG, DBHelper.NOT_DISTURB,
            DBHelper.TIME, DBHelper.IS_AT, DBHelper.CHAT_ID, DBHelper.AVATAR_URL,
            DBHelper.CHAT_TYPE, DBHelper.BG_ID);

    String message_update_v2 = String
        .format("alter table %s add column %s varchar", DBHelper.TABLE_MESSAGE,
            DBHelper.HAS_READED);

    String message_v3_add_members = String
        .format("alter table %s add column %s varchar", DBHelper.TABLE_MESSAGE,
            DBHelper.READED_MEMBERS);

    String message_v3_add_read = String
        .format("alter table %s add column %s varchar", DBHelper.TABLE_MESSAGE,
            DBHelper.SERVER_READED);


    public FingerDBHelper(Context context, String userId) {
        super(context, userId + DB_NAME, null, DB_VERSION);
        pool = ExecutorHolder.getChatUpdatesExecutor();
        mLock = FingerDBHelper.class;
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
            create(db);
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
                for (int i = oldVersion; i < newVersion; i++) {
                    switch (i) {
                        case 1://旧版本为1，升级到2
                            upgradeV2(db);
                            break;
                        case 2://旧版本为2，升级到3
                            upgradeV3(db);
                            break;
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

    }

    private void create(SQLiteDatabase db) {
        db.execSQL(CREATE_ROSTER_TABLE);
        if (DB_VERSION == 1) {
            db.execSQL(CREATE_MESSAGE_TABLE_V1);
            db.execSQL(CREATE_RECENT_MSG_TABLE_V1);
        } else if (DB_VERSION == 2) {
            db.execSQL(CREATE_MESSAGE_TABLE_V2);
            db.execSQL(CREATE_RECENT_MSG_TABLE_V2);
        } else if (DB_VERSION == 3) {
            db.execSQL(CREATE_MESSAGE_TABLE_V3);
            db.execSQL(CREATE_RECENT_MSG_TABLE_V2);
        }
        db.execSQL(CREATE_MUC_INFO);
        db.execSQL(CREATE_MUC_USER);
    }


    public ExecutorService getPool() {
        if (pool == null) {
            pool = ExecutorHolder.getChatUpdatesExecutor();
        }
        return pool;
    }

    public Object getDBLock() {
        if (mLock == null) {
            mLock = FingerDBHelper.class;
        }
        return mLock;
    }

   /*
   *  更新数据库版本2
   *  1. recent 表删除 msg, hint,msg_type,unread_count字段, 不支持直接删除字段，所以只能通过copy
   *  2. message表新增 has_readed字段
   * */

    private void upgradeV2(SQLiteDatabase db) {
        //rename 旧表
        String temp_recent = String
            .format("alter table %s rename to temp_recent", DBHelper.TABLE_RECENT);
        db.execSQL(temp_recent);
        //创建新表
        db.execSQL(CREATE_RECENT_MSG_TABLE_V2);
        //将旧表数据拷贝到新表
        String copy_recent = String.format(
            "insert into %s " + recent_fields_v2 + " select " + recent_fields_v1
                + " from temp_recent", DBHelper.TABLE_RECENT);
        db.execSQL(copy_recent);
        //删除临时表
        db.execSQL("drop table temp_recent");
        db.execSQL(message_update_v2);
    }

    /*
    * message表新增 readed_members 字段，记录消息已读的群成员，私聊即聊天对象
    * */
    private void upgradeV3(SQLiteDatabase db) {
        db.execSQL(message_v3_add_members);
        db.execSQL(message_v3_add_read);
    }
}
