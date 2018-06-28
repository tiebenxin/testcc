package com.lensim.fingerchat.fingerchat.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.interf.IEventProduct;

import java.util.List;

/**
 * Created by LL130386 on 2017/12/4.
 */

public class FragmentSearchFriend extends BaseFragment {

    public static final int CLICK_ITEM = 0;

    private RecyclerView recyclerView;
    private List<SearchTableBean> mSearchResults;
    private AdapterSearchUser mAdapter;
    private OnItemClickListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_friend, null);
    }

    @Override
    protected void initView() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        if (mAdapter == null) {
            mAdapter = new AdapterSearchUser(getActivity());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);


    }


    @Override
    public void notifyRequestResult(IEventProduct event) {
        super.notifyRequestResult(event);
        mSearchResults = ((ActivitySearchContacts) getActivity()).getSearchResult();
        if (mSearchResults != null && mSearchResults.size() > 0) {
            if (mAdapter != null) {
                mAdapter.setData(mSearchResults);
            }
        }
    }

    @Override
    public void notifyResumeData() {
        super.notifyResumeData();
        mSearchResults = ((ActivitySearchContacts) getActivity()).getSearchResult();
        if (mSearchResults != null && mSearchResults.size() > 0) {
            if (mAdapter != null) {
                mAdapter.setData(mSearchResults);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        if (mAdapter == null) {
            mAdapter = new AdapterSearchUser(ContextHelper.getContext());
        }
        mAdapter.setItemClickListener(l);

    }
}
