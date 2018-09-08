package com.lens.chatmodel.ui.contacts;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.User;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.RosterGroupBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.databinding.ActivityGroupsDetailBinding;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.UIHelper;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LY309313 on 2017/2/27.
 */

public class GroupsDetailActivity extends BaseUserInfoActivity implements OnClickListener {

    private GroupsAdaper mAdapter;
    private List<String> users;
    private ArrayList<UserBean> rosterContactTempList;
    private ArrayList<UserBean> originList;
    private String account;
    private String groupName; //原来的名字

    private static final int GROUP_EDIT = 1;
    private static final int GROUP_CREATE = 2;

    private static final int CREATE = 1;
    private static final int ADD = 2;
    private static final int DELE = 3;
    private static final int UPDATE = 4;
    private static final int DESTROY = 5;


    private int optionType;
    private ActivityGroupsDetailBinding ui;
    private String currentName;
    private RosterGroupBean mRosterGroupBean;

    private int currentOption;
    private ArrayList<UserBean> selectUsers = new ArrayList<>();


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_groups_detail);
        initToorBar();
        UIHelper.setTextSize2(10, ui.tvGroupName, ui.tvMember);
        UIHelper.setTextSize2(12, ui.editGroupName, ui.btDeleteGroup);
    }

    private void initToorBar() {
        initBackButton(ui.toolbar, true);
        ui.toolbar.setTitleText("编辑分组");
        ui.toolbar.setConfirmBt(getString(R.string.save), new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    public static Intent createEditGroupIntent(Context context, String groupName) {
        Intent intent = new Intent(context, GroupsDetailActivity.class);
        intent.putExtra("edit_group_name", groupName);
        intent.putExtra("group_option_type", GROUP_EDIT);
        return intent;
    }

    public static Intent createNewGroupIntent(Context context, ArrayList<UserBean> users) {
        Intent intent = new Intent(context, GroupsDetailActivity.class);
        intent.putParcelableArrayListExtra("group_memebers", users);
        intent.putExtra("group_option_type", GROUP_CREATE);
        return intent;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (users == null) {
            users = new ArrayList<>();
        }
        //originList = new ArrayList<>();
        account = getUserId();
        optionType = getIntent().getIntExtra("group_option_type", 1);
        rosterContactTempList = new ArrayList<>();
        if (optionType == GROUP_EDIT) {
            groupName = getIntent().getStringExtra("edit_group_name");
            mRosterGroupBean = ProviderUser
                .getGroupByName(ContextHelper.getContext(), groupName);
            if (mRosterGroupBean != null) {
                rosterContactTempList.clear();
                rosterContactTempList.addAll(mRosterGroupBean.getUsers());
            }
            ui.editGroupName.setText(groupName);
        } else {
            currentOption = CREATE;
            rosterContactTempList = getIntent().getParcelableArrayListExtra("group_memebers");
            ui.btDeleteGroup.setVisibility(View.GONE);
        }
        originList = new ArrayList<>(rosterContactTempList);

        mAdapter = new GroupsAdaper(rosterContactTempList);
        ui.groupMemberlist.setAdapter(mAdapter);

        initListener();

    }

    public void initListener() {
        ui.groupMemberlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<UserBean> rosterEntries = mAdapter.getEntries();
                if (position == rosterEntries.size()) {
                    Intent intent = new Intent(GroupsDetailActivity.this,
                        GroupSelectListActivity.class);
                    intent.putExtra(Constant.KEY_OPERATION,
                        Constant.MODE_GROUP_ADD_MEMEBER);
                    if (currentOption == CREATE) {
                        intent.putParcelableArrayListExtra("group_member", rosterEntries);
                    }else {
                        intent.putParcelableArrayListExtra("alreadyin", rosterEntries);

                    }
                    startActivityForResult(intent, 0);
                } else if (position == (rosterEntries.size() + 1)) {
                    Intent intent = new Intent(GroupsDetailActivity.this,
                        GroupSelectListActivity.class);
                    intent.putExtra(Constant.KEY_OPERATION,
                        Constant.MODE_GROUP_DELE_MEMEBER);
                    intent.putParcelableArrayListExtra("group_member", rosterEntries);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(GroupsDetailActivity.this,
                        FriendDetailActivity.class);
                    intent
                        .putExtra(AppConfig.FRIEND_NAME, rosterEntries.get(position).getUserId());
                    startActivity(intent);
                }
            }
        });

        ui.btDeleteGroup.setOnClickListener(this);
    }


    private void destroyGroup() {
        currentOption = DESTROY;
        showProgress("稍等...", true);
        RosterManager.getInstance().deleGroup(groupName);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_delete_group) {// TODO: 2017/2/28 删除当前群组
            String name = ui.editGroupName.getText().toString();
            if (!name.equals(groupName)) {
                showToast("组名已经改变，无法删除");
            } else {
                final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
                builder.withTitle("删除分组!")
                    .withMessage("确定删除\"" + groupName + "\"分组吗?")
                    .withButton1Text("取消")
                    .withButton2Text("确定")
                    .setButton1Click(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    }).setButton2Click(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.dismiss();
                        destroyGroup();

                    }
                }).show();

            }

        }
    }

    private class GroupsAdaper extends BaseAdapter {


        private ArrayList<UserBean> entries;

        public GroupsAdaper(ArrayList<UserBean> entries) {
            this.entries = entries;
        }

        @Override
        public int getCount() {
            return entries.size() + 2;
        }

        @Override
        public IChatUser getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null) {
                convertView = View
                    .inflate(ContextHelper.getContext(), R.layout.item_group_member, null);
                holder = new GroupViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((GroupViewHolder) convertView.getTag());
            }
            UIHelper.setTextSize2(12, holder.tvName);
            if (position == entries.size()) {
                holder.tvName.setVisibility(View.INVISIBLE);
                holder.ivHead.setImageResource(R.drawable.group_of_add);
            } else if (position == (entries.size() + 1)) {
                holder.tvName.setVisibility(View.INVISIBLE);
                holder.ivHead.setImageResource(R.drawable.group_of_delete);
            } else {
                IChatUser entry = getItem(position);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(ChatHelper
                    .getUserRemarkName(entry.getRemarkName(), entry.getUserNick(),
                        entry.getUserId()));
                ImageHelper.loadAvatarPrivate(entry.getAvatarUrl(), holder.ivHead);
            }

            return convertView;
        }

        public ArrayList<UserBean> getEntries() {
            return entries;
        }

        public void setData(ArrayList<UserBean> results) {
            this.entries = results;
            notifyDataSetChanged();
        }

        public void addData(ArrayList<UserBean> list) {
            if (entries != null) {
                entries.addAll(list);
            }
            notifyDataSetChanged();
        }

        public void removeData(ArrayList<UserBean> list) {
            if (entries != null) {
                if (list != null && list.size() > 0) {
                    int len = list.size();
                    for (int i = 0; i < len; i++) {
                        UserBean bean = list.get(i);
                        if (entries.contains(bean)) {
                            entries.remove(bean);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        private class GroupViewHolder {

            private final ImageView ivHead;
            private final TextView tvName;

            public GroupViewHolder(View view) {
                ivHead = (ImageView) view.findViewById(R.id.iv_member_avatar);
                tvName = (TextView) view.findViewById(R.id.tv_memeber_name);
            }
        }
    }

    protected void confirm() {
        currentName = ui.editGroupName.getText().toString();
        if (TextUtils.isEmpty(currentName.trim())) {
            showToast("请输入组名");
            return;
        }

        ArrayList<UserBean> entries = mAdapter.getEntries();
        if (entries.isEmpty()) {
            showToast("请选择一个或多个联系人");
            return;
        }
        if (currentName.length() > 10) {
            showToast("组名不要大于十个字符");
            return;
        }
        if (StringUtils.isContainSpecailChar(currentName)) {
            T.show("分组名不能包含特殊字符");
            return;
        }
        showProgress("稍等...", true);
        if (optionType == GROUP_CREATE) {
            RosterManager.getInstance().addGroup(entries, currentName);
        } else {
            // 名称一致，需要找出删除了哪些人，然后又增加了哪些人
            // 与原始集合比对，在原始集合中存在就移除掉，不存在就定为新加入，而原始集合中存在，新集合不存在，就定位删除
            // 需要三个集合，原始集合  新加入集合   需要删除的人的集合
            if (currentOption == ADD) {
                if (selectUsers != null) {
                    RosterManager.getInstance().addGroup(selectUsers, currentName);
                }
            } else if (currentOption == DELE) {
                if (selectUsers != null) {
                    RosterManager.getInstance().deleGroup(selectUsers, currentName);
                }
            } else if (currentOption == DESTROY) {
                RosterManager.getInstance().destroyGroup(mRosterGroupBean.getUsers(), currentName);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                finish();
            }
        }, 300);

    }


    //创建分组
    private void createNewGroup(List<UserBean> users, String groupName) {
        List<String> userIds = null;
        if (users != null && users.size() > 0) {
            userIds = new ArrayList<>();
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean user = users.get(i);
                if (user != null && !TextUtils.isEmpty(user.getUserId())) {
                    userIds.add(user.getUserId());
                }
            }
        }
        if (userIds != null && userIds.size() > 0 && !TextUtils.isEmpty(groupName)) {
            currentOption = CREATE;
            RosterManager.getInstance().createAndUpdateGroup(userIds, groupName);
        }
    }

    //修改分组组名
    private void renameGroup(final String groupName, final List<UserBean> users) {
        List<String> userIds = null;
        if (users != null && users.size() > 0) {
            userIds = new ArrayList<>();
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean user = users.get(i);
                if (user != null && !TextUtils.isEmpty(user.getUserId())) {
                    userIds.add(user.getUserId());
                }
            }
        }
        if (userIds != null && userIds.size() > 0 && !TextUtils.isEmpty(groupName)) {
            currentOption = UPDATE;
            RosterManager.getInstance().createAndUpdateGroup(userIds, groupName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentName = ui.editGroupName.getText().toString();
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {//添加
                selectUsers = data
                    .getParcelableArrayListExtra(Constant.KEY_SELECT_USER);
                if (selectUsers != null) {
                    if (currentOption >= 0 && currentOption == CREATE) {
                        mAdapter.setData(selectUsers);
                    } else {
                        currentOption = ADD;
                        mAdapter.addData(selectUsers);
                    }
                }
            } else if (requestCode == 1) {//删除
                selectUsers = data
                    .getParcelableArrayListExtra(Constant.KEY_SELECT_USER);
                if (selectUsers != null) {
                    currentOption = DELE;
                    mAdapter.removeData(selectUsers);
                }
            }
        }

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
                        } else if (currentOption == CREATE) {
                            ProviderUser
                                .updateRosterGroup(ContextHelper.getContext(), item.getUsername(),
                                    currentName);
                        } else {
                            ProviderUser
                                .updateRosterGroup(ContextHelper.getContext(), item.getUsername(),
                                    currentName);

                        }
                    }
                    if (!TextUtils.isEmpty(currentName)) {
                        mRosterGroupBean = ProviderUser
                            .getGroupByName(ContextHelper.getContext(), currentName);
                    } else {
                        mRosterGroupBean = ProviderUser
                            .getGroupByName(ContextHelper.getContext(), groupName);
                    }
                    if (mRosterGroupBean != null) {
                        mAdapter.setData((ArrayList<UserBean>) mRosterGroupBean.getUsers());
                    }
                }
                dismissProgress();
                if (currentOption == CREATE || currentOption == UPDATE) {
                    finish();
                }
            } else if (message.message.getCode() == Common.UPDATE_ROSTER_FAILURE) {
                dismissProgress();
                T.show("操作失败");
            }
        } else if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message.response.getCode() == Common.ROSTER_GROUP_DELETE_SUCCESS) {
                if (mRosterGroupBean != null) {
                    List<UserBean> users = mRosterGroupBean.getUsers();
                    if (users != null) {
                        for (int i = 0; i < users.size(); i++) {
                            UserBean bean = users.get(i);
                            ProviderUser
                                .updateRosterGroup(ContextHelper.getContext(), bean.getUserId(),
                                    "");
                        }
                    }
                    mRosterGroupBean = ProviderUser
                        .getGroupByName(ContextHelper.getContext(), groupName);
                    if (mRosterGroupBean != null) {
                        mAdapter.setData((ArrayList<UserBean>) mRosterGroupBean.getUsers());
                    }
                }
                dismissProgress();
                if (currentOption == DESTROY) {
                    finish();
                }
            } else if (message.response.getCode() == Common.UPDATE_ROSTER_FAILURE) {
                dismissProgress();
                T.show("操作失败");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
    }
}
