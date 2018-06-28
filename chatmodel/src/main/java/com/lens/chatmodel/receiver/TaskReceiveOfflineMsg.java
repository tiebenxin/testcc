package com.lens.chatmodel.receiver;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
import com.fingerchat.proto.message.Roster.RosterMessage;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lensim.fingerchat.commons.helper.ContextHelper;

import com.lensim.fingerchat.commons.interf.IChatUser;
import java.util.List;

/**
 * Created by LL130386 on 2018/3/23.
 * 离线消息接受Task
 */

public class TaskReceiveOfflineMsg extends AsyncTask<Void, Integer, Boolean> {

    OfflineMessage message;
    private boolean isPrivate;
    private boolean isRoom;
    private boolean isRoster;
    private boolean isAction;
    private boolean isPush;

    public TaskReceiveOfflineMsg(OfflineMessage message) {
        this.message = message;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (message != null) {
            List<MucAction> actionList = message.offlineMessage.getActionList();
            List<PrivateMessage> privateMessageList = message.offlineMessage
                .getPrivateMessageList();
            List<RoomMessage> roomMessageList = message.offlineMessage
                .getRoomMessageList();
            List<RosterMessage> rosterMessageList = message.offlineMessage
                .getRosterMessageList();
            List<PushMessage> pushMessageList = message.offlineMessage
                .getNotifyList();
            System.out.println(
                "action离线：" + actionList.size() + "--private离线：" + privateMessageList.size()
                    + "--Room离线：" + roomMessageList.size() + "--roster离线：" + rosterMessageList
                    .size() + "--roster离线：" + pushMessageList.size());
            isPrivate = savePrivateMessageList(privateMessageList);
            isRoom = saveRoomMessageList(roomMessageList);
            isRoster = saveRostList(rosterMessageList);
            isAction = saveActionList(actionList);
            isPush = savePushMessageList(pushMessageList);
            if (isPrivate || isRoom || isRoster || isAction || isPush) {
                return true;
            }
        }
        return false;
    }

    private boolean saveRostList(List<RosterMessage> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (RosterMessage roster : list) {

        }
        return true;
    }

    private boolean saveActionList(List<MucAction> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (MucAction action : list) {
            MucManager.getInstance().actionOperation(action);
        }
        return true;
    }

    private boolean savePrivateMessageList(List<PrivateMessage> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (PrivateMessage message : list) {
            MessageBean msg = MessageManager.getInstance().createMessageBean(message);
            if (msg != null) {
                if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                    ProviderChat.insertPrivateMessage(ContextHelper.getContext(), msg);
                    RecentMessage recent = ProviderChat
                        .selectSingeRecent(ContextHelper.getContext(), msg.getTo());
                    IChatUser user = ProviderUser
                        .selectRosterSingle(ContextHelper.getContext(), msg.getTo());
                    if (user != null) {
                        if (recent != null) {
                            MessageManager.getInstance()
                                .saveRecent(msg, user.getAvatarUrl(), user.getUserNick(),
                                    recent.getNotDisturb(), recent.getBackgroundId(),
                                    recent.getTopFlag(),
                                    !MessageManager.getInstance().isCurrentChat(msg.getTo()));
                        } else {
                            MessageManager.getInstance()
                                .saveRecent(msg, user.getAvatarUrl(), user.getUserNick(),
                                    ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                    ESureType.NO.ordinal(),
                                    !MessageManager.getInstance().isCurrentChat(msg.getTo()));
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean saveRoomMessageList(List<RoomMessage> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (RoomMessage message : list) {
            MessageBean msg = MessageManager.getInstance().createMessageBean(message);
            if (msg != null) {
                if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                    ProviderChat.insertPrivateMessage(ContextHelper.getContext(), msg);
                    RecentMessage recent = ProviderChat
                        .selectSingeRecent(ContextHelper.getContext(), msg.getTo());
                    IChatUser user = ProviderUser
                        .selectRosterSingle(ContextHelper.getContext(), msg.getTo());
                    if (user != null) {
                        if (recent != null) {
                            MessageManager.getInstance()
                                .saveRecent(msg, user.getAvatarUrl(), user.getUserNick(),
                                    recent.getNotDisturb(), recent.getBackgroundId(),
                                    recent.getTopFlag(),
                                    !MessageManager.getInstance().isCurrentChat(msg.getTo()));
                        } else {
                            MessageManager.getInstance()
                                .saveRecent(msg, user.getAvatarUrl(), user.getUserNick(),
                                    ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                    ESureType.NO.ordinal(),
                                    !MessageManager.getInstance().isCurrentChat(msg.getTo()));
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean savePushMessageList(List<PushMessage> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        for (PushMessage message : list) {
            MessageBean msg = MessageManager.getInstance().createMessageBean(message);
            if (msg != null) {
                if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                    ProviderChat.insertPrivateMessage(ContextHelper.getContext(), msg);
                    IChatUser user = ProviderUser
                        .selectRosterSingle(ContextHelper.getContext(), msg.getTo());
                    if (user != null) {
                        MessageManager.getInstance()
                            .saveRecent(msg, user.getAvatarUrl(), user.getUserNick(),
                                ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                ESureType.NO.ordinal(),
                                !MessageManager.getInstance().isCurrentChat(msg.getTo()));
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            if (isAction || isPrivate || isRoom) {
                if (!TextUtils.isEmpty(MessageManager.getInstance().getCurrentChatId())) {
                    MessageManager.getInstance().notifyDataChange(EActivityNum.CHAT, null);
                } else {
                    MessageManager.getInstance()
                        .notifyDataChange(EActivityNum.MAIN, EFragmentNum.TAB_MESSAGE);
                }
            } else if (isRoster) {
                MessageManager.getInstance()
                    .notifyDataChange(EActivityNum.MAIN, EFragmentNum.TAB_MESSAGE);
            }
        }
    }
}
