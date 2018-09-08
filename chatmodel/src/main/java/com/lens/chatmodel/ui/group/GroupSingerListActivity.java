package com.lens.chatmodel.ui.group;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.SimpleItemAnimator;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.ui.group.AdapterSelectedGroupList.OnItemClickListener;
import com.lensim.fingerchat.components.widget.CustomDocaration;

/**
 * Created by xhdl0002 on 2018/1/18.
 * 单选群列表
 */

public class GroupSingerListActivity extends GroupListActivity implements
    OnItemClickListener {

    private AdapterSelectedGroupList adapterGroupList;

    @Override
    public void initView() {
        super.initView();
        //清空toolbar
        toolbar.setConfirmBt("");
        toolbar.initRightView(null);
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
        adapterGroupList = new AdapterSelectedGroupList(this, localMucItems);
        adapterGroupList.setListener(this);
        groupListRecyclerView.setAdapter(adapterGroupList);
        groupListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupListRecyclerView.addItemDecoration(new CustomDocaration(this,
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));
    }

    @Override
    public void onItemClick(int positioin) {
        Muc.MucItem mucItem = adapterGroupList.getData().get(positioin);
        UserBean groupUserBean = new UserBean(mucItem.getMucid(), mucItem.getMucname(),
            Constant.ROLE_GROUP_MODE);
        Intent intent = new Intent();
        intent.putExtra("selectUser", groupUserBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
