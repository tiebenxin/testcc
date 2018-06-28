package com.lens.chatmodel.ui.group;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lens.chatmodel.bean.SearchUserResult;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.global.CommonEnum;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhdl0002 on 2018/1/11.
 * 群成员adapter
 */

public class AdapterGroupInviteUser extends AbstractRecyclerAdapter<UserBean> {

    public AdapterGroupInviteUser(Context ctx) {
        super(ctx);
        mBeanList = new ArrayList<>();
    }

    public List<UserBean> getData() {
        return mBeanList;
    }

    public void setData(List<UserBean> data) {
        if (data != null) {
            mBeanList = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListHolder(View.inflate(mContext, R.layout.item_operation_user, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ListHolder holder = (ListHolder) viewHolder;
        //user 布局
        UserBean userBean = mBeanList.get(position);
        holder.itemIpAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FriendDetailActivity
                    .createNormalIntent(ContextHelper.getContext(), userBean.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextHelper.getContext().startActivity(intent);
            }
        });
        if (1 == userBean.getType()) {
            doSearch(userBean.getUserId(), holder.itemIpName, holder.itemIpAvatar);
        } else {
//            holder.itemIpName.setText((TextUtils.isEmpty(userBean.getUserNick()) ? userBean.getUserId() : userBean.getUserNick()));
            holder.itemIpName.setText(ChatHelper
                .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                    userBean.getUserId()));
            ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), holder.itemIpAvatar);
        }

    }

    private void doSearch(String value, TextView itemIpName, ImageView itemIpAvatar) {
        HttpUtils.getInstance()
            .searchUserList(value, CommonEnum.ESearchTabs.ACCOUT, 0, new IDataRequestListener() {
                @Override
                public void loadFailure(String reason) {
                }

                @Override
                public void loadSuccess(Object object) {
                    if (object != null && object instanceof String) {
                        String result = (String) object;
                        if (!TextUtils.isEmpty(result)) {
                            SearchUserResult users = GsonHelper
                                .getObject(result, SearchUserResult.class);
                            if (users != null) {
                                List<SearchTableBean> temp = users.getTable();
                                if (temp != null && temp.size() > 0) {
                                    IChatUser userBean = (IChatUser) temp.get(0);
                                    if (userBean != null) {
                                        itemIpName.setText(
                                            ChatHelper.getUserRemarkName(userBean.getRemarkName(),
                                                userBean.getUserNick(), userBean.getUserId()));
                                        ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(),
                                            itemIpAvatar);
                                    }
                                }
                            }
                        }
                    }
                }
            });

    }

    class ListHolder extends RecyclerView.ViewHolder {

        ImageView itemIpAvatar;
        TextView itemIpName;

        public ListHolder(View itemView) {
            super(itemView);
            itemIpAvatar = itemView.findViewById(R.id.item_ip_avatar);
            itemIpName = itemView.findViewById(R.id.item_ip_name);
        }
    }
}
