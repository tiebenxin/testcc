package com.lens.chatmodel.manager;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.proto.message.Roster.ROption;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.Roster.RosterOption;
import com.fingerchat.proto.message.User.BindMessage;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.UserInfoBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/3/7.
 * 联系人管理类
 */

public class RosterManager {

    public static final int NICK = 0;
    public static final int CHAT_BG = 1;
    public static final int STAR_USER = 2;
    public static final int AVATAR = 3;
    public static final int PHONE_NUM = 4;
    public static final int PASSWORD = 5;

    @IntDef({NICK, CHAT_BG, STAR_USER, AVATAR, PHONE_NUM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EUpdateType {

    }


    private static RosterManager instance;


    public static RosterManager getInstance() {
        if (instance == null) {
            instance = new RosterManager();
        }
        return instance;
    }

    /*
   * 创建UserBean集合
   * */
    public List<UserBean> createChatUserFromList(List<RosterItem> l, ERelationStatus status) {
        if (l == null) {
            return null;
        }
        List<UserBean> temp = new ArrayList<>();
        int size = l.size();
        for (int i = 0; i < size; i++) {
            RosterItem item = l.get(i);
            if (item != null) {
                UserBean bean = new UserBean();
                bean.setRoster(item);
                int preERelation = ProviderUser.getUserRelationStatus(bean.getUserId());
                int newStatus = ProviderUser.getUserNewStatus(bean.getUserId());
                if (newStatus == ESureType.YES.ordinal()) {
                    if (preERelation == ERelationStatus.INVITE.ordinal()) {
                        bean.setHasReaded(ESureType.NO.ordinal());
                    } else {
                        int hasReaded = ProviderUser.getUserHasReaded(bean.getUserId());
                        if (hasReaded == 0) {
                            bean.setHasReaded(hasReaded);
                        } else {
                            bean.setHasReaded(ESureType.YES.ordinal());
                        }
                    }
                } else {
                    bean.setHasReaded(ESureType.YES.ordinal());
                }
                bean.setRelationStatus(status.ordinal());
                temp.add(bean);
            }
        }
        return temp;
    }

    /*
   * 创建新好友UserBean集合
   * */
    public List<UserBean> createNewFriendFromList(List<RosterItem> l, ERelationStatus status) {
        if (l == null) {
            return null;
        }
        List<UserBean> temp = new ArrayList<>();
        int size = l.size();
        for (int i = 0; i < size; i++) {
            RosterItem item = l.get(i);
            if (item != null) {
                UserBean bean = new UserBean();
                bean.setRoster(item);
                int preERelation = ProviderUser.getUserRelationStatus(bean.getUserId());
                if (preERelation == ERelationStatus.INVITE.ordinal()) {
                    bean.setHasReaded(ESureType.NO.ordinal());
                } else {
                    bean.setHasReaded(ESureType.YES.ordinal());
                }

                bean.setRelationStatus(status.ordinal());
                temp.add(bean);
            }
        }
        return temp;
    }


    /*
    * 非好友关系
    * */
    public List<UserBean> createChatUserFromList(List<RosterItem> l, ERelationStatus status,
        long time, int hasReaded, int newStatus) {
        if (l == null) {
            return null;
        }
        List<UserBean> temp = new ArrayList<>();
        int size = l.size();
        for (int i = 0; i < size; i++) {
            RosterItem item = l.get(i);
            if (item != null) {
                UserBean bean = new UserBean();
                bean.setRoster(item);
                bean.setRelationStatus(status.ordinal());
                bean.setTime(time);
                bean.setHasReaded(hasReaded);
                bean.setNewStatus(newStatus);
                temp.add(bean);
            }
        }
        return temp;
    }

    public UserBean createUserBean(RosterItem item) {
        int relation = ProviderUser.getUserRelationStatus(item.getUsername());
        UserBean bean = new UserBean();
        bean.setRoster(item);
        if (relation >= 0) {
            bean.setRelationStatus(relation);
        }
        return bean;

    }


    public IChatUser createUser(UserInfo info) {
        if (info == null) {
            return null;
        }
        UserBean bean = new UserBean();
        RosterItem.Builder builder = RosterItem.newBuilder();
        builder.setUsername(StringUtils.checkEmptyString(info.getUserid()));
        builder.setUsernick(StringUtils.checkEmptyString(info.getUsernick()));
        builder.setAvatar(StringUtils.checkEmptyString(info.getImage()));
        builder.setIsvalid(info.getIsvalid());
        builder.setSex(StringUtils.checkEmptyString(info.getSex()));
        builder.setEmpName(StringUtils.checkEmptyString(info.getEmpName()));
        builder.setEmpNo(StringUtils.checkEmptyString(info.getEmpNo()));
        builder.setWorkAddress(StringUtils.checkEmptyString(info.getWorkAddress()));
        builder.setDptName(StringUtils.checkEmptyString(info.getDptName()));
        builder.setDptNo(StringUtils.checkEmptyString(info.getDptNo()));
        bean.setRoster(builder.build());
        bean.setRelationStatus(ERelationStatus.SELF.ordinal());
        bean.setHasReaded(ESureType.YES.ordinal());
        return bean;
    }

    public UserInfo createUserInfo(UserInfoBean info) {
        if (info == null) {
            return null;
        }
        UserInfo bean = new UserInfo();
        bean.setUserid(StringUtils.checkEmptyString(info.getUserId()));
        //TODO:手机号没有，不改
        bean.setPhoneNumber(StringUtils.checkEmptyString(UserInfoRepository.getPhoneNumber()));
        bean.setUsernick(StringUtils.checkEmptyString(info.getUserNick()));
        bean.setImage(StringUtils.checkEmptyString(info.getAvatarUrl()));
        bean.setIsvalid(info.getValid());
        bean.setSex(StringUtils.checkEmptyString(info.getSex()));
        bean.setEmpName(StringUtils.checkEmptyString(info.getEmpName()));
        bean.setEmpNo(StringUtils.checkEmptyString(info.getEmpNo()));
        bean.setWorkAddress(StringUtils.checkEmptyString(info.getWorkAddress()));
        bean.setDptName(StringUtils.checkEmptyString(info.getDptName()));
        bean.setDptNo(StringUtils.checkEmptyString(info.getDptNo()));
        return bean;
    }

    /*
    * 更新好友信息
    * */
    public void updateRoster(String userId, Object content, @EUpdateType int type) {
        if (!TextUtils.isEmpty(userId)) {
            IChatUser user = ProviderUser.selectRosterSingle(ContextHelper.getContext(), userId);
            if (user == null) {
                return;
            }
            RosterItem.Builder builder = RosterItem.newBuilder();
            builder.setUsername(userId);
            builder.setAvatar(user.getAvatarUrl());
            builder.setDptNo(user.getDptNo());
            builder.setDptName(user.getDptName());
            builder.setWorkAddress(user.getWorkAddress());
            builder.setEmpNo(user.getEmpNo());
            builder.setEmpName(user.getEmpName());
            builder.setUsernick(user.getUserNick());
            builder.setIsvalid(user.isValid() ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
            builder.setIsQuit(user.isQuit() ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
            builder.setIsBlock(user.isBlock() ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
            switch (type) {
                case NICK:
                    builder.setRemarkName((String) content);
                    builder.setIsStar(
                        user.isStar() ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
                    builder.setChatBg(user.getBgId() + "");
                    break;
                case CHAT_BG:
                    int bg = (int) content;
                    builder.setChatBg(bg + "");
                    builder.setIsStar(
                        user.isStar() ? ESureType.YES.ordinal() : ESureType.NO.ordinal());
                    builder.setRemarkName(user.getRemarkName());

                    break;
                case STAR_USER:
                    int star = (int) content;
                    builder.setIsStar(star);
                    builder.setChatBg(user.getBgId() + "");
                    builder.setRemarkName(user.getRemarkName());
                    break;
            }
            FingerIM.I.updateFriendInfo(userId, builder.build());
        }
    }

    /*
    * 更新登录账号信息
    * */
    public void updateUser(String userId, Object content, @EUpdateType int type) {
        BindMessage.Builder builder = BindMessage.newBuilder();
        builder.setUsername(userId);
        switch (type) {
            case NICK:
                builder.setUsernick((String) content);
                break;
            case AVATAR:
                builder.setAvatar((String) content);
                break;
            case PHONE_NUM:
                builder.setPhoneNumber((String) content);
                break;
            case PASSWORD:
                builder.setPassword((String) content);
                break;
        }
        FingerIM.I.updateUserInfo(builder.build());
    }

    /*
    * 批量创建，修改分组
    * */
    public void createAndUpdateGroup(List<String> uerIds, String groupName) {
        RosterOption.Builder builder = RosterOption.newBuilder();
        builder.setOption(ROption.Group).addAllGroupMember(uerIds).setGroupName(groupName);
        FingerIM.I.updateGroup(builder.build());
    }

    /*
    * 修改个人分组
    * */
    public void createAndUpdateGroup(String userId, String groupName) {
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        RosterOption.Builder builder = RosterOption.newBuilder();
        builder.setOption(ROption.Group).addAllGroupMember(ids).setGroupName(groupName);
        FingerIM.I.updateGroup(builder.build());
    }

    /*
    * 批量移出分组
    * */
    public void romoveGroup(List<String> uerIds) {
        RosterOption.Builder builder = RosterOption.newBuilder();
        builder.setOption(ROption.Group).addAllGroupMember(uerIds);
        FingerIM.I.updateGroup(builder.build());
    }

    /*
    *销毁分组
    * */
    public void deleGroup(String groupName) {
        RosterOption.Builder builder = RosterOption.newBuilder();
        builder.setOption(ROption.GroupDelete).setGroupName(groupName);
        FingerIM.I.updateGroup(builder.build());
    }

    //删除分组
    public void deleGroup(ArrayList<UserBean> users, String groupName) {
        if (users != null && !TextUtils.isEmpty(groupName)) {
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean bean = users.get(i);
                List<String> groups = bean.getGroups();
                if (groups != null && groups.contains(groupName)) {
                    groups.remove(groupName);
                    ProviderUser.updateRosterGroup(ContextHelper.getContext(), bean.getUserId(),
                        StringUtils.getStringByList(groups));
                }
            }
        }
    }

    public void destroyGroup(List<UserBean> users, String groupName) {
        if (users != null && !TextUtils.isEmpty(groupName)) {
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean bean = users.get(i);
                List<String> groups = bean.getGroups();
                if (groups != null && groups.contains(groupName)) {
                    groups.remove(groupName);
                    ProviderUser.updateRosterGroup(ContextHelper.getContext(), bean.getUserId(),
                        StringUtils.getStringByList(groups));
                }
            }
        }
    }

    public void addGroup(ArrayList<UserBean> users, String groupName) {
        if (users != null && !TextUtils.isEmpty(groupName)) {
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean bean = users.get(i);
                List<String> groups = bean.getGroups();
                if (groups != null) {
                    if (!groups.contains(groupName)) {
                        groups.add(groupName);
                        ProviderUser.updateRosterGroup(ContextHelper.getContext(), bean.getUserId(),
                            StringUtils.getStringByList(groups));
                    }
                } else {
                    ProviderUser
                        .updateRosterGroup(ContextHelper.getContext(), bean.getUserId(), groupName);
                }
            }
        }
    }
}
