package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Resp;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.ChatBgResBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.manager.RosterManager;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.db.DBHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/2/9.
 * 应用内图片选项
 */

public class ChatBgLocalSelActivity extends FGActivity implements
    AbstractRecyclerAdapter.OnItemClickListener {

    private FGToolbar toolbar;
    private RecyclerView chatBgRecyclerView;
    private AdapterChatBgLocal chatBgLocal;
    private String mucId;
    private int oldChatBg;
    private int chatBg_result_code;
    private ChatBgResBean selectChatBgBean;
    private int chatType;

    @Override
    public void initView() {
        setContentView(R.layout.activity_chatbg_local_select);
        toolbar = findViewById(R.id.viewTitleBar);
        chatBgRecyclerView = findViewById(R.id.chatbg_local_recycler);
        toolbar.setTitleText("选择背景图片");
        initBackButton(toolbar, true);
        // 每行显示的item项数目 垂直排列
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3,
            StaggeredGridLayoutManager.VERTICAL);
        chatBgRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mucId = getIntent().getStringExtra("mucId");
        chatType = getIntent().getIntExtra("chatType", 0);
        chatBgLocal = new AdapterChatBgLocal(ChatBgLocalSelActivity.this);
        chatBgLocal.setItemClickListener(this);
        if (chatType == EChatType.GROUP.ordinal()) {
            oldChatBg = MucInfo.getMucChatBg(getApplicationContext(), mucId);
        } else {
            oldChatBg = ProviderUser.getUserChatBg(mucId);
        }
        chatBgLocal.setSelectPosition(oldChatBg);
        chatBgRecyclerView.setAdapter(chatBgLocal);
    }

    @Override
    public void onItemClick(Object bean) {
        selectChatBgBean = (ChatBgResBean) bean;
        if (oldChatBg == selectChatBgBean.getBackId()) {
            return;
        } else {
            showProgress("加载中...", true);
            if (chatType == EChatType.GROUP.ordinal()) {
                Muc.PersonalConfig.Builder personalConfig = Muc.PersonalConfig.newBuilder();
                personalConfig.setChatBg(selectChatBgBean.getBackId() + "");
                chatBg_result_code = MucManager
                    .getInstance().updateMucConfig(personalConfig, Muc.UpdateOption.UChatBg, mucId);
            } else if (chatType == EChatType.PRIVATE.ordinal()) {
                RosterManager.getInstance()
                    .updateRoster(mucId, selectChatBgBean.getBackId(), RosterManager.CHAT_BG);
            }
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
        if (eventProduct != null && eventProduct instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) eventProduct;
            Resp.Message msg = response.getPacket().response;
            if (chatBg_result_code == response.getPacket().getSessionId()
                && Common.MUC_UPDATE_OK == msg.getCode()) {
                MucInfo.updateById(getApplicationContext(), mucId, DBHelper.CHATBG,
                    selectChatBgBean.getBackId() + "");
                ProviderChat
                    .updateBackGround(getApplicationContext(), mucId, selectChatBgBean.getBackId());
                MucManager.getInstance().refreshUI(ChatEnum.EActivityNum.CHAT.ordinal());
                Intent intent = new Intent();
                intent.putExtra("chat_bg", selectChatBgBean.getBackId());
                setResult(RESULT_OK, intent);
                finish();
            } else if (msg.getCode() == Common.UPDATE_ROSTER_SUCCESS) {
                ProviderUser.updateUserChatBg(mucId, selectChatBgBean.getBackId());
                Intent intent = new Intent();
                intent.putExtra("chat_bg", selectChatBgBean.getBackId());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
