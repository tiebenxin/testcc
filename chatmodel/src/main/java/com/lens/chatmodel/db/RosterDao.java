package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.RosterGroupBean;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lens.chatmodel.bean.UserBean;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/13.
 */

public class RosterDao extends BaseDao<IChatUser> {


    public RosterDao(Context context, String userId) {
        super(context, userId);
    }

    @Override
    public IChatUser selectSingle(String id) {
        if (!TextUtils.isEmpty(id)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields,
                        String.format("%s=?", DBHelper.ACCOUT),
                        new String[]{id}, null, null, null);
                IChatUser roster = null;
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        roster = createRosterItem(cursor);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return roster;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return null;
    }


    /*
    * 获取所有好友关系的用户
    * */
    @Override
    public List<IChatUser> selectAll() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            List<IChatUser> list = new ArrayList<>();
            String sql = DBHelper.STATUS + " = " + ERelationStatus.FRIEND.ordinal();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields, sql, null, null, null,
                    DBHelper.IS_STAR + " DESC, " + DBHelper.SHORT + " ASC");
            UserBean bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = createRosterItem(cursor);
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
        return null;
    }

    /*
    * 获取所有好友关系的用户
    * */
    public List<UserBean> selectAllFriend() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            List<UserBean> list = new ArrayList<>();
            String sql = DBHelper.STATUS + " = " + ERelationStatus.FRIEND.ordinal();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields, sql, null, null, null,
                    DBHelper.IS_STAR + " DESC, " + DBHelper.SHORT + " ASC");
            UserBean bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = createRosterItem(cursor);
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
        return null;
    }

    @Override
    public boolean insert(IChatUser user) {
        if (user != null) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.ACCOUT, user.getUserId());
                values.put(DBHelper.USER_NICK, user.getUserNick());
                values.put(DBHelper.WORK_ADDRESS, user.getWorkAddress());
                values.put(DBHelper.GROUP, user.getGroup());
                values.put(DBHelper.EMP_NAME, user.getEmpName());
                values.put(DBHelper.REMARK_NAME, user.getRemarkName());
                values.put(DBHelper.SEX, user.getSex());
                values.put(DBHelper.IMAGE, user.getAvatarUrl());
                values.put(DBHelper.IS_VALID, user.isValid());
                values.put(DBHelper.JOB_NAME, user.getJobName());
                values.put(DBHelper.DPT_NO, user.getDptNo());
                values.put(DBHelper.DPT_NAME, user.getDptName());
                values.put(DBHelper.EMP_NO, user.getEmpNo());
                values.put(DBHelper.IS_BLOCK, user.isBlock());
                values.put(DBHelper.SHORT, user.getFirstChar());
                values.put(DBHelper.PINYIN, user.getPinYin());
                values.put(DBHelper.STATUS, user.getRelationStatus());
                values.put(DBHelper.TIME, user.getTime());
                values.put(DBHelper.HAS_READED, user.hasReaded());
                values.put(DBHelper.CHAT_BG, user.getBgId());
                if (user.getNewStatus() >= 0) {
                    values.put(DBHelper.NEW_STATUS, user.getNewStatus());
                }
                values.put(DBHelper.IS_STAR, user.getStar());
                values.put(DBHelper.IS_QUIT, user.isQuit());

                long result = db.insert(DBHelper.TABLE_ROSTER, null, values);
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

    public boolean updateRoster(IChatUser user) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.ACCOUT, user.getUserId());
            values.put(DBHelper.USER_NICK, user.getUserNick());
            values.put(DBHelper.WORK_ADDRESS, user.getWorkAddress());
            values.put(DBHelper.GROUP, user.getGroup());
            values.put(DBHelper.EMP_NAME, user.getEmpName());
            values.put(DBHelper.REMARK_NAME, user.getRemarkName());
            values.put(DBHelper.SEX, user.getSex());
            values.put(DBHelper.IMAGE, user.getAvatarUrl());
            values.put(DBHelper.IS_VALID, user.isValid());
            values.put(DBHelper.JOB_NAME, user.getJobName());
            values.put(DBHelper.DPT_NO, user.getDptNo());
            values.put(DBHelper.DPT_NAME, user.getDptName());
            values.put(DBHelper.EMP_NO, user.getEmpNo());
            values.put(DBHelper.IS_BLOCK, user.isBlock());
            values.put(DBHelper.SHORT, user.getFirstChar());
            values.put(DBHelper.PINYIN, user.getPinYin());
            values.put(DBHelper.STATUS, user.getRelationStatus());
            values.put(DBHelper.TIME, user.getTime());
            values.put(DBHelper.HAS_READED, user.hasReaded());
            if (user.getNewStatus() > 0) {
                values.put(DBHelper.NEW_STATUS, user.getNewStatus());
            }
            values.put(DBHelper.IS_STAR, user.getStar());
            values.put(DBHelper.CHAT_BG, user.getBgId());
            values.put(DBHelper.IS_QUIT, user.isQuit());

            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{user.getUserId()});
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

    public String selectUserNick(String user) {
        if (!TextUtils.isEmpty(user)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.USER_NICK},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{user}, null, null,
                        null);
                String nick = user;
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            nick = cursor.getString(0);
                        }
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return nick;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return user;
    }

    @Override
    public boolean delete(String id) {
        if (!TextUtils.isEmpty(id)) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                int result = db
                    .delete(DBHelper.TABLE_ROSTER, String.format("%s=?", DBHelper.ACCOUT),
                        new String[]{id});
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

    private UserBean createRosterItem(Cursor cursor) {
        UserBean bean = new UserBean();

        RosterItem.Builder roster = RosterItem.newBuilder();
        if (cursor.getString(0) != null) {
            roster.setUsername(cursor.getString(0));
        }
        if (cursor.getString(1) != null) {
            roster.setUsernick(cursor.getString(1));
        }
        if (cursor.getString(2) != null) {
            roster.setWorkAddress(cursor.getString(2));
        }
        if (cursor.getString(3) != null) {
            roster.setGroup(cursor.getString(3));
        }
        if (cursor.getString(4) != null) {
            roster.setEmpName(cursor.getString(4));
        }
        if (cursor.getString(5) != null) {
            roster.setRemarkName(cursor.getString(5));
        }
        if (cursor.getString(6) != null) {
            roster.setSex(cursor.getString(6));
        }
        if (cursor.getString(7) != null) {
            roster.setAvatar(cursor.getString(7));
        }

        roster.setIsvalid(cursor.getInt(8));

        if (cursor.getString(9) != null) {
            roster.setJobname(cursor.getString(9));
        }
        if (cursor.getString(10) != null) {
            roster.setDptNo(cursor.getString(10));
        }
        if (cursor.getString(11) != null) {
            roster.setDptName(cursor.getString(11));
        }
        if (cursor.getString(12) != null) {
            roster.setEmpNo(cursor.getString(12));
        }

        roster.setIsBlock(cursor.getInt(13));

        bean.setRoster(roster.build());

        if (cursor.getString(14) != null) {
            bean.setFirstChar(cursor.getString(14));
        }

        if (cursor.getString(15) != null) {
            bean.setPinYin(cursor.getString(15));
        }

        bean.setRelationStatus(cursor.getInt(16));
        bean.setTime(cursor.getLong(17));
        bean.setHasReaded(cursor.getInt(18));
        bean.setStar(cursor.getInt(19));
        bean.setBgId(cursor.getInt(20));
        bean.setQuit(cursor.getInt(21));
        return bean;
    }


    public boolean updateRosterNick(String userId, String nick) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.USER_NICK, nick);
            values.put(DBHelper.SHORT, StringUtils.getFristChar(nick));
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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

    /*
    * 关键字模糊查询user
    * */
    public AllResult selectUserByContent(String text) {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            String sql = DBHelper.USER_NICK + " like '%" + text + "%' or " +
                DBHelper.SHORT + " like '%" + text + "%' or " +
                DBHelper.PINYIN + " like '%" + text + "%' or " +
                DBHelper.ACCOUT + " like '%" + text + "%'";
            cursor = db
                .query(DBHelper.TABLE_ROSTER,
                    new String[]{DBHelper.ACCOUT, DBHelper.USER_NICK, DBHelper.SHORT}, sql,
                    null, null, null, null);
            AllResult result = null;

            if (cursor != null) {
                result = new AllResult();
                result.setKey(EResultType.CONTACT.ordinal());
                List<SearchMessageBean> beans = new ArrayList<>();
                result.setResults(beans);
                while (cursor.moveToNext()) {
                    SearchMessageBean bean = new SearchMessageBean();
                    String user = cursor.getString(0);
                    String nick = cursor.getString(1);
                    String shortPin = cursor.getString(2);
                    bean.setUserId(user);
                    bean.setNick(nick);
                    bean.setAlpha(shortPin);
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


    /*
    * 更新分组
    * */
    public boolean updateRosterGroup(String userId, String groupName) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.GROUP, groupName);
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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


    /*
     * 获取某一分组成员信息
     * */
    public RosterGroupBean selectGroupByName(String groupName) {
        SQLiteDatabase db = openWriter();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields,
                    String.format("%s=?", DBHelper.GROUP), new String[]{groupName}, null, null,
                    null);
            RosterGroupBean bean = null;
            if (cursor != null) {
                bean = new RosterGroupBean();
                bean.setName(groupName);
                List<IChatUser> beans = new ArrayList<>();
                bean.setUsers(beans);
                while (cursor.moveToNext()) {
                    IChatUser user = createRosterItem(cursor);
                    if (user != null) {
                        beans.add(user);
                    }
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
            return bean;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }


    /*
     * 获取所有分组成员信息,逻辑暂时有问题
     * */
    public List<RosterGroupBean> selectAllGroup() {
        SQLiteDatabase db = openWriter();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields,
                    DBHelper.GROUP + " != '' and " + DBHelper.GROUP + " is not null", null, null,
                    null,
                    DBHelper.GROUP + " ASC");
            List<RosterGroupBean> list = null;
            if (cursor != null) {
                list = new ArrayList<>();
                RosterGroupBean bean = null;
                List<IChatUser> beans = null;
                while (cursor.moveToNext()) {
                    if (bean == null) {
                        bean = new RosterGroupBean();
                        beans = new ArrayList<>();
                    }
                    IChatUser user = createRosterItem(cursor);
                    if (user == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(bean.getName())) {
                        beans.add(user);
                        bean.setName(user.getGroup());
                    } else {
                        if (user.getGroup().equalsIgnoreCase(bean.getName())) {//是同一组的
                            beans.add(user);
                            if (!cursor.moveToNext()) {//最后一个
                                bean.setUsers(beans);
                                list.add(bean);
                            }
                        } else {//新分组
                            bean.setUsers(beans);
                            list.add(bean);
                            bean.setName(user.getGroup());
                            beans.clear();
                            beans.add(user);
                        }
                    }
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
            return list;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }

    /*
     * 获取通讯录中所有未分组成员信息
     * 备注：由于当前需求是一个用户只能进入单一分组，所以新建分组的时候，必须选择未分组的
     * */
    public List<IChatUser> selectAllNoGroupUser() {
        SQLiteDatabase db = openWriter();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields,
                    DBHelper.GROUP + " = '' or " + DBHelper.GROUP + " is null", null, null,
                    null, DBHelper.PINYIN + " ASC");
            List<IChatUser> beans = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    IChatUser user = createRosterItem(cursor);
                    if (user != null) {
                        beans.add(user);
                    }
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
            return beans;
        } catch (Exception e) {
            L.e(e);
        } finally {
            db.endTransaction();
            closeDatabase(db, null);
        }
        return null;
    }

    /*
    *
    * 获取所有非好友关系的用户
    * */
    public List<IChatUser> selectAllNewFriend() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            List<IChatUser> list = new ArrayList<>();
//            String sql = DBHelper.STATUS + " = " + ERelationStatus.INVITE.ordinal() + " or "
//                + DBHelper.STATUS + " = " + ERelationStatus.RECEIVE.ordinal();
            String sql =
                DBHelper.NEW_STATUS + " = " + ESureType.YES.ordinal(); //新好友，不管是已经添加成功，还是未添加成功
            cursor = db.query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields, sql,
                null, null, null, DBHelper.TIME + " DESC");
            UserBean bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = createRosterItem(cursor);
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
        return null;
    }


    /*
    *
    * 获取未读好友信息数
    * */
    public int selectUnreadRosterCount() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        int count = 0;
        try {
            db.beginTransaction();
            String sql =
                DBHelper.HAS_READED + " = " + ESureType.NO.ordinal() /*+ " and " + DBHelper.STATUS
                    + " = " + ERelationStatus.INVITE.ordinal()*/;
            cursor = db
                .rawQuery("select * from " + DBHelper.TABLE_ROSTER + " where " + sql, null);
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


    /*
   * 更新好友状态
   * */
    public boolean updateFriendStatus(String userId, int status) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.STATUS, status);
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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

    /*
     * 更新新好友消息已读
     * */
    public boolean updateHasReaded(String userId, int status) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.HAS_READED, status);
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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


    /*
    *
    * 获取所有新好友
    * */
    public List<IChatUser> selectAllUnreadNewFriend() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            List<IChatUser> list = new ArrayList<>();
//            String sql = DBHelper.STATUS + " = " + ERelationStatus.INVITE.ordinal() + " or "
//                + DBHelper.STATUS + " = " + ERelationStatus.RECEIVE.ordinal();
            String sql = DBHelper.NEW_STATUS + " = " + ESureType.YES.ordinal() /*+ " and "
                + DBHelper.HAS_READED + " = " + ESureType.NO.ordinal()*/; //新好友，并且是未读的
            cursor = db.query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields, sql,
                null, null, null, DBHelper.TIME + " DESC");
            UserBean bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = createRosterItem(cursor);
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
        return null;
    }


    /*
    * 更新星标好友
    * */
    public boolean updateStarUser(String userId, int status) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.IS_STAR, status);
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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

    public String selectUserAvatar(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.IMAGE},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{userId}, null, null,
                        null);
                String url = "";
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        if (!TextUtils.isEmpty(cursor.getString(0))) {
                            url = cursor.getString(0);
                        }
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return url;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return "";
    }

    /*
  * 获取所有星标好友关系的用户
  * */
    public List<IChatUser> selectAllStarUser() {
        SQLiteDatabase db = openReader();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            List<IChatUser> list = new ArrayList<>();
            String sql = DBHelper.STATUS + " = " + ERelationStatus.FRIEND.ordinal() + " and "
                + DBHelper.IS_STAR + " = " + ESureType.YES.ordinal();
            cursor = db
                .query(DBHelper.TABLE_ROSTER, DBHelper.roster_fields, sql, null, null, null,
                    DBHelper.TIME + " DESC");
            UserBean bean = null;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bean = createRosterItem(cursor);
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
        return null;
    }


    public boolean updateRosterChatBg(String userId, int chatBg) {
        SQLiteDatabase db = openWriter();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_BG, chatBg);
            int result = db
                .update(DBHelper.TABLE_ROSTER, values, String.format("%s=?", DBHelper.ACCOUT),
                    new String[]{userId});
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

    public int getUserChatBg(String user) {
        int bg = 0;
        if (!TextUtils.isEmpty(user)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.CHAT_BG},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{user}, null, null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        bg = cursor.getInt(0);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return bg;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return bg;
    }

    /*
    * 获取用户好友好友状态
    * */
    public int getUserRelationStatus(String user) {
        int relation = -1;
        if (!TextUtils.isEmpty(user)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.STATUS},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{user}, null, null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        relation = cursor.getInt(0);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return relation;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return relation;
    }

    /*
    * 获取用户好友是否被阅读
    * */
    public int getUserHasRead(String user) {
        int relation = 1;
        if (!TextUtils.isEmpty(user)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.HAS_READED},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{user}, null, null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        relation = cursor.getInt(0);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return relation;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return relation;
    }

    /*
    * 获取用户好友是否是新浩宇状态
    * */
    public int getUserNewStatus(String user) {
        int relation = 0;
        if (!TextUtils.isEmpty(user)) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_ROSTER, new String[]{DBHelper.NEW_STATUS},
                        String.format("%s=?", DBHelper.ACCOUT), new String[]{user}, null, null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        relation = cursor.getInt(0);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return relation;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }

        }
        return relation;
    }
}
