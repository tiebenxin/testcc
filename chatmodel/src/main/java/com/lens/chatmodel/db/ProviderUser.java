package com.lens.chatmodel.db;

import android.content.Context;
import android.text.TextUtils;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.RosterGroupBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.manager.RosterManager;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.db.DaoManager;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/19.
 */

public class ProviderUser {

    public static boolean insertRoster(Context context, IChatUser roster) {
        if (context != null && roster != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.insert(roster);
        }
        return false;
    }

    public static void updateRoster(Context context, IChatUser user) {
        if (context != null && user != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            boolean result = dao.updateRoster(user);
            if (!result) {
                dao.insert(user);
            }
        }
    }

    public static void updateUser(Context context, UserInfo user) {
        if (context != null && user != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            UserBean bean = (UserBean) RosterManager.getInstance().createUser(user);
            boolean result = dao.updateRoster(bean);
            if (!result) {
                dao.insert(bean);
            }
        }
    }

    public static IChatUser selectRosterSingle(Context context, String userId) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectSingle(userId);
        }
        return null;
    }

    /*
    获取所有非星标好友
    * */
    public static List<UserBean> selectRosterAll(Context context) {
        if (context != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectAllFriend();
        }
        return null;
    }

    /*
    获取所有星标好友
    * */
    public static List<IChatUser> selectAllStarUser(Context context) {
        if (context != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectAllStarUser();
        }
        return null;
    }

    public static boolean deleRoster(Context context, String userId) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.delete(userId);
        }
        return false;
    }

    public static String getRosterNick(Context context, String userId) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectUserNick(userId);
        }
        return userId;
    }

    public static String getUserAvatar(Context context, String userId) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectUserAvatar(userId);
        }
        return userId;
    }

    public static boolean updateRosterNick(Context context, String userId, String nick) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.updateRosterNick(userId, nick);
        }
        return false;
    }

    public static AllResult searchUserByContent(Context context, String key) {
        RosterDao dao = new RosterDao(context, DaoManager.getUserID());
        return dao.selectUserByContent(key);
    }

    /*
    * 更新用户分组信息
    * */
    public static boolean updateRosterGroup(Context context, String userId, String group) {
        if (context != null && !TextUtils.isEmpty(userId)) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.updateRosterGroup(userId, group);
        }
        return false;
    }

    /*
    * 获取单一分组信息
    * */
    public static RosterGroupBean getGroupByName(Context context, String groupName) {
        if (context != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectGroupByName(groupName);
        }
        return null;
    }

    /*
     * 获取所有分组信息
     * */
    public static List<RosterGroupBean> getAllGroup(Context context) {
        if (context != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectAllGroup();
        }
        return null;
    }

    /*
     * 获取所有未分组的用户信息
     * */
    public static List<IChatUser> getAllNoGroupUser(Context context) {
        if (context != null) {
            RosterDao dao = new RosterDao(context, DaoManager.getUserID());
            return dao.selectAllNoGroupUser();
        }
        return null;
    }

    public static int getUnreadedRosterCount() {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectUnreadRosterCount();
    }

    /*
    * 获取所有新好友
    * */
    public static List<IChatUser> getAllNewFriends() {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectAllNewFriend();
    }

    public static boolean updateFirendStatus(String userId, int status) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateFriendStatus(userId, status);
    }

    /*
    * 更新新好友消息已读
    * */
    public static boolean updateHasReaded(String userId, int status) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateHasReaded(userId, status);
    }

    /*
     * 获取所有未读的新好友
     * */
    public static List<IChatUser> getAllUnreadNewFriends() {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.selectAllUnreadNewFriend();
    }

    /*
     * 更新星标好友
     * */
    public static boolean updateStarUser(String userId, int status) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateStarUser(userId, status);
    }

    /*
     * 更新好友聊天背景
     * */
    public static boolean updateUserChatBg(String userId, int chatBg) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.updateRosterChatBg(userId, chatBg);
    }

    /*
     * 更新好友聊天背景
     * */
    public static int getUserChatBg(String userId) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.getUserChatBg(userId);
    }

    /*
        * 更新好友聊天背景
        * */
    public static int getUserRelationStatus(String userId) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.getUserRelationStatus(userId);
    }

    /*
        * 更新好友聊天背景
        * */
    public static int getUserHasReaded(String userId) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.getUserHasRead(userId);
    }

    /*
        * 更新好友聊天背景
        * */
    public static int getUserNewStatus(String userId) {
        RosterDao dao = new RosterDao(ContextHelper.getContext(), DaoManager.getUserID());
        return dao.getUserNewStatus(userId);
    }


}
