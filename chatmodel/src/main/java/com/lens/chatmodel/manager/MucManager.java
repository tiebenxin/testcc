package com.lens.chatmodel.manager;

import android.content.Context;
import android.text.TextUtils;

import com.fingerchat.api.listener.MucListener;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent.MucRefreshEnum;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.group.MucRequestCode;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.db.DBHelper;

import java.util.List;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by xhdl0002 on 2018/1/24.
 * 群管理类
 */

public class MucManager implements MucListener {

    private static MucManager instance;
    private static Context mContext;
    private MucRequestCode.Builder requestBuilder;
    //action请求单个群聊信息
    private int actionOneRoomCode = -1;
    private int qHomeRoomAllRoomCode = -1;
    private static UserInfo userInfo;
    private MucActionMessageEvent mucActionMessageEvent;

    public MucRequestCode.Builder getRequestBuilder() {
        if (null == requestBuilder) {
            requestBuilder = MucRequestCode.newBuilder();
        }
        return requestBuilder;
    }

    public static MucManager getInstance() {
        if (instance == null) {
            instance = new MucManager();
        }
        initUserInfo();
        return instance;
    }

    private static void initUserInfo() {
        if (userInfo == null) {
            userInfo = UserInfoRepository.getInstance().getUserInfo();
        }
    }

    public static MucManager getInstance(Context context) {
        if (instance == null) {
            instance = new MucManager();
        }
        if (null != context) {
            mContext = context;
        }
        return instance;
    }

    /**
     * 登录成功后查询群列表信息
     */
    public void qHomeAllRoomInfo() {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setOption(Muc.MOption.MQuery).setQueryType(Muc.QueryType.QAllRoomsOfUser);
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        qHomeRoomAllRoomCode = FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 查询单个群信息
     */
    public int qRoomInfo(Muc.QueryType queryType, String mucId) {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setOption(Muc.MOption.MQuery).setQueryType(queryType);
        if (!TextUtils.isEmpty(mucId)) {
            builder.setMucid(mucId);
        }
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        return FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * update群配置
     */
    public int updateMucConfig(Muc.PersonalConfig.Builder personalConfig,
        Muc.UpdateOption updateOption, String mucId) {
        Muc.MucItem.Builder config = Muc.MucItem.newBuilder();
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        config.setPConfig(personalConfig.build()).setMucid(mucId);
        builder.setConfig(config.build()).setMucid(mucId).setOption(Muc.MOption.UpdateConfig)
            .setUpdateOption(updateOption);
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        return FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 修改muc名称
     */
    public int updateMucName(String mucName, String mucId) {
        Muc.MucItem.Builder config = Muc.MucItem.newBuilder();
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        config.setMucname(mucName).setMucid(mucId);
        builder.setConfig(config.build()).setMucid(mucId).setOption(Muc.MOption.UpdateConfig)
            .setUpdateOption((Muc.UpdateOption.UName));
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        return FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 操作群组
     *
     * @param isAdmin 群主
     */
    public int operationMuc(boolean isAdmin, String mucId) {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setMucid(mucId);
        if (isAdmin) {
            //解散
            builder.setOption(Muc.MOption.Destory);
        } else {
            //退出
            builder.setOption(Muc.MOption.Leave);
        }
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        return FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 创建组
     *
     * @param selectUserIds 所选ids
     * @param mucnName 群名字
     */
    public int createMuc(ArrayList<String> selectUserIds, String mucnName) {
        Muc.MucItem.Builder config = Muc.MucItem.newBuilder();
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        config.setMucname(mucnName);
        builder.setOption(Muc.MOption.Create).addAllUserid(selectUserIds);
        builder.setConfig(config.build());
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        return FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 成员操作
     *
     * @param userIds 选择ids
     * @param opetion 类型
     */
    public void mucMberOperation(ArrayList<String> userIds, Muc.MOption opetion, String mucId) {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setMucid(mucId).addAllUserid(userIds).setOption(opetion);
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 群主确认
     *
     * @param userIds 被邀请的ids
     * @param optionUser 邀请者的ids
     * @param optionUser 操作者userId
     */
    public void confirmInvite(ArrayList<String> userIds, String from, String optionUser,
        String mucId) {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setMucid(mucId).setFrom(from).addAllUserid(userIds).setOption(MOption.Confirm)
            .setOperator(optionUser);
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 邀请撤销
     *
     * @param userIds 被邀请的ids
     * @param optionUser 邀请者的ids
     * @param optionUser 操作者userId
     */
    public void cancelInvite(ArrayList<String> userIds, String optionUser,
        String mucId) {
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setMucid(mucId).setFrom(optionUser).addAllUserid(userIds).setOption(MOption.Revoke)
            .setOperator(optionUser);
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
    }

    /**
     * 删除本地群相关info
     * (解散群聊，离开群聊，接受到销毁Action)
     */
    public void clearLocalMucInfo(String mucId, boolean isProvider) {
        //删组
        MucInfo.delGroupUser(mContext, mucId);
        //离开或者解散群聊都没必要清除本地群信息
        if (isProvider) {
//            ProviderChat.deleChat(mContext, mucId);
            //删除群成员
//            MucUser.delGroupUser(mContext, mucId);
        }
    }

    /**
     * 刷新聊天界面
     */
    public void refreshUI(int eActitityNum) {
        RefreshEntity entity = new RefreshEntity();
        entity.setActivity(eActitityNum);
        RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MAIN_REFRESH, entity);
        EventBus.getDefault().post(event);
    }

    /**
     * 群操作Action保存
     */
    public void saveMessage(Muc.MucAction action, String resultContent) {
        if (action.getAction() == MOption.Create) {//create消息不在此处保存
            return;
        }
        if (TextUtils.isEmpty(resultContent)) {
            return;
        }
        EventBus.getDefault().post
            (MucRefreshEvent.createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
        String mucId = action.getMucid();
        IChatRoomModel model = MessageManager.getInstance()
            .createActionMessage(action.getFrom().getUsername(), action.getFrom().getMucusernick(),
                mucId, action.getMucname(),
                ChatHelper.getActionType(action), resultContent, action.getTime());
        if (model != null) {
            int backId = ProviderChat.getBackGroundId(ContextHelper.getContext(), model.getTo());
            MessageManager.getInstance()
                .saveMessage(model, "", action.getMucname(), backId, false);
            if (action.getMucid()
                .equals(MessageManager.getInstance().getCurrentChatId())) {//当前处于聊天页面，刷新聊天页面
                refreshUI(ChatEnum.EActivityNum.CHAT.ordinal());
            } else {
                refreshUI(EActivityNum.MAIN.ordinal());
            }
        }
    }

    /**
     * 群主转让操作
     *
     * @param mucId 群id
     * @param fromUserName 操作人id
     * @param ownerId 转让的用户id
     */
    public void changerMucOwner(String mucId, String fromUserName, String ownerId) {
        String mySelfUserId = AppConfig.INSTANCE.get(AppConfig.ACCOUT).toLowerCase();
        //转让给自己
        if (mySelfUserId.equals(ownerId.toLowerCase())) {
            //修改mucItem
            MucInfo.updateById(mContext, mucId, DBHelper.ROLE, Muc.Role.Owner_VALUE);
        } else if (mySelfUserId.equals(fromUserName.toLowerCase())) {
            MucInfo.updateById(mContext, mucId, DBHelper.ROLE, Muc.Role.Member_VALUE);
        }
        //修改mucUser
        MucUser.updateById(mContext, mucId, "", DBHelper.GROUP_ROLE, Muc.Role.Member_VALUE);
        MucUser.updateById(mContext, mucId, ownerId, DBHelper.GROUP_ROLE, Muc.Role.Owner_VALUE);
    }

    @Override
    public void onMucAction(MucActionMessage actionMessage) {
        //解析action
        Muc.MucAction action = actionMessage.action;
        //判断操作是否来自自己
        boolean mySelf = AppConfig.INSTANCE.get(AppConfig.ACCOUT).toLowerCase()
            .equals(action.getFrom().getUsername().toLowerCase());
        actionOperation(action);
        if (mySelf || action.getAction().ordinal() == MOption.Invite_VALUE) {
            //post Message
            if (mucActionMessageEvent == null) {
                mucActionMessageEvent = (MucActionMessageEvent) EventFactory.INSTANCE
                    .create(EventEnum.MUC_ACTION_MESSAGE, actionMessage);
            } else {
                mucActionMessageEvent.setPacket(actionMessage);
            }
            EventBus.getDefault().post(mucActionMessageEvent);
        }
    }

    @Override
    public void onMuc(MucMessage mucMessage) {
        try {
            if (null != mucMessage) {
                //判断是否为action请求单个群聊info返回
                //查询所有群成员成功
                if (Common.MUC_QUERY_All_ROOMS_OF_USER_OK == mucMessage.message.getCode()) {
                    new Thread() {
                        @Override
                        public void run() {
                            MucInfo.delAllGroupUser(mContext);
                            //登录后拉取群列表 存储逻辑
                            MucInfo.updateListMucInfo(mContext, mucMessage.message.getItemList(),
                                MucInfo.selectAllMucInfo(mContext), UserInfoRepository.getUserId());
                        }
                    }.start();
                    EventBus.getDefault().post(MucRefreshEvent
                        .createMucRefreshEvent(MucRefreshEnum.GROUP_LIST_REFRESH));
                } else if (Common.MUC_QUERY_ROOM_BY_ID_OK == mucMessage.message
                    .getCode()) {//查询单个群成员成功
                    MucInfo.insertMucInfo(mContext, mucMessage.message.getItem(0),
                        UserInfoRepository.getUserId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMucMember(MucMemberMessage mucMemberMessage) {
    }

    /**
     * 解析action
     */

    public void actionOperation(Muc.MucAction action) {
        try {
            String mucId = action.getMucid();
            String mySelfId = AppConfig.INSTANCE.get(AppConfig.ACCOUT).toLowerCase();
            String fromUserName = action.getFrom().getUsername().toLowerCase();
            //判断是否存在群
            Muc.MucItem mucItem = MucInfo.selectByMucId(mContext, mucId);
            if (null == mucItem || TextUtils.isEmpty(mucItem.getMucid())) {
                if (action.getAction().ordinal() == MOption.Create_VALUE) {//创建群
                    //添加加入的成员 取群成员更新数据库
                    MucInfo.insertMucInfo(ContextHelper.getContext(), createMucItem(action),
                        UserInfoRepository.getUserId());
                    MucUser.insertMultipleGroupUser(mContext, action.getUsernamesList(),
                        mucId);
                } else {
                    //请求群聊信息 查询单个群信息
                    actionOneRoomCode = MucManager.getInstance()
                        .qRoomInfo(Muc.QueryType.QRoomById, mucId);
                }

            } else {
                switch (action.getAction().ordinal()) {
                    //邀请
                    case Muc.MOption.Invite_VALUE:
                        //添加加入的成员 取群成员更新数据库
                        MucUser.insertMultipleGroupUser(mContext, action.getUsernamesList(),
                            mucId);
                        break;
                    //加入
                    case Muc.MOption.Join_VALUE:
                        //添加加入的成员 取群成员更新数据库
                        MucUser.insertMultipleGroupUser(mContext, action.getUsernamesList(),
                            mucId);
                        break;
                    //踢人
                    case Muc.MOption.Kick_VALUE:
                        //删除成员 取群成员更新数据库
                        for (Muc.MucMemberItem item : action.getUsernamesList()) {
                            //剔除的为自己
                            if (item.getUsername().toLowerCase().equals
                                (mySelfId)) {
                                //删除本地群及群成员信息？
                                clearLocalMucInfo(mucId, false);
                                break;
                            }
                            MucUser.delGroupUserByUserId(mContext, mucId, item.getUsername());
                        }
                        break;
                    //离开
                    case Muc.MOption.Leave_VALUE:
                        //操作的对象为自己离开
                        if (mySelfId.equals(fromUserName)) {
                            //删除本地群及群成员信息？
                            clearLocalMucInfo(mucId, true);
                        } else {
                            //删除成员取群成员更新数据库
                            MucUser.delGroupUserByUserId(mContext, mucId, fromUserName);
                        }
                        break;
                    //改变角色
                    case Muc.MOption.ChangeRole_VALUE:
                        changerMucOwner(mucId, action.getFrom().getUsername(),
                            action.getUsernames(0).getUsername());
                        break;
                    //销毁
                    case Muc.MOption.Destory_VALUE:
                        //操作的对象为自己离开
                        clearLocalMucInfo(mucId, mySelfId.equals(fromUserName));
                        break;
                    //更新
                    case Muc.MOption.UpdateConfig_VALUE:
                        //啥啥更新
                        switch (action.getUpdateOption().ordinal()) {
                            //群验证方式
                            case Muc.UpdateOption.UAutoEnter_VALUE:
                                MucInfo.updateById(mContext, mucId, DBHelper.AUTOENTER,
                                    action.getNeedConfirm());
                                break;
                            //群名
                            case Muc.UpdateOption.UName_VALUE:
                                MucInfo.updateById(mContext, mucId, DBHelper.MUC_NAME,
                                    action.getMucname());
                                //修改会话名称
                                ProviderChat.updateChatName(mContext, mucId, action.getMucname());
                                break;

                            case Muc.UpdateOption.UUsernick_VALUE:    //群昵称,在修改页面已经处理
                                break;
                            //公告
                            case Muc.UpdateOption.USubject_VALUE:
                                MucInfo.updateById(mContext, mucId, DBHelper.SUBJECT,
                                    action.getSubject());
                                break;
                        }
                        break;
                    case MOption.Confirm_VALUE:
                        System.out.println("收到群确认消息");
                        break;
                    case MOption.Revoke_VALUE:
                        System.out.println("收到群邀请撤销消息");
                        //删除成员 取群成员更新数据库
                        for (Muc.MucMemberItem item : action.getUsernamesList()) {
                            MucUser.delGroupUserByUserId(mContext, mucId, item.getUsername());
                        }
                        break;
                }
            }
            //创建action消息，并保存数据库
            saveMessage(action, MucHelper.getActionText(mContext, action));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MucItem createMucItem(MucAction action) {
        Muc.MucItem.Builder mucItem = Muc.MucItem.newBuilder();
        mucItem.setMucid(action.getMucid()).
            setMucname(action.getMucname()).
            setSubject("0").
            setNeedConfirm(ESureType.YES.ordinal()).
            setMemberCount(action.getUsernamesCount());
        Muc.PersonalConfig.Builder persional = Muc.PersonalConfig.newBuilder();
        persional.setRoleValue(action.getFrom().getRoleValue()).
            setNoDisturb(ESureType.NO.ordinal()).
            setChatBg("0").
            setMucusernick(userInfo.getUsernick());
        mucItem.setPConfig(persional.build());
        return mucItem.build();

    }

    public static MucMemberItem createMemberItemFromUser(IChatUser user) {
        if (user == null) {
            return null;
        }
        MucMemberItem.Builder builder = MucMemberItem.newBuilder();
        builder.setAvatar(user.getAvatarUrl());
        builder.setUsername(user.getUserId());
        builder.setMucusernick(user.getUserNick());
        builder.setInviter(UserInfoRepository.getUserName());
        return builder.build();
    }

    public static List<MucMemberItem> createMemberListFromUsers(List<IChatUser> users) {
        if (users == null || users.size() <= 0) {
            return null;
        }
        int len = users.size();
        List<MucMemberItem> memberItems = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            memberItems.add(createMemberItemFromUser(users.get(i)));
        }
        return memberItems;
    }
}
