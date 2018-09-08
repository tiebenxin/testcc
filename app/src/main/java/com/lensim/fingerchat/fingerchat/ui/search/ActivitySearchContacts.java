package com.lensim.fingerchat.fingerchat.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lens.chatmodel.bean.SearchUserResult;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.controller.ControllerNoRecord;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.interf.ISearchTypeClickListener;
import com.lens.chatmodel.interf.ISearchTypeListener;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.CommonEnum.ESearchTabs;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.fingerchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/4.
 */

public class ActivitySearchContacts extends FGActivity {

    private ESearchTabs mCurrentTab;
    private Fragment mCurrentFragment;
    private ControllerSearchContacts viewSearch;
    private List<SearchTableBean> results = new ArrayList<>();
    private FGToolbar toolbar;
    private String searchKey;
    private ControllerNoRecord viewNoRecord;
    private boolean isShowSoftKeyboard;
    private int currentPager = 0;


    @Override
    public void initView() {
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_search_contacts, null));
        mCurrentTab = ESearchTabs.SEARCH_TYPE;
        toolbar = findViewById(R.id.viewTitleBar);
        viewSearch = new ControllerSearchContacts(findViewById(R.id.viewSearch));
        viewSearch.setSearchType(mCurrentTab);
        viewSearch.setOnClickListener(new ISearchTypeClickListener() {
            @Override
            public void search(String value, boolean change) {
                if (TextUtils.isEmpty(value)) {
                    viewNoRecord.setVisible(false);
                    if (change && mCurrentTab == ESearchTabs.SEARCH_TYPE) {
                        showFragment(ESearchTabs.DEFAULT);
                    }
                } else {
                    if (mCurrentTab == ESearchTabs.SEARCH_TYPE) {
                        showFragment(ESearchTabs.DEFAULT);
                    } else {
                        if (results != null && results.size() > 0) {
                            results.clear();
                        }
                        showProgress("正在搜索", true);
                        if (!TextUtils.isEmpty(searchKey) && !searchKey.equals(value)) {
                            currentPager = 0;
                        }
                        doSearch(value, currentPager);
                    }
                }
            }
        });
        viewNoRecord = new ControllerNoRecord(findViewById(R.id.viewNoRecord));
        viewNoRecord.setVisible(false);
        showFragment(mCurrentTab);
    }

    public int getCurrentPager() {
        return currentPager;
    }

    public void loadNextPager() {
        currentPager++;
        doSearch(searchKey, currentPager);
    }

    public void doSearch(String value, int page) {
        currentPager = page;
        HttpUtils.getInstance()
            .searchUserList(value, mCurrentTab, page, new IDataRequestListener() {
                @Override
                public void loadFailure(String reason) {
                    System.out.println("搜索好友failure");
                    searchKey = value;
                    dismissProgress();
                }

                @Override
                public void loadSuccess(Object object) {
                    System.out.println("搜索好友success");
                    dismissProgress();
                    searchKey = value;
                    if (object != null && object instanceof String) {
                        String result = (String) object;
                        if (!TextUtils.isEmpty(result)) {
                            SearchUserResult users = GsonHelper
                                .getObject(result, SearchUserResult.class);
                            if (users != null) {
                                List<SearchTableBean> temp = users.getTable();
                                if (temp != null && temp.size() > 0) {
                                    viewNoRecord.setVisible(false);
                                    checkList(temp, results);
                                } else {
                                    if (currentPager == 0) {
                                        viewSearch.setSearchContainerVisible(false);
                                        viewNoRecord.setVisible(true);
                                    }
                                }
                            }
                            notifyCurrentFragment();
                        } else {
                            if (currentPager == 0) {
                                viewNoRecord.setVisible(true);
                                viewSearch.setSearchContainerVisible(false);
                            }
                        }
                    }

                }
            });

    }

    private void checkList(List<SearchTableBean> temp, List<SearchTableBean> result) {
        if (temp != null && !temp.isEmpty()) {
            int len = temp.size();
            List<String> userIds = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                SearchTableBean bean = temp.get(i);
                if (!userIds.contains(bean.getUserId())) {
                    result.add(bean);
                    userIds.add(bean.getUserId());
                }
            }
        }
    }

    private void notifyCurrentFragment() {
        ((BaseFragment) getCurrentFragment()).notifyResumeData();
    }


    private void showFragment(ESearchTabs tab) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tab.toString());
        if (newFragment == null) {
            newFragment = createFragment(tab);
        }

        prepareFragment(newFragment);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.fl_content, newFragment, tab.toString());
        ft.attach(newFragment);
        ft.commitAllowingStateLoss();
        mCurrentTab = tab;
        setCurrentFragment(newFragment);
        initTitleBar(mCurrentTab);
        initSearchHint(mCurrentTab);
        viewSearch.setSearchType(mCurrentTab);
        if (tab != ESearchTabs.SEARCH_TYPE) {
            viewSearch.focus(true);
            isShowSoftKeyboard = true;
            showSoftKeyboard();
        } else {
            viewSearch.focus(false);
            isShowSoftKeyboard = false;
            hideSoftKeyboard(viewSearch.getEditText());
        }
    }

    private void initSearchHint(ESearchTabs tabs) {
        if (tabs == ESearchTabs.DEFAULT) {
            viewSearch.setHint(R.string.search_hint_accout_nick);
            viewSearch.clearText();
            viewSearch.setSearchContainerVisible(false);
        } else if (tabs == ESearchTabs.ACCOUT) {
            viewSearch.setHint(R.string.search_hint_accout);
        } else if (tabs == ESearchTabs.NICK) {
            viewSearch.setHint(R.string.search_hint_nick);
        } else if (tabs == ESearchTabs.PHONE_NUM) {
            viewSearch.setHint(R.string.search_hint_phone_num);
        } else if (tabs == ESearchTabs.DEPARTMENT) {
            viewSearch.setHint(R.string.search_hint_department);
        } else if (tabs == ESearchTabs.REAL_NAME) {
            viewSearch.setHint(R.string.search_hint_real_name);
        }
    }

    private void initTitleBar(ESearchTabs tab) {
        if (tab == ESearchTabs.SEARCH_TYPE) {
            toolbar.setTitleText(ContextHelper.getString(R.string.select_search_type));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.DEFAULT) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.ACCOUT) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.PHONE_NUM) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.NICK) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.REAL_NAME) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        } else if (tab == ESearchTabs.DEPARTMENT) {
            toolbar.setTitleText(ContextHelper.getString(R.string.search_friend));
            initBackButton(toolbar, true);
        }
    }


    private void prepareFragment(Fragment fragment) {
        if (fragment instanceof FragmentSearchType) {
            prepareFragmentSearchType((FragmentSearchType) fragment);

        } else if (fragment instanceof FragmentSearchFriend) {
            prepareFragmentSearchFriend((FragmentSearchFriend) fragment);

        }

    }

    private Fragment createFragment(ESearchTabs tab) {
        switch (tab) {
            case SEARCH_TYPE:
                return new FragmentSearchType();
            case DEFAULT:
                return new FragmentSearchFriend();
            case ACCOUT:
                return new FragmentSearchFriend();
            case PHONE_NUM:
                return new FragmentSearchFriend();
            case NICK:
                return new FragmentSearchFriend();
            case REAL_NAME:
                return new FragmentSearchFriend();
            case DEPARTMENT:
                return new FragmentSearchFriend();
        }

        return null;
    }

    private void prepareFragmentSearchType(FragmentSearchType fragment) {
        if (fragment != null) {
            fragment.setOnClickListenr(new ISearchTypeListener() {
                @Override
                public void clickAccout() {
                    showFragment(ESearchTabs.ACCOUT);
                }

                @Override
                public void clickPhoneNum() {
                    showFragment(ESearchTabs.PHONE_NUM);
                }

                @Override
                public void clickNick() {
                    showFragment(ESearchTabs.NICK);
                }

                @Override
                public void clickRealName() {
                    showFragment(ESearchTabs.REAL_NAME);
                }

                @Override
                public void clickDepartment() {
                    showFragment(ESearchTabs.DEPARTMENT);
                }
            });
        }
    }

    private void prepareFragmentSearchFriend(FragmentSearchFriend fragment) {
        if (fragment != null) {
            fragment.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(Object bean) {
                    if (bean == null) {
                        return;
                    }
                    if (bean instanceof SearchTableBean) {
                        SearchTableBean user = (SearchTableBean) bean;
                        int relationStatus = ProviderUser.getUserRelationStatus(user.getUserId());
                        UserBean userBean = new UserBean();
                        userBean.setBean(user);
                        userBean.setRelationStatus(relationStatus);
                        Intent intent = FriendDetailActivity
                            .createNormalIntent(ActivitySearchContacts.this, userBean);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void backPressed() {
        if (mCurrentTab == ESearchTabs.SEARCH_TYPE) {
            ActivitySearchContacts.this.finish();
        } else {
            hideSoftKeyboard(viewSearch.getEditText());
            showFragment(ESearchTabs.SEARCH_TYPE);
            viewSearch.clearText();
            viewNoRecord.setVisible(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideSoftKeyboard(viewSearch.getEditText());
    }

    private void setCurrentFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }

    private Fragment getCurrentFragment() {
        return mCurrentFragment;
    }


    public void showSoftKeyboard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) ActivitySearchContacts.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(viewSearch.getEditText(), 0);
            }
        }, 200);
    }


    public List<SearchTableBean> getSearchResult() {
        return results;
    }


}
