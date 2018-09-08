package com.lens.chatmodel.receiver;

import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.fingerchat.api.message.OfflineMessage;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.Notify.PushMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.Roster.RosterMessage;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.manager.RosterManager;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
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

    @RequiresApi(api = VERSION_CODES.N)
    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println(TaskReceiveOfflineMsg.class.getSimpleName() + "--doInBackground");
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
                "action离线--" + actionList.size() + "  private离线--" + privateMessageList.size()
                    + "  Room离线--" + roomMessageList.size() + "  roster离线：" + rosterMessageList
                    .size() + "  roster离线--" + pushMessageList.size());
            List<MessageBean> privateList = createAndSortPrivateMessageList(privateMessageList);
            isPrivate = savePrivateMessageList(privateList, 0);
            List<MessageBean> roomList = createAndSortRoomMessageList(roomMessageList);
            isRoom = saveRoomMessageList(roomList, 0);
            isRoster = saveRostList(rosterMessageList, 0);
            isAction = saveActionList(actionList, 0);
            List<MessageBean> pushList = createAndSortPushMessageList(pushMessageList);
            isPush = savePushMessageList(pushList, 0);
            if (isPrivate || isRoom || isRoster || isAction || isPush) {
                System.out.println("离线消息存储成功");
                return true;
            }
        }
        System.out.println("离线消息存储失败");
        return true;
    }

    private boolean saveRostList(List<RosterMessage> list, int position) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        RosterMessage message = list.get(position);
        int code = message.getCode();
        if (code == Common.INVITE_OK) {//邀请好友成功，刷新列表
            List<RosterItem> rosters = message.getItemList();
            if (rosters != null && rosters.size() > 0) {
                List<UserBean> users = RosterManager.getInstance()
                    .createNewFriendFromList(rosters,
                        ERelationStatus.FRIEND);
                if (users != null && users.size() > 0) {
                    ProviderUser.updateRosterAsyn(ContextHelper.getContext(), users.get(0));
                }
            }

        } else if (code == Common.ADD_SUCCESS) {//添加好友成功，刷新列表
            List<RosterItem> rosters = message.getItemList();
            if (rosters != null && rosters.size() > 0) {
                List<UserBean> users = RosterManager.getInstance()
                    .createNewFriendFromList(rosters,
                        ERelationStatus.FRIEND);
                if (users != null && users.size() > 0) {
                    ProviderUser.updateRosterAsyn(ContextHelper.getContext(), users.get(0));
                }

            }
        } else if (code == Common.SEND_INVITE) {//收到好友邀请
            List<RosterItem> rosters = message.getItemList();
            List<UserBean> newRoster = RosterManager.getInstance()
                .createChatUserFromList(rosters, ERelationStatus.RECEIVE,
                    System.currentTimeMillis(), ESureType.NO.ordinal(),
                    ESureType.YES.ordinal());
            if (newRoster != null && newRoster.size() > 0) {
                ProviderUser.updateRosterAsyn(ContextHelper.getContext(), newRoster.get(0));
            }
        } else if (code == Common.DELETE_SUCCESS) {//删除成功
            List<RosterItem> rosters = message.getItemList();
            List<UserBean> newRoster = RosterManager.getInstance()
                .createChatUserFromList(rosters, ERelationStatus.RECEIVE,
                    System.currentTimeMillis(), ESureType.NO.ordinal(),
                    ESureType.YES.ordinal());
            if (newRoster != null && newRoster.size() > 0) {
                for (int i = 0; i < rosters.size(); i++) {
                    ProviderUser
                        .deleRoster(ContextHelper.getContext(), newRoster.get(i).getUserId());
                }
            }
        }
        return true;
    }


    private boolean savePrivateMessageList(List<MessageBean> list, int position) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        int len = list.size();
        MessageBean msg = list.get(position);
        if (msg != null) {
            if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                boolean msgSuccess = ProviderChat
                    .insertMessageAsyn(ContextHelper.getContext(), msg);
                if (msgSuccess) {
                    RecentMessage recent = ProviderChat
                        .selectSingeRecent(ContextHelper.getContext(), msg.getTo());
                    IChatUser user = MessageManager.getInstance()
                        .getCacheUserBean(msg.getTo());
                    if (user != null) {
                        if (recent != null) {
                            MessageManager.getInstance()
                                .saveRecentOfflineAsyn(msg, user.getAvatarUrl(),
                                    user.getUserNick(),
                                    recent.getNotDisturb(), recent.getBackgroundId(),
                                    recent.getTopFlag(),
                                    !MessageManager.getInstance()
                                        .isCurrentChat(msg.getTo()),
                                    len);
                            System.out.println("saveRecentOfflineAsyn 成功" + position);
                        } else {
                            MessageManager.getInstance()
                                .saveRecentOfflineAsyn(msg, user.getAvatarUrl(),
                                    user.getUserNick(),
                                    ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                    ESureType.NO.ordinal(),
                                    !MessageManager.getInstance()
                                        .isCurrentChat(msg.getTo()),
                                    len);
                            System.out.println("saveRecentOfflineAsyn 成功" + position);
                        }
                    } else {
                        MessageManager.getInstance()
                            .saveRecentOfflineAsyn(msg, "", "",
                                ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                ESureType.NO.ordinal(),
                                !MessageManager.getInstance()
                                    .isCurrentChat(msg.getTo()),
                                len);
                        System.out.println("saveRecentOfflineAsyn 成功" + "user =null");
                    }
                    if (position < len - 1) {
                        position = position + 1;
                        savePrivateMessageList(list, position);
                    } else if (position == len - 1) {
                        return true;
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private boolean saveRoomMessageList(List<MessageBean> list, int position) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        int len = list.size();
        MessageBean msg = list.get(position);
        if (msg != null) {
            if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                System.out.println("是否insert room 成功" + position);
                boolean msgSuccess = ProviderChat
                    .insertMessageAsyn(ContextHelper.getContext(), msg);
                String mucId = msg.getTo();
                int disturb = MucInfo
                    .getMucNoDisturb(ContextHelper.getContext(), mucId);
                int backId = MucInfo.getMucChatBg(ContextHelper.getContext(), mucId);
                int topFlag = ProviderChat
                    .getTopFlag(ContextHelper.getContext(), mucId);
                MessageManager.getInstance()
                    .saveRecentOfflineAsyn(msg, msg.getAvatarUrl(), msg.getNick(), disturb,
                        backId, topFlag,
                        !MessageManager.getInstance().isCurrentChat(msg.getTo()), len);
                if (msgSuccess) {
                    if (position < len - 1) {
                        position = position + 1;
                        saveRoomMessageList(list, position);
                    } else if (position == len - 1) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private boolean savePushMessageList(List<MessageBean> list, int position) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        int len = list.size();
        MessageBean msg = list.get(position);
        if (msg != null) {
            if (!MessageManager.getInstance().isDoingMessage(msg.getMsgType())) {
                boolean success = ProviderChat.insertMessageAsyn(ContextHelper.getContext(), msg);
                if (success) {
                    if (position < len - 1) {
                        position = position + 1;
                        savePushMessageList(list, position);
                    } else if (position == len - 1) {
                        MessageManager.getInstance()
                            .saveRecentOfflineAsyn(msg, "", msg.getNick(),
                                ESureType.NO.ordinal(), EChatBgId.DEFAULT.id,
                                ESureType.NO.ordinal(),
                                !MessageManager.getInstance().isCurrentChat(msg.getTo()), len);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        System.out.println("离线消息onPostExecute");

        if (aBoolean) {
            System.out.println("离线消息刷新");
            if (isAction || isPrivate || isRoom || isPush) {
                if (!TextUtils.isEmpty(MessageManager.getInstance().getCurrentChatId())) {
                    System.out.println("离线消息刷新CHAT");
                    MessageManager.getInstance().setMessageChange(true);
                } else {
                    System.out.println("离线消息刷新MAIN");
                    MessageManager.getInstance()
                        .notifyDataChange(EActivityNum.MAIN, EFragmentNum.TAB_MESSAGE, 1);
                }
            } else if (isRoster) {
                MessageManager.getInstance()
                    .notifyDataChange(EActivityNum.MAIN, EFragmentNum.TAB_CONTACTS, 0);
            }
        }
    }

    //创建并排序
    private List<MessageBean> createAndSortPrivateMessageList(List<PrivateMessage> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<MessageBean> beans = new ArrayList<>();
        for (PrivateMessage message : list) {
            beans.add(MessageManager.getInstance().createMessageBean(message,
                MessageManager.getInstance().checkFromSelf(message.getFrom())));
        }
        Collections.sort(beans);
        return beans;
    }

    private List<MessageBean> createAndSortRoomMessageList(List<RoomMessage> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<MessageBean> beans = new ArrayList<>();
        for (RoomMessage message : list) {
            beans.add(MessageManager.getInstance().createMessageBean(message,
                MessageManager.getInstance().checkFromSelf(message.getUsername())));
        }
        Collections.sort(beans);
        return beans;
    }

    //创建并排序
    private List<MessageBean> createAndSortPushMessageList(List<PushMessage> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<MessageBean> beans = new ArrayList<>();
        for (Object message : list) {
            beans.add(MessageManager.getInstance().createMessageBean(message, false));
        }
        Collections.sort(beans);
        return beans;
    }

    @RequiresApi(api = VERSION_CODES.N)
    public boolean saveActionList(List<MucAction> list, int position) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        int len = list.size();

        if (position < len - 1) {
            actionOperationAsyn(list, position);
        } else if (position == len - 1) {
            actionOperationAsyn(list, position);
            return true;
        }
        return true;
    }

    /**
     * 解析action同步
     */

    public boolean actionOperationAsyn(List<MucAction> list, int position) {
        try {
            Muc.MucAction action = list.get(position);
            int len = list.size();
            String mucId = action.getMucid();
            //判断是否存在群
            MucManager.getInstance().actionOperation(action);
            //创建action消息，并保存数据库
            IChatRoomModel model = MessageManager.getInstance()
                .createActionMessage(action.getFrom().getUsername(),
                    action.getFrom().getMucusernick(),
                    mucId, action.getMucname(),
                    ChatHelper.getActionType(action),
                    MucHelper.getActionText(ContextHelper.getContext(), action), action.getTime());
            if (model != null) {
                boolean flag = ProviderChat.insertMessageAsyn(ContextHelper.getContext(), model);
                if (flag) {
                    System.out.println("offline action" + "--" + len + "--" + position);
                    int disturb = MucInfo
                        .getMucNoDisturb(ContextHelper.getContext(), mucId);
                    int backId = MucInfo.getMucChatBg(ContextHelper.getContext(), mucId);
                    int topFlag = ProviderChat
                        .getTopFlag(ContextHelper.getContext(), mucId);
                    MessageManager.getInstance()
                        .saveRecentOfflineAsyn(model, model.getAvatarUrl(), model.getNick(),
                            disturb,
                            backId, topFlag,
                            !MessageManager.getInstance().isCurrentChat(model.getTo()), 0);
                    if (position < len - 1) {
                        position = position + 1;
                        actionOperationAsyn(list, position);
                    } else if (position == len - 1) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
