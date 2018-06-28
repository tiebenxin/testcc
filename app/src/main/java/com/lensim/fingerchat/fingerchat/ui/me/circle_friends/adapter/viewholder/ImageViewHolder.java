package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.lensim.fingerchat.components.widget.circle_friends.MultiImageView;
import com.lensim.fingerchat.fingerchat.R;

public class ImageViewHolder extends CircleViewHolder {
    /** 图片*/
    public MultiImageView multiImageView;

    public ImageViewHolder(View itemView) {
        super(itemView, TYPE_IMAGE);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = viewStub.inflate();
        MultiImageView multiImageView = subView.findViewById(R.id.multiImagView);
        if(multiImageView != null){
            this.multiImageView = multiImageView;
        }
    }
}
