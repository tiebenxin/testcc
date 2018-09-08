package com.lens.chatmodel.ui.group;

import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_CHANGE_MUC_NICK;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMemberMessage;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.fingerchat.proto.message.Resp;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucGroupMessageEvent;
import com.lens.chatmodel.eventbus.MucMemberMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/10.
 * 群操作
 */

public class GroupOperationActivity extends FGActivity implements GroupOperationListener {

    TextView groupOShowList;
    TextView groupOCount;
    TextView groupUserOGongGao;
    TextView groupOName;
    TextView groupONicheng;
    TextView groupOClear;
    Button itemIpAvatar;

    private CheckBox checkBoxDaRao;
    private CheckBox checkBoxZhiDing;
    private Button ipAvatar;
    private RelativeLayout viewGroupGongGao;
    private RelativeLayout viewGroupManage;
    private FGToolbar toolbar;
    private RecyclerView groupUserRecyclerView;
    private AdapterGroupUserList groupListAdpter;
    private boolean is_admin;
    private List<Muc.MucMemberItem> userBeans;
    //群id
    private String mucId;
    private String mucName;
    private Muc.MucItem mucItem;
    private int chatType;
    private View viewGroupName;
    private View view2Code;
    private View viewNickInGroup;
    private View groupBottonView;
    private boolean isGroupChat;
    private boolean isGroupEnable = false;//有效群聊，自己是群成员

    @Override
    public void initView() {
        setContentView(R.layout.activity_group_manager);
        toolbar = findViewById(R.id.viewTitleBar);
        initBackButton(toolbar, true);
        viewGroupName = findViewById(R.id.group_o_name_rl);
        viewGroupName.setOnClickListener(this);

        view2Code = findViewById(R.id.group_o_name_erweima);
        view2Code.setOnClickListener(this);

        findViewById(R.id.group_o_clear).setOnClickListener(this);
        viewNickInGroup = findViewById(R.id.group_o_nicheng_ll);
        viewNickInGroup.setOnClickListener(this);

        findViewById(R.id.group_o_bchat_ll).setOnClickListener(this);
        groupBottonView = findViewById(R.id.group_botton_view);
        groupOCount = findViewById(R.id.group_o_count);
        checkBoxDaRao = findViewById(R.id.group_o_darao);
        groupOClear = findViewById(R.id.group_o_clear);
        itemIpAvatar = findViewById(R.id.item_ip_avatar);
        viewGroupGongGao = findViewById(R.id.group_o_user_gonggao_rl);
        viewGroupGongGao.setOnClickListener(this);
        groupUserOGongGao = findViewById(R.id.group_user_o_gonggao);
        checkBoxDaRao.setOnClickListener(this);
        ipAvatar = findViewById(R.id.item_ip_avatar);
        ipAvatar.setOnClickListener(this);
        checkBoxZhiDing = findViewById(R.id.group_o_zhiding);
        checkBoxZhiDing.setOnClickListener(this);
        groupUserRecyclerView = findViewById(R.id.recyclerview);
        viewGroupManage = findViewById(R.id.group_o_manage_rl);
        viewGroupManage.setOnClickListener(this);
        groupOShowList = findViewById(R.id.group_o_show_list);
        groupONicheng = findViewById(R.id.group_o_nicheng);
        groupOName = findViewById(R.id.group_o_name);
        groupOShowList.setOnClickListener(this);
        //获取值
        Bundle bundle = getIntent().getExtras();
        mucId = bundle.getString("mucId");
        mucName = bundle.getString("mucName");
        chatType = bundle.getInt("chat_type", 1);
        isGroupChat = ChatHelper.isGroupChat(chatType);

        // 每行显示的item项数目 垂直排列
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(5,
            StaggeredGridLayoutManager.VERTICAL);
        groupUserRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) groupUserRecyclerView.getItemAnimator())
            .setSupportsChangeAnimations(false);
        groupListAdpter = new AdapterGroupUserList(getApplication(), is_admin);
        groupListAdpter.setOperationListener(this);
        groupUserRecyclerView.setAdapter(groupListAdpter);

        if (chatType == EChatType.GROUP.ordinal()) {
            toolbar.setTitleText(mucName);

            viewGroupName.setVisibility(View.VISIBLE);
            view2Code.setVisibility(View.VISIBLE);
            viewGroupGongGao.setVisibility(View.VISIBLE);
            viewGroupManage.setVisibility(is_admin ? View.VISIBLE : View.GONE);
            viewNickInGroup.setVisibility(View.VISIBLE);
            ipAvatar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitleText(mucName);

            groupBottonView.setVisibility(View.GONE);
            groupOCount.setVisibility(View.GONE);
            viewGroupName.setVisibility(View.GONE);
            view2Code.setVisibility(View.GONE);
            viewGroupGongGao.setVisibility(View.GONE);
            viewGroupManage.setVisibility(View.GONE);
            viewNickInGroup.setVisibility(View.GONE);
            ipAvatar.setVisibility(View.GONE);
        }
    }

    /**
     * 查询本地数据库数据
     */
    private void selectDbData() {
        mucItem = MucInfo.selectByMucId(getApplicationContext(), mucId);
        if (mucItem != null) {
            MucMemberItem item = MucUser
                .selectUserById(ContextHelper.getContext(), mucId, UserInfoRepository.getUserId());
            if (item == null) {
                isGroupEnable = true;
            }
        } else {
            isGroupEnable = true;
        }

        //数据库获取群成员信息
        List<Muc.MucMemberItem> members = MucUser
            .selectByGroupId(getApplicationContext(), mucId, is_admin ? 43 : 44);
        if (null != mucItem) {
            setViewInfo(mucItem, members);
            L.d("cj", "加载本地群组及群成员信息");
        }
    }


    //会话
    private RecentMessage recentMessage;

    @Override
    public void initData(Bundle savedInstanceState) {
        if (isGroupChat) {
            //从数据库取群组信息
            selectDbData();

            //读取会话数据
            recentMessage = ProviderChat
                .selectSingeRecent(getApplicationContext(), mucId);
            if (null != recentMessage) {
                checkBoxZhiDing.setChecked(
                    recentMessage.getTopFlag() == ChatEnum.ESureType.YES.ordinal());
                checkBoxDaRao.setChecked(
                    recentMessage.getNotDisturb() == ChatEnum.ESureType.YES.ordinal());
            } else {

            }
        } else {
            recentMessage = ProviderChat
                .selectSingeRecent(getApplicationContext(), mucId);
            if (null != recentMessage) {
                checkBoxZhiDing.setChecked(
                    recentMessage.getTopFlag() == ChatEnum.ESureType.YES.ordinal());
                checkBoxDaRao.setChecked(
                    recentMessage.getNotDisturb() == ChatEnum.ESureType.YES.ordinal());
            } else {
                int top = ProviderChat.getTopFlag(ContextHelper.getContext(), mucId);
                int disturb = ProviderChat.getNoDisturb(ContextHelper.getContext(), mucId);
                checkBoxZhiDing.setChecked(
                    top == ChatEnum.ESureType.YES.ordinal());
                checkBoxDaRao.setChecked(
                    disturb == ChatEnum.ESureType.YES.ordinal());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGroupChat && !isGroupEnable) {
            //查询单个群信息
            loadMucInfo();
        }
    }

    //操作群成员
    @Override
    public void operationGroupUser(boolean type) {
        if (type) {
            Bundle bundle = new Bundle();
            bundle.putString("mucId", mucId);
            bundle.putInt("operation", Constant.GROUP_SELECT_MODE_ADD);
            ArrayList<String> groupUsers = new ArrayList<>();
            for (Muc.MucMemberItem memberItem : groupListAdpter.getData()) {
                groupUsers.add(memberItem.getUsername());
            }
            bundle.putStringArrayList("groupUsers", groupUsers);
            toActivityForResult(GroupSelectListActivity.class, bundle,
                AppConfig.REQUEST_CHANGE_MEMBER);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("mucId", mucId);
            bundle.putInt("operation", Constant.GROUP_SELECT_MODE_REMOVE);
            toActivityForResult(GroupSelectListActivity.class, bundle,
                AppConfig.REQUEST_CHANGE_MEMBER);
        }
    }

    @Override
    public void toUserView(int position) {
        Muc.MucMemberItem memberItem = groupListAdpter.getData().get(position);
        Intent intent = FriendDetailActivity
            .createNormalIntent(getApplicationContext(), memberItem.getUsername());
        startActivity(intent);
    }

    private void setViewInfo(Muc.MucItem mucItem, List<Muc.MucMemberItem> members) {
        this.mucItem = mucItem;
        is_admin = ChatHelper.isMucOwer(mucItem, UserInfoRepository.getUserId());
        groupListAdpter.setIs_admin(is_admin);
        if (chatType == EChatType.GROUP.ordinal()) {
            viewGroupManage.setVisibility(is_admin ? View.VISIBLE : View.GONE);
        }
        groupOCount.setText("全部群成员(总数：" + mucItem.getMemberCount() + ")");
        groupOName.setText(mucItem.getMucname());
        groupONicheng.setText(mucItem.getPConfig().getMucusernick());
        groupUserOGongGao
            .setVisibility(TextUtils.isEmpty(mucItem.getSubject()) ? View.GONE : View.VISIBLE);
        groupUserOGongGao.setText(mucItem.getSubject());
        if (isGroupEnable) {
//            itemIpAvatar.setVisibility(View.GONE);
            itemIpAvatar.setText("删除群聊");
        } else {
            itemIpAvatar.setVisibility(View.VISIBLE);
            itemIpAvatar.setText(is_admin ? "解散群聊" : "删除并退出群聊");
        }
        checkBoxDaRao.setChecked(
            (ChatEnum.ESureType.NO.ordinal() == mucItem.getPConfig().getNoDisturb() ? false
                : true));
        if (null != members) {
            userBeans = members;
            setAdpaterList(mucItem.getMemberCount());
        }
    }

    /**
     * 刷新成员adapter
     */
    private void setAdpaterList(int memberCount) {
        if (memberCount > (is_admin ? 43 : 44)) {
            groupOShowList.setVisibility(View.VISIBLE);
        } else {
            groupOShowList.setVisibility(View.GONE);
        }
        groupListAdpter.setData(userBeans);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.group_o_show_list) {//查看更多
            groupOShowList.setVisibility(View.GONE);
            //本地查询更多
            List<Muc.MucMemberItem> members = MucInfo
                .selectMucMemberItem(getApplicationContext(), mucId);
            groupListAdpter.setData(members);
        } else if (i == R.id.group_o_clear) {//清空聊天记录
            final com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder msgBuilder = com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder
                .getInstance(GroupOperationActivity.this);
            msgBuilder.withTitle("提示")
                .withMessage("确认删除该聊天记录吗？")
                .withButton1Text("取消")
                .withButton2Text("清空")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgBuilder.dismiss();
                    }
                }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProviderChat.deleMessageChat(getApplicationContext(), mucId);
                    ProviderChat.clearRecentMessageById(ContextHelper.getContext(), mucId);
                    MucManager.getInstance()
                        .refreshUI(ChatEnum.EActivityNum.CHAT.ordinal());
                    msgBuilder.dismiss();
                }
            }).show();

        } else if (i == R.id.item_ip_avatar) {//退出群组
            if (isGroupEnable) {
                MucManager.getInstance().clearLocalMucInfo(mucId, true);
                finish();
                return;
            }
            final NiftyDialogBuilder builder = NiftyDialogBuilder
                .getInstance(this);
            builder.withTitle("提示")
                .withMessage(is_admin ? "确认解散该群吗？" : "确认退出群聊吗？")
                .withButton1Text("取消")
                .withButton2Text(is_admin ? "立即解散" : "立即退出")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.dismiss();
                    }
                }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createMucInfoOperation(false, false);
                    builder.dismiss();
                }
            }).show();

            //公告
        } else if (i == R.id.group_o_user_gonggao_rl) {
            if (!is_admin) {
                T.show("只能由群主更改群公告");
                return;
            }
            Bundle gonggao_bundle = new Bundle();
            gonggao_bundle.putString("mucId", mucId);
            gonggao_bundle.putString("content", mucItem.getSubject());
            toActivity(GroupNoticeActivity.class, gonggao_bundle);
            //打扰
        } else if (i == R.id.group_o_darao) {
            if (chatType == EChatType.GROUP.ordinal()) {
                createMucInfoOperation(checkBoxDaRao.isChecked(), true);
            } else {
                ProviderChat
                    .markNoDisturb(getApplicationContext(), mucId, checkBoxDaRao.isChecked());
            }
            //置顶
        } else if (i == R.id.group_o_zhiding) {
            checkBoxZhiDing.setChecked(checkBoxZhiDing.isChecked());
            ProviderChat.markTop(getApplicationContext(), mucId, !checkBoxZhiDing.isChecked());

        } else if (i == R.id.group_o_nicheng_ll) {//群昵称
            Bundle to_bundle = new Bundle();
            to_bundle.putInt("operationType", SetOperationNameActivty.SET_GROUP_NICKNAME);
            to_bundle.putString("mucId", mucId);
            to_bundle.putString("content", groupONicheng.getText().toString());
            toActivity(SetOperationNameActivty.class, to_bundle);
        } else if (i == R.id.group_o_name_rl) {//群名
            if (is_admin) {
                Bundle group_bundle = new Bundle();
                group_bundle.putInt("operationType", SetOperationNameActivty.SET_GROUP_NAME);
                group_bundle.putString("mucId", mucId);
                group_bundle.putString("content", groupOName.getText().toString());
                toActivityForResult(SetOperationNameActivty.class, group_bundle,
                    REQUEST_CHANGE_MUC_NICK);
            } else {
                T.show("只能由群主更改名称");
            }
        } else if (i == R.id.group_o_name_erweima) {
            AppManager.getInstance().initMucAcode(mucId);
            ShowErWeiMaDialog erWeiMaDialog = new ShowErWeiMaDialog(
                GroupOperationActivity.this, mucId, mucName);
            erWeiMaDialog.show();
        } else if (i == R.id.group_o_manage_rl) {//管理
            Bundle bundle = new Bundle();
            bundle.putString("mucId", mucId);
            bundle.putBoolean("isAuto",
                (ChatEnum.ESureType.NO.ordinal() == mucItem.getNeedConfirm() ? false : true));
            toActivityForResult(GroupManageSetActivity.class, bundle, AppConfig.REQUEST_MANAGE_MUC);
        } else if (i == R.id.group_o_bchat_ll) {//设置聊天背景
            Intent intent = new Intent(getApplicationContext(), ChatBgListActivity.class);
            intent.putExtra("mucId", mucId);
            intent.putExtra("chatType", chatType);
            startActivityForResult(intent, AppConfig.REQUEST_CHANGE_CONFIG);
        }
    }

    private void createMucInfoOperation(boolean uNoDisturb, boolean isDisturb) {
        //免打扰
        if (isDisturb) {
            Muc.PersonalConfig.Builder personalConfig = Muc.PersonalConfig.newBuilder();
            personalConfig.setNoDisturb(
                uNoDisturb ? ChatEnum.ESureType.YES.ordinal() : ChatEnum.ESureType.NO.ordinal());
            MucManager.getInstance().getRequestBuilder().uNoDisturbCode(MucManager.getInstance()
                .updateMucConfig(personalConfig, Muc.UpdateOption.UNoDisturb, mucId));
            return;
        }
        //退出群组
        MucManager.getInstance().operationMuc(is_admin, mucId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        //本地数据刷新
        if (event instanceof MucRefreshEvent) {
            MucRefreshEvent refreshEvent = ((MucRefreshEvent) event);
            if (MucRefreshEvent.MucRefreshEnum.MUC_OPTION.value == refreshEvent.getType()) {
                selectDbData();
//                loadMucInfo();
            }
            //群列表返回
        } else if (event instanceof MucGroupMessageEvent) {
            MucMessage mucMessage = ((MucGroupMessageEvent) event).getPacket();
            if (null != mucMessage && mucMessage.getSessionId() == MucManager.getInstance()
                .getRequestBuilder().build().getQueryOneRoomCode()
                && Common.MUC_QUERY_ROOM_BY_ID_OK == mucMessage.message.getCode()) {
//                //更新 群配置 到数据库中
                MucInfo
                    .updateMucInfo(getApplicationContext(), mucId, mucMessage.message.getItem(0),
                        UserInfoRepository.getUserId());
                setViewInfo(mucMessage.message.getItem(0), null);
            }
            //查询所有群成员
        } else if (event instanceof MucMemberMessageEvent) {
            MucMemberMessage mucMessage = ((MucMemberMessageEvent) event).getmPacket();
            if (null != mucMessage
                && MucManager.getInstance().getRequestBuilder().build().getQueryRoomUserCode()
                == mucMessage.getSessionId()
                && Common.MUC_QUERY_All_MEMBER_IN_ROOM_OK == mucMessage.message
                .getCode()) {
                groupListAdpter.setData(mucMessage.message.getItemList());
            }
        } else if (event != null && event instanceof MucActionMessageEvent) {
            MucActionMessage mucActionMessage = ((MucActionMessageEvent) event).getPacket();
            if (Muc.MOption.Leave == mucActionMessage.action.getAction()
                || Muc.MOption.Destory == mucActionMessage.action.getAction()) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (event != null && event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            Resp.Message msg = response.getPacket().response;
            if (response.getPacket().getSessionId() == MucManager.getInstance().getRequestBuilder()
                .build().getuNoDisturbCode()
                ) {
                //T.show("免打扰设置");
                checkBoxDaRao.setChecked(checkBoxDaRao.isChecked());
                MucInfo.updateNoDisturb(mucId, checkBoxDaRao.isChecked());
                ProviderChat
                    .markNoDisturb(getApplicationContext(), mucId, checkBoxDaRao.isChecked());
            } else {
                T.show(MucHelper.getMucCodeResult(msg.getCode()));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.REQUEST_CHANGE_CONFIG) {
                setResult(RESULT_OK, data);
                finish();
//                MessageManager.getInstance().setMessageChange(true);
            } else if (requestCode == AppConfig.REQUEST_CHANGE_MEMBER) {
//                setResult(RESULT_OK, data);
//                finish();
                MessageManager.getInstance().setMessageChange(true);

            } else if (requestCode == AppConfig.REQUEST_MANAGE_MUC) {
//                setResult(RESULT_OK, data);
//                finish();
                MessageManager.getInstance().setMessageChange(true);

            } else if (requestCode == AppConfig.REQUEST_CHANGE_MUC_NICK) {//修改群昵称
                String mucNick;
                if (data.getExtras() != null){
                    SpannableString spannableString = (SpannableString) data.getExtras().get("name");
                    mucNick = spannableString.toString();
                }else {
                    mucNick = data.getStringExtra("name");
                }
                if (!TextUtils.isEmpty(mucNick)) {
                    groupONicheng.setText(mucNick);
                    toolbar.setTitleText(mucNick);
                }
            }
        } else {
            if (requestCode == AppConfig.REQUEST_CHANGE_MEMBER) {
//                setResult(RESULT_CANCELED, data);
//                finish();
            }
        }
    }

    private void loadMucInfo() {
        MucManager.getInstance().getRequestBuilder()
            .queryOneRoomCode(
                MucManager.getInstance().qRoomInfo(Muc.QueryType.QRoomById, mucId));
    }
}