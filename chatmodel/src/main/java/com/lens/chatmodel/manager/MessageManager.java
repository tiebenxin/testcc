package com.lens.chatmodel.manager;

import android.content.Context;
import android.text.TextUtils;

import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.message.FGPushMessage;
import com.fingerchat.api.message.MessageAckMessage;
import com.fingerchat.api.message.MucChatMessage;
import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.api.message.PrivateChatMessage;
import com.fingerchat.api.message.ReadAckMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.fingerchat.proto.message.Excute.ExcuteType;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
import com.fingerchat.proto.message.ReadAck.ReadedMessage;
import com.fingerchat.proto.message.ReadAck.ReadedMessageList;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.bean.body.CardBody;
import com.lens.chatmodel.bean.body.PushEntity;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.ChatMessageDao;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.receiver.TaskReceiveOfflineMsg;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.db.DaoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/1/5.
 * 消息管理类
 */

public class MessageManager implements AckListener {

    private ChatMessageEvent chatMessageEvent;

    private Map<String, IChatRoomModel> sequenceMap = new LinkedHashMap<>();
    private Map<String, UserBean> userMap = new HashMap<>();

    private Map<String, IChatRoomModel> sequenceReadedChatMap = new LinkedHashMap<>();
    private Map<String, ReadedMessageList> sequenceReadedMap = new LinkedHashMap<>();

    private static MessageManager instance;
    private String currentChatId;
    private static UserInfo mUserInfo;
    private boolean firstNotification;
    private boolean isMessageChange;//是否有消息变化

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        initUserInfo();
        return instance;
    }

    /*
    * 初始化登录账号的UserInfo,为保证每次都是最新信息，每次重新创建
    * */
    public static void initUserInfo() {
        mUserInfo = UserInfoRepository.getInstance().getUserInfo();
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setCurrentChat(String chatId) {
        firstNotification = true;
        currentChatId = chatId;
    }

    public String getCurrentChatId() {
        return currentChatId;
    }

    public boolean isCurrentChat(String id) {
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(currentChatId) && id
            .equalsIgnoreCase(currentChatId)) {
            return true;
        }
        return false;
    }


    //发送消息
    public void sendMessage(IChatRoomModel message) {
        MessageContext context = null;
        if (message instanceof MessageBean) {
            MessageBean bean = (MessageBean) message;
            if (bean.isGroupChat()) {
                RoomMessage.Builder builder = RoomMessage.newBuilder();
                builder.setId(bean.getMsgId());
                builder.setMucid(bean.getTo());
                builder.setUsername(bean.getFrom());
                builder.setTime(bean.getTime());
                if (bean.getSendType() != null && bean.getSendType() == ESendType.FILE_SUCCESS
                    && !TextUtils.isEmpty(bean.getUploadUrl())) {//如果是有文件上传的消息，需而更换content内容
                    builder.setContent(bean.getUploadUrl());
                } else {
                    builder.setContent(bean.getBody());
                }
                builder.setType(ChatHelper.getMessageType(bean.getMsgType()));
                context = MessageContext.build(builder.build().toByteArray());
                if (context == null) {
                    return;
                }
                context.setTimeout(20000)
                    .setRetryCount(1);
                FingerIM.I.sendMessage(Command.GROUP_CHAT, context);
            } else {
                PrivateMessage.Builder builder = PrivateMessage.newBuilder();
                builder.setFrom(bean.getFrom());
                builder.setType(ChatHelper.getMessageType(bean.getMsgType()));
                builder.setTo(bean.getTo());
                builder.setTime(bean.getTime());
                builder.setAvatar(bean.getAvatarUrl());
                builder.setId(bean.getMsgId());
                if (bean.getSendType() != null && bean.getSendType() == ESendType.FILE_SUCCESS
                    && !TextUtils.isEmpty(bean.getUploadUrl())) {//如果是有文件上传的消息，需而更换content内容
                    builder.setContent(bean.getUploadUrl());
                } else {
                    builder.setContent(bean.getBody());
                }
                System.out.println(MessageManager.class.getSimpleName() + "--" + bean.getContent());
                context = MessageContext.build(builder.build().toByteArray());
                if (context == null) {
                    return;
                }
                context.setTimeout(20000)
                    .setRetryCount(1);
                FingerIM.I.sendMessage(Command.PRIVATE_CHAT, context);
            }
        }
        sequenceMap.put(message.getMsgId(), message);//添加到发送序列
    }

    /*
    * 发送撤销消息
    * */
    public void sendCancelMessage(IChatRoomModel message) {
        MessageContext context = null;
        if (!message.isGroupChat()) {
            PrivateMessage.Builder builder = PrivateMessage.newBuilder();
            BodyEntity body = createBody(message.getMsgId(), false, EMessageType.NOTICE,
                message.getNick());
            builder.setContent(BodyEntity.toJson(body));
            builder.setFrom(message.getFrom());
            builder.setTo(message.getTo());
            builder.setId(message.getMsgId());
            builder.setType(MessageType.NOTICE);
            builder.setCancel(ESureType.YES.ordinal());
            context = MessageContext.build(builder.build().toByteArray());
            context.setTimeout(20000)
                .setRetryCount(1);
            FingerIM.I.sendMessage(Command.PRIVATE_CHAT, context);
        } else {
            RoomMessage.Builder builder = RoomMessage.newBuilder();
            BodyEntity body = createBody(message.getMsgId(), false, EMessageType.NOTICE,
                getUserInfo().getImage(), getUserInfo().getUsernick(), message.getGroupName());
            builder.setContent(BodyEntity.toJson(body));
            builder.setUsername(message.getFrom());
            builder.setMucid(message.getTo());
            builder.setId(message.getMsgId());
            builder.setType(MessageType.NOTICE);
            builder.setCancel(ESureType.YES.ordinal());
            context = MessageContext.build(builder.build().toByteArray());
            context.setTimeout(20000)
                .setRetryCount(1);
            FingerIM.I.sendMessage(Command.GROUP_CHAT, context);
        }
    }

    //发送已读消息
    public void sendReadMessage() {
        if (sequenceReadedChatMap == null || sequenceReadedChatMap.size() <= 0) {
            return;
        }
        List<ReadedMessage> list = new ArrayList<>();
        for (IChatRoomModel model : sequenceReadedChatMap.values()) {
            ReadedMessage readedMessage = createReadedMessage(model);
            list.add(readedMessage);
        }
        sequenceReadedChatMap.clear();
        ReadedMessageList.Builder builder = ReadedMessageList.newBuilder();
        builder.setId(UUID.randomUUID().toString());
        builder.addAllReadedList(list);
        ReadedMessageList readedMessageList = builder.build();
        sequenceReadedMap.put(readedMessageList.getId(), readedMessageList);
        FingerIM.I.read(readedMessageList);
    }

    private void doSendFailed(String msgId) {
        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            ProviderChat.updateSendStatus(ContextHelper.getContext(),
                sequenceMap.get(msgId).getMsgId(), ESendType.ERROR);
            sequenceMap.remove(msgId);
            notifyUpdateUI(msgId, ChatMessageEvent.ERROR);
        }
    }

    //cancel消息
    private void doCancel(String userId, String msgId) {
        if (!userId.equals(getUserInfo().getUserid())) {//接受到别人cancel的消息
            ProviderChat.updateCancelMessage(ContextHelper.getContext(), msgId);
            IChatRoomModel model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), msgId);
            MucItem mucItem = MucInfo.selectByMucId(ContextHelper.getContext(), model.getTo());
            if (mucItem != null) {
                ((MessageBean) model).setGroupChat(true);
            } else {
                ((MessageBean) model).setGroupChat(false);
            }
            RecentMessage message = ProviderChat
                .selectSingeRecent(ContextHelper.getContext(), userId);
            if (message != null) {
//                message.setHint(getCancelText(model));
//                message.setMsgType(EMessageType.NOTICE);
                message.setTime(System.currentTimeMillis());
                ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
            }
        } else {//自己cancel的回执
            IChatRoomModel model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), msgId);
            ProviderChat.updateCancelMessage(ContextHelper.getContext(), msgId);
            RecentMessage message = ProviderChat
                .selectSingeRecent(ContextHelper.getContext(), model.getTo());
            if (message != null) {
//                message.setHint(ContextHelper.getString(R.string.cancel_message_you));
//                message.setMsgType(EMessageType.NOTICE);
                message.setTime(System.currentTimeMillis());
                ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
            }

        }
        notifyUpdateUI(msgId, ChatMessageEvent.CANCEL);
    }

    //发送消息错误，非好友。非群成员，房间不存在
    private void doSendError(long code, String msgId) {
        if (ChatHelper.isSystemUser(getCurrentChatId())) {//小秘书，系统消息
            return;
        }
        ProviderChat.updateSendStatus(ContextHelper.getContext(), msgId, ESendType.ACK_ERROR);
        int count = ProviderChat.getSendErrorMessageCount(getCurrentChatId());//是否已经提示过了，提示过不再提示
        if (count > 0) {
            return;
        }
        IChatRoomModel model = createErrorMessage(getUserInfo().getUserid(),
            getUserInfo().getUsernick(), getCurrentChatId(), "",
            getErrorText(code, getCurrentChatId()),
            System.currentTimeMillis());
        if (code == Common.ERROR_NO_FRIEND) {
            ((MessageBean) model).setGroupChat(false);
        } else {
            ((MessageBean) model).setGroupChat(true);
        }
        ProviderChat.insertAndUpdateMessage(ContextHelper.getContext(), model);
        notifyDataChange(EActivityNum.CHAT, null);
    }

    public String getErrorText(long code, String userId) {
        if (code == Common.ERROR_NO_FRIEND) {
            UserBean bean = getCacheUserBean(userId);
            String name = userId;
            if (bean != null) {
                name = bean.getUserNick();
            }
            return ContextHelper.getContext().getString(R.string.no_friend_verify_notice, " '" +
                MucHelper.resultBuildName(userId, name) + "' ") + "<verify id=" + " '" + userId
                + "' " + ">发送验证</verify>";
        } else if (code == Common.ERROR_NO_MEMBER) {
            return ContextHelper.getString(R.string.error_no_member_of_muc);
        } else if (code == Common.ERROR_NO_EXSIT) {
            return ContextHelper.getString(R.string.error_no_exit_room);
        }
        return "";
    }

    public String getCancelText(IChatRoomModel msg) {
        String hint;
        if (msg.isGroupChat()) {
            if (msg.isIncoming()) {
                hint = String
                    .format(ContextHelper.getString(R.string.cancel_message), StringUtils
                        .getUserNick(msg.getBodyEntity().getMucNickName(), msg.getFrom()));
            } else {
                hint = ContextHelper.getString(R.string.cancel_message_you);
            }
        } else {
            if (msg.isIncoming()) {
                hint = String
                    .format(ContextHelper.getString(R.string.cancel_message), msg.getNick());
            } else {
                hint = ContextHelper.getString(R.string.cancel_message_you);
            }
        }
        return hint;

    }


    private void doSendSuccess(String msgId, long time) {
        Map<String, IChatRoomModel> sequenceMap = getSequenceMap();
        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            System.out.println(
                "MessageManager成功消息id:" + msgId + "--" + sequenceMap.get(msgId)
                    .getContent());
            if (sequenceMap.get(msgId) != null) {
                ProviderChat.updateSendSuccess(ContextHelper.getContext(),
                    sequenceMap.get(msgId).getMsgId(), ESendType.MSG_SUCCESS, time);
                sequenceMap.remove(msgId);
            }
        }
    }

    private boolean isMapValid(Map<String, IChatRoomModel> map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }

    private boolean isReadMapValid(Map<String, ReadedMessageList> map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }

    private void notifyUpdateUI(String msgId, int type) {
        if (chatMessageEvent == null) {
            chatMessageEvent = (ChatMessageEvent) EventFactory.INSTANCE
                .create(EventEnum.CHAT_MESSAGE, null);
        }
        if (chatMessageEvent != null) {
            if (type == ChatMessageEvent.ERROR) {
                chatMessageEvent.setType(type);
                chatMessageEvent.setMsgId(msgId);
            } else if (type == ChatMessageEvent.CANCEL) {
                chatMessageEvent.setType(type);
                chatMessageEvent.setMsgId(msgId);
            }
            EventBus.getDefault().post(chatMessageEvent);
        }
    }

    public Map<String, IChatRoomModel> getSequenceMap() {
        return sequenceMap;
    }


    //接收消息,以服务器时间为准
    public void onReceive(BaseMessage message) {
        if (message != null) {
            boolean isFromMySelf = false;
            if (message instanceof PrivateChatMessage) {
                PrivateChatMessage privateChatMessage = (PrivateChatMessage) message;
                if (privateChatMessage.message != null) {
                    PrivateMessage privateMessage = privateChatMessage.message;
                    saveUserMap(privateMessage.getFrom());
                    EMessageType type = ChatHelper
                        .getMessageType(privateChatMessage.message.getType());
                    if (type == EMessageType.ERROR) {//接受到错误消息
                        String msgId = privateChatMessage.message.getId();
                        doSendFailed(msgId);
                    } else if (type == EMessageType.NOTICE
                        && privateChatMessage.message.getCancel() == 1) {//是cancel消息
                        doCancel(privateChatMessage.message.getFrom(),
                            privateChatMessage.message.getId());
                    } else {
                        if (checkFromSelf(privateMessage.getFrom())) {
                            isFromMySelf = true;
                        }
                        MessageBean msg = createMessageBean(privateMessage, isFromMySelf);
                        if (msg != null) {
                            UserBean bean = getCacheUserBean(msg.getTo());
                            if (bean == null) {
                                return;
                            }
                            if (!isDoingMessage(msg.getMsgType())) {
                                if (!isFromMySelf) {
                                    msg.setNick(ChatHelper
                                        .getUserRemarkName(bean.getRemarkName(), bean.getUserNick(),
                                            bean.getUserId()));//初始化昵称
                                } else {
                                    msg.setNick(ChatHelper
                                        .getUserRemarkName("", getUserInfo().getUsernick(),
                                            getUserInfo().getUserid()));//初始化昵称
                                }
                                ProviderChat
                                    .insertAndUpdateMessage(ContextHelper.getContext(), msg);
                                RecentMessage recent = ProviderChat
                                    .selectSingeRecent(ContextHelper.getContext(), msg.getTo());
                                if (recent != null) {
                                    saveRecent(msg, bean.getAvatarUrl(), ChatHelper
                                            .getUserRemarkName(bean.getRemarkName(), bean.getUserNick(),
                                                bean.getUserId()), recent.getNotDisturb(),
                                        recent.getBackgroundId(), recent.getTopFlag(),
                                        !isCurrentChat(msg.getTo()));
                                } else {
                                    saveRecent(msg, bean.getAvatarUrl(), ChatHelper
                                            .getUserRemarkName(bean.getRemarkName(), bean.getUserNick(),
                                                bean.getUserId()),
                                        ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                        ESureType.NO.ordinal(), !isCurrentChat(msg.getTo()));
                                }
                                notifyMotification(canNotify(msg, isAtMessage(msg)), msg);
                            }
                            notifyDataChange(msg);
                            L.i(MessageManager.class.getSimpleName() + ": 接受到私聊消息");
                        }
                    }

                }
            } else if (message instanceof MucChatMessage) {
                MucChatMessage mucChatMessage = (MucChatMessage) message;
                if (mucChatMessage.message != null) {
                    RoomMessage roomMessage = mucChatMessage.message;
                    EMessageType type = ChatHelper.getMessageType(roomMessage.getType());
                    if (type == EMessageType.ERROR) {
                        doSendFailed(roomMessage.getId());
                    } else if (type == EMessageType.NOTICE
                        && roomMessage.getCancel() == 1) {//是cancel消息
                        doCancel(roomMessage.getMucid(), roomMessage.getId());
                    } else {
                        if (checkFromSelf(roomMessage.getUsername())) {
                            isFromMySelf = true;
                        }
                        MessageBean group = createMessageBean(roomMessage, isFromMySelf);
                        if (group != null) {
                            String mucId = mucChatMessage.message.getMucid();
                            int disturb = MucInfo
                                .getMucNoDisturb(ContextHelper.getContext(), mucId);
                            int backId = MucInfo.getMucChatBg(ContextHelper.getContext(), mucId);
                            int topFlag = ProviderChat
                                .getTopFlag(ContextHelper.getContext(), mucId);

                            if (!isDoingMessage(group.getMsgType())) {
                                ProviderChat
                                    .insertAndUpdateMessage(ContextHelper.getContext(), group);
                                saveRecent(group, group.getAvatarUrl(), StringUtils
                                        .getUserNick(group.getNick(), group.getFrom()),
                                    disturb, backId, topFlag, !isCurrentChat(group.getTo()));

                                String mucName = MucInfo.getMucName(ContextHelper.getContext(),
                                    mucChatMessage.message.getMucid());
                                group.setGroupName(mucName);
                                notifyMotification(canNotify(group, isAtMessage(group)), group);
                            }
                            notifyDataChange(group);
                        }

                    }
                }
            } else if (message instanceof OfflineMessage) {//接受离线消息
                OfflineMessage offlineMessage = (OfflineMessage) message;
                TaskReceiveOfflineMsg taskReceiveOfflineMsg = new TaskReceiveOfflineMsg(
                    offlineMessage);
                taskReceiveOfflineMsg.execute();
            } else if (message instanceof MessageAckMessage) {
                MessageAckMessage ackMessage = (MessageAckMessage) message;
                if (ackMessage.message != null) {
                    long code = ackMessage.message.getCode();
                    if (code == Common.CANCEL) {
                        doCancel(getUserInfo().getUserid(), ackMessage.message.getId(0));
                    } else if (code == Common.ERROR_NO_FRIEND) {
                        doSendError(code, ackMessage.message.getId(0));
                    } else if (code == Common.ERROR_NO_EXSIT) {
                        doSendError(code, ackMessage.message.getId(0));
                    } else if (code == Common.ERROR_NO_MEMBER) {
                        doSendError(code, ackMessage.message.getId(0));
                    }
                }
            } else if (message instanceof FGPushMessage) {
                MessageBean msg = createMessageBean(message, false);
                if (!isDoingMessage(msg.getMsgType())) {
                    ProviderChat.insertAndUpdateMessage(ContextHelper.getContext(), msg);
                    saveRecent(msg, "", msg.getNick(),
                        ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                        ESureType.NO.ordinal(), !isCurrentChat(msg.getTo()));

                    notifyMotification(true, msg);
                }
                notifyDataChange(msg);
            } else if (message instanceof ReadAckMessage) {
                ReadAckMessage ackMessage = (ReadAckMessage) message;
                ReadedMessageList messageList = ackMessage.message;
                if (messageList != null) {
                    List<ReadedMessage> readedList = messageList.getReadedListList();
                    if (readedList != null) {
                        int len = readedList.size();
                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                ReadedMessage readedMessage = readedList.get(i);
                                if (readedMessage.getSynchro() == 1) {//同步消息或者回执
                                    System.out.println("已读消息回执--" + readedMessage.getOid());
                                    ProviderChat.updateServerReaded(readedMessage.getOid());
                                    notifyDataChange(EActivityNum.CHAT, null);
                                } else if (readedMessage.getSynchro() == 0) {//对方已读消息
                                    System.out.println(
                                        "对方已读消息--" + readedMessage.getOid() + "--mucId="
                                            + readedMessage.getMucId() + "--to=" + readedMessage
                                            .getTo() + "--from=" + readedMessage.getFrom());
                                    String from = readedMessage
                                        .getTo();//readedMessage.getTo() 源消息接受者，即已读消息的发送者
                                    String mucId = readedMessage.getMucId();
                                    boolean isGroup = false;
                                    if (!TextUtils.isEmpty(mucId)) {
                                        isGroup = true;
                                    }
                                    String messageId = readedMessage.getOid();
                                    String readedUserIds = ProviderChat.getReadedUserIds(messageId);
                                    List<String> userIds = null;
                                    if (!TextUtils.isEmpty(readedUserIds)) {
                                        userIds = StringUtils.getUserIds(readedUserIds);
                                    } else {
                                        userIds = new ArrayList<>();
                                        userIds.add(from);
                                    }

                                    ProviderChat.updateServerReaded(messageId);

                                    readedUserIds = StringUtils.getStringByList(userIds);
                                    if (!TextUtils.isEmpty(readedUserIds)) {
                                        ProviderChat.updateReadedUserIds(messageId, readedUserIds);
                                    }
                                    if (isGroup) {
                                        if (mucId.equals(currentChatId)) {
                                            notifyDataChange(EActivityNum.CHAT, null);
                                        }
                                    } else {
                                        if (from.equals(currentChatId)) {
                                            notifyDataChange(EActivityNum.CHAT, null);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public MessageBean createMessageBean(Object message, boolean isFromMySelf) {
        MessageBean bean = null;
        try {
            if (message instanceof PrivateMessage) {
                PrivateMessage privateMessage = (PrivateMessage) message;
                PrivateMessage.Builder builder = privateMessage.toBuilder();
                bean = new MessageBean();
                bean.setMessageType(ChatHelper.getMessageType(builder.getType()));
                bean.setMsgId(builder.getId());
                bean.setContent(builder.getContent());
                bean.setAvatarUrl(builder.getAvatar());
                bean.setCancel(builder.getCancel());
                bean.setCode(builder.getCode());
                bean.setTime(builder.getTime());
                if (isFromMySelf) {
                    bean.setFrom(builder.getFrom());
                    bean.setTo(builder.getTo());
                } else {
                    bean.setFrom(builder.getTo());
                    bean.setTo(builder.getFrom());
                }
                bean.setServerReaded(1);//服务器未读
                BodyEntity entity = bean.getBodyEntity();
                if (entity != null) {
                    bean.setSecret(entity.isSecret());
                    if (!isFromMySelf) {
                        ProviderUser.updateSenderAvatar(builder.getFrom(), builder.getAvatar(),
                            entity.getMucNickName());
                        if (getCacheUserBean(builder.getFrom()) != null) {
                            getCacheUserBean(builder.getFrom()).setAvatarUrl(builder.getAvatar());
                            getCacheUserBean(builder.getFrom())
                                .setUserNick(entity.getMucNickName());
                        }
                    }
                } else {
                    ProviderUser.updateSenderAvatar(builder.getFrom(), builder.getAvatar(),
                        "");
                    if (getCacheUserBean(builder.getFrom()) != null) {
                        getCacheUserBean(builder.getFrom()).setAvatarUrl(builder.getAvatar());
                    }
                }
                bean.setSendType(ESendType.MSG_SUCCESS);
                if (checkMeToMe(builder.getFrom(), builder.getTo())) {
                    bean.setIncoming(true);
                } else {
                    bean.setIncoming(!isFromMySelf);
                }
                bean.setGroupChat(false);
                bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
                bean.setHasReaded(0);
            } else if (message instanceof RoomMessage) {
                RoomMessage roomMessage = (RoomMessage) message;
                RoomMessage.Builder builder = roomMessage.toBuilder();
                bean = new MessageBean();
                bean.setMessageType(ChatHelper.getMessageType(builder.getType()));
                bean.setMsgId(builder.getId());
                bean.setContent(builder.getContent());
                bean.setCancel(builder.getCancel());
                bean.setCode(builder.getCode());
                bean.setTime(builder.getTime());
                bean.setFrom(builder.getUsername());
                bean.setTo(builder.getMucid());
                BodyEntity entity = bean.getBodyEntity();
                if (entity != null) {
                    bean.setSecret(entity.isSecret());
                    bean.setAvatarUrl(entity.getSenderAvatar());
                    bean.setNick(StringUtils
                        .getUserNick(entity.getMucNickName(), builder.getUsername()));
                    if (!isFromMySelf) {
                        //更新群备注名和群头像
                        MucUser.updateMemberNickAndAvatar(builder.getMucid(), builder.getUsername(),
                            entity.getMucNickName(), entity.getSenderAvatar());
                    }
                } else {
                    bean.setAvatarUrl("");
                    bean.setNick(builder.getUsername());
                }
                bean.setSendType(ESendType.MSG_SUCCESS);
                bean.setIncoming(!isFromMySelf);
                bean.setGroupChat(true);
                bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
                bean.setHasReaded(0);
                bean.setServerReaded(1);//服务器未读
            } else if (message instanceof FGPushMessage) {
                FGPushMessage pushMessage = (FGPushMessage) message;
                PushMessage pMsg = pushMessage.message;
                bean = new MessageBean();
                bean.setMsgId(pMsg.getMessageId());
                bean.setContent(pMsg.getContent());
                bean.setCancel(0);
                bean.setCode(0);
                bean.setTime(pMsg.getTime());
                bean.setHasReaded(0);
                bean.setServerReaded(1);//服务器未读
                String content = pMsg.getContent();
                try {
                    JSONObject object = new JSONObject(content);
                    if (object != null) {
                        String type = object.optString("type");
                        if (!TextUtils.isEmpty(type)) {
                            String userName = object.optString("username");
                            if (!TextUtils.isEmpty(userName)) {
                                bean.setTo(userName);
                            }
                            if (type.equalsIgnoreCase("OA")) {
                                PushEntity entity = GsonHelper
                                    .getObject(pMsg.getContent(), PushEntity.class);
                                if (TextUtils.isEmpty(bean.getTo())) {
                                    bean.setTo(ChatHelper.MYTIP);
                                }
                                bean.setNick(entity.getFrom());
                                bean.setMessageType(EMessageType.OA);
                            } else {
                                String from = object.optString("from");
                                if (!TextUtils.isEmpty(from)) {
                                    bean.setNick(from);
                                    if (TextUtils.isEmpty(bean.getTo())) {
                                        bean.setTo(ChatHelper.MYTIP_SYS);
                                    }
                                }
                                bean.setMessageType(EMessageType.SYSTEM);
                            }
                        }
                    }
                } catch (JSONException e) {
                    bean.setTo(ChatHelper.MYTIP_SYS);
                    bean.setNick("系统");
                    bean.setMessageType(EMessageType.SYSTEM);
                }

                bean.setFrom(getUserInfo().getUserid());
                bean.setSecret(false);
                bean.setSendType(ESendType.MSG_SUCCESS);
                bean.setIncoming(true);
                bean.setGroupChat(false);
                bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
                bean.setAvatarUrl(bean.getBodyEntity().getSenderAvatar());
            } else if (message instanceof PushMessage) {
                PushMessage pMsg = (PushMessage) message;
                bean = new MessageBean();
                bean.setMsgId(pMsg.getMessageId());
                bean.setContent(pMsg.getContent());
                bean.setCancel(0);
                bean.setCode(0);
                bean.setTime(pMsg.getTime());
                bean.setHasReaded(0);
                bean.setServerReaded(0);//服务器已读
                String content = pMsg.getContent();
                try {
                    JSONObject object = new JSONObject(content);
                    if (object != null) {
                        String type = object.optString("type");
                        if (!TextUtils.isEmpty(type)) {
                            String userName = object.optString("username");
                            if (!TextUtils.isEmpty(userName)) {
                                bean.setTo(userName);
                            }
                            if (type.equalsIgnoreCase("OA")) {
                                PushEntity entity = GsonHelper
                                    .getObject(pMsg.getContent(), PushEntity.class);
                                if (TextUtils.isEmpty(bean.getTo())) {
                                    bean.setTo(ChatHelper.MYTIP);
                                }
                                bean.setNick(entity.getFrom());
                                bean.setMessageType(EMessageType.OA);
                            } else {
                                String from = object.optString("from");
                                if (!TextUtils.isEmpty(from)) {
                                    if (TextUtils.isEmpty(bean.getTo())) {
                                        bean.setTo(ChatHelper.MYTIP_SYS);
                                    }
                                    bean.setNick(from);
                                }
                                bean.setMessageType(EMessageType.SYSTEM);
                            }
                        }
                    }
                } catch (JSONException e) {
                    bean.setTo(ChatHelper.MYTIP_SYS);
                    bean.setNick("系统");
                    bean.setMessageType(EMessageType.SYSTEM);
                }

                bean.setFrom(getUserInfo().getUserid());
                bean.setSecret(false);
                bean.setSendType(ESendType.MSG_SUCCESS);
                bean.setIncoming(true);
                bean.setGroupChat(false);
                bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
                bean.setAvatarUrl(bean.getBodyEntity().getSenderAvatar());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /*
     * s是否是正在输入或者录音的消息
     * */

    public boolean isDoingMessage(EMessageType type) {
        if (type == EMessageType.INPUTING || type == EMessageType.RECORDING) {
            return true;
        } else {
            return false;
        }
    }

    /*
    * 能否通知
    * */
    public boolean canNotify(IChatRoomModel model, boolean isAt) {
        int disturb = ProviderChat.getNoDisturb(ContextHelper.getContext(), model.getTo());
        if (!isCurrentChat(model.getTo())) {
            if (disturb == ESureType.NO.ordinal()) {
                return true;
            } else {
                if (isAt) {
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public UserBean getCacheUserBean(String userId) {
        if (userMap != null && userMap.get(userId) != null) {
            return userMap.get(userId);
        } else {
            UserBean bean = (UserBean) ProviderUser
                .selectRosterSingle(ContextHelper.getContext(), userId);
            if (bean != null) {
                userMap.put(userId, bean);
            }
            return bean;
        }
    }

    public void updateCacheUserBean(UserBean bean) {
        if (userMap != null) {
            if (userMap.containsKey(bean.getUserId())) {
                userMap.remove(bean.getUserId());
            }
            userMap.put(bean.getUserId(), bean);
        }
    }

    private void saveUserMap(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        UserBean user;
        if (userMap == null) {
            userMap = new HashMap<>();
            user = (UserBean) ProviderUser.selectRosterSingle(ContextHelper.getContext(), userId);
            if (user != null) {
                userMap.put(userId, user);
            }
        } else {
            user = userMap.get(userId);
            if (user == null) {
                user = (UserBean) ProviderUser
                    .selectRosterSingle(ContextHelper.getContext(), userId);
                if (user != null) {
                    userMap.put(userId, user);
                }
            }
        }
    }

    public boolean checkFromSelf(String from) {
        if (!TextUtils.isEmpty(from) && from.equalsIgnoreCase(UserInfoRepository.getUserId())) {
            return true;
        }
        return false;
    }

    public boolean checkMeToMe(String from, String to) {
        if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to)) {
            if (from.equalsIgnoreCase(to) && from
                .equalsIgnoreCase(UserInfoRepository.getUserId())) {
                return true;
            }
        }
        return false;

    }


    /*
    * 通知通知栏
    * */
    private void notifyMotification(boolean isNotify, IChatRoomModel model) {
        if (isNotify && SettingsManager.eventsShowNotify()) {
            NotifyManager.getInstance().onMessageNotification(model);
        }
    }

    //通知消息更新(单条)
    public void notifyDataChange(IChatRoomModel msg) {
        if (!TextUtils.isEmpty(currentChatId) && !isCurrentChat(msg.getTo())) {
            return;
        }
        if (chatMessageEvent == null) {
            chatMessageEvent = (ChatMessageEvent) EventFactory.INSTANCE
                .create(EventEnum.CHAT_MESSAGE, msg);
        } else {
            chatMessageEvent.setPacket(msg);
        }
        if (chatMessageEvent != null) {
            chatMessageEvent.setType(ChatMessageEvent.RECEIVE);
            EventBus.getDefault().post(chatMessageEvent);
        }
    }


    public void notifyDataChange(EActivityNum activity, EFragmentNum fragment) {
        if (activity != null) {
            RefreshEntity entity = new RefreshEntity();
            entity.setActivity(activity.value);
            if (fragment != null) {
                entity.setFragment(fragment.value);
            }

            RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
                .create(EventEnum.MAIN_REFRESH, entity);
            EventBus.getDefault().post(event);
        }
    }

    public void notifyDataChange(EActivityNum activity, EFragmentNum fragment, int type) {
        if (activity != null) {
            RefreshEntity entity = new RefreshEntity();
            entity.setActivity(activity.value);
            if (fragment != null) {
                entity.setFragment(fragment.value);
            }

            entity.setType(type);

            RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
                .create(EventEnum.MAIN_REFRESH, entity);
            EventBus.getDefault().post(event);
        }
    }

    //保存私聊recent
    public void saveRecent(IChatRoomModel model, String avatar, String userNick, int noDisturb,
                           int backId, int topFlag, boolean isNew) {
        RecentMessage message = createRecentMessage(model, avatar, userNick, noDisturb, backId,
            topFlag, isNew);
        ProviderChat.updateRecentMessageAsyn(ContextHelper.getContext(), message);
    }


    public boolean saveRecentOfflineAsyn(IChatRoomModel model, String avatar, String userNick,
                                         int noDisturb, int backId, int topFlag, boolean isNew, int unreadCount) {
        RecentMessage message = createRecentMessage(model, avatar, userNick, noDisturb, backId,
            topFlag, isNew);
        return ProviderChat
            .updateRecentMessageAsyn(ContextHelper.getContext(), message, unreadCount);
    }

    /*
    * @param avatar 对方头像
    * @param nick 对方昵称
    * */
    public void saveMessage(IChatRoomModel message, String avatar, String nick, int disturb,
                            int backId, int topFlag) {
        boolean flag = ProviderChat.updateMessage(ContextHelper.getContext(), message);
        if (!flag) {
            ProviderChat.insertAndUpdateMessage(ContextHelper.getContext(), message);
            saveRecent(message, avatar, nick, disturb, backId, topFlag, false);
        }
    }


    /*
    * @param avatarUrl 对方头像
    * @param userNick 对方昵称
    * */
    public void saveMessage(IChatRoomModel message, String avatarUrl, String userNick, int backId,
                            boolean isNew) {
        boolean flag = ProviderChat.updateMessage(ContextHelper.getContext(), message);
        if (!flag) {
            ProviderChat.insertAndUpdateMessage(ContextHelper.getContext(), message);
            RecentMessage recent = ProviderChat
                .selectSingeRecent(ContextHelper.getContext(), message.getTo());
            if (recent != null) {
                saveRecent(message, avatarUrl, userNick,
                    recent.getNotDisturb(), backId, recent.getTopFlag(), isNew);
            } else {
                saveRecent(message, avatarUrl, userNick,
                    //群聊从群配置中拿免打扰 单聊直接默认值
                    (message.isGroupChat() ? MucInfo
                        .getMucNoDisturb(ContextHelper.getContext(), message.getTo())
                        : ESureType.NO.ordinal())
                    , backId, ESureType.NO.ordinal(), isNew);
            }
        }
    }

    /*
    * @param avatar对方头像
    * @param nick对方昵称
    * */
    public RecentMessage createRecentMessage(IChatRoomModel msg, String avatar, String userNick,
                                             int noDisturb, int backId, int topFlag, boolean isNew) {
        RecentMessage message = new RecentMessage();
        message.setNotDisturb(noDisturb);
        message.setTime(msg.getTime());
        message.setBackgroundId(backId);
        message.setTopFlag(topFlag);
        message.setChatType(
            msg.isGroupChat() ? EChatType.GROUP.ordinal() : EChatType.PRIVATE.ordinal());
        if (msg.isGroupChat()) {
            message.setUserId(msg.getFrom());
            message.setChatId(msg.getTo());
            if (!TextUtils.isEmpty(msg.getGroupName())) {
                message.setGroupName(msg.getGroupName());
            } else {
                String mucName = MucInfo.getMucName(ContextHelper.getContext(), msg.getTo());
                message.setGroupName(mucName);
            }
            message.setNick(
                StringUtils.getUserNick(msg.getNick(), msg.getFrom()));
        } else {
            message.setUserId(msg.getTo());
            message.setChatId(msg.getTo());
            message.setAvatarUrl(avatar);
            message.setNick(userNick);
        }
        if (isAtMessage(msg)) {
            message.setAt(true);
        } else {
            message.setAt(false);
        }
        message.setNew(isNew);
        return message;
    }

    /*
    * 名片消息
    * */
    public IChatRoomModel createCardMessage(String user, String nick,
                                            UserBean userBean, int chatType) {
        MessageBean bean = null;
        if (userBean == null) {
            return null;
        }
        if (!TextUtils.isEmpty(user)) {
            bean = new MessageBean();
            CardBody entity = new CardBody();
            entity.setFriendId(userBean.getUserId());
            entity.setFriendHeader(userBean.getAvatarUrl());
            entity.setFriendName(userBean.getUserNick());
            entity.setEnable(userBean.getQuit());
            entity.setValid(userBean.getValid());
            if (!ChatHelper.isGroupChat(chatType)) {
                entity.setSecret(0);
                bean.setGroupChat(false);
                entity.setMucNickName(getUserInfo().getUsernick());
            } else {
                entity.setSecret(0);
                entity.setSenderAvatar(getUserInfo().getImage());
                String mucUserNick = MucUser
                    .getMucUserNick(ContextHelper.getContext(), user, getUserInfo().getUserid());
                entity.setMucNickName(ChatHelper
                    .getUserRemarkName(mucUserNick, getUserInfo().getUsernick(),
                        getUserInfo().getUserid()));
                entity.setGroupName(MucInfo.getMucName(ContextHelper.getContext(), user));
                bean.setGroupName(nick);
                bean.setGroupChat(true);
            }
            bean.setMsgId(UUID.randomUUID().toString());
            bean.setTo(user);
            bean.setFrom(getUserInfo().getUserid());
            bean.setContent(entity.toJson());
            bean.setMessageType(EMessageType.CONTACT);
            bean.setTime(System.currentTimeMillis());
            bean.setNick(getUserInfo().getUsernick());
            bean.setAvatarUrl(getUserInfo().getImage());
            bean.setSendType(ESendType.SENDING);
            bean.setIncoming(false);

        }
        return bean;
    }


    /*
    * @param account 发送者userId
    * @param user 接收者者userId
    * @param nick 发送者昵称
    * @param avatar 发送者头像
    * */
    public IChatRoomModel createTransforMessage(String account, String user, String content,
                                                String nick, String avatar, boolean isGroupChat, boolean isSecret, boolean isInconming,
                                                EMessageType type) {
        MessageBean bean = new MessageBean();
        bean.setMsgId(UUID.randomUUID().toString());
        bean.setTo(user);
        bean.setFrom(account);
        if (type == EMessageType.TEXT) {
            BodyEntity bodyEntity = new BodyEntity();
            bodyEntity.setBody(content);
            bodyEntity.setSecret(isSecret ? 1 : 0);
            if (isGroupChat) {
                bodyEntity.setMucNickName(nick);
                bodyEntity.setSenderAvatar(avatar);
                String mucUserNick = MucUser
                    .getMucUserNick(ContextHelper.getContext(), user, account);
                bodyEntity.setGroupName(ChatHelper.getUserRemarkName(mucUserNick, nick, account));
            } else {
                bodyEntity.setMucNickName(nick);
            }
            bean.setContent(BodyEntity.toJson(bodyEntity));
        } else {
            if (isGroupChat) {//群聊更换发送者头像，昵称
                try {
                    JSONObject object = new JSONObject(content);
                    if (object != null) {
                        if (object.has("mucNickName")) {
                            object.remove("mucNickName");
                            object.put("mucNickName", nick);
                        }
                        if (object.has("senderAvatar")) {
                            object.remove("senderAvatar");
                            object.put("senderAvatar", avatar);
                        }
                        bean.setContent(object.toString());
                    } else {
                        bean.setContent(content);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    bean.setContent(content);
                }
            } else {
                bean.setContent(content);
            }
        }
        bean.setMessageType(type);
        bean.setTime(System.currentTimeMillis());
        bean.setAvatarUrl(avatar);
        bean.setNick(nick);
        bean.setSendType(ESendType.SENDING);
        bean.setIncoming(isInconming);
        bean.setGroupChat(isGroupChat);
        bean.setSecret(isSecret);
        bean.setHasReaded(1);
        bean.setServerReaded(0);//未读
        return bean;
    }


    /*
    * 创建action消息
    * @param creator action发起者userId
    * @param nick action发起者nick
    * @param chatId 当前群聊id
    * @param groupName 当前群聊名称
    * @param type 当前action类型
    * @param actionText 内容
    *
    * */
    public MessageBean createActionMessage(String creator, String nick, String chatId,
                                           String groupName, EActionType type, String actionText, long time) {
        MessageBean model = null;
        if (!TextUtils.isEmpty(creator) && !TextUtils.isEmpty(chatId) && type != null) {
            model = new MessageBean();
            model.setMsgId(UUID.randomUUID().toString());
            model.setTo(chatId);
            model.setContent(actionText);
            model.setFrom(creator);
            model.setTime(time);
            model.setActionType(type);
            model.setGroupName(groupName);
            model.setSendType(ESendType.MSG_SUCCESS);
            model.setNick(nick);
            model.setMessageType(EMessageType.ACTION);
            model.setGroupChat(true);
        }
        return model;
    }

    /*
    * 创建notice消息
    * @param creator 发送者userId
    * @param nick 发送者nick
    * @param chatId 当前群聊id
    * @param groupName 当前群聊名称
    * @param type 当前action类型
    * @param actionText 内容
    *
    * */
    public MessageBean createErrorMessage(String creator, String nick,
                                          String chatId, String groupName, String noticeText, long time) {
        MessageBean model = null;
        if (!TextUtils.isEmpty(creator) && !TextUtils.isEmpty(chatId)) {
            model = new MessageBean();
            model.setMsgId(UUID.randomUUID().toString());
            model.setTo(chatId);
            model.setContent(noticeText);
            model.setFrom(creator);
            model.setTime(time);
            model.setGroupName(groupName);
            model.setSendType(ESendType.ACK_ERROR);
            model.setNick(nick);
            model.setMessageType(EMessageType.ACTION);
            model.setActionType(EActionType.NONE);
            model.setIncoming(true);
        }
        return model;
    }

    /*
    * 获取所有未读消息总数
    * */
    public int getTotalUnreadMessageCount() {
        return ProviderChat.selectTotalUnreadMessageCount();
    }


    public void getAllImageMessage(Context context, String user, List<String> urls,
                                   List<String> ids) {
        ProviderChat.selectAllImageMessage(context, user, urls, ids);
    }

    public List<IChatRoomModel> getMessagesByIds(Context context, ArrayList<String> msgIds) {
        return ProviderChat.getMessagesByIds(context, msgIds);
    }

    //私聊body
    public BodyEntity createBody(String content, boolean secret, EMessageType type,
                                 String sendNick) {
        BodyEntity entity = new BodyEntity();

        switch (type) {
            case VIDEO:
                VideoUploadEntity video = VideoUploadEntity.fromJson(content);
                if (video != null) {
                    String body = video.remove("timeLength", content);
                    entity.setBody(body);
                    entity.setTimeLength(video.getTimeLength());
                }
                break;
            case VOICE:
                VoiceUploadEntity voice = VoiceUploadEntity.fromJson(content);
                if (voice != null) {
                    entity.setBody(voice.getVoiceUrl());
                    entity.setTimeLength(voice.getTimeLength());
                }
                break;
            case TEXT:
                entity.setBody(content);
                entity.setTimeLength(0);
                break;
            default:
                entity.setBody(content);
                entity.setTimeLength(0);
                break;
        }
        entity.setSecret(secret ? 1 : 0);
        entity.setMucNickName(sendNick);
        entity.setBubbleHeight(0);
        entity.setBubbleWidth(0);
        return entity;
    }

    //群聊body
    public BodyEntity createBody(String content, boolean secret, EMessageType type, String avatar,
                                 String nick, String mucName) {
        BodyEntity entity = new BodyEntity();

        switch (type) {
            case VIDEO:
                VideoUploadEntity video = VideoUploadEntity.fromJson(content);
                if (video != null) {
                    String body = video.remove("timeLength", content);
                    entity.setBody(body);
                    entity.setTimeLength(video.getTimeLength());
                }
                break;
            case VOICE:
                VoiceUploadEntity voice = VoiceUploadEntity.fromJson(content);
                if (voice != null) {
                    entity.setBody(voice.getVoiceUrl());
                    entity.setTimeLength(voice.getTimeLength());
                }
                break;
            case TEXT:
                entity.setBody(content);
                entity.setTimeLength(0);
                break;
            default:
                entity.setBody(content);
                entity.setTimeLength(0);
                break;
        }
        entity.setSecret(secret ? 1 : 0);
        entity.setBubbleHeight(0);
        entity.setBubbleWidth(0);
        entity.setSenderAvatar(avatar);
        entity.setMucNickName(nick);
        entity.setGroupName(mucName);
        return entity;
    }


    //密聊消息阅读后立即销毁内容
    public void destryMessageContent(Context context, String msgId, String destroyEntity) {
        if (context == null || TextUtils.isEmpty(msgId) || TextUtils.isEmpty(destroyEntity)) {
            return;
        }
        ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
        dao.updateMessageContent(msgId, destroyEntity);
    }

    public AllResult searchMessageRecord(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        ChatMessageDao dao = new ChatMessageDao(context, DaoManager.getUserID());
        return dao.searchAllMessageByContent(text);
    }

    /**
     * 获取第一条通知
     */
    public boolean getFirstNotification() {
        boolean result = firstNotification;
        firstNotification = false;
        return result;
    }

    /*
    * activityChat销毁的时候，将发送队列中未发送成功的清除掉
    * */
    public void clearSequenceMap() {
        if (sequenceMap != null && sequenceMap.size() > 0) {
            Set<Entry<String, IChatRoomModel>> entrySet = sequenceMap.entrySet();
            Iterator<Entry<String, IChatRoomModel>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IChatRoomModel> m = iterator.next();
                IChatRoomModel message = m.getValue();
//                if (message.getMsgType() != EMessageType.TEXT) {//非文本消息置为发送失败
                ProviderChat.updateSendStatus(ContextHelper.getContext(), message.getMsgId(),
                    ESendType.NET_ERROR);
//                }
            }
            if (sequenceMap != null) {
                sequenceMap.clear();
            }
        }
    }


    public void setMessageChange(boolean val) {
        isMessageChange = val;
    }

    public boolean isMessageChange() {
        return isMessageChange;
    }

    public String getMessageBodyJson(IChatRoomModel model) {
        String content;
        if (!model.isIncoming()) {
            if (model.getMsgType() == EMessageType.IMAGE) {
                BodyEntity entity = model.getBodyEntity();
                String uploadUrl = model.getUploadUrl();
                if (!TextUtils.isEmpty(uploadUrl)) {
                    entity.setBody(uploadUrl);
                }
                content = BodyEntity.toJson(entity);
            } else if (model.getMsgType() == EMessageType.VOICE) {
                BodyEntity entity = model.getBodyEntity();
                String uploadUrl = model.getUploadUrl();
                VoiceUploadEntity voice = VoiceUploadEntity.fromJson(uploadUrl);
                if (voice != null && !TextUtils.isEmpty(voice.getVoiceUrl())) {
                    entity.setBody(voice.getVoiceUrl());
                }
                content = BodyEntity.toJson(entity);
            } else if (model.getMsgType() == EMessageType.FACE) {
                BodyEntity entity = model.getBodyEntity();
                String uploadUrl = model.getUploadUrl();
                if (!TextUtils.isEmpty(uploadUrl)) {
                    entity.setBody(uploadUrl);
                }
                content = BodyEntity.toJson(entity);
            } else if (model.getMsgType() == EMessageType.VIDEO) {
                BodyEntity entity = model.getBodyEntity();
                String uploadUrl = model.getUploadUrl();
                if (!TextUtils.isEmpty(uploadUrl)) {
                    entity.setBody(uploadUrl);
                }
                content = BodyEntity.toJson(entity);
            } else {
                content = model.getBody();
            }
        } else {
            content = model.getBody();
        }
        return content;
    }

    public ExcuteMessage createExcuteBody(ExcuteType type, String param) {
        switch (type) {
            case QUER_USER_PHONE:
                if (!TextUtils.isEmpty(param)) {
                    ExcuteMessage.Builder builder = ExcuteMessage.newBuilder();
                    builder.setExcuteType(ExcuteType.QUER_USER_PHONE);
                    builder.setParam(param);
                    return builder.build();
                }
                break;
            case QUERY_OFFLINE:
                ExcuteMessage.Builder builder = ExcuteMessage.newBuilder();
                builder.setExcuteType(ExcuteType.QUERY_OFFLINE);
                if (param != null && !param.equals("")) {
                    builder.setParam(param);
                }
                return builder.build();
            case CHECK_VALIDATECODE:
                //{"user":"用户名","code":"短信验证码"}
                if (!TextUtils.isEmpty(param)) {
                    ExcuteMessage.Builder checkCodeBuild = ExcuteMessage.newBuilder();
                    checkCodeBuild.setExcuteType(ExcuteType.CHECK_VALIDATECODE);
                    checkCodeBuild.setParam(param);
                    return checkCodeBuild.build();
                }
                break;
            case EMOTICON_SAVE:
            case EMOTICON_QUERY:
            case EMOTICON_DELETE:
            case EMOTICON_TOFIRST:
                if (!TextUtils.isEmpty(param)) {
                    ExcuteMessage.Builder checkCodeBuild = ExcuteMessage.newBuilder();
                    checkCodeBuild.setExcuteType(type);
                    checkCodeBuild.setParam(param);
                    return checkCodeBuild.build();
                }
                break;
            default:
                return null;
        }
        return null;
    }

    //退出登录后，清除缓存数据
    public void clearLoginData() {
        DaoManager.clearUserId();
        PasswordRespository.cleanPassword();
        SSOTokenRepository.getInstance().clearSSOToken();
        AppConfig.INSTANCE.remove(AppConfig.ACCOUT);
        AppConfig.INSTANCE.remove(AppConfig.PASSWORD);
        AppConfig.INSTANCE.remove(AppConfig.PHONE);
    }

    public void registerAckListener() {
        ClientConfig.I.registerListener(AckListener.class, this);
    }

    public void removeAckListener() {
        ClientConfig.I.removeListener(AckListener.class, this);
    }

    @Override
    public void onAck(AckMessage message) {
        if (message != null) {
            //成功逻辑
            String msgId = message.ack.getIdList().get(0);
            long time = message.ack.getTime();
            doSendSuccess(msgId, time);
        }
    }

    public boolean isAtMessage(IChatRoomModel model) {
        if (model.isGroupChat() && model.isIncoming() && model.getMsgType() == EMessageType.TEXT) {
            BodyEntity entity = model.getBodyEntity();
            if (entity != null && !TextUtils.isEmpty(entity.getBody())) {
                String mucUserNick = MucUser
                    .getMucUserNick(ContextHelper.getContext(), model.getTo(),//群备注名
                        getUserInfo().getUserid());
                if (entity.getBody().contains("@" + mucUserNick) || entity.getBody()
                    .contains("@" + getUserInfo().getUsernick()) || entity.getBody()
                    .contains("@" + getUserInfo().getUserid())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, IChatRoomModel> getSequenceReadedChatMap() {
        return sequenceReadedChatMap;
    }

    public Map<String, ReadedMessageList> getSequenceReadedMessageMap() {
        return sequenceReadedMap;
    }

    public ReadedMessage createReadedMessage(IChatRoomModel model) {
        ReadedMessage.Builder builder = ReadedMessage.newBuilder();
        builder.setOid(model.getMsgId());
        if (model.isGroupChat()) {
            builder.setFrom(model.getFrom());
            builder.setMucId(model.getTo());
        } else {
            builder.setFrom(model.getTo());
        }
        return builder.build();

    }
}



