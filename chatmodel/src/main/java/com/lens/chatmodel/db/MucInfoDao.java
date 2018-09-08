package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.TextView;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.Role;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;
import com.lensim.fingerchat.db.DaoManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/19.
 * 群信息
 */

public class MucInfoDao extends BaseDao<Muc.MucItem> {

    @Override
    public Muc.MucItem selectSingle(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.rawQuery(
                    "select * from " + DBHelper.TABLE_MUC_INFO + " where " + DBHelper.MUC_ID
                        + "" + String.format("%s=?", ""), new String[]{mucId});
                Muc.MucItem.Builder mucItem = null;
                if (null != cursor && cursor.moveToNext()) {
                    mucItem = Muc.MucItem.newBuilder();
                    mucItem.setMucid(cursor.getString(1)).
                        setMucname(cursor.getString(2)).
                        setSubject(cursor.getString(3)).
                        setNeedConfirm(cursor.getInt(4)).
                        setMemberCount(cursor.getInt(5));
                    Muc.PersonalConfig.Builder persional = Muc.PersonalConfig.newBuilder();
                    persional.setRoleValue(cursor.getInt(6)).
                        setNoDisturb(cursor.getInt(7)).
                        setChatBg(cursor.getString(8)).
                        setMucusernick(cursor.getString(9));
                    mucItem.setPConfig(persional.build());
                    mucItem.setCreationTime(cursor.getLong(10));
                    mucItem.setCreator(cursor.getString(11));
                    db.setTransactionSuccessful();
                }
                if (mucItem != null) {
                    return mucItem.build();
                } else {
                    return null;
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return null;
        }
    }

    @Override
    public List<Muc.MucItem> selectAll() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .rawQuery(
                        "select * from " + DBHelper.TABLE_MUC_INFO + " order by " + DBHelper.ROLE
                            + " desc", null);
                List<Muc.MucItem> mucItems = new ArrayList<>();
                for (int i = 0; i < cursor.getCount(); i++) {
                    Muc.MucItem.Builder mucItem = Muc.MucItem.newBuilder();
                    cursor.moveToNext();
                    mucItem.setMucid(cursor.getString(1)).
                        setMucname(cursor.getString(2)).
                        setSubject(cursor.getString(3)).
                        setNeedConfirm(cursor.getInt(4)).
                        setMemberCount(cursor.getInt(5));
                    Muc.PersonalConfig.Builder persional = Muc.PersonalConfig.newBuilder();
                    persional.setRoleValue(cursor.getInt(6)).
                        setNoDisturb(cursor.getInt(7)).
                        setChatBg(cursor.getString(8)).
                        setMucusernick(cursor.getString(9));
                    mucItem.setPConfig(persional.build());
                    mucItem.setCreationTime(cursor.getLong(10));
                    mucItem.setCreator(cursor.getString(11));
                    mucItems.add(mucItem.build());
                }
                db.setTransactionSuccessful();
                return mucItems;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return null;
        }
    }

    @Override
    public boolean insert(Muc.MucItem mucItem) {
        return false;
    }

    public boolean insertMucItem(Context context, Muc.MucItem mucItem, String currentUserId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
//                MucUserDao mucUserDao = new MucUserDao(context, DaoManager.getUserID());
                ContentValues values = new ContentValues();
                values.put(DBHelper.MUC_ID, mucItem.getMucid());
                values.put(DBHelper.MUC_NAME, mucItem.getMucname());
                values.put(DBHelper.SUBJECT, mucItem.getSubject());
                values.put(DBHelper.AUTOENTER, mucItem.getNeedConfirm());
                values.put(DBHelper.MEMBERCOUNT, mucItem.getMemberCount());
//            values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                values.put(DBHelper.NOTDISTURB, mucItem.getPConfig().getNoDisturb());
                values.put(DBHelper.CHATBG, mucItem.getPConfig().getChatBg());
                values.put(DBHelper.MUC_USERNICK, mucItem.getPConfig().getMucusernick());
                values.put(DBHelper.CREATION_TIME, mucItem.getCreationTime());
                values.put(DBHelper.CREATOR, mucItem.getCreator());
                if (mucItem.getPConfig().getRole() == null
                    || mucItem.getPConfig().getRole() == Role.Member) {
                    if (!TextUtils.isEmpty(mucItem.getCreator()) && currentUserId
                        .equalsIgnoreCase(mucItem.getCreator())) {
                        values.put(DBHelper.ROLE, Role.Owner.ordinal());
                    } else {
                        values.put(DBHelper.ROLE, Role.Member.ordinal());
                    }
                } else {
                    values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                }
                db.insert(DBHelper.TABLE_MUC_INFO, null, values);
                //插入群成员
//                if (null != mucItem.getMembersList() && mucItem.getMembersList().size() > 0) {
//                    for (Muc.MucMemberItem item : mucItem.getMembersList()) {
//                        mucUserDao.builderMucUser(db, mucItem.getMucid(), item);
//                    }
//                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return false;
        }
    }

    public boolean insertMultiple(Context context, List<Muc.MucItem> mucItems,
        String currentUserId) {
        synchronized (getDBLock()) {

            if (mucItems != null && mucItems.size() > 0) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    long result = -1;
                    MucUserDao mucUserDao = new MucUserDao(context, DaoManager.getUserID());
                    for (Muc.MucItem mucItem : mucItems) {
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.MUC_ID, mucItem.getMucid());
                        values.put(DBHelper.MUC_NAME, mucItem.getMucname());
                        values.put(DBHelper.SUBJECT, mucItem.getSubject());
                        values.put(DBHelper.AUTOENTER, mucItem.getNeedConfirm());
                        values.put(DBHelper.MEMBERCOUNT, mucItem.getMemberCount());
//                    values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                        values.put(DBHelper.NOTDISTURB, mucItem.getPConfig().getNoDisturb());
                        values.put(DBHelper.CHATBG, mucItem.getPConfig().getChatBg());
                        values.put(DBHelper.MUC_USERNICK, mucItem.getPConfig().getMucusernick());
                        values.put(DBHelper.CREATION_TIME, mucItem.getCreationTime());
                        values.put(DBHelper.CREATOR, mucItem.getCreator());
                        if (mucItem.getPConfig().getRole() == null
                            || mucItem.getPConfig().getRole() == Role.Member) {
                            if (!TextUtils.isEmpty(mucItem.getCreator()) && currentUserId
                                .equalsIgnoreCase(mucItem.getCreator())) {
                                values.put(DBHelper.ROLE, Role.Owner.ordinal());
                            } else {
                                values.put(DBHelper.ROLE, Role.Member.ordinal());
                            }
                        } else {
                            values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                        }
                        result = db.insert(DBHelper.TABLE_MUC_INFO, null, values);
                        //插入群成员
                        if (null != mucItem.getMembersList()
                            && mucItem.getMembersList().size() > 0) {
                            for (Muc.MucMemberItem item : mucItem.getMembersList()) {
                                mucUserDao.builderMucUser(db, mucItem.getMucid(), item,
                                    mucItem.getCreator());
                            }
                        }
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
                    //删群
                    int result = db
                        .delete(DBHelper.TABLE_MUC_INFO, String.format("%s=?", DBHelper.MUC_ID),
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

    public boolean deleteAll() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                int result = db
                    .delete(DBHelper.TABLE_MUC_INFO, null, null);
//            //删成员
//            db.delete(DBHelper.TABLE_MUC_USER, null, null);
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
            return false;
        }
    }

    public boolean updateById(String id, String lineName, Object values) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(id)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    if (values instanceof String) {
                        values = "'" + values + "'";
                    }
                    db.execSQL("UPDATE " + DBHelper.TABLE_MUC_INFO + " SET " + lineName + " = "
                        + values + " where " + DBHelper.MUC_ID + " = " + id);
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

    public boolean updateMucInfo(String mucId, Muc.MucItem mucItem, String currentUserId) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(mucId)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.MUC_NAME, mucItem.getMucname());
                    values.put(DBHelper.SUBJECT, mucItem.getSubject());
                    values.put(DBHelper.AUTOENTER, mucItem.getNeedConfirm());
                    values.put(DBHelper.MEMBERCOUNT, mucItem.getMemberCount());
                    values.put(DBHelper.NOTDISTURB, mucItem.getPConfig().getNoDisturb());
                    values.put(DBHelper.CHATBG, mucItem.getPConfig().getChatBg());
                    values.put(DBHelper.MUC_USERNICK, mucItem.getPConfig().getMucusernick());
                    values.put(DBHelper.CREATION_TIME, mucItem.getCreationTime());
                    values.put(DBHelper.CREATOR, mucItem.getCreator());
                    if (mucItem.getPConfig().getRole() == null
                        || mucItem.getPConfig().getRole() == Role.Member) {
                        if (!TextUtils.isEmpty(mucItem.getCreator()) && currentUserId
                            .equalsIgnoreCase(mucItem.getCreator())) {
                            values.put(DBHelper.ROLE, Role.Owner.ordinal());
                        } else {
                            values.put(DBHelper.ROLE, Role.Member.ordinal());
                        }
                    } else {
                        values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                    }
                    int value = db.update(DBHelper.TABLE_MUC_INFO, values,
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucItem.getMucid()});
                    db.setTransactionSuccessful();
                    if (value > 0) {
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
     * 批量更新数据库
     *
     * @param newMucItems 新的集合
     * @param oldMucItems 本地集合
     * @param currentUserId 当前登录userId
     */
    public void updateListMucInfo(List<Muc.MucItem> newMucItems, List<Muc.MucItem> oldMucItems,
        String currentUserId) {
        synchronized (getDBLock()) {
            if (newMucItems != null && newMucItems.size() > 0) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    List<String> mucChatIds = new ArrayList<>();
                    for (Muc.MucItem mucItem : newMucItems) {
                        mucChatIds.add(mucItem.getMucid());
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.MUC_ID, mucItem.getMucid());
                        values.put(DBHelper.MUC_NAME, mucItem.getMucname());
                        values.put(DBHelper.SUBJECT, mucItem.getSubject());
                        values.put(DBHelper.AUTOENTER, mucItem.getNeedConfirm());
                        values.put(DBHelper.MEMBERCOUNT, mucItem.getMemberCount());
                        values.put(DBHelper.NOTDISTURB, mucItem.getPConfig().getNoDisturb());
                        values.put(DBHelper.CHATBG, mucItem.getPConfig().getChatBg());
                        values.put(DBHelper.MUC_USERNICK, mucItem.getPConfig().getMucusernick());
                        values.put(DBHelper.CREATION_TIME, mucItem.getCreationTime());
                        values.put(DBHelper.CREATOR, mucItem.getCreator());
                        if (mucItem.getPConfig().getRole() == null
                            || mucItem.getPConfig().getRole() == Role.Member) {
                            if (!TextUtils.isEmpty(mucItem.getCreator()) && currentUserId
                                .equalsIgnoreCase(mucItem.getCreator())) {
                                values.put(DBHelper.ROLE, Role.Owner.ordinal());
                            } else {
                                values.put(DBHelper.ROLE, Role.Member.ordinal());
                            }
                        } else {
                            values.put(DBHelper.ROLE, mucItem.getPConfig().getRole().ordinal());
                        }
                        int result = db.update(DBHelper.TABLE_MUC_INFO, values,
                            String.format("%s=?", DBHelper.MUC_ID),
                            new String[]{mucItem.getMucid()});
                        if (result > 0) {
                            continue;
                        } else {
                            db.insert(DBHelper.TABLE_MUC_INFO, null, values);
                        }
                        //删成员
                        db.delete(DBHelper.TABLE_MUC_USER,
                            String.format("%s=?", DBHelper.MUC_ID),
                            new String[]{mucItem.getMucid()});
                        //增加群成员
                        for (Muc.MucMemberItem memberItem : mucItem.getMembersList()) {
                            builderMucUser(db, mucItem.getMucid(), memberItem,
                                mucItem.getCreator());
                        }
                    }
                    //判断数据库是否与多余的数据
                    if (null != oldMucItems && oldMucItems.size() > 0) {
                        for (Muc.MucItem delItem : oldMucItems) {
                            if (!mucChatIds.contains(delItem.getMucid())) {
                                db.delete(DBHelper.TABLE_MUC_INFO,
                                    String.format("%s=?", DBHelper.MUC_ID),
                                    new String[]{delItem.getMucid()});
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                    closeDatabase(db, null);
                }
            }
        }
    }

    /*
    * 获取群名
    * */
    public String getMucName(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.MUC_NAME},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                String mucName = mucId;
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            mucName = cursor.getString(0);
                        }
                    }
                }
                db.setTransactionSuccessful();
                return mucName;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return "";
        }
    }

    /*
    * 获取群免打扰信息
    * */
    public int getMucNoDisturb(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            int noDisturb = 0;
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.NOTDISTURB},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        noDisturb = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
                return noDisturb;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return noDisturb;
        }
    }

    public boolean markNoDisturb(String mucId, boolean isNoDisturb) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();

                values.put(DBHelper.NOTDISTURB,
                    isNoDisturb ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
                int result = db
                    .update(DBHelper.TABLE_MUC_INFO, values, String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId});
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
        }
        return false;
    }

    /*
   * 获取群聊天背景
   * */
    public String getMucChatBg(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            String mucChatBg = "";
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.CHATBG},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            mucChatBg = cursor.getString(0);
                        }
                    }
                }
                db.setTransactionSuccessful();
                return mucChatBg;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return null;
        }
    }

    public MucInfoDao(Context context, String userId) {
        super(context, userId);
    }

    /**
     * bilder 群成员
     */
    public long builderMucUser(SQLiteDatabase db, String mucId, Muc.MucMemberItem item,
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
        return db.insert(DBHelper.TABLE_MUC_USER, null, values);
    }

    /*
   * 关键字模糊查询群
   * */
    public AllResult selectMucByContent(String text) {
        if (StringUtils.isContainTransforChar(text)) {
            return null;
        }
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                String sql = DBHelper.MUC_NAME + " like '%" + text + "%' or " +
                    DBHelper.MUC_ID + " like '%" + text + "%'";
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO,
                        new String[]{DBHelper.MUC_ID, DBHelper.MUC_NAME}, sql,
                        null, null, null, null);
                AllResult result = null;

                if (cursor != null) {
                    result = new AllResult();
                    result.setKey(EResultType.MUC.ordinal());
                    List<SearchMessageBean> beans = new ArrayList<>();
                    result.setResults(beans);
                    while (cursor.moveToNext()) {
                        SearchMessageBean bean = new SearchMessageBean();
                        String mucId = cursor.getString(0);
                        String mucName = cursor.getString(1);
                        bean.setUserId(mucId);
                        bean.setNick(mucName);
                        beans.add(bean);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return result;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return null;
        }
    }

    /*
   *
   * 获取群聊总个数
   * */
    public int selectMucCount() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            int count = 0;
            try {
                db.beginTransaction();
                cursor = db
                    .rawQuery(
                        "select * from " + DBHelper.TABLE_MUC_INFO, null);
                if (cursor != null) {
                    count = cursor.getCount();
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return count;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return count;
        }
    }


    /*
     *
     * 获取群聊总个数
     * */
    public String selectMucCreator(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            String creator = "";
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.CREATOR},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            creator = cursor.getString(0);
                        }
                    }
                }
                db.setTransactionSuccessful();
                return creator;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return creator;
        }
    }

    /*
   * 获取群备注名
   * */
    public String getMucUserNick(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.MUC_USERNICK},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                String mucName = "";
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            mucName = cursor.getString(0);
                        }
                    }
                }
                db.setTransactionSuccessful();
                return mucName;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return "";
        }
    }

    /*
 * 获取群聊天背景
 * */
    public int getMucMemberCount(String mucId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            int count = 0;
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MUC_INFO, new String[]{DBHelper.MEMBERCOUNT},
                        String.format("%s=?", DBHelper.MUC_ID),
                        new String[]{mucId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        count = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
                return count;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return count;
        }
    }

}
