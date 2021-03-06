package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.utils.ExecutorHolder;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by LL130386 on 2017/12/30.
 */

public class RecentMsgDao extends BaseDao<RecentMessage> {


    public RecentMsgDao(Context context, String userId) {
        super(context, userId);
    }

    @Override
    public RecentMessage selectSingle(String id) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(id)) {
                SQLiteDatabase db = openReader();
                Cursor cursor = null;
                try {
                    db.beginTransaction();
                    cursor = db
                        .query(DBHelper.TABLE_RECENT, DBHelper.recent_fields,
                            String.format("%s=?", DBHelper.CHAT_ID),
                            new String[]{id}, null, null, null);
                    RecentMessage message = null;
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            message = createMessage(cursor);
                        }
                        cursor.close();
                    }
                    db.setTransactionSuccessful();
                    return message;
                } catch (Exception e) {
                    L.e(e);
                } finally {
                    db.endTransaction();
                    closeDatabase(db, cursor);
                }
            }
        }
        return null;
    }


    @Override
    public List<RecentMessage> selectAll() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            List<RecentMessage> list = new ArrayList<>();
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_RECENT, DBHelper.recent_fields, null, null, null, null,
                        DBHelper.TOP_FLAG + " DESC, " + DBHelper.TIME + " DESC");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        list.add(createMessage(cursor));
                    }
                }
                db.setTransactionSuccessful();
                return list;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return null;
    }


    /*
    * 按页搜索
    * @param pager 页数
    * @param number 每页条目数
    * @param user 聊天对象
    *
    * */
    @Override
    public List<RecentMessage> selectAsPage(String user, int pager, int number) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            List<RecentMessage> list = new ArrayList<>();
            int total = (pager + 1) * number;
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_RECENT, DBHelper.recent_fields, null, null, null, null,
                        DBHelper.TIME + " DESC");
                int end = total - 1;
                int start = end - number;
                int index = 0;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        if (index < start) {
                            index++;
                            continue;
                        } else if (index >= end) {
                            break;
                        }
                        list.add(0, createMessage(cursor));
                        index++;
                    }
                }
                db.setTransactionSuccessful();
                return list;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return null;
    }


    @Override
    public boolean insert(RecentMessage message) {
        synchronized (getDBLock()) {
            if (message != null) {
                SQLiteDatabase db = openWriter();
                try {
                    ContentValues values = new ContentValues();
//                    values.put(DBHelper.MSG, message.getMsg());
                    values.put(DBHelper.NICK, message.getNick());
                    values.put(DBHelper.GROUP_NAME, message.getGroupName());
//                    values.put(DBHelper.MSG_TYPE, message.getMsgType().value);
                    values.put(DBHelper.USER_ID, message.getUserId());
                    values.put(DBHelper.TOP_FLAG, message.getTopFlag());
//                    values.put(DBHelper.UNREAD_COUNT, message.getUnreadCountOfUser());
                    values.put(DBHelper.NOT_DISTURB, message.getNotDisturb());
                    values.put(DBHelper.TIME, message.getTime());
                    values.put(DBHelper.IS_AT,
                        message.isAt() ? ESureType.YES.value : ESureType.NO.value);
                    values.put(DBHelper.CHAT_ID, message.getChatId());
                    values.put(DBHelper.AVATAR_URL, message.getAvatarUrl());
                    values.put(DBHelper.CHAT_TYPE, message.getChatType());
//                    values.put(DBHelper.HINT, message.getHint());
                    values.put(DBHelper.BG_ID, message.getBackgroundId());
                    long result = db.insert(DBHelper.TABLE_RECENT, null, values);
                    if (result != -1) {
                        return true;
                    }
                } catch (Exception e) {
                    L.e(e);
                } finally {
                    closeDatabase(db, null);
                }
            }
        }
        return false;
    }

    public boolean insertAsyn(RecentMessage message) {
        return waitResult(getPool().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return insert(message);
            }
        }));
    }

    @Override
    public boolean delete(String chatId) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(chatId)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    int result = db
                        .delete(DBHelper.TABLE_RECENT, String.format("%s=?", DBHelper.CHAT_ID),
                            new String[]{chatId});
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
        }
        return false;
    }

    public boolean update(RecentMessage message) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.NICK, message.getNick());
                if (!TextUtils.isEmpty(message.getGroupName())) {
                    values.put(DBHelper.GROUP_NAME, message.getGroupName());
                }
                values.put(DBHelper.USER_ID, message.getUserId());
                if (message.getTopFlag() > 0) {
                    values.put(DBHelper.TOP_FLAG, message.getTopFlag());
                }
                if (message.getNotDisturb() > 0) {
                    values.put(DBHelper.NOT_DISTURB, message.getNotDisturb());
                }
                values.put(DBHelper.TIME, System.currentTimeMillis());//不更新消息时间，更新当前操作时间
                if (message.isAt()) {//只更新@消息
                    values.put(DBHelper.IS_AT, ESureType.YES.value);
                }
                values.put(DBHelper.CHAT_ID, message.getChatId());
                values.put(DBHelper.AVATAR_URL, message.getAvatarUrl());
                if (message.getChatType() > 0) {
                    values.put(DBHelper.CHAT_TYPE, message.getChatType());
                }
                if (message.getBackgroundId() > 0) {
                    values.put(DBHelper.BG_ID, message.getBackgroundId());
                }
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{message.getChatId()});
                db.setTransactionSuccessful();
                if (result > 0) {
                    return true;
                } else {
                    return insert(message);
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

    public boolean update(RecentMessage message, int unreadCount) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.NICK, message.getNick());
                if (!TextUtils.isEmpty(message.getGroupName())) {
                    values.put(DBHelper.GROUP_NAME, message.getGroupName());
                }
                values.put(DBHelper.USER_ID, message.getUserId());
                if (message.getTopFlag() > 0) {
                    values.put(DBHelper.TOP_FLAG, message.getTopFlag());
                }
                if (message.getNotDisturb() > 0) {
                    values.put(DBHelper.NOT_DISTURB, message.getNotDisturb());
                }
                values.put(DBHelper.TIME, System.currentTimeMillis());//不更新消息时间，更新当前操作时间
                values.put(DBHelper.IS_AT,
                    message.isAt() ? ESureType.YES.value : ESureType.NO.value);
                values.put(DBHelper.CHAT_ID, message.getChatId());
                values.put(DBHelper.AVATAR_URL, message.getAvatarUrl());
                if (message.getChatType() > 0) {
                    values.put(DBHelper.CHAT_TYPE, message.getChatType());
                }
                if (message.getBackgroundId() > 0) {
                    values.put(DBHelper.BG_ID, message.getBackgroundId());
                }
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{message.getChatId()});
                db.setTransactionSuccessful();
                if (result > 0) {
                    return true;
                } else {
                    return insert(message);
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

    private RecentMessage createMessage(Cursor cursor) {
        RecentMessage message = new RecentMessage();

        if (cursor.getString(0) != null) {
            message.setNick(cursor.getString(0));
        }
        if (cursor.getString(1) != null) {
            message.setGroupName(cursor.getString(1));
        }

        if (cursor.getString(2) != null) {
            message.setUserId(cursor.getString(2));
        }
        message.setTopFlag(cursor.getInt(3));
        message.setNotDisturb(cursor.getInt(4));
        message.setTime(cursor.getLong(5));

        message.setAt(cursor.getInt(6) == ESureType.YES.value ? true : false);

        if (cursor.getString(7) != null) {
            message.setChatId(cursor.getString(7));
        }

        if (cursor.getString(8) != null) {
            message.setAvatarUrl(cursor.getString(8));
        }
        message.setChatType(cursor.getInt(9));
        message.setBackgroundId(cursor.getInt(10));
        return message;
    }

    public boolean hasChat(String chatId) {
        synchronized (getDBLock()) {
            if (TextUtils.isEmpty(chatId)) {
                return false;
            }
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                Cursor cursor = db.query(DBHelper.TABLE_RECENT, new String[]{DBHelper.CHAT_ID},
                    String.format("%s=?", DBHelper.CHAT_ID), new String[]{chatId}, null, null,
                    null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        String chat = cursor.getString(0) + "";
                        if (!TextUtils.isEmpty(chat)) {
                            if (chatId.equals(chat)) {
                                return true;
                            }
                        }
                    }

                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return false;
    }

    //标记是否置顶
    public boolean markTop(String chatId, boolean isTop) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();

                values.put(DBHelper.TOP_FLAG,
                    !isTop ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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

    //标记是否免打扰，1为免打扰，0为未设置
    public boolean markNoDisturb(String chatId, boolean isNoDisturb) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();

                values.put(DBHelper.NOT_DISTURB,
                    isNoDisturb ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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

    //标记是否已读, isUnreaded true 表示当前为未读
    public boolean markReaded(String chatId, boolean isUnReaded) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                if (isUnReaded) {
                    values.put(DBHelper.UNREAD_COUNT, 0);
                } else {
                    values.put(DBHelper.UNREAD_COUNT, 1);

                }
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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


    public List<UserBean> selectRecentTalk() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            List<UserBean> users = new ArrayList<>();
            String[] projections = new String[]{DBHelper.CHAT_ID, DBHelper.NICK,
                DBHelper.AVATAR_URL,
                DBHelper.CHAT_TYPE, DBHelper.GROUP_NAME};
            try {
                db.beginTransaction();
                Cursor cursor = db.query(DBHelper.TABLE_RECENT, projections, null,
                    null, null, null, DBHelper.TIME + " desc ");
                db.setTransactionSuccessful();

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        UserBean userBean = createRecentTalkUser(cursor);
                        users.add(userBean);
                    }
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return users;
        }

    }

    private UserBean createRecentTalkUser(Cursor cursor) {
        RosterItem.Builder builder = RosterItem.newBuilder();
        if (!TextUtils.isEmpty(cursor.getString(0))) {
            builder.setUsername(cursor.getString(0));
        }

        if (!TextUtils.isEmpty(cursor.getString(1))) {
            builder.setUsernick(cursor.getString(1));
        }
        if (!TextUtils.isEmpty(cursor.getString(2))) {
            builder.setAvatar(cursor.getString(2));
        }

        UserBean bean = new UserBean(builder.build());

//        if (!TextUtils.isEmpty(cursor.getString(2))) {
//            bean.setWorkAddress(cursor.getString(2));
//        }
        bean.setChatType(cursor.getInt(3));
        if (ChatEnum.EChatType.GROUP.value == bean.getChatType()) {
            bean.setMucName(cursor.getString(4));
        }

        if (StringUtils.isEmpty(bean.getUserNick())) {
            String F = StringUtils.getPinYinHeadChar(bean.getUserId().trim());
            bean.setFirstChar(F);
        } else {
            String F = StringUtils.getPinYinHeadChar(bean.getUserNick().trim());
            bean.setFirstChar(F);
        }
        return bean;

    }

    public int selectTotalUnreadMessageCount() {
        synchronized (getDBLock()) {

            int count = 0;
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_RECENT, new String[]{DBHelper.UNREAD_COUNT},
                    String.format("%s>0", DBHelper.UNREAD_COUNT), null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count += cursor.getInt(0);
                    }
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

    //更新聊天背景图片
    public boolean updateBackGround(String chatId, int bgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.BG_ID, bgId);
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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

    //更新会话名称
    public boolean updateChatName(String chatId, String chatName) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.GROUP_NAME, chatName);
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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


    //获取聊天背景图片
    public int getBackGroundId(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            int backId = EChatBgId.DEFAULT.id;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_RECENT, new String[]{DBHelper.BG_ID},
                        String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        backId = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return backId;
        }
    }

    //获取免打扰
    public int getNoDisturb(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            int nodisturb = 0;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_RECENT, new String[]{DBHelper.NOT_DISTURB},
                        String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        nodisturb = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return nodisturb;
        }
    }


    /*
    * 获取某个聊天对象的未读消息数
    * */
    public int selectUnreadMessageCountOfUser(String user) {
        synchronized (getDBLock()) {
            int count = 0;
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_RECENT, new String[]{DBHelper.UNREAD_COUNT},
                    String.format("%s=?", DBHelper.CHAT_ID), new String[]{user}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count = cursor.getInt(0);
                    }
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

    //获取置顶信息
    public int getTopFlag(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            int top = ESureType.NO.ordinal();
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_RECENT, new String[]{DBHelper.TOP_FLAG},
                        String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        top = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return top;
        }
    }

    //清除消息记录
    public boolean clearMessage(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
//                values.put(DBHelper.MSG, "");
//                values.put(DBHelper.HINT, "");
                values.put(DBHelper.TIME, System.currentTimeMillis());
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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

    public boolean updateAsyn(RecentMessage message) {
        return waitResult(ExecutorHolder.getChatUpdatesExecutor().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return update(message);
            }
        }));
    }

    public boolean updateAsyn(RecentMessage message, int unreadCount) {
        return waitResult(ExecutorHolder.getChatUpdatesExecutor().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return update(message, unreadCount);
            }
        }));
    }

    public List<String> selectChatIds() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            List<String> users = new ArrayList<>();
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_RECENT, new String[]{DBHelper.CHAT_ID}, null,
                        null, null, null, DBHelper.TIME + " desc ");
                db.setTransactionSuccessful();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        users.add(cursor.getString(0));
                    }
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return users;
        }
    }

    //更新@消息为非@
    public boolean updateAt(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.IS_AT, ESureType.NO.ordinal());
                int result = db
                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
                        new String[]{chatId});
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

    //更新聊天背景图片
//    public boolean updateAvatarAndNick(String chatId, String avatar, String nick) {
//        synchronized (getDBLock()) {
//            SQLiteDatabase db = openWriter();
//            try {
//                db.beginTransaction();
//                ContentValues values = new ContentValues();
//                values.put(DBHelper.BG_ID, bgId);
//                int result = db
//                    .update(DBHelper.TABLE_RECENT, values, String.format("%s=?", DBHelper.CHAT_ID),
//                        new String[]{chatId});
//                db.setTransactionSuccessful();
//                if (result > 0) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (Exception e) {
//                L.e(e);
//            } finally {
//                db.endTransaction();
//                closeDatabase(db, null);
//            }
//            return false;
//        }
//    }

}
