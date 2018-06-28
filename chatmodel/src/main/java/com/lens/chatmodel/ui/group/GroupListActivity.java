package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Resp;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.eventbus.MucRefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.MucHelper;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xhdl0002 on 2018/1/18.
 * 群列表
 */

public class GroupListActivity extends FGActivity implements AdapterGroupList.OnItemClick {

    protected FGToolbar toolbar;
    private RecyclerView groupListRecyclerView;
    protected AdapterGroupList adapterGroupList;

    private List<Muc.MucItem> localMucItems;

    @Override
    public void initView() {
        setContentView(R.layout.activty_group_list);
        toolbar = findViewById(R.id.viewTitleBar);
        groupListRecyclerView = findViewById(R.id.group_list);
        toolbar.setTitleText("群聊列表");
        initBackButton(toolbar, true);
        toolbar.initRightView(createButton());
        toolbar.setConfirmListener(() -> {
            //创建群聊
            Bundle bundle = new Bundle();
            bundle.putInt("operation", Constant.GROUP_SELECT_MODE_CREATE);
            toActivity(GroupSelectListActivity.class, bundle);
        });
    }

    public ImageView createButton() {
        ImageView button = new ImageView(this);
        button.setImageResource(R.drawable.title_add);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        int d = DensityUtil.dip2px(ContextHelper.getContext(), 1);
        params.setMargins(0, 0, d, 0);
        button.setLayoutParams(params);
        button.setPadding(d, d / 2, d, d / 2);
        return button;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        LinearLayoutManager mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        groupListRecyclerView.setLayoutManager(mLayouManager);
        ((SimpleItemAnimator) groupListRecyclerView.getItemAnimator())
            .setSupportsChangeAnimations(false);
        //本地查询
        localMucItems = MucInfo.selectAllMucInfo(getApplicationContext());
//        if 本地群组列表为null 则发起请求服务器列表？
//        if (null == localMucItems || localMucItems.size() == 0) {
        //查询用户所在的群
        MucManager.getInstance().getRequestBuilder().groupListRequestCode(
            MucManager.getInstance().qRoomInfo(Muc.QueryType.QAllRoomsOfUser, null));
//        }
        adapterGroupList = new AdapterGroupList(this, localMucItems);
        adapterGroupList.setListener(this);
        groupListRecyclerView.setAdapter(adapterGroupList);
        groupListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupListRecyclerView.addItemDecoration(new CustomDocaration(this,
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));
    }

    @Override
    public void onitemClick(int positioin) {
        startActivityChat(adapterGroupList.getData().get(positioin));
    }

    //z注意最后参数为聊天背景id
    private void startActivityChat(Muc.MucItem item) {
        int topFlag = ProviderChat.getTopFlag(ContextHelper.getContext(), item.getMucid());
        Intent intent = ChatActivity.createChatIntent(this, item.getMucid(), item.getMucname(),
            EChatType.GROUP.ordinal(),
            MucInfo.getMucChatBg(getApplicationContext(), item.getMucid()),
            MucInfo.getMucNoDisturb(getApplicationContext(), item.getMucid()), topFlag);
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof MucRefreshEvent) {
            MucRefreshEvent refreshEvent = ((MucRefreshEvent) event);
            if (MucRefreshEvent.MucRefreshEnum.GROUP_LIST_REFRESH.value == refreshEvent.getType()) {
                //本地刷新群列表
                localMucItems = MucInfo.selectAllMucInfo(getApplicationContext());
                if (null != localMucItems) {
                    adapterGroupList.setData(localMucItems, false);
                }
            }
        } else if (event != null && event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            Resp.Message msg = response.getPacket().response;
            T.show(MucHelper.getMucCodeResult(msg.getCode()));
        }
    }
}
