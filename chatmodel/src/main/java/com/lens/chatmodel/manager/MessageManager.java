package com.lens.chatmodel.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.message.FGPushMessage;
import com.fingerchat.api.message.MessageAckMessage;
import com.fingerchat.api.message.MucChatMessage;
import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.api.message.PrivateChatMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
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
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.bean.body.PushEntity;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.bean.transfor.VoiceBody;
import com.lens.chatmodel.db.ChatMessageDao;
import com.lens.chatmodel.db.MucInfo;
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
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.db.DaoManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class MessageManager {

    private ChatMessageEvent chatMessageEvent;

    private Map<String, IChatRoomModel> sequenceMap = new HashMap<>();
    private Map<String, UserBean> userMap = new HashMap<>();

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
            BodyEntity body = createBody(message.getMsgId(), false, EMessageType.NOTICE);
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
                getUserInfo().getImage(), getUserInfo().getUsernick());
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
                message.setHint(getCancelText(model));
                message.setMsgType(EMessageType.NOTICE);
                message.setTime(System.currentTimeMillis());
                ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
            }
        } else {//自己cancel的回执
            IChatRoomModel model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), msgId);
            ProviderChat.updateCancelMessage(ContextHelper.getContext(), msgId);
            RecentMessage message = ProviderChat
                .selectSingeRecent(ContextHelper.getContext(), model.getTo());
            if (message != null) {
                message.setHint(ContextHelper.getString(R.string.cancel_message_you));
                message.setMsgType(EMessageType.NOTICE);
                message.setTime(System.currentTimeMillis());
                ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
            }

        }
        notifyUpdateUI(msgId, ChatMessageEvent.CANCEL);
    }

    //发送消息错误，非好友。非群成员，房间不存在
    private void doSendError(long code, String msgId) {
        ProviderChat.updateSendStatus(ContextHelper.getContext(), msgId, ESendType.ERROR);
        IChatRoomModel model = createErrorMessage(getUserInfo().getUserid(),
            getUserInfo().getUsernick(), getCurrentChatId(), "",
            getErrorText(code, getCurrentChatId()),
            System.currentTimeMillis());
        if (code == Common.ERROR_NO_FRIEND) {
            ((MessageBean) model).setGroupChat(false);
        } else {
            ((MessageBean) model).setGroupChat(true);
        }
        ProviderChat.insertPrivateMessage(ContextHelper.getContext(), model);
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


    private void doSendSuccess(String msgId) {
        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            System.out.println(
                "成功消息id:" + msgId + "--" + sequenceMap.get(msgId)
                    .getContent());
            ProviderChat.updateSendStatus(ContextHelper.getContext(),
                sequenceMap.get(msgId).getMsgId(), ESendType.MSG_SUCCESS);
            sequenceMap.remove(msgId);
//            notifyUpdateUI(msgId, false);
        }
    }

    private boolean isMapValid(Map<String, IChatRoomModel> map) {
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
                    } else if (type == EMessageType.NOTICE) {
                        if (privateChatMessage.message.getCancel() == 1) {//是cancel消息
                            doCancel(privateChatMessage.message.getFrom(),
                                privateChatMessage.message.getId());
                        }
                    } else {
                        MessageBean msg = createMessageBean(privateMessage);
                        if (msg != null) {
                            UserBean bean = getCacheUserBean(msg.getTo());
                            if (bean == null) {
                                return;
                            }
                            if (!isDoingMessage(msg.getMsgType())) {
                                msg.setNick(ChatHelper
                                    .getUserRemarkName(bean.getRemarkName(), bean.getUserNick(),
                                        bean.getUserId()));//初始化昵称
                                ProviderChat.insertPrivateMessage(ContextHelper.getContext(), msg);
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
                                msg.setNick(ChatHelper
                                    .getUserRemarkName(bean.getRemarkName(), bean.getUserNick(),
                                        bean.getUserId()));
                                notifyMotification(canNotify(msg.getTo()), msg);
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
                    } else if (type == EMessageType.NOTICE) {
                        if (roomMessage.getCancel() == 1) {//是cancel消息
                            doCancel(roomMessage.getMucid(), roomMessage.getId());
                        }
                    } else {
                        MessageBean group = createMessageBean(roomMessage);
                        if (group != null) {
                            String mucId = mucChatMessage.message.getMucid();
                            int disturb = MucInfo
                                .getMucNoDisturb(ContextHelper.getContext(), mucId);
                            int backId = MucInfo.getMucChatBg(ContextHelper.getContext(), mucId);
                            int topFlag = ProviderChat
                                .getTopFlag(ContextHelper.getContext(), mucId);

                            if (!isDoingMessage(group.getMsgType())) {
                                ProviderChat
                                    .insertPrivateMessage(ContextHelper.getContext(), group);
                                saveRecent(group, group.getAvatarUrl(), StringUtils
                                        .getUserNick(group.getNick(), group.getFrom()),
                                    disturb, backId, topFlag, !isCurrentChat(group.getTo()));

                                String mucName = MucInfo.getMucName(ContextHelper.getContext(),
                                    mucChatMessage.message.getMucid());
                                group.setGroupName(mucName);
                                notifyMotification(canNotify(group.getTo()), group);
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
                MessageBean msg = createMessageBean(message);
                if (!isDoingMessage(msg.getMsgType())) {
                    ProviderChat.insertPrivateMessage(ContextHelper.getContext(), msg);
                    saveRecent(msg, "", msg.getNick(),
                        ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                        ESureType.NO.ordinal(), !isCurrentChat(msg.getTo()));

                    notifyMotification(true, msg);
                }
                notifyDataChange(msg);
            }
        }
    }

    public MessageBean createMessageBean(Object message) {
        MessageBean bean = null;
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
            bean.setFrom(builder.getTo());
            bean.setTo(builder.getFrom());
            BodyEntity entity = bean.getBodyEntity();
            if (entity != null) {
                bean.setSecret(entity.isSecret());
            }
            bean.setSendType(ESendType.MSG_SUCCESS);
            bean.setIncoming(true);
            bean.setGroupChat(false);
            bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
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
            }
            bean.setSendType(ESendType.MSG_SUCCESS);
            bean.setIncoming(true);
            bean.setGroupChat(true);
            bean.setPlayStatus(EPlayType.NOT_DOWNLOADED);
            bean.setAvatarUrl(bean.getBodyEntity().getSenderAvatar());
            bean.setNick(StringUtils
                .getUserNick(bean.getBodyEntity().getMucNickName(), builder.getUsername()));
        } else if (message instanceof FGPushMessage) {
            FGPushMessage pushMessage = (FGPushMessage) message;
            PushMessage pMsg = pushMessage.message;
            bean = new MessageBean();
            bean.setMsgId(pMsg.getMessageId());
            bean.setContent(pMsg.getContent());
            bean.setCancel(0);
            bean.setCode(0);
            bean.setTime(pMsg.getTime());

            String content = pMsg.getContent();
            try {
                JSONObject object = new JSONObject(content);
                if (object != null) {
                    String type = object.optString("type");
                    if (!TextUtils.isEmpty(type)) {
                        if (type.equalsIgnoreCase("OA")) {
                            PushEntity entity = GsonHelper
                                .getObject(pMsg.getContent(), PushEntity.class);
                            bean.setTo(entity.getFrom());
                            bean.setNick(entity.getFrom());
                            bean.setMessageType(EMessageType.OA);
                        } else {
                            String from = object.optString("from");
                            if (!TextUtils.isEmpty(from)) {
                                bean.setTo(from);
                                bean.setNick(from);
                            } else {
                                bean.setTo(type);
                                bean.setNick(type);
                            }
                            bean.setMessageType(EMessageType.SYSTEM);

                        }
                    }
                }
            } catch (JSONException e) {
                bean.setTo("系统");
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
        return bean;
    }

    /*
    * @params content 消息内容
    * @user 消息接受者userId
    * */
    private IChatRoomModel createTransforMessage(String content, String user, boolean isGroup,
        boolean isSecret, EMessageType type) {
        MessageBean message = new MessageBean();

        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(user) && !TextUtils
            .isEmpty(getUserInfo().getUserid())) {
            if (type == EMessageType.MAP || type == EMessageType.VOTE) {
                message.setContent(content);
            } else {
                BodyEntity entity;
                if (isGroup) {
                    entity = MessageManager.getInstance()
                        .createBody(content, isSecret, type, getUserInfo().getImage(),
                            getUserInfo().getUsernick());
                } else {
                    entity = MessageManager.getInstance()
                        .createBody(content, isSecret, type);
                }
                message.setContent(BodyEntity.toJson(entity));
            }
            message.setMessageType(type);
            message.setMsgId(UUID.randomUUID().toString());
            message.setAvatarUrl(getUserInfo().getImage());
            message.setTime(System.currentTimeMillis());
            message.setFrom(getUserInfo().getUserid());
            message.setTo(user);
            message.setSecret(isSecret);
            message.setSendType(ESendType.SENDING);
            message.setIncoming(false);
            message.setNick(getUserInfo().getUsernick());
            if (isGroup) {
                message.setGroupChat(false);
            } else {
                message.setGroupChat(true);
            }
        }
        return message;
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
    public boolean canNotify(String userId) {
        int disturb = ProviderChat.getNoDisturb(ContextHelper.getContext(), userId);
        if (!isCurrentChat(userId) && disturb == ESureType.NO.ordinal()) {
            return true;
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

    //保存recent
    public void saveRecent(IChatRoomModel model, String avatar, String userNick, int noDisturb,
        int backId, int topFlag, boolean isNew) {
        RecentMessage message = createRecentMessage(model, avatar, userNick, noDisturb, backId,
            topFlag, isNew);
        ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
    }

    /*
    * @param avatar 对方头像
    * @param nick 对方昵称
    * */
    public void saveMessage(IChatRoomModel message, String avatar, String nick, int disturb,
        int backId, int topFlag) {
        boolean flag = ProviderChat.updateMessage(ContextHelper.getContext(), message);
        if (!flag) {
            ProviderChat.insertPrivateMessage(ContextHelper.getContext(), message);
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
            ProviderChat.insertPrivateMessage(ContextHelper.getContext(), message);
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
        message.setMsg(msg.getContent());
        if (msg.getMsgType() != null) {
            message.setMsgType(msg.getMsgType());
        }
        message.setNotDisturb(noDisturb);
        message.setUnreadCount(0);
        message.setTime(msg.getTime());
        message.setBackgroundId(backId);
        message.setTopFlag(topFlag);
        message.setChatType(
            msg.isGroupChat() ? EChatType.GROUP.ordinal() : EChatType.PRIVATE.ordinal());
        if (msg.isIncoming()) {
            message.setHint(ChatHelper.getHint(msg.getMsgType(), msg.getContent(), msg.isSecret()));
        }
        if (msg.isGroupChat()) {
            message.setUserId(msg.getFrom());
            message.setChatId(msg.getTo());
            String mucName = MucInfo.getMucName(ContextHelper.getContext(), msg.getTo());
            message.setGroupName(mucName);
            message.setNick(
                StringUtils.getUserNick(msg.getNick(), msg.getFrom()));
        } else {
            message.setUserId(msg.getTo());
            message.setChatId(msg.getTo());
            message.setAvatarUrl(avatar);
            message.setNick(userNick);
        }
        if (msg.getMsgType() == EMessageType.TEXT) {
            if (checkAt(msg.getContent())) {
                message.setAt(true);
            } else {
                message.setAt(false);
            }
        } else {
            message.setAt(false);
        }
        message.setNew(isNew);
        return message;
    }

    public boolean checkAt(String msg) {
        if (msg.contains("@" + AppConfig.INSTANCE.get(AppConfig.ACCOUT))) {
            return true;
        }
        return false;
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
            entity.setEnable(userBean.isQuit());
            entity.setValid(userBean.isValid());
            if (!ChatHelper.isGroupChat(chatType)) {
                entity.setSecret(false);
                bean.setGroupChat(false);
            } else {
                entity.setSecret(false);
                entity.setSenderAvatar(getUserInfo().getImage());
                entity.setMucNickName(getUserInfo().getUsernick());
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
            bodyEntity.setSecret(isSecret);
            if (isGroupChat) {
                bodyEntity.setMucNickName(nick);
            }
            bean.setContent(BodyEntity.toJson(bodyEntity));
        } else {
            bean.setContent(content);
        }
        bean.setMessageType(type);
        bean.setTime(System.currentTimeMillis());
        bean.setAvatarUrl(avatar);
        bean.setNick(nick);
        bean.setSendType(ESendType.SENDING);
        bean.setIncoming(isInconming);
        bean.setGroupChat(isGroupChat);
        bean.setSecret(isSecret);
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
            model.setSendType(ESendType.ERROR);
            model.setNick(nick);
            model.setMessageType(EMessageType.ACTION);
            model.setActionType(EActionType.NONE);
        }
        return model;
    }

    /*
    * 获取所有未读消息总数
    * */
    public int getTotalUnreadMessageCount() {
        return ProviderChat.selectTotalUnreadMessageCount(ContextHelper.getContext());
    }


    public void getAllImageMessage(Context context, String user, List<String> urls,
        List<String> ids) {
        ProviderChat.selectAllImageMessage(context, user, urls, ids);
    }

    public List<IChatRoomModel> getMessagesByIds(Context context, ArrayList<String> msgIds) {
        return ProviderChat.getMessagesByIds(context, msgIds);
    }

    //私聊body
    public BodyEntity createBody(String content, boolean secret, EMessageType type) {
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
        entity.setSecret(secret);
        entity.setBubbleHeight(0);
        entity.setBubbleWidth(0);
        return entity;
    }

    //群聊body
    public BodyEntity createBody(String content, boolean secret, EMessageType type, String avatar,
        String nick) {
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
        entity.setSecret(secret);
        entity.setBubbleHeight(0);
        entity.setBubbleWidth(0);
        entity.setSenderAvatar(avatar);
        entity.setMucNickName(nick);
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
    * activityChat2销毁的时候，将发送队列中未发送成功的清除掉，并且置为发送失败
    * */
    public void clearSequenceMap() {
        if (sequenceMap != null && sequenceMap.size() > 0) {
            Set<Entry<String, IChatRoomModel>> entrySet = sequenceMap.entrySet();
            Iterator<Entry<String, IChatRoomModel>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IChatRoomModel> m = iterator.next();
                IChatRoomModel message = m.getValue();
                ProviderChat.updateSendStatus(ContextHelper.getContext(), message.getMsgId(),
                    ESendType.ERROR);
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
}



