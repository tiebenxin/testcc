package com.lens.chatmodel.ui.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.fingerchat.proto.message.Muc.MucItem;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.ChatEnum.ETransforModel;
import com.lens.chatmodel.ChatEnum.ETransforType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.bean.transfor.BaseTransforEntity;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.IEventClickListener;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.ui.contacts.RecentTalkAdapter;
import com.lens.chatmodel.ui.contacts.UserAvatarAdapter;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import com.lensim.fingerchat.components.widget.HAvatarsRecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;


public class TransforMsgActivity extends BaseUserInfoActivity implements IEventClickListener,
    AckListener {

    private HAvatarsRecyclerView mAvatarList;
    private EditText mMemberSearch;
    private RecyclerView mRecentlyTalk;
    private LinearLayoutManager mLayouManager;
    private RecentTalkAdapter adapter;
    private UserAvatarAdapter mUserAdapter;
    private boolean isSingleMode;
    private List<UserBean> mInviteList = new ArrayList<>();

    private List<UserBean> contactList = new ArrayList<>();
    private String content;
    private int type;
    private int messagetype;
    private String user;
    private String accout;
    private int transforMode;
    private FGToolbar toolbar;
    private Context mContext;
    private List<IChatRoomModel> sendQuery;
    private String nick;
    private List<IChatRoomModel> chatModelList;
    private int chatType;
    private int backGroundId;
    private List<UserBean> selectBeans;
    private TransferDialog dialogTransfor;
    private Map<String, UserBean> sendUser;
    private String editMessage;//留言信息
    private TextView tv_success;


    public static Intent newMultiMultiIntent(Context context, ArrayList<String> msgids, String user,
        int mode, int chatType, int backgroundId) {
        Intent intent = new Intent(context, TransforMsgActivity.class);
        intent.putExtra("option_type", ETransforType.MULTI_MSG.ordinal());
        intent.putStringArrayListExtra("transfor_msgs", msgids);
        intent.putExtra("transfor_mode", mode);
        intent.putExtra("message_user", user);
        intent.putExtra("chat_type", chatType);
        intent.putExtra("backgroundId", backgroundId);
        return intent;
    }

    public static Intent newMultiSingleIntent(Context context, ArrayList<String> msgids,
        String user, int mode, int chatType) {
        Intent intent = new Intent(context, TransforMsgActivity.class);
        intent.putExtra("option_type", ETransforType.SINGLE_MSG.ordinal());
        intent.putStringArrayListExtra("transfor_msgs", msgids);
        intent.putExtra("transfor_mode", mode);
        intent.putExtra("message_user", user);
        intent.putExtra("chat_type", chatType);
        return intent;
    }

    public static Intent newBusinessCardIntent(Context context, String user, String nick,
        int chatType) {
        Intent intent = new Intent(context, TransforMsgActivity.class);
        intent.putExtra("option_type", ETransforType.CARD_MSG.ordinal());
        intent.putExtra("card_user", user);
        intent.putExtra("card_nick", nick);
        intent.putExtra("chat_type", chatType);
        return intent;
    }

    public static Intent newPureIntent(Context context, String content,
        int messageType, int chatType, String user) {
        Intent intent = new Intent(context, TransforMsgActivity.class);
        intent.putExtra("option_type", ETransforType.PURE_MSG.ordinal());
        intent.putExtra("transfor_msg", content);
        intent.putExtra("transfor_user", user);
        intent.putExtra("trasfor_msg_type", messageType);
        intent.putExtra("chat_type", chatType);
        return intent;
    }

    public static Intent newTransforIntent(Context context, String content, int messageType) {
        Intent intent = new Intent(context, TransforMsgActivity.class);
        intent.putExtra("option_type", ETransforType.PURE_MSG.ordinal());
        intent.putExtra("transfor_msg", content);
//        intent.putExtra("transfor_user", user);
        intent.putExtra("trasfor_msg_type", messageType);
//        intent.putExtra("chat_type", chatType);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ClientConfig.I.registerListener(AckListener.class, this);
        sendQuery = new ArrayList<>();
        sendUser = new HashMap<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogTransfor != null) {
            dialogTransfor = null;
        }
        ClientConfig.I.removeListener(AckListener.class, this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_createmuc);
        mContext = this;
        toolbar = findViewById(R.id.createmuc_toolbar);
        toolbar.setTitleText("选择");
        initBackButton(toolbar, true);
        toolbar.setConfirmBt("多选", (view) -> confirm());

        isSingleMode = true;
        mAvatarList = findViewById(R.id.mAvatarList);
        mMemberSearch = findViewById(R.id.mMemberSearch);
        mRecentlyTalk = findViewById(R.id.mRecentlyTalk);
        tv_success = findViewById(R.id.tv_success);

        mAvatarList.setHasFixedSize(true);
        mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mAvatarList.setLayoutManager(mLayouManager);
        mAvatarList.setCanDelete(true);

        mRecentlyTalk.setHasFixedSize(false);
        LinearLayoutManager mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        mRecentlyTalk.setLayoutManager(mLayouManager);
        mRecentlyTalk.setItemAnimator(new DefaultItemAnimator());
        mRecentlyTalk.addItemDecoration(new CustomDocaration(this,
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));

        initListener();
    }


    protected void confirm() {
        if (mInviteList.size() > 0) {
            showTransforDialog(mInviteList);
            return;
        }

        isSingleMode = !isSingleMode;
        if (isSingleMode) {
            toolbar.setConfirmBt("多选");
            adapter.changeMode(isSingleMode);
        } else {
            toolbar.setConfirmBt("单选");
            adapter.changeMode(isSingleMode);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        type = intent.getIntExtra("option_type", ETransforType.SINGLE_MSG.ordinal());
        chatType = intent.getIntExtra("chat_type", EChatType.PRIVATE.ordinal());
        accout = getUserId();
        if (type == ETransforType.CARD_MSG.ordinal()) {
            user = intent.getStringExtra("card_user");
            nick = intent.getStringExtra("card_nick");
        } else if (type != ETransforType.PURE_MSG.ordinal()) {
            ArrayList<String> multiMessageids = intent.getStringArrayListExtra("transfor_msgs");
            user = intent.getStringExtra("message_user");
            transforMode = intent
                .getIntExtra("transfor_mode", ETransforModel.MODE_ONE_BY_ONE.ordinal());
            chatModelList = MessageManager.getInstance()
                .getMessagesByIds(this, multiMessageids);
            backGroundId = intent.getIntExtra("backgroundId", 0);
        } else {
            user = intent.getStringExtra("transfor_user");
            content = intent.getStringExtra("transfor_msg");
            messagetype = intent.getIntExtra("trasfor_msg_type", MessageType.TEXT.getNumber());
        }

        Observable.just(1)
            .map(userBeen -> ProviderChat.selectRecentChatUser(ContextHelper.getContext()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.empty())
            .subscribe(users -> {
                if (type == ETransforType.CARD_MSG.ordinal()) {
                    for (UserBean bean : users) {
                        if (!ChatHelper.isGroupChat(bean.getChatType())) {
                            contactList.add(bean);
                        }
                    }
                } else {
                    contactList = users;
                }

                adapter = new RecentTalkAdapter(mContext, contactList);
                mRecentlyTalk.setAdapter(adapter);
                mUserAdapter = new UserAvatarAdapter(mContext, mInviteList);
                mAvatarList.setAdapter(mUserAdapter);
                mAvatarList.setItemAnimator(new DefaultItemAnimator());
                adapter.setOnItemClickListener(new RecentTalkAdapter.OnItemClickListener() {
                    @Override
                    public void onMoreContactClick() {
                        Intent intent = new Intent(TransforMsgActivity.this,
                            GroupSelectListActivity.class);
                        intent
                            .putExtra(Constant.KEY_OPERATION, getOptionType());
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onItemClick(View view, UserBean bean) {
                        RecentTalkAdapter.RcTalkViewHolder holder = (RecentTalkAdapter.RcTalkViewHolder) view
                            .getTag();
                        if (isSingleMode) {
                            List<UserBean> rosterContactTempList = new ArrayList<>();
                            rosterContactTempList.add(bean);
                            showTransforDialog(rosterContactTempList);
                        } else {
                            if (!mInviteList.contains(bean)) {
                                if (mInviteList.size() >= 9) {
                                    T.show("最多只能选择9位");
                                    return;
                                }
                                holder.setCheck(true);
                                mUserAdapter.setCanDelete(true);
                                mInviteList.add(bean);
                                L.d("添加", bean.getUserId());
                                mUserAdapter.notifyDataSetChanged();
                                if (mInviteList.size() > 5) {
                                    mLayouManager.scrollToPosition(mInviteList.size() - 1);
                                }
                            } else {
                                holder.setCheck(false);
                                mInviteList.remove(bean);
                                L.d("移除", bean.getUserId());
                                mUserAdapter.notifyDataSetChanged();

                            }
                            adapter.setSelected(mInviteList);
                            if (mInviteList.size() != 0) {
                                toolbar.setConfirmBt("确定(" + mInviteList.size() + ")");
                            } else {
                                toolbar.setConfirmBt("单选");
                            }
                        }
                    }
                });
            });
    }

    private int getOptionType() {
        if (type == ETransforType.CARD_MSG.ordinal()) {
            return Constant.ROLE_USER_MODE;
        } else {
            return Constant.MODE_TRANSFOR_MSG;
        }
    }

    private void showTransforDialog(List<UserBean> list) {
        dialogTransfor = new TransferDialog(mContext, list, this);
        dialogTransfor.setCarbonType(type);
        dialogTransfor.setUser(user);
        dialogTransfor.setCarbonMode(transforMode);
        dialogTransfor.setContent(content);
        dialogTransfor.setMessageType(messagetype);
        dialogTransfor.setMessageModels(chatModelList);
        dialogTransfor.show();
    }


    public void initListener() {
        mAvatarList.setListener(new HAvatarsRecyclerView.OnBackListener() {
            @Override
            public void onPreDel() {
                mUserAdapter.setCanDelete(true);
            }

            @Override
            public void onDel() {
                mUserAdapter.setCanDelete(false);
                if (mInviteList == null || mInviteList.size() == 0) {
                    return;
                }
                mInviteList.remove(mInviteList.size() - 1);
                mUserAdapter.notifyDataSetChanged();
                adapter.setSelected(mInviteList);
                adapter.notifyDataSetChanged();
                toolbar.setConfirmBt("确定(" + mInviteList.size() + ")");
            }

            @Override
            public void onDelCancel() {
                mUserAdapter.setCanDelete(false);
            }
        });
        mMemberSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSingleMode) {
                    if (s == null || s.toString().equals("")) {
                        mAvatarList.setCanDelete(true);
                    } else {
                        mAvatarList.setCanDelete(false);
                    }
                }

                if (s != null) {
                    adapter.setCondition(s.toString());
                    Observable.just(s.toString())
                        .map(new Function<String, List<UserBean>>() {
                            @Override
                            public List<UserBean> apply(@NonNull String s)
                                throws Exception {
                                List<UserBean> userlist = new ArrayList<>();

                                for (UserBean bean : contactList) {
                                    if (bean.getChatType() == EChatType.GROUP.ordinal()) {
                                        if (bean.getMucName() != null && bean.getMucName()
                                            .startsWith(s)) {
                                            userlist.add(bean);
                                        } else if (bean.getMucId() != null && bean.getMucId()
                                            .startsWith(s)) {
                                            userlist.add(bean);
                                        }
                                    } else {
                                        if (bean.getPinYin() != null && bean.getPinYin()
                                            .startsWith(s)) {
                                            userlist.add(bean);
                                        } else if (bean.getFirstChar() != null
                                            && bean.getFirstChar().startsWith(s)) {
                                            userlist.add(bean);
                                        } else if (bean.getUserId() != null
                                            && bean.getUserId().contains(s)) {
                                            userlist.add(bean);
                                        } else if (bean.getUserNick() != null
                                            && bean.getUserNick().contains(s)) {
                                            userlist.add(bean);
                                        } else if (bean.getRemarkName() != null
                                            && bean.getRemarkName().contains(s)) {
                                            userlist.add(bean);
                                        }
                                    }
                                }
                                return userlist;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<UserBean>>() {
                            @Override
                            public void accept(@NonNull List<UserBean> userBeens)
                                throws Exception {
                                adapter.setUserList(userBeens);
                            }
                        });
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            mAvatarList.delContact();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mInviteList = data.getParcelableArrayListExtra(Constant.KEY_SELECT_USER);
            showTransforDialog(mInviteList);
        }
    }

    private void dismissTransforDialog() {
        if (dialogTransfor != null) {
            dialogTransfor.dismiss();
        }
    }


    @Override
    public void onEvent(int event, Object o) {
        if (dialogTransfor != null) {
            editMessage = dialogTransfor.getEditMessage();
        }
        ETransforType type = ETransforType.fromInt(event);
        dismissTransforDialog();
        switch (type) {
            case SINGLE_MSG:
                // TODO: 2018/4/13  暂无应用场景

                break;
            case MULTI_MSG:
                if (transforMode >= 0) {
                    ETransforModel model = ETransforModel.fromInt(transforMode);
                    if (model != null) {
                        selectBeans = (List<UserBean>) o;
                        if (model == ETransforModel.MODE_ONE_BY_ONE) {
                            sendMessageByOne(selectBeans);
                        } else if (model == ETransforModel.MODE_ALL) {
                            sendMessageByAll(selectBeans);
                        } else {

                        }
                    }
                }
                break;
            case PURE_MSG:
                selectBeans = (List<UserBean>) o;
                if (selectBeans == null || selectBeans.size() <= 0) {
                    return;
                }
                sendMessageToUser(selectBeans);
                break;
            case CARD_MSG:
                selectBeans = (List<UserBean>) o;
                if (selectBeans != null && selectBeans.size() > 0) {
                    int len = selectBeans.size();
                    for (int i = 0; i < len; i++) {
                        UserBean bean = selectBeans.get(i);
                        IChatRoomModel message = MessageManager.getInstance()
                            .createCardMessage(user,
                                nick, bean, chatType);
                        sendQuery.add(message);
                        sendUser.put(message.getMsgId(), bean);
                    }
                    sendMessageList();
                }
                break;
        }
    }

    //多条消息逐一发送
    private void sendMessageByOne(List<UserBean> users) {
        if (chatModelList == null || chatModelList.isEmpty()) {
            return;
        }
        if (users == null || users.isEmpty()) {
            return;
        }
        int msgLen = chatModelList.size();
        int userLen = users.size();
        for (int i = 0; i < msgLen; i++) {
            IChatRoomModel msg = chatModelList.get(i);
            for (int j = 0; j < userLen; j++) {
                UserBean bean = users.get(j);
                IChatRoomModel model = MessageManager.getInstance()
                    .createTransforMessage(accout, bean.getUserId(),
                        MessageManager.getInstance().getMessageBodyJson(msg), getUserNick(),
                        getUserAvatar(), ChatHelper.isGroupChat(bean.getChatType()), false,
                        false, msg.getMsgType());
                sendQuery.add(model);
                sendUser.put(model.getMsgId(), bean);
            }
        }
        sendMessageList();


    }

    //多条消息合并发送
    private void sendMessageByAll(List<UserBean> users) {
        if (chatModelList == null || chatModelList.isEmpty()) {
            return;
        }

        if (users == null || users.isEmpty()) {
            return;
        }

        int msgLen = chatModelList.size();
        int userLen = users.size();
        MultiMessageEntity entity = new MultiMessageEntity();
        ArrayList<BaseTransforEntity> list = new ArrayList<>();
        String toUser = "";
        String fromUser = "";
        for (int i = 0; i < msgLen; i++) {
            IChatRoomModel msg = chatModelList.get(i);
            if (i == 0) {
                if (msg.isIncoming()) {
                    toUser = ChatHelper.getUserNick(msg.getNick(), msg.getTo());
                    fromUser = ChatHelper.getUserNick(getUserNick(), msg.getFrom());
                } else {
                    toUser = ProviderUser
                        .getRosterNick(ContextHelper.getContext(), msg.getTo());
                    fromUser = ChatHelper.getUserNick(getUserNick(), msg.getFrom());
                }
            }
            BaseTransforEntity bean = createTransforEntity(msg);
            if (bean != null) {
                list.add(bean);
            }
        }
        entity.setBody(list);
        entity.setType(chatType);
        entity.setSenderUserid(accout);
        entity.setSenderUserName(nick);
        if (chatType == EChatType.PRIVATE.ordinal()) {
            entity.setTransitionTitle(
                toUser + "与" + fromUser + "的聊天记录");
        } else {
            entity.setTransitionTitle("群聊的聊天记录");
        }

        for (int j = 0; j < userLen; j++) {
            UserBean bean = users.get(j);
            if (bean.getChatType() == EChatType.GROUP.ordinal()) {
                entity.setSenderAvatar(getUserAvatar());
                entity.setMucNickName(MucUser
                    .getMucUserNick(ContextHelper.getContext(), bean.getUserId(), getUserId()));
                entity.setGroupName(bean.getMucName());
            } else {
                entity.setSenderAvatar(getUserAvatar());
                entity.setMucNickName(getUserNick());
            }
            IChatRoomModel model = MessageManager.getInstance()
                .createTransforMessage(accout, bean.getUserId(), entity.toJson(entity),
                    getUserNick(), getUserAvatar(), ChatHelper.isGroupChat(bean.getChatType()),
                    false, false, EMessageType.MULTIPLE);
            sendQuery.add(model);
            sendUser.put(model.getMsgId(), bean);
        }
        sendMessageList();

    }

    private void sendMessageToUser(List<UserBean> beans) {
        for (UserBean resultBean : beans) {
            IChatRoomModel model = MessageManager.getInstance()
                .createTransforMessage(accout, resultBean.getUserId(), content,
                    getUserNick(), getUserAvatar(),
                    ChatHelper.isGroupChat(resultBean.getChatType()), false, false,
                    EMessageType.fromInt(messagetype));
            if (model == null) {
                continue;
            }
            RecentMessage recentMessage = ProviderChat
                .selectSingeRecent(ContextHelper.getContext(), model.getTo());
            if (recentMessage != null) {
                MessageManager.getInstance()
                    .saveMessage(model, resultBean.getAvatarUrl(), resultBean.getUserNick(),
                        recentMessage.getNotDisturb(), recentMessage.getBackgroundId(),
                        recentMessage.getTopFlag());
            } else {
                MessageManager.getInstance()
                    .saveMessage(model, resultBean.getAvatarUrl(), resultBean.getUserNick(),
                        ESureType.NO.ordinal(), backGroundId, ESureType.NO.ordinal());
            }
            MessageManager.getInstance().sendMessage(model);
            sendQuery.add(model);
            sendUser.put(model.getMsgId(), resultBean);
        }
    }

    private BaseTransforEntity createTransforEntity(IChatRoomModel model) {
        if (model == null) {
            return null;
        }
        return BaseTransforEntity.createEntity(model);
    }

    private void sendMessageList() {
        if (sendQuery != null && sendQuery.size() > 0) {
            int len = sendQuery.size();
            for (int i = 0; i < len; i++) {
                IChatRoomModel model = sendQuery.get(i);
                UserBean bean = sendUser.get(model.getMsgId());
                if (bean != null) {
                    if (model.isGroupChat()) {
                        MucItem item = MucInfo
                            .selectMucInfoSingle(ContextHelper.getContext(), model.getTo());
                        if (item != null) {
                            MessageManager.getInstance()
                                .saveMessage(model, "", item.getMucname(), backGroundId, false);
                        } else {
                            MessageManager.getInstance()
                                .saveMessage(model, "", "", backGroundId, false);
                        }
                    } else {
                        if (bean.getUserId().equalsIgnoreCase(model.getTo())) {
                            MessageManager.getInstance()
                                .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                                    backGroundId,
                                    false);
                        } else {
                            bean = MessageManager.getInstance().getCacheUserBean(model.getTo());
                            MessageManager.getInstance()
                                .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                                    backGroundId, false);
                        }
                    }
                } else {
                    bean = MessageManager.getInstance().getCacheUserBean(model.getTo());
                    MessageManager.getInstance()
                        .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(), backGroundId,
                            false);

                }
                MessageManager.getInstance().sendMessage(model);
            }
        }
    }

    @Override
    public void onAck(AckMessage message) {
        if (message != null) {
            if (sendUser != null && sendUser.size() > 0) {
                //成功逻辑
                String msgId = message.ack.getIdList().get(0);
                if (sendUser.containsKey(msgId)) {
                    doSendSuccess(msgId);
                }
            }
        }
    }

    private void doSendSuccess(String msgId) {
        Map<String, IChatRoomModel> sequenceMap = MessageManager.getInstance().getSequenceMap();
        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            System.out.println(
                "成功消息id:" + msgId + "--" + sequenceMap.get(msgId)
                    .getContent());
            ProviderChat.updateSendStatus(ContextHelper.getContext(),
                sequenceMap.get(msgId).getMsgId(), ESendType.MSG_SUCCESS);
            sequenceMap.remove(msgId);
            if (sendUser.containsKey(msgId)) {
                sendUser.remove(msgId);
            }
            if (sendUser.size() == 0) {
                if (!TextUtils.isEmpty(editMessage)) {
                    sendEditMessage();
                } else {
                    notifyChatUpdate();
                    startChatActivity();
                }
            }
        }
    }

    private void startChatActivity() {
        if (!TextUtils.isEmpty(user)) {
//            Intent intent = ChatActivity
//                .createUpdataChatIntent(this, user);
//            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        } /*else if (selectBeans != null && selectBeans.size() == 1) {
            UserBean user = selectBeans.get(0);
            Intent intent;
            if (user.getChatType() == EChatType.GROUP.ordinal()) {
                intent = ChatActivity
                    .createChatIntent(this, user.getUserId(), user.getMucName(), user.getChatType(),
                        user.getBgId(),
                        MucInfo.getMucNoDisturb(ContextHelper.getContext(), user.getMucId()),
                        ProviderChat.getTopFlag(ContextHelper.getContext(), user.getMucId()));
            } else {
                intent = ChatActivity
                    .createChatIntent(this, selectBeans.get(0));
            }
            startActivity(intent);
            finish();
        }*/ else if (selectBeans != null && selectBeans.size() > 1) {
            Intent intent = ActivitysRouter.getInstance()
                .invoke(this, ActivityPath.ACTIVITY_MAIN_PATH);
            intent.putExtra("page", 0);
            startActivity(intent);
            finish();
        } else {
            Intent intent = ActivitysRouter.getInstance()
                .invoke(this, ActivityPath.ACTIVITY_MAIN_PATH);
            intent.putExtra("page", 0);
            startActivity(intent);
            finish();
        }
    }

    public void notifyChatUpdate() {
        RefreshEntity entity = new RefreshEntity();
        entity.setActivity(EActivityNum.CHAT.ordinal());
        RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MAIN_REFRESH, entity);
        EventBus.getDefault().post(event);
    }


    private boolean isMapValid(Map<String, IChatRoomModel> map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }

    private void sendEditMessage() {
        int index = 0;
        if (type == ETransforType.CARD_MSG.ordinal()) {
            int len = 1;
            IChatRoomModel model = MessageManager.getInstance()
                .createTransforMessage(getUserId(), user, editMessage,
                    getUserNick(), getUserAvatar(),
                    ChatHelper.isGroupChat(chatType),
                    false, false, EMessageType.TEXT);
            UserBean bean;
            if (chatType == EChatType.GROUP.ordinal()) {
                bean = new UserBean();
                bean.setUserId(user);
                bean.setUserNick(nick);
                RecentMessage recentMessage = ProviderChat
                    .selectSingeRecent(ContextHelper.getContext(), model.getTo());
                if (recentMessage != null) {
                    MessageManager.getInstance()
                        .saveMessage(model, "", "",
                            recentMessage.getNotDisturb(), recentMessage.getBackgroundId(),
                            recentMessage.getTopFlag());
                } else {
                    MessageManager.getInstance()
                        .saveMessage(model, "", "",
                            ESureType.NO.ordinal(), backGroundId, ESureType.NO.ordinal());
                }
            } else {
                bean = (UserBean) ProviderUser
                    .selectRosterSingle(ContextHelper.getContext(), user);
                RecentMessage recentMessage = ProviderChat
                    .selectSingeRecent(ContextHelper.getContext(), model.getTo());
                if (recentMessage != null) {
                    MessageManager.getInstance()
                        .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                            recentMessage.getNotDisturb(), recentMessage.getBackgroundId(),
                            recentMessage.getTopFlag());
                } else {
                    MessageManager.getInstance()
                        .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                            ESureType.NO.ordinal(), backGroundId, ESureType.NO.ordinal());
                }

            }
            sendQuery.add(model);
            sendUser.put(model.getMsgId(), bean);

            if (len == 1) {
                clearEditMessage();
            } else {
                index--;
            }
            MessageManager.getInstance().sendMessage(model);
            if (index == 0) {
                clearEditMessage();
            }
        } else {
            if (selectBeans != null && selectBeans.size() > 0) {
                int len = selectBeans.size();
                index = len;
                for (int i = 0; i < len; i++) {
                    UserBean bean = selectBeans.get(i);
                    IChatRoomModel model = MessageManager.getInstance()
                        .createTransforMessage(getUserId(), bean.getUserId(), editMessage,
                            getUserNick(), getUserAvatar(),
                            ChatHelper.isGroupChat(bean.getChatType()),
                            false, false, EMessageType.TEXT);
                    sendQuery.add(model);
                    sendUser.put(model.getMsgId(), bean);
                    RecentMessage recentMessage = ProviderChat
                        .selectSingeRecent(ContextHelper.getContext(), model.getTo());
                    if (recentMessage != null) {
                        MessageManager.getInstance()
                            .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                                recentMessage.getNotDisturb(), recentMessage.getBackgroundId(),
                                recentMessage.getTopFlag());
                    } else {
                        MessageManager.getInstance()
                            .saveMessage(model, bean.getAvatarUrl(), bean.getUserNick(),
                                ESureType.NO.ordinal(), backGroundId, ESureType.NO.ordinal());
                    }
                    if (len == 1) {
                        clearEditMessage();
                    } else {
                        index--;
                    }
                    MessageManager.getInstance().sendMessage(model);
                }
                if (index == 0) {
                    clearEditMessage();
                }
            }
        }


    }

    private void clearEditMessage() {
        if (!TextUtils.isEmpty(editMessage)) {
            editMessage = "";
        }
        if (dialogTransfor != null) {
            dialogTransfor.clearEditMessage();
        }
    }
}
