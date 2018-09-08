package com.lens.chatmodel.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.fingerchat.proto.message.Muc.Role;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
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
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db.query(DBHelper.TABLE_MUC_USER, null,
                    String.format("%s=?", DBHelper.MUC_ID), new String[]{mucId}, null, null,
                    DBHelper.ROLE + " desc ", count == -1 ? null : count + "");
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
    }

    /**
     * 查询单个群成员
     */
    public Muc.MucMemberItem selectUserById(String mucId, String userId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db.rawQuery(
                    "select * from " + DBHelper.TABLE_MUC_USER + " where " + DBHelper.MUC_ID
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
    }

    @Override
    public boolean insert(Muc.MucMemberItem mucItem) {
        return false;
    }

//    public boolean insertSingle(MucMemberItem mucItem, String mucId) {
//        synchronized (getDBLock()) {
//            SQLiteDatabase db = openWriter();
//            try {
//                db.beginTransaction();
//                boolean result = builderMucUser(db, mucId, mucItem);
//                db.setTransactionSuccessful();
//                return result;
//            } catch (Exception e) {
//                L.e(e);
//            } finally {
//                db.endTransaction();
//                closeDatabase(db, null);
//            }
//            return false;
//        }
//    }

    public boolean updateMultiple(List<Muc.MucMemberItem> mucItems, String mucId, String creator) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                boolean result = false;
                for (Muc.MucMemberItem item : mucItems) {
                    result = builderMucUser(db, mucId, item, creator);
                }
                db.setTransactionSuccessful();
                return result;
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
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(id)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    int result = db
                        .delete(DBHelper.TABLE_MUC_USER,
                            String.format("%s=?", DBHelper.MUC_ID),
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
    }

    public boolean deleteByUserId(String id, String userId) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(id)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    int result = db
                        .delete(DBHelper.TABLE_MUC_USER,
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
    }

    /**
     * 查询群头像url集合
     */
    public List<String> qMucUserAvatars(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db.rawQuery(
                    "select " + DBHelper.GROUP_AVATAR + " from " + DBHelper.TABLE_MUC_USER
                        + " where " + DBHelper.MUC_ID + " " + String.format("%s=?", "")
                        + " limit 9 ",
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
    }

    /**
     * 批量修改字段
     */
    public boolean updateById(String mucId, String userId, String lineName, Object values) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(mucId)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    if (values instanceof String) {
                        values = "'" + values + "'";
                    }
                    db.execSQL(
                        "UPDATE " + DBHelper.TABLE_MUC_USER + " SET " + lineName + " = "
                            + values + " where " + DBHelper.MUC_ID + " = " + mucId +
                            (TextUtils.isEmpty(userId) ? ""
                                : " and " + DBHelper.GROUP_USERNAME + " = " + "\'" + userId
                                    + "\'"));
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
    }

    /**
     * @param db
     * @param mucId
     * @param item
     * @return
     */
    public boolean builderMucUser(SQLiteDatabase db, String mucId, Muc.MucMemberItem item,
        String creator) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.MUC_ID, mucId);
        if (!TextUtils.isEmpty(creator) && creator.equalsIgnoreCase(item.getUsername())) {
            values.put(DBHelper.GROUP_ROLE, Role.Owner.getNumber());
        } else {
            values.put(DBHelper.GROUP_ROLE, item.getRoleValue());
        }

        values.put(DBHelper.GROUP_USERNAME, item.getUsername());
        values.put(DBHelper.GROUP_USERNICK, item.getUsernick());
        values.put(DBHelper.GROUP_MUC_USERNICK, item.getMucusernick());
        values.put(DBHelper.GROUP_INVITER, item.getInviter());
        values.put(DBHelper.GROUP_AVATAR, item.getAvatar());
        int result = db.update(DBHelper.TABLE_MUC_USER, values,
            String.format("%s=? and %s=?", DBHelper.MUC_ID, DBHelper.GROUP_USERNAME),
            new String[]{mucId, item.getUsername()});
        if (result <= 0) {
            db.insert(DBHelper.TABLE_MUC_USER, null, values);
            return true;
        }
        return true;

    }


    public MucUserDao(Context context, String userId) {
        super(context, userId);
    }

    /**
     * 查询群备注名url集合
     */
    public List<String> qMucUserNick(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db.rawQuery(
                    "select " + DBHelper.GROUP_MUC_USERNICK + " from "
                        + DBHelper.TABLE_MUC_USER
                        + " where " + DBHelper.MUC_ID + " " + String.format("%s=?", "")
                        + " limit 5 ",
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


    /**
     * 查询群成员的群备注名
     */
    public String getMucNick(String mucId, String userId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_MUC_USER,
                        new String[]{DBHelper.GROUP_MUC_USERNICK},
                        String.format("%s=? and %s=?", DBHelper.MUC_ID, DBHelper.GROUP_USERNAME),
                        new String[]{mucId, userId},
                        null, null,
                        null);
                String nick = userId;
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            nick = cursor.getString(0);
                        }
                    }
                    db.setTransactionSuccessful();
                    return nick;
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return null;
        }
    }

    public boolean deleteMemberUser(String mucId, String userId) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(mucId) && !TextUtils.isEmpty(userId)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    //删群
                    int result = db
                        .delete(DBHelper.TABLE_MUC_USER, String
                                .format("%s=? and %s=?", DBHelper.MUC_ID, DBHelper.GROUP_USERNAME),
                            new String[]{mucId, userId});
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
    }

    /*
    * 更新群备注名和昵称
    * */
    public boolean updateMemberNickAndAvatar(String mucId, String userId, String mucNick,
        String avatar) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.GROUP_MUC_USERNICK, mucNick);
                values.put(DBHelper.GROUP_AVATAR, avatar);
                int result = db
                    .update(DBHelper.TABLE_MUC_USER, values, String
                            .format("%s=? and %s=?", DBHelper.MUC_ID, DBHelper.GROUP_USERNAME),
                        new String[]{mucId, userId});
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

}