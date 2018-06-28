package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.interf.MaxLengthWatcher;
import com.lensim.fingerchat.fingerchat.view.TagCloudView;
import java.util.ArrayList;

/**
 * Created by LY309313 on 2016/11/5.
 */

public class FriendAliasFragment extends BaseFragment {

    private EditText mFriendRemark;
    private String alias;
    private String userjid;

    private TagCloudView mTagCloudView;
    private TextView mAddGroupHint;
    private FrameLayout mAddGroup;
    //    private ArrayList<String> names;
    private TextView groupsLabel;


    public static FriendAliasFragment newInstance(String alias, String jid) {
        FriendAliasFragment fragment = new FriendAliasFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        bundle.putString("userjid", jid);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FriendAliasFragment newMucInstance(String alias) {
        FriendAliasFragment fragment = new FriendAliasFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_alias, container, false);
    }

    @Override
    protected void initView() {
        mFriendRemark = getView().findViewById(R.id.mFriendRemark);
        mTagCloudView = getView().findViewById(R.id.groups_tag);
        mAddGroupHint = getView().findViewById(R.id.add_groups_to_divide);
        mAddGroup = getView().findViewById(R.id.groups_container);
        groupsLabel = getView().findViewById(R.id.groupsLabel);
        mFriendRemark.addTextChangedListener(
            new MaxLengthWatcher(((FriendAliasActivity) getActivity()).getTextMaxLength(),
                mFriendRemark, null));
    }

    @Override
    public void initData() {
        Bundle arguments = getArguments();
        alias = arguments.getString("alias");
        userjid = arguments.getString("userjid");
        if (StringUtils.isEmpty(userjid)) {
            mAddGroup.setVisibility(View.GONE);
            groupsLabel.setVisibility(View.GONE);
        } else {
//      names = RosterManager.getInstance().getLocalGroups(userjid);
//            L.i(this.getClass().getName(), "全部分组:" + names);
//            if (!names.isEmpty()) {
//                mAddGroupHint.setVisibility(View.GONE);
//                mTagCloudView.setVisibility(View.VISIBLE);
//                mTagCloudView.setTags(names);
//            }
        }
        if (!StringUtils.isEmpty(alias)) {
            mFriendRemark.setText(checkNameLength(alias));
        }

    }

//    @Override
//    public void initListener() {
//        mAddGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = GroupCreateActivity.newIntent(getActivity(), userjid, names);
//                startActivityForResult(intent, 0);
//            }
//        });
//    }

    public String getResult() {
        return mFriendRemark.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> user_labels = data.getStringArrayListExtra("user_labels");
            if (user_labels != null && !user_labels.isEmpty()) {
                mTagCloudView.setVisibility(View.VISIBLE);
                mAddGroupHint.setVisibility(View.GONE);
                mTagCloudView.setTags(user_labels);
            }
        }
    }


    private String checkNameLength(String mucName) {
        String s = mucName;
        if (mucName.length() > 20) {
            s = mucName.substring(0, 20);
        }
        return s;
    }
}
