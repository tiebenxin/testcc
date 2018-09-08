package com.lens.chatmodel.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.MaxLengthWatcher;
import com.lens.chatmodel.view.TagCloudView;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    private TextView groupsLabel;
    private String groupName;
    private ArrayList<String> names;
    private boolean hasChangeGroupName;


    public static FriendAliasFragment newInstance(String alias, String jid, String groupName) {
        FriendAliasFragment fragment = new FriendAliasFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        bundle.putString("userjid", jid);
        bundle.putString("group", groupName);
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

        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CreateGroupActivity.newIntent(getActivity(), userjid, names);
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    public void initData() {
        Bundle arguments = getArguments();
        alias = arguments.getString("alias");
        userjid = arguments.getString("userjid");
        groupName = arguments.getString("group");
        if (StringUtils.isEmpty(userjid)) {
            mAddGroup.setVisibility(View.GONE);
            groupsLabel.setVisibility(View.GONE);
        } else {
            names = StringUtils.getGroups(groupName);
            if (names !=null && !names.isEmpty()){
                mAddGroupHint.setVisibility(View.GONE);
                mTagCloudView.setVisibility(View.VISIBLE);
                mTagCloudView.setTags(names);
            }
//            if (!TextUtils.isEmpty(groupName)) {
//                names = new ArrayList<>();
//                names.add(groupName);
//                mAddGroupHint.setVisibility(View.GONE);
//                mTagCloudView.setVisibility(View.VISIBLE);
//                mTagCloudView.setTags(names);
//            }
        }
        if (!StringUtils.isEmpty(alias)) {
            mFriendRemark.setText(checkNameLength(alias));
        }

    }


    public String getResult() {
        return mFriendRemark.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList names = data.getStringArrayListExtra("group");
            if (names != null && names.size() > 0){
                mTagCloudView.setVisibility(View.VISIBLE);
                mAddGroupHint.setVisibility(View.GONE);
                mTagCloudView.setTags(names);
            }else {
                mTagCloudView.setVisibility(View.GONE);
                mAddGroupHint.setVisibility(View.VISIBLE);
            }

//            if (names != null && names.size() > 0){
//                hasChangeGroupName = true;
//            }else {
//                hasChangeGroupName = false;
//            }
//            if (!TextUtils.isEmpty(groupName) && groupName.equalsIgnoreCase(name)) {
//                hasChangeGroupName = false;
//            } else {
//                if (!TextUtils.isEmpty(name)) {
//                    hasChangeGroupName = true;
//                }
//            }
//            groupName = name;
//            if (!TextUtils.isEmpty(groupName)) {
//                List<String> list = new ArrayList<>();
//                list.add(groupName);
//                mTagCloudView.setVisibility(View.VISIBLE);
//                mAddGroupHint.setVisibility(View.GONE);
//                mTagCloudView.setTags(list);
//            } else {
//                mTagCloudView.setVisibility(View.GONE);
//                mAddGroupHint.setVisibility(View.VISIBLE);
//            }

            //更新fragment参数
            Bundle bundle = new Bundle();
            bundle.putString("alias", alias);
            bundle.putString("userjid", userjid);
            bundle.putStringArrayList("group", names);
            setArguments(bundle);
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();

        }
    }


    private String checkNameLength(String mucName) {
        String s = mucName;
        if (mucName.length() > 20) {
            s = mucName.substring(0, 20);
        }
        return s;
    }

    public boolean hasChangeGroupName() {
        return hasChangeGroupName;
    }
}
