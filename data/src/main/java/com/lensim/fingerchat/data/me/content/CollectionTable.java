package com.lensim.fingerchat.data.me.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class CollectionTable extends SQLiteOpenHelper {

    private final static CollectionTable instance = new CollectionTable();
    private static final String DATABASE_NAME = "fingerIM";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "collection";


    private static final class Fields {

        static final String _ID = "_id";
        static final String USER = "user";
        static final String UNIQUEID = "uniqueid";
        static final String PROVIDER = "provider";
        static final String NICKNAME = "nickname";
        static final String CONTENT = "content";
        static final String THUMB = "thumb";
        static final String URI = "uri";
        static final String TYPE = "type";
        static final String DES = "des";
        static final String TIME = "time";
        static final String USERNAME = "username";
    }

    private CollectionTable() {
        super(ContextHelper.getApplication(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql;
        sql = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + Fields._ID
            + " INTEGER ,"
            + Fields.UNIQUEID
            + " TEXT PRIMARY KEY,"
            + Fields.USER
            + " TEXT, "
            + Fields.PROVIDER
            + " TEXT, "
            + Fields.NICKNAME
            + " TEXT, "
            + Fields.CONTENT
            + " TEXT, "
            + Fields.THUMB
            + " TEXT, "
            + Fields.URI
            + " TEXT, "
            + Fields.TYPE
            + " INTEGER, "
            + Fields.DES
            + " TEXT, "
            + Fields.TIME
            + " INTEGER, "
            + Fields.USERNAME
            + " TEXT);";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static CollectionTable getInstance() {
        return instance;
    }

    long add(String uniqueid, String user, String nick, String provider, String content,
        String thumb, String uri, int type, String des, long time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Fields.UNIQUEID, uniqueid);
        values.put(Fields.USER, user);
        values.put(Fields.NICKNAME, nick);
        values.put(Fields.PROVIDER, provider);
        values.put(Fields.CONTENT, content);
        values.put(Fields.THUMB, thumb);
        values.put(Fields.URI, uri);
        values.put(Fields.TYPE, type);
        values.put(Fields.DES, des);
        values.put(Fields.TIME, time);
        values.put(Fields.USERNAME, UserInfoRepository.getUserName());
        return db.insert(TABLE_NAME, null, values);
    }

    public int deleteItemByUniqueID(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {id};
        return db.delete(TABLE_NAME, Fields.UNIQUEID + "=?", args);
    }


    public boolean checkItemExistByID(String uniqueID) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db
            .query(TABLE_NAME, null, CollectionTable.Fields.UNIQUEID + "=?", new String[]{uniqueID},
                null, null, CollectionTable.Fields.TIME + " desc ");
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {

            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public Cursor query(String content) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projections = new String[]{
            Fields._ID,
            Fields.UNIQUEID,
            Fields.USER,
            Fields.PROVIDER,
            Fields.NICKNAME,
            Fields.CONTENT,
            Fields.THUMB,
            Fields.URI,
            Fields.TYPE,
            Fields.DES,
            Fields.TIME,
            Fields.USERNAME};

        Cursor cursor = db.query(TABLE_NAME, projections, "((" +
                Fields.CONTENT + " like '%" + content + "%' and " +
                Fields.TYPE + "=" + 1 + " ) or " +
                Fields.DES + " like '%" + content + "%' ) and " +
                Fields.USERNAME + "=?", new String[]{UserInfoRepository.getUserName()}, null, null,
            Fields.TIME + " desc ", null);

        if (cursor == null) {
            Log.i("queryContacts", "结果集为空");
            return null;
        }
        return cursor;
    }

    FavJson createStore(Cursor cursor) {
        FavJson fav = new FavJson();
        fav.setProviderJid(cursor.getString(cursor.getColumnIndex(Fields.USER)));
        fav.setFavMsgId(cursor.getString(cursor.getColumnIndex(Fields.UNIQUEID)));
        fav.setProviderNick(cursor.getString(cursor.getColumnIndex(Fields.NICKNAME)));
        fav.setFavProvider(cursor.getString(cursor.getColumnIndex(Fields.PROVIDER)));
        String string = cursor.getString(cursor.getColumnIndex(Fields.CONTENT));
        String replace = string.replace("''", "'");
        fav.setFavContent(replace);
        fav.setFavCreaterAvatar(cursor.getString(cursor.getColumnIndex(Fields.THUMB)));
        fav.setFavUrl(cursor.getString(cursor.getColumnIndex(Fields.URI)));
        fav.setFavType(cursor.getInt(cursor.getColumnIndex(Fields.TYPE)) + "");
        fav.setFavDes(cursor.getString(cursor.getColumnIndex(Fields.DES)));
        fav.setFavTime(
            TimeUtils.getDateString(cursor.getLong(cursor.getColumnIndex(Fields.TIME)) + ""));
        return fav;
    }

    Cursor list(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db
            .query(TABLE_NAME, null, Fields.USERNAME + "=?", new String[]{username}, null, null,
                Fields.TIME + " desc ");
    }


    public Cursor queryByType(int type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "(" +Fields.TYPE + "=" + type + " ) and " +
            Fields.USERNAME + "=?", new String[]{UserInfoRepository.getUserName()}, null, null, Fields.TIME + " desc ",null);
        if (cursor == null) {
            Log.i("queryContacts", "结果集为空");
            return null;
        }
        return cursor;
    }

    /**
     * 分页加载
     */
    Cursor listByPage(String username, String pagenum, String pageSize) {
        SQLiteDatabase db = getReadableDatabase();
        String start = "0";
        try {
            int size = Integer.parseInt(pageSize);
            start = String.valueOf(Integer.parseInt(pagenum) * size);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return db.query(TABLE_NAME, null, Fields.USERNAME + "=?", new String[]{username}, null, null, Fields.TIME + " desc ", start + "," + pageSize);
    }

    public int updateItemDES(String uniqueID, String DES) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Fields.DES, DES);
        String[] args = {String.valueOf(uniqueID)};
        String where = Fields.UNIQUEID + "=?";
        return db.update(TABLE_NAME, cv, where, args);
    }
}