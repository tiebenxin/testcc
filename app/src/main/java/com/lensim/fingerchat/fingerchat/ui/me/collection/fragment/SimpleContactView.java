package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.utils.ImageLoader;
import com.lensim.fingerchat.fingerchat.R;

public class SimpleContactView implements AbsContentView{

    private Context context;
    private String text;

    public SimpleContactView(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {

        View rootView = mInflater.inflate(R.layout.view_simple_contact, parent, false);
        CardViewBean bean = new Gson().fromJson(text, CardViewBean.class);

        TextView name = rootView.findViewById(R.id.tv_card_usernick);
        TextView account = rootView.findViewById(R.id.tv_card_username);
        ImageView imageView = rootView.findViewById(R.id.card_avatar);

        name.setText(bean.getFriendName());
        account.setText(bean.getFriendId());
        ImageLoader.loadImage(bean.getFriendHeader(), imageView);

        rootView.setOnClickListener(v -> {
            Intent intent = FriendDetailActivity.createNormalIntent(context, bean.getFriendId());
            context.startActivity(intent);
        });
        return rootView;
    }
    
}
