package com.lensim.fingerchat.fingerchat.ui.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.controller.ControllerNetError;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatItemClickListener;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.ISearchClickListener;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lens.chatmodel.ui.message.AdapterNewMessage;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.search.SearchActivity;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.dialog.NewMsgDialog;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IDialogItemClickListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.pulltorefresh.refresh_listview.OnMyRefreshListener;
import com.lensim.fingerchat.components.pulltorefresh.refresh_listview.RefreshListView;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import com.lensim.fingerchat.fingerchat.ui.search.ControllerSearch;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentTabMessage extends BaseFragment {

    private final int RELOGIN = 0x01;
    private final int MIN_SECENDS = 1000;

    private RefreshListView listView;
    private AdapterNewMessage mAdapter;
    private List<RecentMessage> listData;

    private ControllerNetError viewNetError;
    private int num = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RELOGIN) {
                Log.e("num", "num = " + num);
                num++;
                relogin();
            }
        }
    };
    private String userId;
    private String psw;

    public static FragmentTabMessage newInstance() {
        return new FragmentTabMessage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }


    @Override
    protected void initView() {
        ControllerSearch viewSearch = new ControllerSearch(getView().findViewById(R.id.viewSearch));
        viewSearch.setOnClickListener(new ISearchClickListener() {
            @Override
            public void search(String value) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        viewNetError = new ControllerNetError(
            getView().findViewById(R.id.viewNetError));
        viewNetError.setControllerListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {//重新登录
                if (!NetworkUtils.isNetAvaliale()) {
                    T.showShort(R.string.no_network_connection);
                } else {
                    mHandler.removeMessages(RELOGIN);
                    relogin();
                }
            }
        });

        listView = getView().findViewById(R.id.lv_list);
        mAdapter = new AdapterNewMessage(getContext());
        mAdapter.setUserId(((BaseUserInfoActivity) getActivity()).getUserId());
        mAdapter.setItemClickListener(new IChatItemClickListener() {
            @Override
            public void clickAvatar(RecentMessage model) {
                if (model != null) {
                    startActivityUserDetail(model);
                }
            }

            @Override
            public void click(RecentMessage model) {
                if (model != null) {
                    if (model.isAt()) {
                        ProviderChat.updateAt(model.getChatId());
                    }
                    startActivityChat(model);
                }
            }

            @Override
            public void onLongClick(RecentMessage model) {
                showDialog(model);
            }
        });
        listView.setAdapter(mAdapter);

        listView.setOnRefreshListener(new OnMyRefreshListener() {
            @Override
            public void onDownPullRefresh() {
                loadData();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.bindData(listData);
                        listView.refreshCompleted();
                    }
                }, 1000);

            }

            @Override
            public void onLoadingMore() {

            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listData == null) {
            listData = new ArrayList<>();
        }
        listData.clear();
        loadData();

    }

    /*
    * 重新登录
    * */
    private void relogin() {
        if (FingerIM.I.isLogin()) {
            if (mHandler != null) {
                mHandler.removeMessages(RELOGIN);
            }
            return;
        }

        if (!FingerIM.I.hasStarted() || !FingerIM.I.isClientState()) {
            //service是否已经启动，client不为空
            FingerIM.I.startFingerIM();
        } else if (!FingerIM.I.isConnected()) {
            FingerIM.I.manualReconnect();
        } else if (FingerIM.I.isConnected()) {
            if (TextUtils.isEmpty(userId)) {
                userId = UserInfoRepository.getUserName();
            }
            if (TextUtils.isEmpty(psw)) {
                psw = PasswordRespository.getPassword();
            }
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(psw) && FingerIM.I.isHandOk()) {
                FingerIM.I.login(userId, psw);
            }
        }
        mHandler.sendEmptyMessageDelayed(RELOGIN, MIN_SECENDS);
    }

    @Override
    public void notifyResumeData() {
        super.notifyResumeData();
        loadData();
        bindData();
        showNetStatus();
    }

    private void loadData() {
        List<RecentMessage> temp = ProviderChat.selectAllRecents(getContext());
        for (RecentMessage message : temp) {
            boolean isGroupChat = message.getChatType() == EChatType.GROUP.ordinal();
            IChatRoomModel model = ProviderChat.getLastMessage(message.getChatId(), isGroupChat);
            if (model != null) {
                message.setLastMessage(model);
            }
            message.setUnreadCount(ProviderChat
                .selectUnreadMessageCountOfUser(ContextHelper.getContext(), message.getChatId()));
        }
        if (temp != null) {
            if (listData != null) {
                listData.clear();
                listData.addAll(temp);
            }
        }
    }

    private void bindData() {
        if (mAdapter != null && listData != null) {
            mAdapter.bindData(listData);
        }
    }

    private void startActivityChat(RecentMessage message) {
        int chatType = message.getChatType();
        Intent intent = null;
        if (chatType == EChatType.GROUP.ordinal()) {
            intent = ChatActivity
                .createChatIntent(getActivity(), message.getChatId(), message.getGroupName(),
                    chatType, message.getBackgroundId(),
                    message.getNotDisturb(), message.getTopFlag());
        } else if (chatType == EChatType.PRIVATE.ordinal()) {
            if (ChatHelper.isSystemUser(message.getChatId())) {
                UserBean bean = new UserBean();
                bean.setUserId(message.getChatId());
                bean.setRemarkName(message.getNick());
                intent = ChatActivity
                    .createChatIntent(getActivity(), bean);
            } else {
                intent = ChatActivity
                    .createChatIntent(getActivity(), message.getChatId(), message.getBackgroundId(),
                        message.getNotDisturb(), message.getTopFlag());
            }
        }
        getActivity().startActivity(intent);
    }

    private void startActivityUserDetail(RecentMessage message) {
        Intent intent = FriendDetailActivity
            .createNormalIntent(getActivity(), message.getChatId());
        getActivity().startActivity(intent);
    }

    private void showDialog(final RecentMessage mBean) {
        final boolean isGroup, isTop, isUnread;
        final String jid = mBean.getChatId();
        isGroup = (mBean.getChatType() == EChatType.GROUP.ordinal());
        int unreadCount = ProviderChat
            .selectUnreadMessageCountOfUser(ContextHelper.getContext(), mBean.getChatId());
        if (unreadCount != 0) {
            isUnread = true;
        } else {
            isUnread = false;
        }
        if (mBean.getTopFlag() == ESureType.YES.ordinal()) {
            isTop = true;
        } else {
            isTop = false;
        }

        NewMsgDialog dialog = new NewMsgDialog(getActivity(), R.style.MyDialog, jid, isGroup,
            isUnread,
            isTop);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setItemClickListener(new IDialogItemClickListener() {
            @Override
            public void dele() {
                ProviderChat.deleChat(getActivity(), mBean.getChatId());
                //为群聊
                if (EChatType.GROUP.value == mBean.getChatType()) {
                    //判断是否存在群
                    Muc.MucItem mucItem = MucInfo.selectByMucId(getActivity(), mBean.getChatId());
                    if (null == mucItem || TextUtils.isEmpty(mucItem.getMucid())) {
                        //删除群成员
                        MucUser.delGroupUser(getActivity(), mBean.getChatId());
                    }
                }
                onChatChanged();
                ((MainActivity) getActivity()).initUnreadCounts();

            }

            @Override
            public void markTop() {
                ProviderChat.markTop(getActivity(), mBean.getChatId(), isTop);
                onChatChanged();
            }

            @Override
            public void markUnread() {
                if (isUnread) {
                    ProviderChat.updateHasReaded(mBean.getChatId(), isUnread);
                } else {
                    ProviderChat.updateHasReaded(mBean.getMsgId());
                }
                onChatChanged();
                ((MainActivity) getActivity()).initUnreadCounts();
            }
        });
        dialog.show();
    }

    private void onChatChanged() {
        loadData();
        bindData();

    }

    public void notifyNetStatusChange(ENetStatus status) {
        if (status == null) {
            return;
        }

        if (status == ENetStatus.ERROR_NET) {
            AppManager.getInstance().setLoginStatus(false);

        } else if (status == ENetStatus.SUCCESS_ON_SERVICE) {//握手成功
            relogin();//自动重登陆
        } else if (status == ENetStatus.SUCCESS_ON_NET || status == ENetStatus.ERROR_LOGIN
            || status == ENetStatus.ERROR_CONNECT) {

            if (status == ENetStatus.ERROR_CONNECT) {
                AppManager.getInstance().setLoginStatus(false);
            }
            //连接断开,先判断上次断开连接是不是因为登录冲突
            if (!FingerIM.I.isConnected() && !FingerIM.I.isBannedAutoLogin()) {
                relogin();//自动重登陆
            } else if (FingerIM.I.isConnected() && !FingerIM.I.isBannedAutoLogin()) {//连接未断
                FingerIM.I.onNetStateChange(true);
                mHandler.sendEmptyMessageDelayed(RELOGIN, MIN_SECENDS);
            }
        } else if (status == ENetStatus.LOGIN_SUCCESS) {
            mHandler.removeMessages(RELOGIN);
        }
        showNetStatus();
    }

    private void showNetStatus() {
        if (viewNetError == null) {
            return;
        }
        if (FingerIM.I.isConnected() &&
            (AppManager.getInstance().hasLogin() || FingerIM.I.isLogin())) {
            if (mHandler != null) {
                mHandler.removeMessages(RELOGIN);
            }
            viewNetError.setVisiable(false);
        } else {
            viewNetError.setVisiable(true);
            if (FingerIM.I.isConnected()) {
                viewNetError.updateHint(ContextHelper.getString(R.string.reconnecting));
            } else {
                viewNetError.updateHint(ContextHelper.getString(R.string.network_error));
            }
        }
    }

    public void loginOut() {
        if (mHandler != null) {
            mHandler.removeMessages(RELOGIN);
        }
    }

}
