package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import java.util.List;


/**
 * Created by LY309313 on 2017/2/13.
 */

public class OnlyAvatarAdapter extends RecyclerView.Adapter {

    private List<UserBean> users;
    private LayoutInflater mInflater;
    private Context mContext;


    public OnlyAvatarAdapter(Context context, List<UserBean> users) {
        this.mContext = context;
        this.users = users;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_avater_item, parent, false);

        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        AvatarViewHolder holder = ((AvatarViewHolder) viewholder);
        UserBean userBean = users.get(position);
        L.i("NewMsgAdapter", "消息列表重绘");
        if (ChatHelper.isGroupChat(userBean.getChatType())) {
            holder.avatarView.setDrawText(
                MucInfo.selectMucUserNickList(ContextHelper.getContext(), userBean.getUserId()));
        } else {
            holder.avatarView.setChatType(true);
            ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), holder.avatarView);
        }

    }

    @Override
    public int getItemCount() {
        return users.size() == 1 ? 2 : users.size();
    }


    private class AvatarViewHolder extends RecyclerView.ViewHolder {


        private final AvatarImageView avatarView;

        public AvatarViewHolder(View view) {
            super(view);
            avatarView = view.findViewById(R.id.iv_msg_stub);

        }
    }
}
