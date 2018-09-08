package com.lensim.fingerchat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lensim.fingerchat.commons.utils.ExecutorHolder;
import com.lensim.fingerchat.commons.utils.L;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by LL130386 on 2017/12/13.
 */

public abstract class BaseDao<T> {

    FingerDBHelper helper;
    Context _context;

    public BaseDao(Context context, String userId) {
        if (userId == null || "".equals(userId)) {
            userId = checkUserIdInit(userId);
        }
        helper = FingerDBHelper.getInstance(context, userId);
        _context = context;
    }

    private String checkUserIdInit(String userId) {
        if (userId == null || "".equals(userId)) {

        }
        return userId;
    }

    public final SQLiteDatabase openReader() {
        return helper.getReadableDatabase();
    }

    public final SQLiteDatabase openWriter() {
        return helper.getWritableDatabase();
    }

    public final void closeDatabase(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
//    if (database != null && database.isOpen()) {
//      database.close();
//    }
    }

    public T selectSingle(String id) {
        return null;
    }

    public List<T> selectAll() {
        return null;
    }

    public List<T> selectAsPage(String user, int pager, int number) {
        return null;
    }


    public abstract boolean insert(T t);

    public abstract boolean delete(String id);

    public boolean waitResult(Future<Boolean> future) {
        try {
            return future.get();
        } catch (Exception ex) {
            L.e(ex);
        }
        return false;
    }

    public ExecutorService getPool() {
        return helper.getPool();
    }

    public Object getDBLock(){
        return helper.getDBLock();
    }

}
