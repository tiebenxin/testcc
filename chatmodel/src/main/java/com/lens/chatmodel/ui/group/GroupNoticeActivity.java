package com.lens.chatmodel.ui.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.MucManager;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.T;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/17.
 * 群公告编辑页面
 */

public class GroupNoticeActivity extends BaseUserInfoActivity {

    private EditText noticeContent;
    private FGToolbar fgToolbar;
    private Bundle bundle;
    private String mucId;

    @Override
    public void initView() {
        setContentView(R.layout.activity_group_notice);
        noticeContent = findViewById(R.id.group_notice_et);
        fgToolbar = findViewById(R.id.viewTitleBar);
        fgToolbar.setTitleText("群公告");
        initBackButton(fgToolbar, true);
        bundle = getIntent().getExtras();
        mucId = bundle.getString("mucId");
        if (null != bundle.getString("content")) {
            noticeContent.setText(bundle.getString("content"));
            noticeContent.setSelection(bundle.getString("content").length());//将光标移至文字末尾
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        fgToolbar.setConfirmBt("确定", (view) -> {
            String etNameStr = noticeContent.getText().toString().trim();
            if (TextUtils.isEmpty(etNameStr)) {
                T.show("请输入群公告");
            } else {
                Muc.MucItem.Builder config = Muc.MucItem.newBuilder();
                Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
                config.setSubject(etNameStr).setMucid(mucId);
                builder.setConfig(config.build()).setMucid(mucId)
                    .setOption(Muc.MOption.UpdateConfig)
                    .setUpdateOption((Muc.UpdateOption.USubject));
                MessageContext context = MessageContext.build(builder.build().toByteArray());
                context.setTimeout(5000)
                    .setRetryCount(4);
                FingerIM.I.sendMessage(Command.MUC_OPTION, context);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct eventProduct) {
        //群操作返回
        if (eventProduct != null && eventProduct instanceof MucActionMessageEvent) {
            MucActionMessage mucActionMessage = ((MucActionMessageEvent) eventProduct).getPacket();
            if ((Muc.MOption.UpdateConfig == mucActionMessage.action.getAction()
                && Muc.UpdateOption.USubject == mucActionMessage.action.getUpdateOption())) {
//                MucManager.getInstance().refreshUI(ChatEnum.EActivityNum.ATALL.ordinal());
                //刷新
                EventBus.getDefault().post(MucRefreshEvent
                    .createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum.MUC_OPTION));
                finish();
            }
        }
    }

}