package com.lens.chatmodel.ui.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.db.MucInfo;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import java.util.List;

/**
 * Created by LL130386 on 2018/9/4.
 * 群聊单选列表
 */

public class AdapterSelectedGroupList extends AbstractRecyclerAdapter<Muc.MucItem> {

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private OnItemClickListener listener;

    public AdapterSelectedGroupList(Context ctx, List<Muc.MucItem> data) {
        super(ctx);
        buliedData(data);
    }


    /**
     * 构建数据
     */
    private void buliedData(List<Muc.MucItem> data) {
        if (data != null && data.size() > 0) {
            mBeanList = data;
        }
    }

    public void setData(List<Muc.MucItem> data) {
        buliedData(data);
        notifyDataSetChanged();
    }

    public List<Muc.MucItem> getData() {
        return mBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupListHolder(View.inflate(mContext, R.layout.item_select_group, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        GroupListHolder holder = (GroupListHolder) viewHolder;
        Muc.MucItem mucItem = mBeanList.get(position);
        holder.tvName.setText(mucItem.getMucname());
        holder.friendRoot.setOnClickListener((v) -> listener.onItemClick(position));
        holder.ivHeader.setDrawText(MucInfo.selectMucUserNickList(mContext, mucItem.getMucid()));
        holder.tvCount.setText("(" + mucItem.getMemberCount() + ")");
    }


    class GroupListHolder extends RecyclerView.ViewHolder {

        View friendRoot;
        AvatarImageView ivHeader;
        TextView tvName;
        TextView tvCount;

        public GroupListHolder(View itemView) {
            super(itemView);
            friendRoot = itemView.findViewById(R.id.ll_root);
            ivHeader = itemView.findViewById(R.id.ivHeader);
            tvName = itemView.findViewById(R.id.tvName);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }

    interface OnItemClickListener {

        void onItemClick(int position);
    }
}
