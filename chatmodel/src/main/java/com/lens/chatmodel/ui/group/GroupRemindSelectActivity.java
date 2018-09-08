package com.lens.chatmodel.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.utils.Cn2Spell;
import com.lens.chatmodel.utils.SortUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.components.widget.CustomDocaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xhdl0002 on 2018/2/8.
 *
 * @选人
 */

public class GroupRemindSelectActivity extends FGActivity implements
    AbstractRecyclerAdapter.OnItemClickListener {

    private Intent intent;
    private FGToolbar toolbar;
    private EditText remindSearch;
    private RecyclerView remindContacts;
    private AdapterGroupRemind adapterRemind;
    //操作群id
    private String mucId;
    //可选的好友列表
    private List<UserBean> friendUserBeans;

    @Override
    public void initView() {
        setContentView(R.layout.activity_remind_select);
        toolbar = findViewById(R.id.viewTitleBar);
        initBackButton(toolbar, true);
        toolbar.setTitleText("选择提醒的人");
        remindSearch = findViewById(R.id.remind_et_search);
        remindContacts = findViewById(R.id.remind_rvContacts);
        LinearLayoutManager mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        remindContacts.setLayoutManager(mLayouManager);
        remindContacts.setItemAnimator(new DefaultItemAnimator());
        remindContacts.addItemDecoration(new CustomDocaration(this,
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));
        adapterRemind = new AdapterGroupRemind(getApplicationContext());
        adapterRemind.setItemClickListener(this);
//        adapterRemind.setSelectListener(this);
        remindContacts.setAdapter(adapterRemind);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        intent = getIntent();
        mucId = intent.getStringExtra("mucId");

        remindSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<UserBean> searchList = filterContacts(remindSearch.getText().toString(),
                    friendUserBeans);
                if (null != searchList && searchList.size() > 0) {
                    SortUtils.sortContacts(searchList);
                    adapterRemind.setData(searchList);
                }
            }
        });

        //数据库获取群成员信息
        List<Muc.MucMemberItem> members = MucInfo
            .selectMucMemberItem(getApplicationContext(), mucId);
        if (null != members && members.size() > 0) {
            friendUserBeans = new ArrayList<>();
            for (Muc.MucMemberItem memberItem : members) {
                if (AppConfig.INSTANCE.get(AppConfig.ACCOUT)
                    .equals(memberItem.getUsername())) {
                    continue;
                }
                UserBean userBean = new UserBean();
                userBean.setUserId(memberItem.getUsername());
                userBean.setUserNick(TextUtils.isEmpty(memberItem.getMucusernick()) ? (
                    TextUtils.isEmpty(memberItem.getUsernick()) ? memberItem.getUsername()
                        : memberItem.getUsernick()) : memberItem.getMucusernick());
                userBean.setAvatarUrl(memberItem.getAvatar());
                userBean.setPinYin(Cn2Spell.getInstance().getSelling(userBean.getUserNick()));
                friendUserBeans.add(userBean);
            }
            SortUtils.sortContacts(friendUserBeans);
            adapterRemind.setData(friendUserBeans);
        }
    }

    @Override
    public void onItemClick(Object bean) {
        UserBean userBean = (UserBean) bean;
        Intent intent = new Intent();
        intent.putExtra("remindBean", userBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param str 过滤字符
     * @param mAllContactsList 过滤集合
     * @return 过滤后的集合
     */
    public List<UserBean> filterContacts(final String str, List<UserBean> mAllContactsList) {
        if (TextUtils.isEmpty(str)) {
            return mAllContactsList;
        }
        List<UserBean> filterList = new ArrayList<>();// 过滤后的list
        for (UserBean contact : mAllContactsList) {
            if (!TextUtils.isEmpty(contact.getUserNick())) {
                if (contact.getUserNick().contains(str)) {
                    if (!filterList.contains(contact)) {
                        filterList.add(contact);
                    }
                }
                if (contact.getRemarkName().contains(str)) {
                    if (!filterList.contains(contact)) {
                        filterList.add(contact);
                    }
                }
                if (contact.getUserId().contains(str)) {
                    if (!filterList.contains(contact)) {
                        filterList.add(contact);
                    }
                }
            }

        }
//        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
//            for (UserBean contact : mAllContactsList) {
//                if (contact.getUserNick() != null) {
//                    if (contact.getUserNick().contains(str)) {
//                        if (!filterList.contains(contact)) {
//                            filterList.add(contact);
//                        }
//                    }
//                }
//            }
//        } else {
//            for (UserBean contact : mAllContactsList) {
//                if (contact.getUserNick() != null) {
//                    //姓名全匹配,姓名首字母匹配,姓名全字母匹配
//                    if (contact.getUserNick().toLowerCase(Locale.CHINESE)
//                        .startsWith(str.toLowerCase(Locale.CHINESE))
//                        || contact.getPinYin().toLowerCase(Locale.CHINESE)
//                        .contains(str.toLowerCase(Locale.CHINESE))
//                        ) {
//                        if (!filterList.contains(contact)) {
//                            filterList.add(contact);
//                        }
//                    }
//                }
//            }
//        }
        return filterList;
    }
}
