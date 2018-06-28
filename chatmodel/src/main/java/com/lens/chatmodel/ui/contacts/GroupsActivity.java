package com.lens.chatmodel.ui.contacts;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.RosterGroupBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/27.
 * 分组好友列表
 */

public class GroupsActivity extends BaseUserInfoActivity {


    private RecyclerView mGroupsList;
    private LinearLayoutManager mLayouManager;
    private OnlyTextAdapter mAdapter;
    private FGToolbar toolbar;

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
        String account = getUserId();
        Collection<RosterGroupBean> rosterGroup = ProviderUser.getAllGroup(this);

        List<String> names = new ArrayList<>();
        for (RosterGroupBean group : rosterGroup) {
            L.i("GroupsActivity", "分组名称:" + group.getName());
            L.i("GroupsActivity", "好友数量:" + group.getUsers());

            if (group.getMemberCount() <= 0) {
                continue;
            }

            String name = group.getName() + "(" + group.getMemberCount() + ")";
            names.add(name);
            //  int count = RosterManager.getInstance().getGroupCount();
        }
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
                    L.i("GroupsActivity", "点击了哪个群:" + content);
                    int index = content.indexOf("(");
                    String groupName = content.substring(0, index);
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

}
