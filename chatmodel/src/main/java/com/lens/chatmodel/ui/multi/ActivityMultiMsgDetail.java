package com.lens.chatmodel.ui.multi;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.controller.multi.FactoryMultiCell;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;

/**
 * Created by LL130386 on 2018/2/1.
 * 点击查看合并转发消息
 */

public class ActivityMultiMsgDetail extends FGActivity implements IChatEventListener {

    private MultiMessageEntity entity;
    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_multi_msg_detail);
        toolbar = findViewById(R.id.viewTitleBar);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            entity = (MultiMessageEntity) intent.getExtras().get("data");
        }
        initToolBar();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        AdapterMultiList mAdapter = new AdapterMultiList(this);
        mAdapter.setViewFactory(new FactoryMultiCell(this, this));
        mAdapter.setEntity(entity);

        recyclerView.setAdapter(mAdapter);
    }

    private void initToolBar() {
        if (entity != null) {
            toolbar.setTitleText(entity.getTransitionTitle());
        } else {
            toolbar.setTitleText("转发详情");
        }
        initBackButton(toolbar, true);
    }

    @Override
    public void onEvent(ECellEventType type, Object o1, Object o2) {

    }
}
