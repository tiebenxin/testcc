package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingerchat.api.message.RespMessage;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.RosterGroupBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.databinding.ActivityGroupsDetailBinding;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.UIHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

/**
 * Created by LY309313 on 2017/2/27.
 */

public class GroupsDetailActivity extends BaseUserInfoActivity implements OnClickListener {

    // private RosterGroup rosterGroup;


    private GroupsAdaper mAdapter;
    private List<String> users;
    private ArrayList<UserBean> rosterContactTempList;
    private ArrayList<UserBean> originList;
    private String account;
    private String groupName; //原来的名字

    private static final int GROUP_EDIT = 1;
    private static final int GROUP_CREATE = 2;
    private int optionType;
    private ActivityGroupsDetailBinding ui;
    private String currentName;

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
            List<UserBean> contacts = ProviderUser.selectRosterAll(ContextHelper.getContext());

            for (IChatUser contact : contacts) {
                if (contact.getGroup().contains(groupName)) {
                    rosterContactTempList.add((UserBean) contact);
                }
            }

            ui.editGroupName.setText(groupName);
        } else {
            rosterContactTempList = getIntent().getParcelableArrayListExtra("group_memebers");
            ui.btDeleteGroup.setVisibility(View.GONE);
        }
        originList = new ArrayList<>(rosterContactTempList);

        // List<RosterEntry> entries =  rosterGroup.getEntries();
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
                    intent.putParcelableArrayListExtra("alreadyin", rosterEntries);
                    startActivityForResult(intent, 0);
                } else if (position == (rosterEntries.size() + 1)) {
                    Intent intent = new Intent(GroupsDetailActivity.this,
                        GroupSelectListActivity.class);
                    intent.putExtra(Constant.KEY_OPERATION,
                        Constant.MODE_GROUP_DELE_MEMEBER);
                    intent.putParcelableArrayListExtra("alreadyin", rosterEntries);
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


    private void removeGroup() {
        showProgress("稍等...", true);
        Observable.just(1)
            .map(new Function<Integer, Boolean>() {

                @Override
                public Boolean apply(@NonNull Integer integer) throws Exception {
                    try {
//                        return RosterManager.getInstance()
//                            .removeGroup(account, groupName, originList);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    // return null;
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean bool) throws Exception {
                    dismissProgress();
                    finish();
                }
            });
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
//                        removeGroup();
                        T.show("暂不支持");

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

                String name = entry.getUserNick();
                if (TextUtils.isEmpty(name)) {
                    name = entry.getUserId();
                }
                holder.tvName.setText(name);

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
        if (TextUtils.isEmpty(currentName)) {
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
        if (optionType == GROUP_CREATE) {
            showProgress("稍等...", true);
            createNewGroup(entries, currentName);
        } else {
            // 名称一致，需要找出删除了哪些人，然后又增加了哪些人
            // 与原始集合比对，在原始集合中存在就移除掉，不存在就定为新加入，而原始集合中存在，新集合不存在，就定位删除
            // 需要三个集合，原始集合  新加入集合   需要删除的人的集合
            ArrayList<IChatUser> newEntries = new ArrayList<>();

            for (IChatUser bean : entries) {
                if (!originList.contains(bean)) {
                    newEntries.add(bean);
                }
            }
            originList.removeAll(entries);
            showProgress("稍等...", true);
            if (currentName.equals(groupName)) {
                Observable.just(newEntries)
                    .map(new Function<ArrayList<IChatUser>, Boolean>() {

                        @Override
                        public Boolean apply(@NonNull ArrayList<IChatUser> userBeen)
                            throws Exception {
                            try {
                                //add Roster

                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                            // return null;
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean bool) throws Exception {
                            dismissProgress();
                            finish();
                        }
                    });
            } else {
                if (newEntries.isEmpty() && originList.isEmpty()) {
                    //改名字
                    renameGroup(currentName, entries);
                } else {
                    //创建新的
                    createNewGroup(entries, currentName);
                }

            }

        }


    }


    //创建分组
    private void createNewGroup(List<UserBean> users, String groupName) {
//        if (users != null && users.size() > 0) {
//            int len = users.size();
//            for (int i = 0; i < len; i++) {
//                UserBean user = users.get(i);
//                if (user != null) {
//                    RosterItem.Builder builder = RosterItem.newBuilder();
//                    builder.setUsername(user.getUserId());
//                    builder.setGroup(groupName);
//                    FingerIM.I.updateFriendInfo(user.getUserId(), builder.build());
//                }
//            }
//        }
    }

    //修改分组组名
    private void renameGroup(final String groupName, final List<UserBean> users) {
//        if (users != null && users.size() > 0) {
//            int len = users.size();
//            for (int i = 0; i < len; i++) {
//                UserBean user = users.get(i);
//                if (user != null) {
//                    RosterItem.Builder builder = RosterItem.newBuilder();
//                    builder.setUsername(user.getUserId());
//                    builder.setGroup(groupName);
//                    FingerIM.I.updateFriendInfo(user.getUserId(), builder.build());
//                }
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {//添加
                ArrayList<UserBean> results = data
                    .getParcelableArrayListExtra("invite_list");
                mAdapter.setData(results);
            } else if (requestCode == 1) {//删除
                ArrayList<UserBean> results = data
                    .getParcelableArrayListExtra("invite_list");
                mAdapter.setData(results);
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
        if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message.response.getCode() == Common.UPDATE_INFO_SUCCESS) {//更新好友信息成功
                ArrayList<UserBean> users = mAdapter.getEntries();
                if (users != null) {
                    int len = users.size();
                    for (int i = 0; i < len; i++) {
                        IChatUser user = users.get(i);
                        ProviderUser
                            .updateRosterGroup(ContextHelper.getContext(), user.getUserId(),
                                currentName);
                    }
                    List<RosterGroupBean> list = ProviderUser
                        .getAllGroup(ContextHelper.getContext());
                    System.out.println("所有分组：" + list.size());
                    dismissProgress();
                }


            }
        }
    }
}
