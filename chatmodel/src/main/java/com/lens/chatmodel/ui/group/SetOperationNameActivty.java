package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.MucMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.fingerchat.proto.message.Resp;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucGroupMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.db.DBHelper;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.base.BaseUserInfoActivity;

import java.util.ArrayList;

import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/12.
 * 设置操作信息
 */

public class SetOperationNameActivty extends BaseUserInfoActivity {

    public static final int SET_GROUP_NAME = 0X11;
    public static final int SET_GROUP_NICKNAME = 0X12;
    public static final int ADD_GROUP_NAME = 0X13;
    private int operationType;
    private EditText etName;
    private FGToolbar fgToolbar;
    private TextView tv_aline;
    private Bundle bundle;
    private String mucId;

    private int MUC_SETOPERATION_CODE;
    private ArrayList<String> selectUserIds;

    @Override
    public void initView() {
        setContentView(R.layout.activity_operation_set_name);
        etName = findViewById(R.id.etName);
        fgToolbar = findViewById(R.id.viewTitleBar);
        initBackButton(fgToolbar, true);
        tv_aline = findViewById(R.id.tv_aline);
        fgToolbar.setConfirmBt("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etNameStr = etName.getText().toString().trim();
                if (TextUtils.isEmpty(etNameStr)) {
                    T.show("请输入内容");
                } else {
                    showProgress("加载中...", true);
                    createMucInfoOperation(etNameStr);
                }
            }
        });
        bundle = getIntent().getExtras();
        if (null != bundle.getString("content")) {
            etName.setText(bundle.getString("content"));
            etName.setSelection(etName.getText().toString().length());//将光标移至文字末尾
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mucId = bundle.getString("mucId");
        operationType = bundle.getInt("operationType", 0);
        switch (operationType) {
            //修改组名称
            case SET_GROUP_NAME:
                fgToolbar.setTitleText("修改群聊名称");
                tv_aline.setText("群名称");
                break;
            //修改组昵称
            case SET_GROUP_NICKNAME:
                fgToolbar.setTitleText("修改群聊昵称");
                tv_aline.setText("群昵称");
                break;
            //创建组
            case ADD_GROUP_NAME:
                fgToolbar.setTitleText("设置群聊名称");
                tv_aline.setText("群名称");
                break;
        }
    }

    private void createMucInfoOperation(String content) {
        switch (operationType) {
            //修改组名称
            case SET_GROUP_NAME:
                MUC_SETOPERATION_CODE = MucManager.getInstance().updateMucName(content, mucId);
                break;
            //修改组昵称
            case SET_GROUP_NICKNAME:
                Muc.PersonalConfig.Builder personalConfig = Muc.PersonalConfig.newBuilder();
                personalConfig.setMucusernick(content);
                MUC_SETOPERATION_CODE = MucManager.getInstance()
                    .updateMucConfig(personalConfig, Muc.UpdateOption.UUsernick, mucId);
                break;
            //创建组
            case ADD_GROUP_NAME:
                selectUserIds = bundle.getStringArrayList("selectUserIds");
                MUC_SETOPERATION_CODE = MucManager.getInstance().createMuc(selectUserIds, content);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dismissProgress();
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct eventProduct) {
        //群操作返回
        if (eventProduct != null && eventProduct instanceof MucActionMessageEvent) {
            MucActionMessage mucActionMessage = ((MucActionMessageEvent) eventProduct).getPacket();
            //群聊创建成功
            if (MOption.Create == mucActionMessage.action.getAction()) {
                //添加加入的成员 取群成员更新数据库
                updateMucData(mucActionMessage);
                //跳转到chat2
                startChat2Activity(mucActionMessage.action.getMucid(),
                    mucActionMessage.action.getMucname());
                //群聊列表刷新
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.GROUP_LIST_REFRESH));
                setResult(RESULT_OK);
                finish();
                //群聊名
            } else if (Muc.MOption.Invite == mucActionMessage.action.getAction()) {
                //跳转到chat2
                startChat2Activity(mucActionMessage.action.getMucid(),
                    mucActionMessage.action.getMucname());
                //群聊列表刷新
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.GROUP_LIST_REFRESH));
                setResult(RESULT_OK);
                finish();
                //群聊名
            } else if ((Muc.MOption.UpdateConfig == mucActionMessage.action.getAction()
                && Muc.UpdateOption.UName == mucActionMessage.action.getUpdateOption())) {
                //刷新群详情
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                finish();
                //群昵称
            } else if ((Muc.MOption.UpdateConfig == mucActionMessage.action.getAction()
                && Muc.UpdateOption.UUsernick == mucActionMessage.action.getUpdateOption())) {
                MucInfo.updateById(getApplicationContext(), mucActionMessage.action.getMucid(),
                    DBHelper.MUC_USERNICK,
                    mucActionMessage.action.getFrom().getMucusernick()
                );
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                finish();
            }
        } else if (eventProduct != null && eventProduct instanceof MucGroupMessageEvent) {
            MucMessage mucMessage = ((MucGroupMessageEvent) eventProduct).getPacket();
            //群聊中没有人 创建成功
            if (MUC_SETOPERATION_CODE == mucMessage.getSessionId()
                && Common.CREATE_OK == mucMessage.message.getCode()) {
                //跳转到chat2
                Muc.MucItem mucItem = mucMessage.message.getItem(0);
                startChat2Activity(mucItem.getMucid(), mucItem.getMucname());
                //查询用户所在的群
                MucManager.getInstance().getRequestBuilder().groupListRequestCode(
                    MucManager.getInstance().qRoomInfo(Muc.QueryType.QAllRoomsOfUser, null));
                setResult(RESULT_OK);
                finish();
            }
        } else if (eventProduct instanceof ResponseEvent) {
            ResponseEvent event = (ResponseEvent) eventProduct;
            Resp.Message message = event.getPacket().response;
            if (message.getCode() == Common.MUC_UPDATE_OK) {//群昵称更新成功
                MucInfo.updateById(getApplicationContext(), mucId, DBHelper.MUC_USERNICK,
                    etName.getText().toString());
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                Intent intent = new Intent();
                intent.putExtra("name",etName.getText());
                setResult(RESULT_OK,intent);
                finish();
            } else {
                T.show("修改群昵称失败");
            }

        }
    }

    private void startChat2Activity(String mucId, String mucName) {
        int topFlag = ProviderChat.getTopFlag(ContextHelper.getContext(), mucId);
        Intent intent = ChatActivity
            .createChatIntent(getApplicationContext(), mucId,
                mucName, ChatEnum.EChatType.GROUP.ordinal(),
                MucInfo.getMucChatBg(getApplicationContext(), mucId),
                MucInfo.getMucNoDisturb(getApplicationContext(), mucId), topFlag);
        startActivity(intent);
    }

    private void updateMucData(MucActionMessage mucActionMessage) {
        if (selectUserIds != null && selectUserIds.size() > 0) {
            int len = selectUserIds.size();
            List<MucMemberItem> memberItems = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                IChatUser user = ProviderUser
                    .selectRosterSingle(ContextHelper.getContext(), selectUserIds.get(i));
                if (user != null) {
                    memberItems.add(MucManager.createMemberItemFromUser(user));
                }
            }
            MucAction action = mucActionMessage.action;
            MucAction.Builder builder = action.toBuilder();
            builder.addAllUsernames(memberItems);
            action = builder.build();
            MucUser.insertMultipleGroupUser(ContextHelper.getContext(), memberItems,
                action.getMucid());//初始化群成员
            IChatRoomModel model = MessageManager.getInstance()
                .createActionMessage(action.getFrom().getUsername(),
                    action.getFrom().getMucusernick(),
                    action.getMucid(), action.getMucname(),
                    ChatHelper.getActionType(action),
                    MucHelper.getActionText(ContextHelper.getContext(), action),
                    action.getTime());
            MessageManager.getInstance()
                .saveMessage(model, "", action.getMucname(), EChatBgId.DEFAULT.id, false);
        }
    }
}
