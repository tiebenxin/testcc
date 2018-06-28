package com.lensim.fingerchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lensim.fingerchat.db.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * Created by ll147996 on 2018/1/19.
 */

public class GreenDBOpenHelper extends DaoMaster.DevOpenHelper {

    public static final String TAG = "GreenDBOpenHelper";

    public GreenDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }


    /**
     * 默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 不做super.onUpgrade操作
        Log.w(TAG, "db version update from " + oldVersion + " to " + newVersion);

    }
}
