package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.R;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.manager.MessageManager;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/23.
 * 群管理设置界面
 */

public class GroupManageSetActivity extends FGActivity implements View.OnClickListener {

    private CheckBox ckSetAuto;
    private FGToolbar toolbar;
    private String mucId;

    @Override
    public void initView() {
        setContentView(R.layout.activity_manage_set);
        toolbar = findViewById(R.id.viewTitleBar);
        ckSetAuto = findViewById(R.id.group_manage_set_auto);
        ckSetAuto.setOnClickListener(this);
        findViewById(R.id.group_manage_set_role).setOnClickListener(this);
        toolbar.setTitleText("群管理");
        initBackButton(toolbar, true);
        Bundle bundle = getIntent().getExtras();
        mucId = bundle.getString("mucId");
        ckSetAuto.setChecked(bundle.getBoolean("isAuto", false));
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.group_manage_set_auto) {
            ckSetAuto.setChecked(ckSetAuto.isChecked() ? true : false);
            setGroupAuto(ckSetAuto.isChecked());

        } else if (i == R.id.group_manage_set_role) {
            Bundle bundle = new Bundle();
            bundle.putString("mucId", mucId);
            bundle.putInt("operation", Constant.GROUP_SELECT_MODE_CHANGE_ROLE);
            toActivityForResult(GroupSelectListActivity.class, bundle, RESULT_FIRST_USER);

        }
    }

    private void setGroupAuto(boolean isCheck) {
        showProgress("正在处理中...", true);
        Muc.MucItem.Builder config = Muc.MucItem.newBuilder();
        config.setNeedConfirm(isCheck ? ChatEnum.ESureType.YES.value : ChatEnum.ESureType.NO.value)
            .setMucid(mucId);
        Muc.MucOption.Builder builder = Muc.MucOption.newBuilder();
        builder.setConfig(config.build()).setMucid(mucId).setOption(Muc.MOption.UpdateConfig)
            .setUpdateOption((Muc.UpdateOption.UAutoEnter));
        MessageContext context = MessageContext.build(builder.build().toByteArray());
        context.setTimeout(5000)
            .setRetryCount(4);
        FingerIM.I.sendMessage(Command.MUC_OPTION, context);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
            dismissProgress();
        }
    }

    private void dealWithEvent(IEventProduct eventProduct) {
        dismissProgress();
        //群操作返回
        if (eventProduct != null && eventProduct instanceof MucActionMessageEvent) {
            MucActionMessage msg = ((MucActionMessageEvent) eventProduct).getPacket();
            if (Muc.MOption.UpdateConfig == msg.action.getAction()
                && Muc.UpdateOption.UAutoEnter == msg.action.getUpdateOption()) {
                if (ckSetAuto.isChecked()) {
                    T.show("已开启");
                } else {
                    T.show("已关闭");
                }
                MessageManager.getInstance().setMessageChange(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
