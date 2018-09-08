package com.lens.chatmodel.ui.search;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lens.chatmodel.databinding.ActivitySearchBinding;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/3/5.
 */

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding ui;
    private int searchType;
    private String condition;
    private List<SearchMessageBean> beanList;
    private LinearLayoutManager mLayouManager;
    private MoreRecordAdapter mMoreRecordAdapter;
    private List<AllResult> results;
    private SearchBaseAdapter mResultAdapter;
    private Disposable subscription;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_search);
        initBackButton(ui.toolbar, true);
        searchType = EResultType.DEFUALT.ordinal();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Intent intent = getIntent();
        searchType = intent.getIntExtra("search_type", 0);
        if (searchType != EResultType.DEFUALT.ordinal()) {
            AllResult result = intent.getParcelableExtra("search_result");
            condition = intent.getStringExtra("search_condition");
            ui.etSearch.setText(condition);
            EResultType type = EResultType.fromInt(searchType);
            switch (type) {
                case CONTACT:
                    ui.etSearch.setHint("搜索联系人");
                    break;
                case MUC:
                    ui.etSearch.setHint("搜索群聊");
                    break;
                case RECORD:
                    ui.etSearch.setHint("搜索消息记录");
                    break;
            }
            beanList = result.getResults();
            ui.recyclerview.setHasFixedSize(true);
            mLayouManager = new LinearLayoutManager(this);
            mLayouManager.setOrientation(OrientationHelper.VERTICAL);
            ui.recyclerview.setLayoutManager(mLayouManager);

            mMoreRecordAdapter = new MoreRecordAdapter(this, beanList);
            mMoreRecordAdapter.setType(type);
            mMoreRecordAdapter.setCondition(condition);
            ui.recyclerview.setAdapter(mMoreRecordAdapter);
            ui.recyclerview.setItemAnimator(new DefaultItemAnimator());
            ui.recyclerview.addItemDecoration(new CustomDocaration(this,
                LinearLayoutManager.HORIZONTAL, 1,
                ContextCompat.getColor(this, R.color.custom_divider_color)));
        } else {
            results = new ArrayList<>();
            ui.recyclerview.setHasFixedSize(true);
            mLayouManager = new LinearLayoutManager(this);
            mLayouManager.setOrientation(OrientationHelper.VERTICAL);
            ui.recyclerview.setLayoutManager(mLayouManager);

            mResultAdapter = new SearchBaseAdapter(this, results);
            ui.recyclerview.setAdapter(mResultAdapter);
            ui.recyclerview.setItemAnimator(new DefaultItemAnimator());
            ui.recyclerview.addItemDecoration(new CustomDocaration(this,
                LinearLayoutManager.HORIZONTAL, 10,
                ContextCompat.getColor(this, R.color.custom_divider_color)));
        }

        initListener();
    }

    private void initListener() {
        ui.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || TextUtils.isEmpty(s.toString())) {
                    if (searchType == EResultType.DEFUALT.ordinal()) {
                        results.clear();
                        mResultAdapter.setResults(results);
                        mResultAdapter.notifyDataSetChanged();
                    } else {
                        mMoreRecordAdapter.clear();
                    }
                } else {
                    condition = s.toString();
                    if (searchType == EResultType.DEFUALT.ordinal()) {
                        mResultAdapter.setCondition(s.toString());
                        filterAll(s);
                    } else {
                        mMoreRecordAdapter.setCondition(s.toString());
                        filterByType(s);
                    }
                }
            }
        });
        if (mResultAdapter != null) {
            mResultAdapter.setClickListener(new ISearchEventListener() {
                @Override
                public void clickItem(EResultType type, SearchMessageBean bean) {
                    switch (type) {
                        case CONTACT:
                            Intent intentContact = FriendDetailActivity
                                .createNormalIntent(SearchActivity.this, bean.getUserId());
                            startActivity(intentContact);
                            finish();
                            break;
                        case MUC:
                            Intent intentMuc = ChatActivity
                                .createUpdataChatIntent(SearchActivity.this, bean.getUserId());
                            startActivity(intentMuc);
                            finish();

                            break;
                        case RECORD:
                            Intent intent = new Intent(SearchActivity.this,
                                MoreRecordActivity.class);
                            intent.putExtra("search_userId", bean.getUserId());
                            intent.putExtra("search_condition", condition);
                            intent.putExtra("search_nick", bean.getNick());
                            startActivity(intent);
                            finish();
                            break;
                    }
                }

                @Override
                public void clickMore(EResultType type, AllResult result) {
                    switch (type) {
                        case CONTACT:
                            Intent intentContact = new Intent(SearchActivity.this,
                                SearchActivity.class);
                            intentContact.putExtra("search_type", type.ordinal());
                            intentContact.putExtra("search_condition", condition);
                            intentContact.putExtra("search_result", result);
                            startActivity(intentContact);
                            break;
                        case MUC:
                            Intent intentMuc = new Intent(SearchActivity.this,
                                SearchActivity.class);
                            intentMuc.putExtra("search_type", type.ordinal());
                            intentMuc.putExtra("search_condition", condition);
                            intentMuc.putExtra("search_result", result);
                            startActivity(intentMuc);
                            break;
                        case RECORD:
                            Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                            intent.putExtra("search_type", type.ordinal());
                            intent.putExtra("search_condition", condition);
                            intent.putExtra("search_result", result);
                            startActivity(intent);
                            break;
                    }
                }
            });
        }

        if (mMoreRecordAdapter != null) {
            mMoreRecordAdapter.setClickListener(new ISearchEventListener() {
                @Override
                public void clickItem(EResultType type, SearchMessageBean bean) {
                    switch (type) {
                        case CONTACT:
                            Intent intentContact = FriendDetailActivity
                                .createNormalIntent(SearchActivity.this, bean.getUserId());
                            startActivity(intentContact);
                            finish();
                            break;
                        case MUC:
                            Intent intentMuc = ChatActivity
                                .createUpdataChatIntent(SearchActivity.this, bean.getUserId());
                            startActivity(intentMuc);
                            break;
                        case RECORD:
                            Intent intent = ChatActivity
                                .createUpdataChatIntent(SearchActivity.this, bean.getUserId());
                            startActivity(intent);
                            break;
                    }

                }

                @Override
                public void clickMore(EResultType type, AllResult result) {

                }
            });
        }
    }

    private void filterByType(Editable s) {
        String value = s.toString();
        subscription = Observable.just(value)
            .map(new Function<String, AllResult>() {
                @Override
                public AllResult apply(@NonNull String s) throws Exception {
                    AllResult result = null;
                    EResultType type = EResultType.fromInt(searchType);
                    switch (type) {
                        case CONTACT:
                            result = ProviderUser
                                .searchUserByContent(ContextHelper.getContext(), s);
                            break;
                        case MUC:
                            result = MucInfo.selectMucByContent(ContextHelper.getContext(), s);
                            break;
                        case RECORD:
                            result = MessageManager.getInstance()
                                .searchMessageRecord(ContextHelper.getContext(), s);
                            break;
                    }

                    return result;

                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.empty())
            .subscribe(new Consumer<AllResult>() {
                @Override
                public void accept(@NonNull AllResult Result) throws Exception {
                    //     L.d("查询结果数量:" + allResults.get(0).getResults().size());
                    //results = allResults;
                    if (mMoreRecordAdapter != null) {
                        mMoreRecordAdapter.setResult(Result);
                        mMoreRecordAdapter.notifyDataSetChanged();
                    }
                }
            });
    }

    private void filterAll(Editable s) {
        String value = s.toString();
        subscription = Observable.just(value)
            .map(new Function<String, List<AllResult>>() {
                @Override
                public List<AllResult> apply(@NonNull String s) throws Exception {
                    List<AllResult> results = new ArrayList<>();
                    AllResult contactResult = ProviderUser
                        .searchUserByContent(ContextHelper.getContext(), s);//搜索通讯录中名字，或者昵称等包含关键字的用户
                    AllResult mucResult = MucInfo
                        .selectMucByContent(ContextHelper.getContext(), s);//搜索群组名字中包含关键字的群组
                    AllResult messageResult = MessageManager.getInstance().searchMessageRecord(
                        ContextHelper.getContext(), s);
                    if (contactResult != null && contactResult.getResults() != null
                        && !contactResult.getResults()
                        .isEmpty()) {
                        results.add(contactResult);
                    }
                    if (mucResult != null && mucResult.getResults() != null && !mucResult
                        .getResults().isEmpty()) {
                        results.add(mucResult);
                    }
                    if (messageResult != null && messageResult.getResults() != null
                        && !messageResult.getResults()
                        .isEmpty()) {
                        results.add(messageResult);
                    }
                    return results;

                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.empty())
            .subscribe(new Consumer<List<AllResult>>() {
                @Override
                public void accept(@NonNull List<AllResult> allResults) throws Exception {
                    mResultAdapter.setResults(allResults);
                }
            });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ui.etSearch.clearFocus();
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        View view = getWindow().getDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
