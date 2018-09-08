package com.lens.chatmodel.ui.contacts;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.view.SwitchButton;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2018/3/14.
 */

public class ActivityUserSetting extends BaseActivity {

    private View viewStarSetting;
    private FGToolbar toolbar;
    //    private String userId;
    UserBean userBean;
    private SwitchButton viewStarSwitch;

    @Override
    public void initView() {
        setContentView(R.layout.activity_user_setting);
        Intent intent = getIntent();
        userBean = intent.getParcelableExtra("user");

        toolbar = findViewById(R.id.toolbar);
        initToolBar();
        viewStarSetting = findViewById(R.id.viewStarSetting);
        TextView tv_star = viewStarSetting.findViewById(R.id.tv_title);
        tv_star.setText(ContextHelper.getString(R.string.set_for_star_friend));
        viewStarSwitch = viewStarSetting.findViewById(R.id.viewSwitch);
        viewStarSwitch.setChecked(userBean.isStar());
        viewStarSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBean == null || TextUtils.isEmpty(userBean.getUserId())) {
                    return;
                }
                if (viewStarSwitch.isChecked()) {
//                    System.out.println("是星标好友");
                    updateStarUser(ESureType.YES.ordinal());
                } else {
//                    System.out.println("非星标好友");
                    updateStarUser(ESureType.NO.ordinal());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message.response.getCode() == Common.UPDATE_ROSTER_SUCCESS) {//更新好友信息成功
                if (viewStarSwitch.isChecked()) {
                    ProviderUser.updateStarUser(userBean.getUserId(), ESureType.YES.ordinal());
                } else {
                    ProviderUser.updateStarUser(userBean.getUserId(), ESureType.NO.ordinal());
                }
            }
        }
    }


    private void initToolBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText("好友设置");
    }

    private void updateStarUser(int status) {
        if (userBean == null || TextUtils.isEmpty(userBean.getUserId())) {
            return;
        }
        RosterItem.Builder builder = RosterItem.newBuilder();
        builder.setUsername(userBean.getUserId());
        builder.setIsStar(status);
        RosterManager.getInstance()
            .updateRoster(userBean.getUserId(), status, RosterManager.STAR_USER);
    }
}
