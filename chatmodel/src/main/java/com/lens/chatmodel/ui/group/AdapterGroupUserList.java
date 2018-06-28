package com.lens.chatmodel.ui.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;


import com.lens.chatmodel.helper.ImageHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/11.
 * 群成员adapter
 */

public class AdapterGroupUserList extends AbstractRecyclerAdapter<Muc.MucMemberItem> {
    private final static int TYPE_LIST = 0X11;
    private final static int TYPE_DELETE = 0X12;
    private final static int TYPE_ADD = 0X13;

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    //是否是群主
    private boolean is_admin;

    public void setOperationListener(GroupOperationListener operationListener) {
        this.operationListener = operationListener;
    }

    private GroupOperationListener operationListener;

    public AdapterGroupUserList(Context ctx, boolean is_admin) {
        super(ctx);
        this.is_admin = is_admin;
        mBeanList = new ArrayList<>();
    }

    public List<Muc.MucMemberItem> getData() {
        return mBeanList;
    }

    public void setData(List<Muc.MucMemberItem> data) {
        if (data != null) {
            mBeanList = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LIST) {
            return new ListHolder(View.inflate(mContext, R.layout.item_operation_user, null), TYPE_LIST);
        } else if (viewType == TYPE_ADD) {
            return new ListHolder(View.inflate(mContext, R.layout.item_operation_user, null), TYPE_ADD);
        } else {
            return new ListHolder(View.inflate(mContext, R.layout.item_operation_user, null), TYPE_DELETE);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ListHolder holder = (ListHolder) viewHolder;
        //加人
        if (TYPE_ADD == holder.getType()) {
            holder.itemIpAvatar.setImageResource(R.drawable.group_of_add);
            holder.itemIpAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operationListener.operationGroupUser(true);
                }
            });
        } else if (TYPE_DELETE == holder.getType()) {
            holder.itemIpAvatar.setImageResource(R.drawable.group_of_delete);
            holder.itemIpAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operationListener.operationGroupUser(false);
                }
            });
        } else {
            holder.itemIpAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operationListener.toUserView(position);
                }
            });
            //user 布局
            Muc.MucMemberItem memberItem = mBeanList.get(position);
            holder.itemIpName.setText(TextUtils.isEmpty(memberItem.getMucusernick()) ? (TextUtils.isEmpty(memberItem.getUsernick()) ? memberItem.getUsername() : memberItem.getUsernick()) : memberItem.getMucusernick());

            ImageHelper.loadAvatarPrivate(memberItem.getAvatar(),holder.itemIpAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return (mBeanList.size() != 0 ? (mBeanList.size() + 1 + (is_admin ? 1 : 0)) : 0);
    }


    @Override
    public int getItemViewType(int position) {
        if (is_admin && position == getItemCount() - 1) {
            return TYPE_DELETE;
        } else if ((is_admin && position == getItemCount() - 2)
            || (!is_admin && position == getItemCount() - 1)) {
            return TYPE_ADD;
        } else {
            return TYPE_LIST;
        }
    }

    class ListHolder extends RecyclerView.ViewHolder {

        public int getType() {
            return type;
        }

        private int type;

        ImageView itemIpAvatar;
        TextView itemIpName;

        public ListHolder(View itemView, int type) {
            super(itemView);
            this.type = type;
            itemIpAvatar = itemView.findViewById(R.id.item_ip_avatar);
            itemIpName = itemView.findViewById(R.id.item_ip_name);
        }
    }
}
