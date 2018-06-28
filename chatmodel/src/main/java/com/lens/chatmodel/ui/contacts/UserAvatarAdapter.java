package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2016/8/13.
 */

public class UserAvatarAdapter extends RecyclerView.Adapter {

    private List<UserBean> users;
    private Context context;
    private boolean canDelete;
    private LayoutInflater infalter;


    public UserAvatarAdapter(Context context, List<UserBean> names) {
        this.users = names;
        this.context = context;
        infalter = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = infalter.inflate(R.layout.item_single_img, parent, false);
        return new AvatarHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AvatarHolder avatarHolder = (AvatarHolder) holder;
        if (users.size() == 0) {
            avatarHolder.avatarView.setImageResource(R.drawable.icon_search);
        } else {
            UserBean userBean = users.get(position);
            L.i("NewMsgAdapter", "消息列表重绘");
            List<String> jids;
            //群
            if (ChatHelper.isGroupChat(userBean.getChatType())) {
                avatarHolder.avatarView
                    .setDrawText(MucInfo.selectMucUserNick(context, userBean.getUserId()));
            } else {
                //人
                avatarHolder.avatarView.setChatType(true);
                ImageHelper
                    .loadAvatarPrivate(userBean.getAvatarUrl(), avatarHolder.avatarView);
            }
            if (canDelete && position == getItemCount() - 1) {
                avatarHolder.mask.setVisibility(View.VISIBLE);
            } else {
                avatarHolder.mask.setVisibility(View.GONE);
            }
            avatarHolder.avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = FriendDetailActivity
                        .createNormalIntent(ContextHelper.getContext(), userBean);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextHelper.getContext().startActivity(intent);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return (users == null || users.size() == 0) ? 1 : users.size();
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        notifyDataSetChanged();
    }

    public void removeLast() {
        if (users == null || users.size() == 0) {
            return;
        }
        int pos = users.size() - 1;
        users.remove(pos);
        notifyDataSetChanged();
    }

    public void setData(ArrayList<UserBean> beanlist) {
        this.users = beanlist;
        this.notifyDataSetChanged();
    }

    private class AvatarHolder extends RecyclerView.ViewHolder {

        private final View mask;

        private final AvatarImageView avatarView;


        public AvatarHolder(View view) {
            super(view);
            avatarView = itemView.findViewById(R.id.iv_msg_stub);
            mask = itemView.findViewById(R.id.mask);
        }
    }
}
