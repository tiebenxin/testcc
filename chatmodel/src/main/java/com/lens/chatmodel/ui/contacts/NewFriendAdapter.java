package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.INewFriendItemClickListener;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.UIHelper;
import java.util.List;

public class NewFriendAdapter extends BaseAdapter {

    private Context context;
    private List<IChatUser> beans;
    private INewFriendItemClickListener listener;

    public NewFriendAdapter(Context context) {
        this.context = context;

    }

    public NewFriendAdapter(Context context, List<IChatUser> beans,
        INewFriendItemClickListener listener) {
        this.context = context;
        this.beans = beans;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public IChatUser getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        beans.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_newfriends, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UIHelper.setTextSize2(14, holder.mTvName);
        UIHelper.setTextSize2(10, holder.mTvInfo);
        final IChatUser bean = getItem(position);
        ImageHelper.loadAvatarPrivate(bean.getAvatarUrl(), holder.mIvHead);
        holder.mTvName.setText(bean.getUserNick());
        if (bean.getRelationStatus() == ERelationStatus.INVITE.ordinal()) {
            holder.mTvInfo.setVisibility(View.VISIBLE);
            holder.mBtAccept.setVisibility(View.GONE);
            holder.mTvInfo.setText("已发出邀请");
        } else if (bean.getRelationStatus() == ERelationStatus.RECEIVE.ordinal()) {
            holder.mTvInfo.setVisibility(View.INVISIBLE);
            holder.mBtAccept.setVisibility(View.VISIBLE);
            holder.mBtAccept.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAccept(bean);
                    }

                }
            });
        } else if (bean.getRelationStatus() == ERelationStatus.FRIEND.ordinal()) {
            holder.mTvInfo.setVisibility(View.VISIBLE);
            holder.mBtAccept.setVisibility(View.GONE);
            holder.mTvInfo.setText("已添加");
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(bean);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(bean, position);
                }
                return true;
            }
        });
        return convertView;
    }

    private class ViewHolder {

        private ImageView mIvHead;
        private TextView mTvName;
        private TextView mTvInfo;
        private Button mBtAccept;
        //private TextView mTvDesc;

        public ViewHolder(View v) {
            mTvName = (TextView) v.findViewById(R.id.tv_friend_name);
            mTvInfo = (TextView) v.findViewById(R.id.tv_info);
            mBtAccept = (Button) v.findViewById(R.id.bt_accept_invite);
            //mTvDesc = (TextView) v.findViewById(R.id.tv_friend_description);
            mIvHead = (ImageView) v.findViewById(R.id.iv_friends_head);
        }
    }

    public List<IChatUser> getBeans() {
        return beans;
    }

    public void setBeans(List<IChatUser> beans) {
        this.beans = beans;
        notifyDataSetChanged();

    }

    public void setClickListener(INewFriendItemClickListener l) {
        listener = l;
    }

}
