package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.view.View;

import com.lensim.fingerchat.fingerchat.base.BaseRecycleViewAdapter;
import com.lensim.fingerchat.fingerchat.databinding.ItemHeadCircleBinding;

public class HeaderViewHolder extends BaseRecycleViewAdapter.BaseRecycleViewHolder {
    public final static String NEW_COMMENT_COUNT = "NewCommentCount";
    public final static String MY_AVATAR_TIME = "MyAvatarTime";
    public final static String DEF_VALUE = "0";

    public ItemHeadCircleBinding binding;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}