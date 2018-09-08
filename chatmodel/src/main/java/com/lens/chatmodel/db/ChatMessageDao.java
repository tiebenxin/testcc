package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.fingerchat.proto.message.Muc.MucItem;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatRoleType;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/13.
 * 聊天消息数据库操作类
 */

public class ChatMessageDao extends BaseDao<IChatRoomModel> {


    public ChatMessageDao(Context context, String userId) {
        super(context, userId);
    }

    public IChatRoomModel selectSingle(String id) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(id)) {
                SQLiteDatabase db = openReader();
                Cursor cursor = null;
                try {
                    db.beginTransaction();
                    cursor = db
                        .query(DBHelper.TABLE_MESSAGE, DBHelper.chat_fields,
                            String.format("%s=?", DBHelper.ID),
                            new String[]{id}, null, null, null);
                    IChatRoomModel message = null;
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            message = createMessageBean(cursor);
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
            return null;
        }

    }

    public AllResult selectMsgByContent(String chatId, String key) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                String sql = DBHelper.CONTENT + " like '%" + key + "%' and " + DBHelper.TYPE + "="
                    + EMessageType.TEXT.ordinal() + " and " + String.format("%s=?", DBHelper.TO);
                Cursor cursor = db
                    .query(DBHelper.TABLE_MESSAGE,
                        new String[]{DBHelper.TO, DBHelper.CONTENT, DBHelper.CHAT_TYPE}, sql,
                        new String[]{chatId}, null, null,
                        DBHelper.TIME + " DESC");
                AllResult result = null;
                if (cursor != null) {
                    result = new AllResult();
                    List<SearchMessageBean> beans = new ArrayList<>();
                    result.setKey(EResultType.RECORD.ordinal());
                    while (cursor.moveToNext()) {
                        String userId = cursor.getString(0);
                        String content = cursor.getString(1);
                        int chatType = cursor.getInt(2);
                        SearchMessageBean bean = new SearchMessageBean();
                        bean.setUserId(userId);
                        bean.setMessage(content);
                        bean.setGroupChat(chatType == EChatType.GROUP.ordinal());
                        beans.add(bean);
                    }
                    result.setResults(beans);
                }
                db.setTransactionSuccessful();
                return result;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return null;
        }
    }


    /*
    * 按页搜索
    * @param pager 页数
    * @param number 每页条目数
    * @param user 聊天对象
    *
    * */
    public List<IChatRoomModel> selectAsPage(String user, int pager, int number,
        boolean isGroupChat) {
        synchronized (getDBLock()) {

            SQLiteDatabase db = openWriter();
            List<IChatRoomModel> list = new ArrayList<>();
            int total = (pager + 1) * number;
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_MESSAGE, DBHelper.chat_fields,
                        String.format("%s=?", DBHelper.TO), new String[]{user}, null, null,
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
                        list.add(0, createMessageBean(cursor, isGroupChat));
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
            return null;
        }
    }


    @Override
    public boolean insert(IChatRoomModel model) {

        if (model != null) {
            synchronized (getDBLock()) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    ContentValues values = new ContentValues();
                    if (model.getMsgType() != null) {
                        values.put(DBHelper.TYPE, model.getMsgType().value);
                    }
                    values.put(DBHelper.TO, model.getTo());
                    values.put(DBHelper.FROM, model.getFrom());
                    if (model.getMsgType() == EMessageType.TEXT) {
                        BodyEntity entity = model.getBodyEntity();
                        if (entity != null) {
                            values.put(DBHelper.CONTENT, entity.getBody());
                        } else {
                            values.put(DBHelper.CONTENT, model.getBody());
                        }
                    } else {
                        values.put(DBHelper.CONTENT, model.getBody());

                    }
                    values.put(DBHelper.ID, model.getMsgId());
                    values.put(DBHelper.TIME, model.getTime());
                    values.put(DBHelper.CODE, model.getCode());
                    values.put(DBHelper.CANCLE, model.getCancel());
//                values.put(DBHelper.USER_IMAGE, model.getAvatarUrl());
                    if (model.getSendType() != null) {
                        values.put(DBHelper.SEND_TYPE, model.getSendType().value);
                    }
                    values.put(DBHelper.CHAT_TAG,
                        model.isIncoming() ? EChatRoleType.RECIPIENT.value
                            : EChatRoleType.SENDER.value);
                    values.put(DBHelper.UPLOAD_URL, model.getUploadUrl());
                    values.put(DBHelper.IS_SECRET,
                        model.isSecret() ? ESureType.YES.value : ESureType.NO.value);
                    if (model.getPlayStatus() != null) {
                        values.put(DBHelper.PLAY_STATUS, model.getPlayStatus().ordinal());
                    }
                    if (model.getActionType() != null) {
                        values.put(DBHelper.ACTION_TYPE, model.getActionType().ordinal());
                    }
                    values.put(DBHelper.USER_AVATAR, model.getAvatarUrl());
                    values.put(DBHelper.NICK, model.getNick());
                    values.put(DBHelper.CHAT_TYPE,
                        model.isGroupChat() ? EChatType.GROUP.ordinal()
                            : EChatType.PRIVATE.ordinal());
                    values.put(DBHelper.HAS_READED, model.getHasReaded());
                    values.put(DBHelper.READED_MEMBERS, model.getReadedUserId());
                    values.put(DBHelper.SERVER_READED, model.getServerReaded());
                    long result = db.insert(DBHelper.TABLE_MESSAGE, null, values);
                    db.setTransactionSuccessful();
                    return result > 0;
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

    /*
    * 检测数据库中是否已有该msgid
    * */
    public boolean update(IChatRoomModel model) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.ID, model.getMsgId());
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{model.getMsgId()});
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
    public boolean delete(String msgId) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(msgId)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    int result = db
                        .delete(DBHelper.TABLE_MESSAGE, String.format("%s=?", DBHelper.ID),
                            new String[]{msgId});
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

    //删除和某人的所有消息
    public boolean deleteChat(String user) {
        synchronized (getDBLock()) {
            if (!TextUtils.isEmpty(user)) {
                SQLiteDatabase db = openWriter();
                try {
                    db.beginTransaction();
                    int result = db
                        .delete(DBHelper.TABLE_MESSAGE, String.format("%s=?", DBHelper.TO),
                            new String[]{user});
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




  /*
  * 更新播放状态，未下载，未播放，已播放
  * */

    public void updatePlayStatus(String msgId, EPlayType type) {
        synchronized (getDBLock()) {
            if (type == null) {
                return;
            }
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.PLAY_STATUS, type.value);
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    //获取某个聊天对象的所有图片消息
    public void selectAllImageMessages(String user, List<String> urls, List<String> msgIds) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                String query = String.format("%s=? and %s=?", DBHelper.TYPE, DBHelper.TO);
                Cursor cursor = db
                    .query(DBHelper.TABLE_MESSAGE, new String[]{DBHelper.CONTENT, DBHelper.ID},
                        query, new String[]{EMessageType.IMAGE.ordinal() + "", user}, null, null,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String body = cursor.getString(0);
                        JSONObject object = new JSONObject(body);
                        if (object != null) {
                            String entity = object.optString("body");
                            if (!TextUtils.isEmpty(entity)) {
                                urls.add(entity);
                                String msgId = cursor.getString(1);
                                msgIds.add(msgId);
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
    }

    public void updateContentAfterUpload(String msgId, String uploadUrl, ESendType type) {
        synchronized (getDBLock()) {
            if (TextUtils.isEmpty(uploadUrl)) {
                return;
            }
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.UPLOAD_URL, uploadUrl);
                values.put(DBHelper.SEND_TYPE, type.ordinal());
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values,
                        String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    public void updateSendStatus(String msgId, ESendType type) {
        synchronized (getDBLock()) {
            if (type == null) {
                return;
            }
            SQLiteDatabase db = openWriter();

            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.SEND_TYPE, type.value);
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    public void updateCancel(String msgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.TYPE, EMessageType.NOTICE.ordinal());
                values.put(DBHelper.CANCLE, ESureType.YES.ordinal());
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    /*
    * 发送成功，更新发送状态及后台回执返回的时间
    * */
    public void updateSendSuccess(String msgId, ESendType type, long time) {
        synchronized (getDBLock()) {
            if (type == null) {
                return;
            }
            SQLiteDatabase db = openWriter();

            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.SEND_TYPE, type.value);

                //TODO:暂不更新服务器时间，以本地时间为准
                values.put(DBHelper.TIME, time);
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    public void updateMessageContent(String msgId, String content) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.CONTENT, content);
                int result = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
    }

    /*
  * 模糊搜索所有
  *
  * */
    public AllResult searchAllMessageByContent(String text) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            AllResult result = null;
            try {
                db.beginTransaction();
                String sql;
                if (StringUtils.isContainTransforChar(text)) {
                    text = StringUtils.getTranforChar(text);
                    sql =
                        DBHelper.CONTENT + " like '%" + text + "%' escape '/' and " + DBHelper.TYPE
                            + "="
                            + EMessageType.TEXT.ordinal();
                } else {
                    sql = DBHelper.CONTENT + " like '%" + text + "%' and " + DBHelper.TYPE + "="
                        + EMessageType.TEXT.ordinal();
                }
                Cursor cursor = db
                    .query(DBHelper.TABLE_MESSAGE,
                        new String[]{DBHelper.TO, DBHelper.CONTENT, DBHelper.CHAT_TYPE},
                        sql, null, null, null, DBHelper.TIME + " DESC");

                if (cursor != null) {
                    result = new AllResult();

                    List<SearchMessageBean> beans = new ArrayList<>();
                    result.setKey(EResultType.RECORD.ordinal());
                    Map<String, SearchMessageBean> beanMap = new HashMap<>();
                    while (cursor.moveToNext()) {
                        String userId = cursor.getString(0);
                        String content = cursor.getString(1);
                        int chatType = cursor.getInt(2);
                        SearchMessageBean bean = beanMap.get(userId);
                        if (bean != null) {
                            int count = bean.getCount();
                            count++;
                            bean.setCount(count);
                        } else {
                            bean = new SearchMessageBean();
                            bean.setUserId(userId);
                            bean.setMessage(content);
                            bean.setGroupChat(chatType == EChatType.GROUP.ordinal());
                            bean.setCount(1);
                            beanMap.put(userId, bean);
                        }
                    }
                    Set<Entry<String, SearchMessageBean>> entries = beanMap.entrySet();
                    for (Map.Entry<String, SearchMessageBean> entry : entries) {
                        SearchMessageBean bean = entry.getValue();
                        int count = bean.getCount();
                        bean.setMessage(count + "条相关记录");
                        beans.add(bean);
                    }
                    result.setResults(beans);
                }
                db.setTransactionSuccessful();
                return result;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return result;
        }
    }

    private IChatRoomModel createMessageBean(Cursor cursor) {
        MessageBean bean = new MessageBean();
        EMessageType type = EMessageType.fromInt(cursor.getInt(0));
        bean.setMessageType(type);

        bean.setIncoming(cursor.getInt(9) == EChatRoleType.RECIPIENT.value ? true : false);

        if (cursor.getString(1) != null) {
            bean.setTo(cursor.getString(1));
        }
        if (cursor.getString(2) != null) {
            bean.setContent(cursor.getString(2));
        }
        if (cursor.getString(3) != null) {
            bean.setMsgId(cursor.getString(3));
        }
        bean.setTime(cursor.getLong(4));

        bean.setCode(cursor.getInt(5));
        bean.setCancel(cursor.getInt(6));

        if (cursor.getString(7) != null) {
            bean.setFrom(cursor.getString(7));
        }

        bean.setSendType(ESendType.fromInt(cursor.getInt(8)));

        if (cursor.getString(10) != null) {
            bean.setUploadUrl(cursor.getString(10));
        }
        bean.setSecret(cursor.getInt(11) == ESureType.YES.value ? true : false);
        bean.setPlayStatus(EPlayType.fromInt(cursor.getInt(12)));
        bean.setActionType(EActionType.fromInt(cursor.getInt(13)));
        if (cursor.getString(14) != null) {
            bean.setAvatarUrl(cursor.getString(14));
        }
        if (cursor.getString(15) != null) {
            bean.setNick(cursor.getString(15));
        }
        bean.setHasReaded(cursor.getInt(16));

        if (cursor.getString(17) != null) {
            bean.setReadedUserIds(cursor.getString(17));
        }
        bean.setServerReaded(cursor.getInt(18));
        MucItem item = MucInfo.selectByMucId(ContextHelper.getContext(), bean.getTo());
        if (item != null) {
            bean.setGroupChat(true);
        } else {
            bean.setGroupChat(false);
        }
        return bean;
    }

    private IChatRoomModel createMessageBean(Cursor cursor, boolean isGroupChat) {
        MessageBean bean = new MessageBean();
        EMessageType type = EMessageType.fromInt(cursor.getInt(0));
        bean.setMessageType(type);

        bean.setIncoming(cursor.getInt(9) == EChatRoleType.RECIPIENT.value ? true : false);

        if (cursor.getString(1) != null) {
            bean.setTo(cursor.getString(1));
        }
        if (cursor.getString(2) != null) {
            bean.setContent(cursor.getString(2));
        }
        if (cursor.getString(3) != null) {
            bean.setMsgId(cursor.getString(3));
        }
        bean.setTime(cursor.getLong(4));

        bean.setCode(cursor.getInt(5));
        bean.setCancel(cursor.getInt(6));

        if (cursor.getString(7) != null) {
            bean.setFrom(cursor.getString(7));
        }

        bean.setSendType(ESendType.fromInt(cursor.getInt(8)));

        if (cursor.getString(10) != null) {
            bean.setUploadUrl(cursor.getString(10));
        }
        bean.setSecret(cursor.getInt(11) == ESureType.YES.value ? true : false);
        bean.setPlayStatus(EPlayType.fromInt(cursor.getInt(12)));
        bean.setActionType(EActionType.fromInt(cursor.getInt(13)));
        if (cursor.getString(14) != null) {
            bean.setAvatarUrl(cursor.getString(14));
        }
        if (cursor.getString(15) != null) {
            bean.setNick(cursor.getString(15));
        }
        bean.setHasReaded(cursor.getInt(16));
        if (cursor.getString(17) != null) {
            bean.setReadedUserIds(cursor.getString(17));
        }
        bean.setServerReaded(cursor.getInt(18));
        bean.setGroupChat(isGroupChat);
        return bean;
    }

    //同步
    public boolean insertAsyn(IChatRoomModel model) {
        return waitResult(getPool().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return insert(model);
            }
        }));
    }

    public long selectLastMessageTime() {
        synchronized (getDBLock()) {
            long time = 0;
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MESSAGE, new String[]{DBHelper.TIME}, null, null, null,
                        null, DBHelper.TIME + " DESC", "1");
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        time = cursor.getLong(0);
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return time;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return time;
        }
    }

    //获取播放状态
    public int getPlayStatus(String msgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            Cursor cursor = null;
            int status = 0;
            try {
                db.beginTransaction();
                cursor = db
                    .query(DBHelper.TABLE_MESSAGE, new String[]{DBHelper.PLAY_STATUS},
                        String.format("%s=?", DBHelper.ID),
                        new String[]{msgId}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        status = cursor.getInt(0);
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return status;
        }
    }

    //获取最新的一条消息
    public IChatRoomModel selectLastMessage(String user, boolean isGroupChat) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            List<IChatRoomModel> list = new ArrayList<>();
            try {
                db.beginTransaction();
                Cursor cursor = db
                    .query(DBHelper.TABLE_MESSAGE, DBHelper.chat_fields,
                        String.format("%s=?", DBHelper.TO), new String[]{user}, null, null,
                        DBHelper.TIME + " DESC", "1");
                IChatRoomModel model = null;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        model = createMessageBean(cursor, isGroupChat);
                    }
                }
                db.setTransactionSuccessful();
                return model;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
            return null;
        }
    }

    public boolean updateHasReaded(String userId, boolean isUnreaded) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                String sql;
                if (isUnreaded) {//有未读，标记为已读
                    sql = String
                        .format("update %s set %s=1 where %s='%s' and %s=0", DBHelper.TABLE_MESSAGE,
                            DBHelper.HAS_READED, DBHelper.TO, userId, DBHelper.HAS_READED);
                } else {//已读标记为未读,只改一条
                    sql = String
                        .format("update %s set %s=0 where %s='%s' and %s=1", DBHelper.TABLE_MESSAGE,
                            DBHelper.HAS_READED, DBHelper.TO, userId, DBHelper.HAS_READED);
                }
                db.execSQL(sql);
                db.setTransactionSuccessful();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return false;
    }

    public boolean updateUnreadReaded(String msgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                String sql = String
                    .format("update %s set %s=0 where %s='%s'", DBHelper.TABLE_MESSAGE,
                        DBHelper.HAS_READED, DBHelper.ID, msgId);

                db.execSQL(sql);
                db.setTransactionSuccessful();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return false;
    }

    public int getUnreadCountOfUser(String user) {
        synchronized (getDBLock()) {
            int count = 0;
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_MESSAGE, null,
                    String.format("%s=? and %s=?", DBHelper.TO, DBHelper.HAS_READED),
                    new String[]{user, 0 + ""}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count = cursor.getCount();
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

    public int getUnreadCountOfAll() {
        synchronized (getDBLock()) {
            int count = 0;
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                String sql = String
                    .format("select * from %s where %s=0", DBHelper.TABLE_MESSAGE,
                        DBHelper.HAS_READED);
                cursor = db.rawQuery(sql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count = cursor.getCount();
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

    /*
    * 获取有未读消息的chat
    * */
    public List<String> selectUnreadChats() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            List<String> users = new ArrayList<>();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_MESSAGE, new String[]{DBHelper.TO},
                    String.format("%s=?", DBHelper.HAS_READED),
                    new String[]{0 + ""}, null, null, DBHelper.TIME + " desc ");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String userId = cursor.getString(0);
                        if (!TextUtils.isEmpty(userId) && !users.contains(userId)) {
                            users.add(userId);
                        }
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return users;
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
    * 获取发送失败的message
    * messageType 为text, incomeing 为false, sendType为0（sending状态)
    * */
    public List<IChatRoomModel> selectSendFailedMessage() {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            List<IChatRoomModel> messages = new ArrayList<>();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                String where =
                    DBHelper.TYPE + "=" + EMessageType.TEXT.value + " and " + DBHelper.CHAT_TAG
                        + "=" + EChatRoleType.SENDER.value + " and " + DBHelper.SEND_TYPE
                        + " in (0,4)";
                cursor = db
                    .query(DBHelper.TABLE_MESSAGE, DBHelper.chat_fields, where, null, null, null,
                        DBHelper.TIME + " desc ");
                if (cursor != null) {
                    IChatRoomModel message = null;
                    while (cursor.moveToNext()) {
                        message = createMessageBean(cursor);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();
                return messages;
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
   * 获取发送失败提示的message count，count 大于1表示已接受过非好友或非群成员发送失败提示
   * messagetype 为 action, actionType为none, 发送状态为error（3）
   * */
    public int selectSendErrorMessageCount(String chatId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            int count = 0;
            Cursor cursor = null;
            try {
                db.beginTransaction();
                String where =
                    DBHelper.TYPE + "=" + EMessageType.ACTION.value + " and " + DBHelper.ACTION_TYPE
                        + "=" + EActionType.NONE.value + " and " + DBHelper.SEND_TYPE
                        + " in (5)" + " and " + DBHelper.TO + "=" + "\'" + chatId + "\'";
                cursor = db
                    .query(DBHelper.TABLE_MESSAGE, DBHelper.chat_fields, where, null, null, null,
                        DBHelper.TIME + " desc ");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count = cursor.getCount();
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


    /*
   * 获取消息已读 的好友或者群成员
   * */
    public String selectReadedUserIds(String msgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            String result = "";
            Cursor cursor = null;
            try {
                db.beginTransaction();
                cursor = db.query(DBHelper.TABLE_MESSAGE, new String[]{DBHelper.READED_MEMBERS},
                    String.format("%s=?", DBHelper.ID), new String[]{msgId}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        result = cursor.getString(0);
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
   * 获取消息已读 的好友或者群成员
   * */
    public boolean updateReadedUserIds(String msgId, String userIds) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openReader();
            Cursor cursor = null;
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(DBHelper.READED_MEMBERS, userIds);
                int b = db
                    .update(DBHelper.TABLE_MESSAGE, values, String.format("%s=?", DBHelper.ID),
                        new String[]{msgId});
                db.setTransactionSuccessful();
                return b > 0;
            } catch (Exception e) {
                L.e(e);
            } finally {
                db.endTransaction();
                closeDatabase(db, cursor);
            }
            return false;
        }
    }

    //0为已读，1为未读
    public boolean updateServerReaded(String msgId) {
        synchronized (getDBLock()) {
            SQLiteDatabase db = openWriter();
            try {
                db.beginTransaction();
                String sql = String
                    .format("update %s set %s=0 where %s='%s' and %s=1", DBHelper.TABLE_MESSAGE,
                        DBHelper.SERVER_READED, DBHelper.ID, msgId, DBHelper.SERVER_READED);
                db.execSQL(sql);
                db.setTransactionSuccessful();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                closeDatabase(db, null);
            }
        }
        return false;
    }

}
