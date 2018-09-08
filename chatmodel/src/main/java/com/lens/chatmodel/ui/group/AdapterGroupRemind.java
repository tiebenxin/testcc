package com.lens.chatmodel.ui.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/12.
 */

public class AdapterGroupRemind extends AbstractRecyclerAdapter<UserBean> {

    public AdapterGroupRemind(Context ctx) {
        super(ctx);
    }

    public void setData(List<UserBean> data) {
        if (data != null) {
            mBeanList = data;
        }
        notifyDataSetChanged();
    }

    public List<UserBean> getData() {
        if (null == mBeanList) {
            return new ArrayList<>();
        }
        return mBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectHolder(View.inflate(mContext, R.layout.item_group_friend, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SelectHolder holder = (SelectHolder) viewHolder;
        UserBean userBean = mBeanList.get(position);
        holder.tvName.setText(ChatHelper
            .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                userBean.getUserId()));
        holder.cb.setVisibility(View.GONE);
        ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), holder.ivHeader);
        holder.friendRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mItemClickListener) {
                    mItemClickListener.onItemClick(userBean);
                }
            }
        });
    }

    class SelectHolder extends RecyclerView.ViewHolder {

        View friendRoot;
        ImageView ivHeader;
        TextView tvName;
        ImageView cb;

        public SelectHolder(View itemView) {
            super(itemView);
            friendRoot = itemView.findViewById(R.id.friend_root);
            ivHeader = itemView.findViewById(R.id.ivHeader);
            tvName = itemView.findViewById(R.id.tvName);
            cb = itemView.findViewById(R.id.iv_cb);
        }
    }
}
