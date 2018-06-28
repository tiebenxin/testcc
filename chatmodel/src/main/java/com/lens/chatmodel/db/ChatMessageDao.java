package com.lens.chatmodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
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
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.db.BaseDao;
import com.lensim.fingerchat.db.DBHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

    public AllResult selectMsgByContent(String chatId, String key) {
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


    /*
    * 按页搜索
    * @param pager 页数
    * @param number 每页条目数
    * @param user 聊天对象
    *
    * */
    public List<IChatRoomModel> selectAsPage(String user, int pager, int number,
        boolean isGroupChat) {
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


    @Override
    public boolean insert(IChatRoomModel model) {

        if (model != null) {
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
                    model.isGroupChat() ? EChatType.GROUP.ordinal() : EChatType.PRIVATE.ordinal());

                long result = db.insert(DBHelper.TABLE_MESSAGE, null, values);
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

    /*
    * 检测数据库中是否已有该msgid
    * */
    public boolean update(IChatRoomModel model) {
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

    @Override
    public boolean delete(String msgId) {
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

    //删除和某人的所有消息
    public boolean deleteChat(String user) {
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




  /*
  * 更新播放状态，未下载，未播放，已播放
  * */

    public void updatePlayStatus(String msgId, EPlayType type) {
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

    //获取某个聊天对象的所有图片消息
    public void selectAllImageMessages(String user, List<String> urls, List<String> msgIds) {
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

    public void updateContentAfterUpload(String msgId, String uploadUrl, ESendType type) {
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

    public void updateSendStatus(String msgId, ESendType type) {
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

    public void updateCancel(String msgId) {
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

    /*
    * 发送成功，更新发送状态及后台回执返回的时间
    * */
    public void updateSendSuccess(String msgId, ESendType type, long time) {
        if (type == null) {
            return;
        }
        SQLiteDatabase db = openWriter();

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBHelper.SEND_TYPE, type.value);

            //TODO:暂不更新服务器时间，以本地时间为准
//            values.put(DBHelper.TIME, time);
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

    public void updateMessageContent(String msgId, String content) {
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

    /*
  * 模糊搜索所有
  *
  * */
    public AllResult searchAllMessageByContent(String text) {
        SQLiteDatabase db = openWriter();
        AllResult result = null;
        try {
            db.beginTransaction();
            String sql = DBHelper.CONTENT + " like '%" + text + "%' and " + DBHelper.TYPE + "="
                + EMessageType.TEXT.ordinal();
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
        bean.setGroupChat(isGroupChat);
        return bean;
    }


}
