package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.R;
import com.lens.chatmodel.databinding.ActivityCreateGroupBinding;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.view.TagCloudView.OnTagClickListener;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2018/7/2.
 */

public class CreateGroupActivity extends BaseActivity {

    private static final int ADD = 1;
    private static final int DELE = 2;
    private static final int UPDATE = 3;

    private int currentOption;


    private ActivityCreateGroupBinding uiBinding;
    private String userId;
    private String groupName;
    private String currentName;
    private ArrayList<String> groupList;
    private List<String> groupDeleList;
    private List<String> allGroupNames;

    public static Intent newIntent(Context context, String userId, ArrayList<String> labels) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        intent.putExtra("userId", userId);
        intent.putStringArrayListExtra("group", labels);
        return intent;
    }

    @Override
    public void initView() {
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_group);
        uiBinding.toolbar.setTitleText("添加分组");
        initBackButton(uiBinding.toolbar, true);
        uiBinding.toolbar.setConfirmBt("确定", new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentName = uiBinding.GroupsTop.getText().toString().trim();
                if (!TextUtils.isEmpty(userId)) {
                    if (!TextUtils.isEmpty(currentName)) {
                        groupList.add(currentName);
                        ProviderUser.updateRosterGroup(ContextHelper.getContext(), userId,
                            StringUtils.getStringByList(groupList));
                    } else {
                        ProviderUser.updateRosterGroup(ContextHelper.getContext(), userId,
                            StringUtils.getStringByList(groupList));
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("group", groupList);
                intent.putExtra("userjid", userId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        userId = getIntent().getStringExtra("userId");
//        groupName = getIntent().getStringExtra("group");
        groupList = getIntent().getStringArrayListExtra("group");

        groupDeleList = new ArrayList<>();
        //        groupList = new ArrayList<>();
//        if (!TextUtils.isEmpty(groupName)) {
//            groupList.add(groupName);
//        }
        uiBinding.GroupsTop.setTags(groupList);

        allGroupNames = ProviderUser.getAllGroupNames(ContextHelper.getContext());
        if (allGroupNames != null) {
            uiBinding.GroupsAll.setTags(allGroupNames);
        }

        uiBinding.GroupsTop.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (groupDeleList != null) {
                    String name = groupList.remove(position);
//                    currentName = "";
                    if (!groupDeleList.contains(name)) {
                        groupDeleList.add(name);
                    }
                    uiBinding.GroupsTop.setTags(groupList);
                }
            }
        });

        uiBinding.GroupsAll.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (groupList != null) {
                    currentName = allGroupNames.get(position);
                    String name = allGroupNames.get(position);
                    if (groupList.contains(name)) {
                        groupList.remove(name);
                        if (!groupDeleList.contains(name)) {
                            groupDeleList.add(name);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uiBinding.GroupsTop.setTags(groupList);
                            }
                        }, 100);
                    } else {
                        groupList.add(name);
                        if (groupDeleList.contains(name)) {
                            groupDeleList.remove(name);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uiBinding.GroupsTop.setTags(groupList);
                            }
                        }, 100);
                    }
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
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            if (message.message.getCode() == Common.ROSTER_GROUP_UPDATE_SUCCESS) {//更新好友信息成功
                List<RosterItem> rosters = message.message.getItemList();
                if (rosters != null && rosters.size() > 0) {
                    int len = rosters.size();
                    for (int i = 0; i < len; i++) {
                        RosterItem item = rosters.get(i);
                        if (currentOption == DELE) {
                            ProviderUser
                                .updateRosterGroup(ContextHelper.getContext(), item.getUsername(),
                                    "");
                        } else {
                            ProviderUser
                                .updateRosterGroup(ContextHelper.getContext(), item.getUsername(),
                                    currentName);
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("group", currentName);
                intent.putExtra("userjid", userId);
                setResult(RESULT_OK, intent);
                finish();

            } else if (message.message.getCode() == Common.UPDATE_ROSTER_FAILURE) {
                T.show("操作失败");
            }
        } else if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message.response.getCode() == Common.UPDATE_ROSTER_FAILURE) {
                T.show("操作失败");
            }
        }
    }
}
