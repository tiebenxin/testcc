package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.view.ViewStub;

import com.lens.chatmodel.view.friendcircle.FavortListView;
import com.lensim.fingerchat.components.popupwindow.SnsPopupWindow;
import com.lensim.fingerchat.fingerchat.base.BaseRecycleViewAdapter;
import com.lensim.fingerchat.fingerchat.databinding.ItemCircleViewBinding;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.circle_friends_multitype.CommentAdapter;

public abstract class CircleViewHolder extends BaseRecycleViewAdapter.BaseRecycleViewHolder{

    public final static int TYPE_TEXT = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    public int viewType;

    public ItemCircleViewBinding binding;
    public SnsPopupWindow snsPopupWindow;
    public FavortListView.Adapter favortListAdapter;
    public CommentAdapter commentAdapter;

    public CircleViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        binding = DataBindingUtil.bind(itemView);
        commentAdapter = new CommentAdapter(itemView.getContext());
        favortListAdapter = new FavortListView.Adapter();
        snsPopupWindow = new SnsPopupWindow(itemView.getContext());

        ViewStub viewStub = binding.viewStub.getViewStub();
        initSubView(viewType, viewStub);

    }

    public abstract void initSubView(int viewType, ViewStub viewStub);
}
