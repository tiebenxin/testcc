package com.lens.chatmodel.ui.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.ImageHelper;
import java.util.List;


/**
 * Created by LY309313 on 2017/2/13.
 */

public class MemberInfoAdapter extends RecyclerView.Adapter {

    private List<MucMemberItem> users;
    private LayoutInflater mInflater;
    private Context mContext;


    public MemberInfoAdapter(Context context, List<MucMemberItem> users) {
        this.mContext = context;
        this.users = users;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_member_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        AvatarViewHolder holder = ((AvatarViewHolder) viewholder);
        MucMemberItem userBean = users.get(position);
        ImageHelper.loadAvatarPrivate(userBean.getAvatar(), holder.iv_avatar);
        holder.tv_name.setText(userBean.getMucusernick());
    }

    @Override
    public int getItemCount() {
        return users.size() == 1 ? 2 : users.size();
    }


    private class AvatarViewHolder extends RecyclerView.ViewHolder {


        private final ImageView iv_avatar;
        private final TextView tv_name;

        public AvatarViewHolder(View view) {
            super(view);
            iv_avatar = view.findViewById(R.id.iv_avatar);
            tv_name = view.findViewById(R.id.tv_name);

        }
    }
}
