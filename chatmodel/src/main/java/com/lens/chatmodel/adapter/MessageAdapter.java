package com.lens.chatmodel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.controller.cell.ChatCellAction;
import com.lens.chatmodel.controller.cell.ChatCellBase;
import com.lens.chatmodel.controller.cell.FactoryChatCell;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.IShowListener;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.view.CustomContextMenu;
import com.lens.chatmodel.view.CustomContextMenu.OnMenuListener;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.pulltorefresh.XCPullToLoadMoreListView;
import com.lensim.fingerchat.data.login.UserInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/1/3.
 */

public class MessageAdapter extends BaseAdapter implements OnMenuListener {

    private List<IChatRoomModel> mList;
    private final ArrayList<String> selectedIds;
    private final List<IChatRoomModel> selectedModels;
    private final Context context;
    private boolean isShowCheckBox;
    private FactoryChatCell viewFactory;
    private CustomContextMenu mCustomContextMenu;
    private IShowListener mBottomShowListener;
    private OnItemClickListener listener;
    private ChatCellAction mHeader;
    private String user;
    private XCPullToLoadMoreListView lisView;
    private final IChatEventListener eventListener;
    private int chatType;
    private int unreadCount;
    private UserInfo mInfo;
    private UserBean otherBean;


    public MessageAdapter(Context c, IChatEventListener l) {
        context = c;
        mList = new ArrayList<>();
        selectedIds = new ArrayList<>();
        selectedModels = new ArrayList<>();
        showCheckBox(false, false);
        eventListener = l;
    }

    public void setData(List<IChatRoomModel> l, int page) {
        if (mList == null) {
            mList = l;
        } else {
            if (page == 0) {
                mList.clear();
            }
            mList.addAll(0, l);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getTotalItemsCount();
    }

    @Override
    public IChatRoomModel getItem(int position) {
        if (mList.size() > position) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        IChatRoomModel model = mList.get(position);
        ChatCellBase controller;
        if (convertView == null) {
            ChatEnum.EChatCellLayout layout = ChatEnum.EChatCellLayout.fromOrdinal(viewType);
            controller = viewFactory.createController(layout, position);
            convertView = controller.getView();
            convertView.setTag(getTypeId(viewType), controller);
        } else {
            if (convertView.getTag(viewType) != null) {
                controller = (ChatCellBase) convertView.getTag(getTypeId(viewType));
            } else {
                ChatEnum.EChatCellLayout layout = ChatEnum.EChatCellLayout.fromOrdinal(viewType);
                controller = viewFactory.createController(layout, position);
                convertView = controller.getView();
                convertView.setTag(getTypeId(viewType), controller);
            }
        }
        if (unreadCount >= 10) {
            controller.setFirstUnreadPosition(getTotalItemsCount() - unreadCount);
        }

        if (!model.isGroupChat()) {
            if (model.isIncoming()) {
                model.setNick(otherBean.getUserNick());
                model.setAvatarUrl(otherBean.getAvatarUrl());
            } else {
                model.setNick(mInfo.getUsernick());
                model.setAvatarUrl(mInfo.getImage());
            }
        } else {//群聊
            if (model.isIncoming()) {
                Muc.MucMemberItem memberItem = MucUser
                    .selectUserById(ContextHelper.getContext(), model.getTo(), model.getFrom());
                if (null != memberItem) {
                    model.setNick(TextUtils.isEmpty(memberItem.getMucusernick()) ? (
                        TextUtils.isEmpty(memberItem.getUsernick()) ? memberItem.getUsername()
                            : memberItem.getUsernick()) : memberItem.getMucusernick());
                    model.setAvatarUrl(memberItem.getAvatar());
                }
            } else {
                model.setNick(mInfo.getUsernick());
                model.setAvatarUrl(mInfo.getImage());
            }
        }
        controller.setModel(model);
        controller.setCustomContextMenu(getCustomContextMenu());
        controller.setMenuListener(this);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getTotalItemsCount() > position) {
            if (position == 0 && mHeader != null) {
                return EChatCellLayout.CHAT_ACTION.ordinal();
            } else {
                return mList.get(mHeader == null ? position : position - 1).getChatCellLayoutId()
                    .ordinal();
            }
        }
        return super.getItemViewType(position);
    }

    private int getTypeId(int viewType) {
        int rid = -1;
        if (viewType == EChatCellLayout.TEXT_RECEIVED.ordinal()) {
            rid = R.id.type_text_received;
        } else if (viewType == EChatCellLayout.TEXT_SEND.ordinal()) {
            rid = R.id.type_text_send;
        } else if (viewType == EChatCellLayout.IMAGE_RECEIVED.ordinal()) {
            rid = R.id.type_image_received;
        } else if (viewType == EChatCellLayout.IMAGE_SEND.ordinal()) {
            rid = R.id.type_image_send;
        } else if (viewType == EChatCellLayout.VOICE_RECEIVED.ordinal()) {
            rid = R.id.type_voice_received;
        } else if (viewType == EChatCellLayout.VOICE_SEND.ordinal()) {
            rid = R.id.type_voice_send;
        } else if (viewType == EChatCellLayout.VIDEO_RECEIVED.ordinal()) {
            rid = R.id.type_video_received;
        } else if (viewType == EChatCellLayout.VIDEO_SEND.ordinal()) {
            rid = R.id.type_video_send;
        } else if (viewType == EChatCellLayout.VIDEO_RECEIVED.ordinal()) {
            rid = R.id.type_video_received;
        } else if (viewType == EChatCellLayout.VIDEO_SEND.ordinal()) {
            rid = R.id.type_video_send;
        } else if (viewType == EChatCellLayout.BUSINESS_CARD_RECEIVED.ordinal()) {
            rid = R.id.type_business_card_received;
        } else if (viewType == EChatCellLayout.BUSINESS_CARD_SEND.ordinal()) {
            rid = R.id.type_business_card_send;
        } else if (viewType == EChatCellLayout.WORK_LOGIN_RECEIVED.ordinal()) {
            rid = R.id.type_work_login_received;
        } else if (viewType == EChatCellLayout.WORK_LOGIN_SEND.ordinal()) {
            rid = R.id.type_work_login_send;
        } else if (viewType == EChatCellLayout.VOTE_RECEIVED.ordinal()) {
            rid = R.id.type_vote_received;
        } else if (viewType == EChatCellLayout.VOTE_SEND.ordinal()) {
            rid = R.id.type_vote_send;
        } else if (viewType == EChatCellLayout.CHAT_ACTION.ordinal()) {
            rid = R.id.type_action;
        } else if (viewType == EChatCellLayout.SECRET.ordinal()) {
            rid = R.id.type_secret_received;
        } else if (viewType == EChatCellLayout.NOTICE.ordinal()) {
            rid = R.id.type_notice;
        } else if (viewType == EChatCellLayout.OA.ordinal()) {
            rid = R.id.type_oa;
        } else if (viewType == EChatCellLayout.SYSTEM.ordinal()) {
            rid = R.id.type_system;
        }
        return rid;
    }


    public void addMessage(IChatRoomModel model) {
        if (mList != null) {
            mList.add(model);
            notifyDataSetChanged();
        }
    }

    public void removeMessage(IChatRoomModel model) {
        if (mList != null) {
            int i = getMessagePostion(model);
            if (i >= 0) {
                mList.remove(i);
                notifyDataSetChanged();
            }
        }
    }

    public void updateItemSendType(View view, int position, ESendType type, int progress) {
        if (position >= 0 && position < getTotalItemsCount()) {
            System.out.println("updateUploadProgress:" + progress);
            if (type == ESendType.SENDING) {
                ChatCellBase controller = (ChatCellBase) view.getTag(getTypeId(getItemViewType(position)));
                if (controller != null) {
                    controller.updateProgress(progress);
                }
            } else if (type == ESendType.MSG_SUCCESS) {
                ChatCellBase controller = (ChatCellBase) view.getTag(getTypeId(getItemViewType(position)));
                if (controller != null) {
                    controller.updateProgress(100);
                }
            }
        }
    }


    public int getMessagePostion(IChatRoomModel model) {
        int position = -1;
        if (model != null && mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                IChatRoomModel m = mList.get(i);
                if (model.getMsgId().equalsIgnoreCase(m.getMsgId())) {
                    position = i;
                }
            }
        }
        return position;
    }

    public int getTotalItemsCount() {
        return mList.size() + (mHeader != null ? 1 : 0);
    }

    public void setViewFactory(FactoryChatCell factory) {
        viewFactory = factory;
    }

    public void initUserBean(UserInfo info, UserBean bean) {
        mInfo = info;
        otherBean = bean;
    }

    public void setUser(String n) {
        user = n;
    }

    public String getUser() {
        return user;
    }

    public void setChatType(int type) {
        chatType = type;
    }

    public int getChatType() {
        return chatType;
    }

    public void setUnreadCount(int position) {
        unreadCount = position;
    }


    public void showCheckBox(boolean flag, boolean update) {
        isShowCheckBox = flag;
        if (update) {
            notifyDataSetChanged();
        }
    }


    @Override
    public void OnSelect(IChatRoomModel selectedModel, int pos) {
        if (selectedModel == null) {
            return;
        }
        switch (pos) {
            case CustomContextMenu.COPY:
                eventListener.onEvent(ECellEventType.COPY, selectedModel, null);
                break;
            case CustomContextMenu.ADD_EX:
                String data = SPHelper.getString(AppConfig.EX_KEY, "");
                String json = null;
                if (selectedModel.isIncoming()) {
                    json = selectedModel.getContent();
                } else {
                    json = selectedModel.getUploadUrl();
                }
                if (!TextUtils.isEmpty(json)) {
                    if (!TextUtils.isEmpty(data)) {
                        if (!data.contains(json)) {
                            data = data + ";" + json;
                            SPHelper.saveValue(AppConfig.EX_KEY, data);
                        } else {
                            T.show("已经收藏");
                        }
                    } else {
                        data = json;
                        SPHelper.saveValue(AppConfig.EX_KEY, data);
                    }

                }
                eventListener.onEvent(ECellEventType.ADD_EX, selectedModel, data);
                break;
            case CustomContextMenu.TRANSMIT:
                eventListener.onEvent(ECellEventType.TRANSFER_MSG, selectedModel, null);
                break;
            case CustomContextMenu.CANCLE:
                long ts = selectedModel.getTime();
                if (System.currentTimeMillis() - ts > 2 * 60 * 1000) {
                    T.showShort(context, "只能撤销两分钟内的消息");
                    return;
                }
                if (selectedModel.getSendType() == ESendType.SENDING) {
                    T.showShort(context, "消息还没有发出");
                    return;
                }
                eventListener.onEvent(ECellEventType.CANCEL, selectedModel, null);
                break;
            case CustomContextMenu.DEL:
                if (mList != null && !mList.isEmpty()) {
                    int index = mList.indexOf(selectedModel);
                    int totalSize = getTotalItemsCount();
                    if (index > 0) {
                        if (index == totalSize - 1) {//最后一条消息
                            IChatRoomModel model = mList.get(index - 1);
                            RecentMessage message = createRecentMessage(model, false);
                            ProviderChat.updateRecentMessage(ContextHelper.getContext(), message);
                        }
                    } else if (index == 0 && index == totalSize - 1) {//当前有且仅有一条消息，删除后，要不要把对话完全删除？
                        ProviderChat.clearRecentMessageById(ContextHelper.getContext(),
                            selectedModel.getTo());
                    }
                    removeMessage(selectedModel);
                    ProviderChat
                        .delePrivateMessage(ContextHelper.getContext(),
                            selectedModel.getMsgId());
                }
                break;
            case CustomContextMenu.COLLECTION:
                eventListener.onEvent(ECellEventType.COLLECT_MSG, selectedModel, null);
                break;
            case CustomContextMenu.MORE:
                if (selectedModel.getMsgType() == EMessageType.MULTIPLE) {
                    T.showShort(context, "合并消息不能转发或作为附件");
                    return;
                }
                showCheckBox(true, true);
                getSelectedIds().add(selectedModel.getMsgId());
                getSelectedModels().add(selectedModel);
                if (mBottomShowListener != null) {
                    mBottomShowListener.isShow(true);
                }
                break;
        }
    }

    private RecentMessage createRecentMessage(IChatRoomModel msg, boolean isNew) {
        RecentMessage message = new RecentMessage();
        message.setMsg(msg.getContent());
        message.setMsgType(msg.getMsgType());
        if (msg.getMsgType() == EMessageType.NOTICE && msg.getCancel() == 1) {
            message.setHint(MessageManager.getInstance().getCancelText(msg));
        }
        message.setUserId(msg.getTo());
        message.setNick(msg.getNick());
        message.setUnreadCount(0);
        message.setChatId(msg.getTo());
        message.setAvatarUrl(msg.getAvatarUrl());
        message.setTime(msg.getTime());
        message.setAt(false);
        message.setNew(isNew);
        return message;
    }

    public void hideBottomMenu() {
        showCheckBox(false, true);
        clearSelectedChats();

    }

    public void clearSelectedChats() {
        if (selectedIds != null) {
            selectedIds.clear();
        }
        if (selectedModels != null) {
            selectedModels.clear();
        }
    }

    public ArrayList<String> getSelectedIds() {
        return selectedIds;
    }

    public List<IChatRoomModel> getSelectedModels() {
        return selectedModels;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setCustomContextMenu(CustomContextMenu view) {
        mCustomContextMenu = view;
    }

    public CustomContextMenu getCustomContextMenu() {
        return mCustomContextMenu;
    }

    public void setBottomShowListener(IShowListener l) {
        mBottomShowListener = l;
    }

    public void setItemClickListener(OnItemClickListener l) {
        listener = l;
    }

    public void onChange() {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        List<IChatRoomModel> temp = ProviderChat
            .selectMsgAsPage(context, user, 0, 20, ChatHelper.isGroupChat(chatType));
        if (temp != null) {
            mList.addAll(temp);
            notifyDataSetChanged();
        }
    }

    public void setListView(XCPullToLoadMoreListView lv) {
        lisView = lv;
    }

    public XCPullToLoadMoreListView getListView() {
        return lisView;
    }
}
