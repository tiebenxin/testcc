package com.lensim.fingerchat.fingerchat.ui.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.controller.ControllerNetError;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatItemClickListener;
import com.lens.chatmodel.interf.ISearchClickListener;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lens.chatmodel.ui.message.AdapterMessage;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.search.SearchActivity;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.dialog.NewMsgDialog;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IDialogItemClickListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
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
//    private int reLogin

    private RefreshListView listView;
    private AdapterMessage mAdapter;
    private List<RecentMessage> listData;

    private ControllerNetError viewNetError;
    private boolean isConnectOk;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RELOGIN) {
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
                relogin();
            }
        });

        listView = getView().findViewById(R.id.lv_list);
        mAdapter = new AdapterMessage(getContext());
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
            return;
        } else {
            if (FingerIM.I.hasRunning()) {//service是否在运行
                if (FingerIM.I.isConnected()) {//client是否在已经连接
                    if (TextUtils.isEmpty(userId)) {
                        userId = UserInfoRepository.getUserName();
                    }
                    if (TextUtils.isEmpty(psw)) {
                        psw = PasswordRespository.getPassword();
                    }
                    if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(psw)) {
                        if (isConnectOk) {
                            mHandler.removeMessages(RELOGIN);
                            FingerIM.I.login(userId, psw);
                        } else {
                            mHandler.sendEmptyMessage(RELOGIN);
                        }
                    }
                } else {
                    if (NetworkUtils.isNetAvaliale()) {
                        FingerIM.I.onNetStateChange(true);
                    }
                }
            } else {//service是否在运行
                if (NetworkUtils.isNetAvaliale()) {
                    FingerIM.I.startFingerIM();
                }
                mHandler.sendEmptyMessage(RELOGIN);
            }
        }
    }

    @Override
    public void notifyResumeData() {
        super.notifyResumeData();
        loadData();
        showNetStatus();
    }

    private void loadData() {
        List<RecentMessage> temp = ProviderChat.selectAllRecents(getContext());
        if (temp != null) {
            if (listData != null) {
                listData.clear();
                listData.addAll(temp);
            }
        }
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
            intent = ChatActivity
                .createChatIntent(getActivity(), message.getChatId(), message.getBackgroundId(),
                    message.getNotDisturb(), message.getTopFlag());
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

        if (mBean.getUnreadCount() != 0) {
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
                ProviderChat.markReaded(getActivity(), mBean.getChatId(), isUnread);
                onChatChanged();
                ((MainActivity) getActivity()).initUnreadCounts();
            }
        });
        dialog.show();
    }

    private void onChatChanged() {
        loadData();
    }

    public void notifyNetStatusChange(ENetStatus status) {
        if (status == null) {
            return;
        } else {
            if (status == ENetStatus.ERROR_NET || status == ENetStatus.ERROR_CONNECT) {
                isConnectOk = false;
                AppManager.getInstance().setLoginStatus(false);
            } else if (status == ENetStatus.SUCCESS_ON_SERVICE
                || status == ENetStatus.SUCCESS_ON_NET
                || status == ENetStatus.ERROR_LOGIN) {
                isConnectOk = true;
                if (FingerIM.I.isConnected()) {//连接未断
                    if (!FingerIM.I.isLoginConflicted()) {//先判断上次断开连接是不是因为登录冲突
                        relogin();//自动重登陆
                    }
                } else {
                    FingerIM.I.onNetStateChange(true);
                }
            }
        }
        if (viewNetError == null) {
            return;
        }

        showNetStatus();
    }

    private void showNetStatus() {
        if (viewNetError == null) {
            return;
        }
        if (FingerIM.I.isConnected()) {
            isConnectOk = true;
        }
        if (isConnectOk && AppManager.getInstance().hasLogin()) {
            viewNetError.setVisiable(false);
        } else {
            viewNetError.setVisiable(true);
            if (isConnectOk) {
                viewNetError.updateHint(ContextHelper.getString(R.string.reconnecting));
            } else {
                viewNetError.updateHint(ContextHelper.getString(R.string.network_error));
            }
        }
    }

}
