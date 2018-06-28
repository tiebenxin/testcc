package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.bean.UserBean;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.view.Sidebar;
import java.util.ArrayList;

/**
 * Created by LL130386 on 2017/11/28.
 */

public class FragmentContactsMore extends BaseFragmentContact {

    private FragmentTabContacts parentFragment;
    private RecyclerView recyclerView;
    private AdapterContacts mAdapter;
    private OnItemClickListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_group, null);
    }

    @Override
    protected void initView() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager mLayouManager = new LinearLayoutManager(getActivity());
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(mLayouManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new CustomDocaration(getActivity(),
            LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(getActivity(), R.color.custom_divider_color)));

        Sidebar sidebar = getView().findViewById(R.id.sidebar);
        sidebar.setListView(recyclerView);
        sidebar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        initAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<UserBean> mAllRosters = bundle.getParcelableArrayList("data");
            if (mAllRosters != null && mAllRosters.size() > 0) {
                if (mAdapter == null) {
                    initAdapter();
                }
                mAdapter.setData(mAllRosters, getParent().getNormalUserSize(),
                    getParent().getNoNickUserSize());
            }
            mAdapter.notifyDataSetChanged();

        }
    }

    private void initAdapter() {
        mAdapter = new AdapterContacts(getContext());
        mAdapter.hideHead();
        recyclerView.setAdapter(mAdapter);

        if (listener != null) {
            mAdapter.setItemClickListener(listener);
        }


    }

    public void setItemClickListener(OnItemClickListener l) {
        listener = l;

    }

    public void setParentFragment(FragmentTabContacts fragment) {
        parentFragment = fragment;
    }

    public FragmentTabContacts getParent() {
        return parentFragment;
    }

    @Override
    public void notifyResumeData() {
        super.notifyResumeData();

    }
}
