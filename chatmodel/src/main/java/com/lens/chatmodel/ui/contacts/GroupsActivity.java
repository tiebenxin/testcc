package com.lens.chatmodel.ui.contacts;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LY309313 on 2017/2/27.
 * 分组好友列表
 */

public class GroupsActivity extends BaseUserInfoActivity {


    private RecyclerView mGroupsList;
    private LinearLayoutManager mLayouManager;
    private OnlyTextAdapter mAdapter;
    private FGToolbar toolbar;
    private List<String> names;

    @Override
    public void initView() {
        setContentView(R.layout.activity_groups);

        toolbar = findViewById(R.id.toolbar);
        initToolBar();
        mGroupsList = (RecyclerView) findViewById(R.id.groups_list);
    }

    private void initToolBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText(getString(R.string.friends_create_groups));
        toolbar.setConfirmBt("新建", new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        names = new ArrayList<>();
        loadGroupUsers();
        if (mAdapter == null) {
            mGroupsList.setHasFixedSize(false);
            mLayouManager = new LinearLayoutManager(this);
            mLayouManager.setOrientation(OrientationHelper.VERTICAL);
            mGroupsList.setLayoutManager(mLayouManager);

            mAdapter = new OnlyTextAdapter(this, names);
            mGroupsList.setAdapter(mAdapter);
            mGroupsList.setItemAnimator(new DefaultItemAnimator());
            mGroupsList.addItemDecoration(new CustomDocaration(this,
                LinearLayoutManager.HORIZONTAL, 1,
                ContextCompat.getColor(this, R.color.custom_divider_color)));

            mAdapter.setOnItemClickListener(new OnlyTextAdapter.OnItemClickListener() {
                @Override
                public void onclick(String content) {
                    int index = content.indexOf("(");
                    String groupName = content.substring(0, index).trim();
                    Intent intent = GroupsDetailActivity
                        .createEditGroupIntent(GroupsActivity.this, groupName);
                    startActivity(intent);
                }
            });
        } else {
            mAdapter.setData(names);
        }

    }

    protected void confirm() {
        Intent intent = new Intent(this, GroupSelectListActivity.class);
        intent.putExtra(Constant.KEY_OPERATION, Constant.MODE_GROUP_CREATE);
        startActivity(intent);
    }

    private void loadGroupUsers() {
        List<UserBean> users = ProviderUser.getAllGroups(this);
        if (users != null && users.size() > 0) {
            Map<String, Integer> gMap = new HashMap<>();
            int len = users.size();
            for (int i = 0; i < len; i++) {
                UserBean bean = users.get(i);
                List<String> groups = bean.getGroups();
                if (groups != null && groups.size() > 0) {
                    int length = groups.size();
                    for (int j = 0; j < length; j++) {
                        String name = groups.get(j);
                        if (!TextUtils.isEmpty(name)) {
                            if (gMap.containsKey(name)) {
                                int count = gMap.get(name);
                                gMap.put(name, ++count);
                            } else {
                                gMap.put(name, 1);
                            }
                        }
                    }
                }
            }
            if (gMap.size() > 0) {
                for (Map.Entry<String, Integer> entry : gMap.entrySet()) {
                    names.add(getGroupName(entry.getKey(), entry.getValue()));
                }
            }
        }
    }

    private String getGroupName(String groupName, int count) {
        return groupName + " (" + count + ") ";
    }

}
