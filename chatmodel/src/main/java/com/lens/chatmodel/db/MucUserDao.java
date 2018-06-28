package com.lens.chatmodel.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fingerchat.proto.message.Muc;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/15.
 * 群聊成员
 */

public class MucUserDao extends BaseDao<Muc.MucMemberItem> {

    @Override
    public Muc.MucMemberItem selectSingle(String id) {
        return null;
    }

    @Override
    public List<Muc.MucMemberItem> selectAll() {
        return null;
    }

    public List<Muc.MucMemberItem> selectAllById(String mucId, int count) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(
                "select * from " + DBHelper.TABLE_PRIVATE_MCU_USER + " where " + DBHelper.MUC_ID
                    + "" + String.format("%s=?", "") + (-1 == count ? "" : " limit " + count),
                new String[]{mucId});
            List<Muc.MucMemberItem> memberItems = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                Muc.MucMemberItem.Builder item = Muc.MucMemberItem.newBuilder();
                cursor.moveToNext();
                item.setRoleValue(cursor.getInt(2))
                    .setUsername(cursor.getString(3))
                    .setUsernick(cursor.getString(4))
                    .setMucusernick(cursor.getString(5))
                    .setInviter(cursor.getString(6))
                    .setAvatar(cursor.getString(7));
                if (Muc.Role.Owner.ordinal() == item.getRoleValue()) {
                    memberItems.add(0, item.build());
                } else {
                    memberItems.add(item.build());
                }
            }
            db.setTransactionSuccessful();
            return memberItems;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }

    /**
     * 查询单个群成员
     */
    public Muc.MucMemberItem selectUserById(String mucId, String userId) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(
                "select * from " + DBHelper.TABLE_PRIVATE_MCU_USER + " where " + DBHelper.MUC_ID
                    + "" + String.format("%s=?", "") + String
                    .format("and %s=?", DBHelper.GROUP_USERNAME), new String[]{mucId, userId});
            Muc.MucMemberItem.Builder item = null;
            if (cursor.moveToNext()) {
                item = Muc.MucMemberItem.newBuilder();
                item.setRoleValue(cursor.getInt(2))
                    .setUsername(cursor.getString(3))
                    .setUsernick(cursor.getString(4))
                    .setMucusernick(cursor.getString(5))
                    .setInviter(cursor.getString(6))
                    .setAvatar(cursor.getString(7));
            }
            db.setTransactionSuccessful();
            if (item != null) {
                return item.build();
            }
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }

    @Override
    public boolean insert(Muc.MucMemberItem mucItem) {
        return false;
    }

    public boolean insertMultiple(List<Muc.MucMemberItem> mucItems, String mucId) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            long result = -1;
            for (Muc.MucMemberItem item : mucItems) {
                result = builderMucUser(db, mucId, item);
            }
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
        return false;
    }

    @Override
    public boolean delete(String id) {
        if (!TextUtils.isEmpty(id)) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                int result = db
                    .delete(DBHelper.TABLE_PRIVATE_MCU_USER, String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{id});
                db.setTransactionSuccessful();
                if (result != -1) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }

        }
        return false;
    }

    public boolean deleteByUserId(String id, String userId) {
        if (!TextUtils.isEmpty(id)) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                int result = db
                    .delete(DBHelper.TABLE_PRIVATE_MCU_USER,
                        String.format("%s=?", DBHelper.MUC_ID) + String
                            .format("and %s=?", DBHelper.GROUP_USERNAME),
                        new String[]{id, userId});
                db.setTransactionSuccessful();
                if (result != -1) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }

        }
        return false;
    }

    /**
     * 查询群头像url集合
     */
    public List<String> qMucUserAvatars(String mucId) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(
                "select " + DBHelper.GROUP_AVATAR + " from " + DBHelper.TABLE_PRIVATE_MCU_USER
                    + " where " + DBHelper.MUC_ID + " " + String.format("%s=?", "") + " limit 9 ",
                new String[]{mucId});
            List<String> avatars = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                avatars.add(TextUtils.isEmpty(cursor.getString(0)) ? "" : cursor.getString(0));
            }
            db.setTransactionSuccessful();
            return avatars;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }

    /**
     * 批量修改字段
     */
    public boolean updateById(String mucId, String userId, String lineName, Object values) {
        if (!TextUtils.isEmpty(mucId)) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                if (values instanceof String) {
                    values = "'" + values + "'";
                }
                db.execSQL("UPDATE " + DBHelper.TABLE_PRIVATE_MCU_USER + " SET " + lineName + " = "
                    + values + " where " + DBHelper.MUC_ID + " = " + mucId +
                    (TextUtils.isEmpty(userId) ? ""
                        : " and " + DBHelper.GROUP_USERNAME + " = " + userId));
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }

        }
        return false;
    }

    /**
     * @param db
     * @param mucId
     * @param item
     * @return
     */
    public long builderMucUser(SQLiteDatabase db, String mucId, Muc.MucMemberItem item) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.MUC_ID, mucId);
        values.put(DBHelper.GROUP_ROLE, item.getRoleValue());
        values.put(DBHelper.GROUP_USERNAME, item.getUsername());
        values.put(DBHelper.GROUP_USERNICK, item.getUsernick());
        values.put(DBHelper.GROUP_MUC_USERNICK, item.getMucusernick());
        values.put(DBHelper.GROUP_INVITER, item.getInviter());
        values.put(DBHelper.GROUP_AVATAR, item.getAvatar());
        return db.insert(DBHelper.TABLE_PRIVATE_MCU_USER, null, values);
    }


    public MucUserDao(Context context, String userId) {
        super(context, userId);
    }

    /**
     * 查询群备注名url集合
     */
    public List<String> qMucUserNick(String mucId) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(
                "select " + DBHelper.GROUP_MUC_USERNICK + " from " + DBHelper.TABLE_PRIVATE_MCU_USER
                    + " where " + DBHelper.MUC_ID + " " + String.format("%s=?", "") + " limit 5 ",
                new String[]{mucId});
            List<String> nicks = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                if (!TextUtils.isEmpty(cursor.getString(0))) {
                    nicks.add(cursor.getString(0));
                }
            }
            db.setTransactionSuccessful();
            return nicks;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }
}