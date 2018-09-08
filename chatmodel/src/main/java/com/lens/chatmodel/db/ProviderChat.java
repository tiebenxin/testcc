package com.lens.chatmodel.db;

import android.content.Context;
import android.text.TextUtils;

import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.EmoBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.db.DaoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/14.
 * 聊天模块数据库操作类，ChatMessageDao，RecentMsgDao
 */

public class ProviderChat {

    public static boolean insertAndUpdateMessage(Context context, IChatRoomModel message) {
        if (context != null && message != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            boolean flag = dao.update(message);
            if (!flag) {
                flag = dao.insert(message);
            }
            return flag;
        }
        return false;
    }

    public static boolean insertMessageAsyn(Context context, IChatRoomModel message) {
        if (context != null && message != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.insertAsyn(message);
        }
        return false;
    }

    public static boolean updateMessage(Context context, IChatRoomModel message) {
        if (context != null && message != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.update(message);
        }
        return false;
    }

    public static void updateSendStatus(Context context, String msgId,
        ESendType type) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.updateSendStatus(msgId, type);
        }
    }

    public static void updateCancelMessage(Context context, String msgId) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.updateCancel(msgId);
        }
    }

    public static void updateSendSuccess(Context context, String msgId,
        ESendType type, long time) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.updateSendSuccess(msgId, type, time);
        }
    }

    public static void updatePlayStatus(Context context, String msgId, EPlayType type) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.updatePlayStatus(msgId, type);
        }
    }

    public static void updateMessageAfterUpload(Context context, String msgId, String content,
        ESendType type) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.updateContentAfterUpload(msgId, content, type);
        }
    }

    public static IChatRoomModel selectMsgSingle(Context context, String msgId) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.selectSingle(msgId);
        }
        return null;
    }

    public static List<IChatRoomModel> selectMsgAsPage(Context context, String user,
        int page, int num, boolean isGroupChat) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.selectAsPage(user, page, num, isGroupChat);
        }
        return null;
    }

    public static AllResult selectMsgByContent(Context context, String user, String key) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.selectMsgByContent(user, key);
        }
        return null;
    }

    public static boolean delePrivateMessage(Context context, String msgId) {
        if (context != null && !TextUtils.isEmpty(msgId)) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.delete(msgId);
        }
        return false;
    }

    //删除聊天记录
    public static void deleMessageChat(Context context, String user) {
        if (context != null && !TextUtils.isEmpty(user)) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.deleteChat(user);
        }
    }

    //删除一个对话
    public static void deleChat(Context context, String user) {
        if (context != null && !TextUtils.isEmpty(user)) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.deleteChat(user);
            deleRecent(context, user);
        }
    }

    public static void selectAllImageMessage(Context context, String user, List<String> urls,
        List<String> msgIds) {
        if (context != null && !TextUtils.isEmpty(user)) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            dao.selectAllImageMessages(user, urls, msgIds);
        }
    }

    public static List<IChatRoomModel> getMessagesByIds(Context context, List<String> ids) {
        if (context != null && ids != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            int len = ids.size();
            List<IChatRoomModel> models = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                IChatRoomModel m = dao.selectSingle(ids.get(i));
                if (m != null) {
                    models.add(m);
                }
            }
            return models;
        }
        return null;
    }

    /*
    * 搜索消息内容
    * */
    public static AllResult getMessageByString(Context context, String text) {
        if (context != null) {
            ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
            return dao.searchAllMessageByContent(text);
        }
        return null;
    }


    /*
    * 消息列表数据增删改查
    *
    * */
    public static boolean insertRecentMessage(Context context, RecentMessage message) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.insert(message);
    }

    public static boolean updateRecentMessage(Context context, RecentMessage message) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.update(message);
    }

    public static boolean updateRecentMessageAsyn(Context context, RecentMessage message) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.updateAsyn(message);
    }

    public static boolean updateRecentMessageAsyn(Context context, RecentMessage message,
        int unreadCount) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.updateAsyn(message, unreadCount);
    }

    public static List<RecentMessage> selectAllRecents(Context context) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.selectAll();
    }

    public static List<RecentMessage> selecRecentstAsPager(Context context, int pager, int num) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.selectAsPage("", pager, num);
    }


    public static boolean hasChat(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.hasChat(chatId);
    }

    public static RecentMessage selectSingeRecent(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.selectSingle(chatId);
    }

    public static boolean deleRecent(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.delete(chatId);
    }

    //置顶
    public static void markTop(Context context, String chatId, boolean isTop) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        dao.markTop(chatId, isTop);
    }

    //已读
    public static void markReaded(Context context, String chatId, boolean isUnReaded) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        dao.markReaded(chatId, isUnReaded);
    }

    //免打扰，群聊才可以设置？
    public static void markNoDisturb(Context context, String chatId, boolean isNoDisturb) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        dao.markNoDisturb(chatId, isNoDisturb);
    }


    public static List<UserBean> selectRecentChatUser(Context context) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.selectRecentTalk();
    }

    //获取所有未读消息总数
    public static int selectTotalUnreadMessageCount() {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.getUnreadCountOfAll();
    }

    //更新聊天背景图片
    public static boolean updateBackGround(Context context, String chatId, int number) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.updateBackGround(chatId, number);
    }

    //获取聊天背景图片
    public static int getBackGroundId(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.getBackGroundId(chatId);
    }

    //更新会话名称
    public static boolean updateChatName(Context context, String chatId, String chatName) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.updateChatName(chatId, chatName);
    }

    //更新会话名称
    public static int getNoDisturb(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.getNoDisturb(chatId);
    }

    //获取某聊天对象的未读消息数量
    public static int selectUnreadMessageCountOfUser(Context context, String user) {
        ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
        return dao.getUnreadCountOfUser(user);
    }

    //更新会话名称
    public static int getTopFlag(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.getTopFlag(chatId);
    }

    public static boolean clearRecentMessageById(Context context, String chatId) {
        RecentMsgDao dao = new RecentMsgDao(context, DaoManager.getUserID());
        return dao.clearMessage(chatId);
    }

    public static long getLastMessageTime() {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectLastMessageTime();
    }

    //获取聊天消息playstatus
    public static int getPlayStatus(Context context, String msgId) {
        ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
        return dao.getPlayStatus(msgId);
    }

    public static IChatRoomModel getLastMessage(String user, boolean isGroupChat) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectLastMessage(user, isGroupChat);
    }

    //更新消息未读状态,isUnreaded true 表示当前有未读，需修改为已读。
    public static boolean updateHasReaded(String userId, boolean isUnreaded) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateHasReaded(userId, isUnreaded);
    }

    //已读标记为未读，只改一条
    public static boolean updateHasReaded(String messageId) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateUnreadReaded(messageId);
    }

    public static List<String> getAllChat() {
        RecentMsgDao dao = new RecentMsgDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectChatIds();
    }

    public static List<String> getAllUnreadChat() {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectUnreadChats();
    }

    public static List<IChatRoomModel> getSendFailedMessage() {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectSendFailedMessage();
    }

    //更新消息at状态。
    public static boolean updateAt(String userId) {
        RecentMsgDao dao = new RecentMsgDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateAt(userId);
    }

    public static int getSendErrorMessageCount(String chatId) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectSendErrorMessageCount(chatId);
    }

    public static String getReadedUserIds(String msgId) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectReadedUserIds(msgId);
    }

    public static boolean updateReadedUserIds(String msgId, String userIds) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateReadedUserIds(msgId, userIds);
    }

    public static boolean updateServerReaded(String msgId) {
        ChatMessageDao dao = new ChatMessageDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateServerReaded(msgId);
    }
}
