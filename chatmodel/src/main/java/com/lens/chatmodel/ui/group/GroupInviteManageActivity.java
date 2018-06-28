package com.lens.chatmodel.ui.group;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MucManager;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;

import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/4/2.
 * 群聊邀请群主确认
 */

public class GroupInviteManageActivity extends FGActivity {

    private FGToolbar toolbar;
    private ImageView invite_img;
    private TextView invite_name;
    private TextView invite_count;
    private Button invite_sure;
    private String operationIds;
    private String msgId;
    private String mucId;
    private RecyclerView invite_recyclerview;
    private AdapterGroupInviteUser inviteUserAdapter;
    private ArrayList<String> inviteUserIds;
    private String from;

    @Override
    public void initView() {
        setContentView(R.layout.activity_group_invite);
        toolbar = findViewById(R.id.viewTitleBar);
        invite_img = findViewById(R.id.invite_img);
        invite_name = findViewById(R.id.invite_name);
        invite_count = findViewById(R.id.invite_count);
        invite_sure = findViewById(R.id.invite_sure);
        invite_recyclerview = findViewById(R.id.invite_recyclerview);
        toolbar.setTitleText("邀请详情");
        initBackButton(toolbar, true);
        msgId = getIntent().getStringExtra("msgId");
        operationIds = getIntent().getStringExtra("inviteId");

        // 每行显示的item项数目 垂直排列
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(5,
            StaggeredGridLayoutManager.VERTICAL);
        invite_recyclerview.setLayoutManager(layoutManager);

        ((SimpleItemAnimator) invite_recyclerview.getItemAnimator())
            .setSupportsChangeAnimations(false);
        inviteUserAdapter = new AdapterGroupInviteUser(getApplication());
        invite_recyclerview.setAdapter(inviteUserAdapter);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        String[] inviteIdArrays = operationIds.split(",");
        invite_count.setText("邀请" + inviteIdArrays.length + "位朋友进群");

        IChatRoomModel roomModel = ProviderChat.selectMsgSingle(getApplicationContext(), msgId);
        //群Id
        mucId = roomModel.getTo();
        //邀请人
        from = roomModel.getFrom();
        IChatUser fromBean = ProviderUser.selectRosterSingle(this, from);
        if (fromBean == null) {

        }
        ImageHelper.loadAvatarPrivate(fromBean.getAvatarUrl(), invite_img);
        //邀请人集合
        invite_name.setText(!TextUtils.isEmpty(fromBean.getUserNick()) ? fromBean.getUserNick()
            : fromBean.getUserId());
        List<UserBean> inviteBens = new ArrayList<>();
        inviteUserIds = new ArrayList<>();
        for (String intiteId : inviteIdArrays) {
            UserBean userBean = (UserBean) ProviderUser.selectRosterSingle(this, intiteId);
            if (null == userBean) {
                //TODO:本地没有从网上获取
                userBean = new UserBean();
                userBean.setUserId(intiteId);
                userBean.setType(1);
            }
            inviteBens.add(userBean);
            inviteUserIds.add(userBean.getUserId());
        }
        inviteUserAdapter.setData(inviteBens);

        invite_sure.setOnClickListener((view) -> {
            //未实现确认逻辑
            if (inviteUserIds.size() > 0) {
                MucManager.getInstance()
                    .confirmInvite(inviteUserIds, UserInfoRepository.getUserName(), from, mucId);
                finish();
            }
        });
    }
}
