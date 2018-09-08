package com.lens.chatmodel.ui.contacts;

import android.content.Intent;
import android.widget.ListView;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.INewFriendItemClickListener;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2018/3/7.
 */

public class ActivityNewFriends extends BaseActivity implements INewFriendItemClickListener,
    NewInviteDialog.removeInviteSuccessListener {

    private FGToolbar toolbar;
    private ListView mLvNewFriends;
    private List<IChatUser> newFriendList;//所有新的好友
    private List<IChatUser> unreadList;//新好友中未读的好友信息
    private NewFriendAdapter mAdapter;
    private IChatUser mAcceptUser;

    @Override
    public void initView() {
        setContentView(R.layout.activity_new_friends);
        toolbar = findViewById(R.id.toolbar);
        mLvNewFriends = findViewById(R.id.lv_new_friends);
        initAdapter();
        mLvNewFriends.setAdapter(mAdapter);
        initToolBar();
    }

    private void initToolBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText("新的朋友");
    }


    private void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewFriendAdapter(this);
        }
        newFriendList = ProviderUser.getAllNewFriends();
        mAdapter.setClickListener(this);
        mAdapter.setBeans(newFriendList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newFriendList = ProviderUser.getAllNewFriends();
        getUnReadFriend();
        if (newFriendList != null && newFriendList.size() > 0) {
            int len = newFriendList.size();
            for (int i = 0; i < len; i++) {
                IChatUser user = newFriendList.get(i);
                ProviderUser.updateHasReaded(user.getUserId(), ESureType.YES.ordinal());//更新为已读
            }
            clearUnrededList();
            notifyContactCountUpdate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearUnrededList();
    }

    private void clearUnrededList() {
        if (unreadList != null) {
            unreadList.clear();
        }
    }

    private List<IChatUser> getUnReadFriend() {
        if (newFriendList != null && newFriendList.size() > 0) {
            unreadList = new ArrayList<>();
            int len = newFriendList.size();
            for (int i = 0; i < len; i++) {
                IChatUser user = newFriendList.get(i);
                if (user.hasReaded() != ESureType.YES.ordinal()) {
                    unreadList.add(user);
                }
            }
            return unreadList;
        }
        return null;
    }

    @Override
    public void onAccept(IChatUser bean) {
        mAcceptUser = bean;
        FingerIM.I.inviteFriend(bean.getUserId());
    }

    @Override
    public void onClick(IChatUser bean) {
        Intent intent;
        if (bean.getRelationStatus() == ERelationStatus.FRIEND.ordinal()) {
            intent = FriendDetailActivity.createNormalIntent(this, (UserBean) bean);
        } else {
            intent = FriendDetailActivity.createNormalIntent(this, (UserBean) bean);
        }
        startActivity(intent);
    }

    @Override
    public void onLongClick(IChatUser bean, int position) {
        showDeleDialog(bean, position);
    }

    private void showDeleDialog(IChatUser bean, int position) {
        NewInviteDialog dialog = new NewInviteDialog(this, R.style.MyDialog, bean.getUserId(),
            position);
        dialog.seOnDeleteListener(this);
        dialog.show();
    }

    @Override
    public void onDelete(int posi) {
        if (null != mAdapter && newFriendList != null) {
            if (posi < newFriendList.size()) {
                IChatUser user = newFriendList.get(posi);
                ProviderUser.updateNewStatus(user.getUserId(), ESureType.NO.ordinal());
                mAdapter.removeItem(posi);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            if (message != null && message.message != null) {
                if (message.message.getCode() == Common.INVITE_OK) {
                    List<RosterItem> rosters = message.message.getItemList();
                    if (rosters != null && rosters.size() > 0) {
                        update(rosters);
                        initAdapter();
                        notifyContactCountUpdate();
                    }
                }
            }
        }
    }

    private void update(List<RosterItem> rosters) {
        List<UserBean> list = RosterManager.getInstance().createChatUserFromList(rosters,
            ERelationStatus.FRIEND);
        if (list != null) {
            int len = list.size();
            for (int i = 0; i < len; i++) {
                IChatUser user = list.get(i);
                if (mAcceptUser != null) {
                    ProviderUser
                        .updateFirendStatus(mAcceptUser.getUserId(),
                            ERelationStatus.FRIEND.ordinal());
                }
            }
        }
    }

    private void notifyContactCountUpdate() {
        RefreshEntity entity = new RefreshEntity();
        entity.setActivity(EActivityNum.MAIN.value);
        entity.setFragment(EFragmentNum.TAB_CONTACTS.value);
        RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MAIN_REFRESH, entity);
        EventBus.getDefault().post(event);
    }

}
