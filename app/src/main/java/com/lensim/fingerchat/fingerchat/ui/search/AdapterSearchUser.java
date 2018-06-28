package com.lensim.fingerchat.fingerchat.ui.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lensim.fingerchat.fingerchat.R;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/23.
 */

public class AdapterSearchUser extends AbstractRecyclerAdapter<SearchTableBean> {

    public AdapterSearchUser(Context ctx) {
        super(ctx);
    }

    public void setData(List<SearchTableBean> l) {
        mBeanList = l;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ControllerSearchUserItem(mInflater.inflate(R.layout.item_contacts_cell, null),
            mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ControllerSearchUserItem) holder).bindData(mBeanList.get(position));
    }
}
