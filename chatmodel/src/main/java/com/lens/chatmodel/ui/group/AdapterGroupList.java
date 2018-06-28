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
import com.lens.chatmodel.helper.ChatHelper;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/18.
 */

public class AdapterGroupList extends AbstractRecyclerAdapter<Muc.MucItem> {

    public void setListener(OnItemClick listener) {
        this.listener = listener;
    }

    private OnItemClick listener;
    private boolean isAll = false;

    public AdapterGroupList(Context ctx, List<Muc.MucItem> data) {
        super(ctx);
        buliedData(data);
    }

    private int ownerSize;
    private int memberSize;

    /**
     * 构建数据
     */
    private void buliedData(List<Muc.MucItem> data) {
        if (data != null && data.size() > 0) {
            mBeanList = data;
            ownerSize = 0;
            memberSize = 0;
            for (Muc.MucItem item : data) {
                if (ChatHelper.isMucOwer(item, UserInfoRepository.getUserName())) {
                    ownerSize++;
                } else /*if (Muc.Role.Member_VALUE == item.getPConfig().getRoleValue())*/ {
                    memberSize++;
                }
            }
        }
    }

    public void setData(List<Muc.MucItem> data, boolean isAll) {
        buliedData(data);
        this.isAll = isAll;
        notifyDataSetChanged();
    }

    public List<Muc.MucItem> getData() {
        return mBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupListHolder(View.inflate(mContext, R.layout.item_group, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        GroupListHolder holder = (GroupListHolder) viewHolder;
        if (position == 0 && ownerSize != 0) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText("我管理的群");
        } else if (position == ownerSize && memberSize != 0) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText("我是成员的群");
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        Muc.MucItem mucItem = mBeanList.get(position);
        holder.tvName.setText(mucItem.getMucname());
        holder.friendRoot.setOnClickListener((v) -> listener.onitemClick(position));
        holder.ivHeader.setDrawText(MucInfo.selectMucUserNick(mContext, mucItem.getMucid()));
    }

    private List<String> resultMucAvatar(Muc.MucItem mucItem) {
        List<String> mucAvatar = new ArrayList<>();
        if (mucItem != null) {
            List<Muc.MucMemberItem> memberItems = mucItem.getMembersList();
            if (memberItems != null && memberItems.size() > 0) {
                for (int position = 0;
                    position < (memberItems.size() >= 9 ? 9 : memberItems.size()); position++) {
                    mucAvatar.add(memberItems.get(position).getAvatar());
                }
            }
        }
        return mucAvatar;
    }

    class GroupListHolder extends RecyclerView.ViewHolder {

        View friendRoot;
        AvatarImageView ivHeader;
        TextView tvName;
        TextView tvTitle;

        public GroupListHolder(View itemView) {
            super(itemView);
            friendRoot = itemView.findViewById(R.id.friend_root);
            ivHeader = itemView.findViewById(R.id.iv_Header);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.item_group_title);
        }
    }

    interface OnItemClick {

        void onitemClick(int positioin);
    }
}
