package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.bean.EmoBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/8/7.
 */

public class EmoticonDao extends BaseDao<EmoBean> {

    public EmoticonDao(Context context, String userId) {
        super(context, userId);
    }

    @Override
    public List<EmoBean> selectAll() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            List<EmoBean> list = new ArrayList<>();
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_EMOTICON,
                    new String[]{DBHelper.EMO_ID, DBHelper.EMO_CONTENT}, null, null, null, null,
                    DBHelper.TIME + " ASC");
                EmoBean bean = null;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        bean = createBean(cursor);
                        list.add(bean);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return list;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return list;
        }
    }


    @Override
    public boolean insert(EmoBean bean) {
        synchronized (getDBLock()) {
            if (bean != null) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.EMO_ID, bean.getKey());
                    values.put(DBHelper.EMO_CONTENT, bean.getContent());
                    values.put(DBHelper.EMO_TIME, bean.getTime());
                    long result = db.insert(DBHelper.TABLE_EMOTICON, null, values);
                    db.setTransactionSuccessful();
                    if (result != -1) {
                        return true;
                    }
                } catch (Exception e) {
                    L.e(e);
                } finally {
                    db.endTransaction();
                    closeDatabase(db, null);
                }

            }
            return false;
        }
    }

    public boolean updateEmo(EmoBean bean) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.EMO_ID, bean.getKey());
                values.put(DBHelper.EMO_CONTENT, bean.getContent());
                values.put(DBHelper.EMO_TIME, bean.getTime());

                int result = db
                    .update(DBHelper.TABLE_EMOTICON, values, String.format("%s=?", DBHelper.EMO_ID),
                        new String[]{bean.getKey()});
                db.setTransactionSuccessful();
                if (result > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    private EmoBean createBean(Cursor cursor) {
        EmoBean bean = new EmoBean();
        if (!TextUtils.isEmpty(cursor.getString(0))) {
            bean.setKey(cursor.getString(0));
        }
        ImageUploadEntity entity = ImageUploadEntity.fromJson(cursor.getString(1));
        if (entity != null) {
            bean.setValue(entity);
        }
        bean.setTime(cursor.getLong(2));
        return bean;
    }

}
