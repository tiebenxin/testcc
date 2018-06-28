package com.lens.chatmodel.ui.search;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lens.chatmodel.databinding.ActivityMoreRecordBinding;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/4/29.
 */

public class MoreRecordActivity extends BaseActivity {

    private ActivityMoreRecordBinding ui;
    private MoreRecordAdapter adapter;
    private List<SearchMessageBean> list;
    private String condition;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_more_record);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = getIntent();
        String chatId = intent.getStringExtra("search_userId");
        condition = intent.getStringExtra("search_condition");
        String nick = intent.getStringExtra("search_nick");
        ui.tvTitle.setText(getString(R.string.message_record_remain, condition));
        initToolBar(nick);
        initAdapter();
        loadData(condition, chatId);


    }

    private void initToolBar(String nick) {
        initBackButton(ui.viewTitleBar, true);
        ui.viewTitleBar.setTitleText(nick);
    }

    private void initAdapter() {
        ui.recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        ui.recyclerView.setLayoutManager(mLayouManager);
        ui.recyclerView.setItemAnimator(new DefaultItemAnimator());
        ui.recyclerView.addItemDecoration(new CustomDocaration(this,
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));
        list = new ArrayList<>();
        adapter = new MoreRecordAdapter(this,list);
        adapter.setCondition(condition);
        ui.recyclerView.setAdapter(adapter);
        adapter.setClickListener(new ISearchEventListener() {
            @Override
            public void clickItem(EResultType type, SearchMessageBean bean) {
                Intent intent = ChatActivity
                    .createUpdataChatIntent(MoreRecordActivity.this, bean.getUserId());
                startActivity(intent);
            }

            @Override
            public void clickMore(EResultType type, AllResult result) {

            }
        });
    }

    private void loadData(String condition, String chatId) {
        if (TextUtils.isEmpty(condition)) {
            return;
        }
        Observable.just(condition)
            .map(new Function<String, AllResult>() {
                @Override
                public AllResult apply(@NonNull String s) throws Exception {
                    return ProviderChat.selectMsgByContent(ContextHelper.getContext(), chatId, s);
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<AllResult>() {
                @Override
                public void accept(@NonNull AllResult allResult) throws Exception {
                    if (allResult == null) {
                        return;
                    }
                    adapter.setResult(allResult);

                }
            });

    }
}
