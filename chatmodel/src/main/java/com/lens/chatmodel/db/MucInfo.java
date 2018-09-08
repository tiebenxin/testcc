package com.lens.chatmodel.db;

import android.content.Context;
import android.text.TextUtils;

import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.bean.AllResult;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.db.DBHelper;
import com.lensim.fingerchat.db.DaoManager;

import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/19.
 * 群信息DB管理类
 */

public class MucInfo {

    public static List<Muc.MucItem> selectAllMucInfo(Context context) {
        if (context != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectAll();
        }
        return null;
    }

    public static boolean insertMucInfo(Context context, Muc.MucItem mucItem,
        String currentUserId) {
        if (context != null && mucItem != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.insertMucItem(context, mucItem, currentUserId);
        }
        return false;
    }

    public static Muc.MucItem selectMucInfoSingle(Context context, String mucId) {
        if (context != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectSingle(mucId);
        }
        return null;
    }

//    public static boolean insertMultipleMucInfo(Context context, List<Muc.MucItem> mucItems,
//        String currentUserId) {
//        if (context != null && mucItems != null && mucItems.size() > 0) {
//            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
//            return dao.insertMultiple(context, mucItems, currentUserId);
//        }
//        return false;
//    }

    /**
     * 查询单个群信息
     */
    public static Muc.MucItem selectByMucId(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectSingle(mucId);
        }
        return null;
    }

    public static boolean delMucInfo(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.delete(mucId);
        }
        return false;
    }

    /**
     * 删除所有群和成员
     */
    public static boolean delAllGroupUser(Context context) {
        if (context != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.deleteAll();
        }
        return false;
    }

    /**
     * 更新某个字段的值
     */
    public static boolean updateById(Context context, String mucId, String lineName,
        Object values) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.updateById(mucId, lineName, values);
        }
        return false;
    }

    /**
     * 全量修改mucInfo
     */
    public static boolean updateMucInfo(Context context, String mucId, Muc.MucItem mucItem,
        String currentUserId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.updateMucInfo(mucId, mucItem, currentUserId);
        }
        return false;
    }


    /**
     * 批量对比更新数据库
     *
     * @param newMucItems 新的集合
     * @param oldMucItems 本地集合
     * @param currentUserId 当前登录ID
     */
    public static void updateListMucInfo(Context context, List<Muc.MucItem> newMucItems,
        List<Muc.MucItem> oldMucItems, String currentUserId) {
        if (context != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            dao.updateListMucInfo(newMucItems, oldMucItems, currentUserId);
        }
    }

    /**
     * 群id查询群名
     */
    public static String getMucName(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.getMucName(mucId);
        }
        return "";
    }

    /**
     * 获取群免打扰信息
     */
    public static int getMucNoDisturb(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.getMucNoDisturb(mucId);
        }
        return 0;
    }

    public static boolean updateNoDisturb(String mucId, boolean isNoDisturb) {
        if (!TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(ContextHelper.getContext(), DaoManager.getUserID());
            return dao.markNoDisturb(mucId, isNoDisturb);
        }
        return false;
    }

    /**
     * 获取群聊天背景
     *
     * @return int
     */
    public static int getMucChatBg(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            String mucChatBg = dao.getMucChatBg(mucId);
            return TextUtils.isEmpty(mucChatBg) ? 0 : Integer.valueOf(mucChatBg);
        }
        return 0;
    }

    /**
     * 查询群成员
     *
     * @return List<Muc.MucMemberItem>
     */
    public static List<Muc.MucMemberItem> selectMucMemberItem(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            return MucUser.selectByGroupId(context, mucId, -1);
        }
        return null;
    }

    /**
     * 查询群头像
     */
    public static List<String> selectMucAvatar(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            return MucUser.qMucUserAvatars(context, mucId);
        }
        return null;
    }

    /**
     * 删除当前群所有成员
     */
    public static boolean deleAllMembers(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            return MucUser.delGroupUser(context, mucId);
        }
        return false;
    }

    public static AllResult selectMucByContent(Context context, String key) {
        if (context != null && !TextUtils.isEmpty(key)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectMucByContent(key);
        }
        return null;
    }

    /**
     * 查询群备注名
     */
    public static List<String> selectMucUserNickList(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            return MucUser.qMucUserNicks(context, mucId);
        }
        return null;
    }

    /*
    * 获取群组总数
    * */
    public static int selectMucCount(Context context) {
        if (context != null) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectMucCount();
        }
        return 0;
    }

    /**
     * 群id查询群创建者
     */
    public static String getMucCreator(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.selectMucCreator(mucId);
        }
        return "";
    }

    /**
     * 群备注名
     */
    public static String getMucUserNick(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(context, DaoManager.getUserID());
            return dao.getMucUserNick(mucId);
        }
        return "";
    }

    /**
     * 获取群成员总数
     *
     * @return int
     */
    public static int getMucMemberCount(String mucId) {
        if (!TextUtils.isEmpty(mucId)) {
            MucInfoDao dao = new MucInfoDao(ContextHelper.getContext(), DaoManager.getUserID());
            return dao.getMucMemberCount(mucId);
        }
        return 0;
    }

}
