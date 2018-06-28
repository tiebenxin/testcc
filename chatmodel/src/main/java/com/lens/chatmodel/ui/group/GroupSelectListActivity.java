package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.fingerchat.proto.message.Resp;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.ui.contacts.GroupsDetailActivity;
import com.lens.chatmodel.ui.contacts.UserAvatarAdapter;
import com.lens.chatmodel.utils.Cn2Spell;
import com.lens.chatmodel.utils.SortUtils;
import com.lens.chatmodel.view.QuickIndexBar;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.widget.HAvatarsRecyclerView;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/11.
 * 创建群聊 拉人 踢人
 */

public class GroupSelectListActivity extends BaseUserInfoActivity implements GroupSelectListener {

    private Intent intent;
    private FGToolbar toolbar;
    private HAvatarsRecyclerView mAvatarList;
    private LinearLayoutManager mLayouManager;
    private UserAvatarAdapter mUserAdapter;
    private EditText etSearch;
    private RecyclerView grouPrvContacts;
    private QuickIndexBar indexBar;
    private TextView groupLetter;
    private AdapterGroupSelectList adapterSelectList;
    //可选的好友列表
    private ArrayList<UserBean> friendUserBeans;
    private LinearLayoutManager layoutManager;
    //操作类型
    private int operation;
    //操作群id
    private String mucId;
    private Bundle bundle;
    private MucItem currentMucItem;

    @Override
    public void initView() {
        setContentView(R.layout.activity_group_select);
        toolbar = findViewById(R.id.viewTitleBar);
        initBackButton(toolbar, true);
        grouPrvContacts = findViewById(R.id.group_rvContacts);
        indexBar = findViewById(R.id.group_indexbar);
        indexBar
            .setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        groupLetter = findViewById(R.id.group_Letter);
        mAvatarList = findViewById(R.id.mAvatarList);
        mAvatarList.setHasFixedSize(true);
        mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAvatarList.setLayoutManager(mLayouManager);
        etSearch = findViewById(R.id.group_et_search);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        grouPrvContacts.setLayoutManager(layoutManager);
        adapterSelectList = new AdapterGroupSelectList(GroupSelectListActivity.this);
        adapterSelectList.setSelectListener(this);
        grouPrvContacts.setAdapter(adapterSelectList);
        indexBar.setOnLetterUpdateListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                showLetter(true, letter);
                //滑动到第一个对应字母开头的联系人
                for (int position = 0; position < adapterSelectList.getData().size(); position++) {
                    UserBean userBean = adapterSelectList.getData().get(position);
                    String c = userBean.getFirstChar() + "";
                    if (c.equalsIgnoreCase(letter)) {
                        scrollToPosition(position);
                        break;
                    }
                }
            }

            @Override
            public void onLetterCancel() {
                showLetter(false, "");
            }
        });
        etSearch.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN
                && TextUtils.isEmpty(etSearch.getText().toString())
                && adapterSelectList.getSelectUsers().size() > 0) {
                try {
                    adapterSelectList.getSelectUsers()
                        .remove(adapterSelectList.getSelectUsers().size() - 1);
                    adapterSelectList.notifyDataSetChanged();
                    showSelectedView(false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<UserBean> searchList = adapterSelectList
                    .searchContact(etSearch.getText().toString(), friendUserBeans);
                if (null != searchList) {
                    SortUtils.sortContacts(searchList);
                    adapterSelectList.setData(searchList);
                }
            }
        });
        intent = getIntent();
        //右侧确定按钮
        toolbar.setConfirmBt("确定(0)", (v) -> {
            if (null != adapterSelectList && adapterSelectList.getSelectUsers().size() <= 0) {
                T.show("请选择联系人");
                return;
            }
            //单选 转移群主
            if (Constant.GROUP_SELECT_MODE_CHANGE_ROLE == operation) {
                Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
                builder.setMucid(mucId).setOption(Muc.MOption.ChangeRole)
                    .addUserid(adapterSelectList.getSelectUsers().get(0).getUserId());
                MessageContext context = MessageContext.build(builder.build().toByteArray());
                context.setTimeout(5000)
                    .setRetryCount(4);
                FingerIM.I.sendMessageResult(Command.MUC_OPTION, context);
                showProgress("加载中...", true);
                return;
            }
            //card模式
            if (Constant.GROUP_SELECT_MODE_CARD == operation) {
                ArrayList<UserBean> selectUsers = adapterSelectList.getSelectUsers();
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constant.KEY_SELECT_USER,
                    selectUsers);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            //多选操作
            ArrayList<String> selectUserIds = new ArrayList<>();
            for (UserBean userBean : adapterSelectList.getSelectUsers()) {
                selectUserIds.add(userBean.getUserId());
            }
            switch (operation) {
                case Constant.GROUP_SELECT_MODE_CREATE:
                    Bundle bundle = new Bundle();
                    //添加组名称
                    bundle.putInt("operationType", SetOperationNameActivty.ADD_GROUP_NAME);
                    bundle.putStringArrayList("selectUserIds", selectUserIds);
                    toActivityForResult(SetOperationNameActivty.class, bundle,
                        RESULT_FIRST_USER);
                    break;
                case Constant.MODE_GROUP_CREATE://创建分组
                    ArrayList<UserBean> selectUsers = adapterSelectList.getSelectUsers();
                    intent = GroupsDetailActivity
                        .createNewGroupIntent(GroupSelectListActivity.this, selectUsers);
                    startActivity(intent);
                    finish();
                    break;
                case Constant.MODE_TRANSFOR_MSG:
                    ArrayList<UserBean> selectBeans = adapterSelectList.getSelectUsers();
                    if (selectBeans != null && selectBeans.size() > 0) {
                        Intent result = new Intent();
                        result.putExtra(Constant.KEY_SELECT_USER,
                            adapterSelectList.getSelectUsers());
                        setResult(RESULT_OK, result);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                    break;
                default:
                    //拉人 踢人
                    Muc.MOption opetion = Muc.MOption.Invite;
                    switch (operation) {
                        case Constant.GROUP_SELECT_MODE_ADD:
                            opetion = (Muc.MOption.Invite);
                            break;
                        case Constant.GROUP_SELECT_MODE_REMOVE:
                            opetion = Muc.MOption.Kick;
                            break;
                    }
                    showProgress("加载中...", true);
                    MucManager.getInstance().mucMberOperation(selectUserIds, opetion, mucId);
                    if (currentMucItem == null) {
                        currentMucItem = MucInfo
                            .selectMucInfoSingle(ContextHelper.getContext(), mucId);
                    }
                    if (opetion == MOption.Invite && ChatHelper
                        .isNeedConfirm(currentMucItem)) {//当前群开启了群邀请确认
                        setResult(RESULT_OK);
                        finish();
                    }
                    break;
            }
        });
    }


    private void showLetter(boolean isShow, String letter) {
        if (isShow) {
            groupLetter.setVisibility(View.VISIBLE);
            groupLetter.setText(letter);
        } else {
            groupLetter.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        bundle = intent.getExtras();
        operation = bundle.getInt(Constant.KEY_OPERATION);
        mucId = bundle.getString("mucId");
        currentMucItem = MucInfo.selectMucInfoSingle(ContextHelper.getContext(), mucId);
        adapterSelectList.setSelectMode(operation);
        friendUserBeans = new ArrayList<>();
        mUserAdapter = new UserAvatarAdapter(getApplicationContext(), friendUserBeans);
        mAvatarList.setAdapter(mUserAdapter);
        mAvatarList.setItemAnimator(new DefaultItemAnimator());
        switch (operation) {
            case Constant.GROUP_SELECT_MODE_CARD:
                toolbar.setTitleText("选择联系人");
                ArrayList<UserBean> selectUsers = bundle
                    .getParcelableArrayList(Constant.KEY_SELECT_USER);
                if (null == selectUsers) {
                    selectUsers = new ArrayList<>();
                }
                adapterSelectList.setSelectUsers(selectUsers);
                showSelectedView(false, true);
                bildUserBeans(false);
                break;
            case Constant.GROUP_SELECT_MODE_ADD:
                toolbar.setTitleText("邀请好友");
                //设置群成员
                ArrayList<String> groupUsers = bundle.getStringArrayList("groupUsers");
                adapterSelectList.setGroupUsers(groupUsers);
                bildUserBeans(false);
                break;
            case Constant.GROUP_SELECT_MODE_REMOVE:
                toolbar.setTitleText("踢出成员");
                bildUserBeans(true);
                break;
            case Constant.GROUP_SELECT_MODE_CREATE:
                toolbar.setTitleText("发起群聊");
                bildUserBeans(false);
                break;
            case Constant.GROUP_SELECT_MODE_CHANGE_ROLE:
                toolbar.setTitleText("选择新群主");
                bildUserBeans(true);
                break;
            case Constant.MODE_GROUP_CREATE:
                toolbar.setTitleText("选择联系人");
                List<IChatUser> createGroup = ProviderUser
                    .getAllNoGroupUser(getApplicationContext());
                if (createGroup != null) {
                    friendUserBeans = new ArrayList<>();
                    for (IChatUser chatUser : createGroup) {
                        UserBean userBean = (UserBean) chatUser;
                        userBean.setFirstChar(TextUtils.isEmpty(userBean.getFirstChar()) ? "#"
                            : (!StringUtils.matchAllLetter(userBean.getFirstChar()) ? "#"
                                : userBean.getFirstChar()));
                        friendUserBeans.add(userBean);
                    }
                    SortUtils.sortContacts(friendUserBeans);
                    adapterSelectList.setData(friendUserBeans);
                }
                break;
            case Constant.MODE_TRANSFOR_MSG:
                toolbar.setTitleText("选择联系人");
                bildUserBeans(false);
                break;

        }
    }

    /**
     * 构建列表显示数据
     */
    private void bildUserBeans(boolean isMucMembers) {
        if (isMucMembers) {
            //获取群成员
            List<Muc.MucMemberItem> memberItems = MucInfo
                .selectMucMemberItem(getApplicationContext(), mucId);
            if (null != memberItems && memberItems.size() > 0) {
                friendUserBeans = new ArrayList<>();
                for (Muc.MucMemberItem memberItem : memberItems) {
                    if (AppConfig.INSTANCE.get(AppConfig.ACCOUT).equals(memberItem.getUsername())) {
                        continue;
                    }
                    UserBean userBean = new UserBean();
                    userBean.setUserId(memberItem.getUsername());
                    userBean.setUserNick(TextUtils.isEmpty(memberItem.getMucusernick()) ? (
                        TextUtils.isEmpty(memberItem.getUsernick()) ? memberItem.getUsername()
                            : memberItem.getUsernick()) : memberItem.getMucusernick());
                    userBean.setAvatarUrl(memberItem.getAvatar());
                    userBean.setPinYin(
                        Cn2Spell.getInstance().getSelling(userBean.getUserNick()));
                    userBean.setFirstChar(TextUtils.isEmpty(userBean.getPinYin()) ? "#"
                        : (!StringUtils.matchAllLetter(userBean.getPinYin().substring(0, 1)) ? "#"
                            : userBean.getPinYin().substring(0, 1)));
                    friendUserBeans.add(userBean);
                }
                SortUtils.sortContacts(friendUserBeans);
                adapterSelectList.setData(friendUserBeans);
            }
        } else {
            List<UserBean> temp1 = ProviderUser.selectRosterAll(getApplicationContext());
            if (temp1 != null) {
                friendUserBeans = new ArrayList<>();
                for (UserBean userBean : temp1) {
                    userBean.setFirstChar(TextUtils.isEmpty(userBean.getFirstChar()) ? "#"
                        : (!StringUtils.matchAllLetter(userBean.getFirstChar()) ? "#"
                            : userBean.getFirstChar()));
                    friendUserBeans.add(userBean);
                }
                SortUtils.sortContacts(friendUserBeans);
                adapterSelectList.setData(friendUserBeans);
            }
        }
    }

    /**
     * 移动到指定的条目
     */
    public void scrollToPosition(int position) {
        if (position >= 0 && position < friendUserBeans.size()) {
            grouPrvContacts.stopScroll();
            int firstItem = layoutManager.findFirstVisibleItemPosition();
            int lastItem = layoutManager.findLastVisibleItemPosition();
            if (position <= firstItem) {
                grouPrvContacts.scrollToPosition(position);
            } else if (position <= lastItem) {
                int top = grouPrvContacts.getChildAt(position - firstItem).getTop();
                grouPrvContacts.scrollBy(0, top);
            } else {
                grouPrvContacts.scrollToPosition(position);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
            dismissProgress();
        }
    }

    private void dealWithEvent(IEventProduct event) {
        //踢人 邀请人message
        if (event != null && event instanceof MucActionMessageEvent) {
            MucActionMessage mucActionMessage = ((MucActionMessageEvent) event).getPacket();
            //是否为当前界面Action请求
            if ((Muc.MOption.Invite == mucActionMessage.action.getAction()
                || Muc.MOption.Kick == mucActionMessage.action.getAction())) {
                //邀请 踢出 刷新
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                EventBus.getDefault().post(MucRefreshEvent.
                    createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.GROUP_LIST_REFRESH));
                setResult(RESULT_OK);
                finish();
            } else if (Muc.MOption.ChangeRole == mucActionMessage.action.getAction()) {
                //群主转让 查询单个群信息
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                setResult(RESULT_OK);
                finish();
            }
        } else if (event != null && event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            Resp.Message msg = response.getPacket().response;
            T.show(MucHelper.getMucCodeResult(msg.getCode()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK) {
            finish();
        } else if (requestCode == 888 && resultCode == RESULT_OK) {
            if (null != data) {
                ArrayList<UserBean> selectUsers = adapterSelectList.getSelectUsers();
                UserBean userBean = data.getParcelableExtra("selectUser");
                if (!selectUsers.contains(userBean)) {
                    selectUsers.add(userBean);
                }
                Intent intent = new Intent();
                intent
                    .putParcelableArrayListExtra(Constant.KEY_SELECT_USER, selectUsers);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * 显示选中的成员
     */
    @Override
    public void showSelectedView(boolean isOneSelect, boolean isRemove) {
        if (isRemove && null == adapterSelectList.getSelectUsers()) {
            return;
        }
        mUserAdapter.setData(adapterSelectList.getSelectUsers());
        if (adapterSelectList.getSelectUsers().size() > 5) {
            mLayouManager.scrollToPosition(adapterSelectList.getSelectUsers().size() - 1);
        }
        if (adapterSelectList.getSelectUsers().size() == 0) {
            toolbar.setConfirmBt("确定");
        } else {
            toolbar.setConfirmBt("确定(" + adapterSelectList.getSelectUsers().size() + ")");
        }
    }

    /**
     * 选择一个群
     */
    @Override
    public void selectMucInfo() {
        startActivityForResult(
            new Intent(ContextHelper.getContext(), GroupSingerListActivity.class), 888);
    }

    @Override
    public void backPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
