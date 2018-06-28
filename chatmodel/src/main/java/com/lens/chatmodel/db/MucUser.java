package com.lens.chatmodel.db;

import android.content.Context;
import android.text.TextUtils;

import com.fingerchat.proto.message.Muc;
import com.lensim.fingerchat.db.DaoManager;

import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/15.
 * 群成员DB管理类
 */

public class MucUser {

    public static boolean insertMultipleGroupUser(Context context, List<Muc.MucMemberItem> mucItems, String mucId) {
        if (context != null && mucItems != null && mucItems.size() > 0) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.insertMultiple(mucItems, mucId);
        }
        return false;
    }

    public static List<Muc.MucMemberItem> selectByGroupId(Context context, String mucId, int count) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.selectAllById(mucId, count);
        }
        return null;
    }

    /**
     * 删除群成员
     *
     * @param context
     * @param mucId
     * @return
     */
    public static boolean delGroupUser(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.delete(mucId);
        }
        return false;
    }

    /**
     * 移除群的某个成员
     *
     * @param context
     * @param mucId
     * @param userId
     * @return
     */
    public static boolean delGroupUserByUserId(Context context, String mucId, String userId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.deleteByUserId(mucId, userId);
        }
        return false;
    }

    /**
     * 修改某个字段
     *
     * @param context
     * @param mucId
     * @param userId
     * @param lineName
     * @param values
     * @return
     */
    public static boolean updateById(Context context, String mucId, String userId, String lineName,
                                     Object values) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.updateById(mucId, userId, lineName, values);
        }
        return false;
    }

    /**
     * 查询单个群成员
     *
     * @param context
     * @param mucId
     * @param userId
     * @return
     */
    public static Muc.MucMemberItem selectUserById(Context context, String mucId, String userId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.selectUserById(mucId, userId);
        }
        return null;
    }

    public static List<String> qMucUserAvatars(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.qMucUserAvatars(mucId);
        }
        return null;
    }

    public static List<String> qMucUserNicks(Context context, String mucId) {
        if (context != null && !TextUtils.isEmpty(mucId)) {
            MucUserDao dao = new MucUserDao(context, DaoManager.getUserID());
            return dao.qMucUserNick(mucId);
        }
        return null;
    }
}
