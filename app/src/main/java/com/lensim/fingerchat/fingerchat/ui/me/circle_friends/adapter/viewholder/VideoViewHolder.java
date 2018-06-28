package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.fingerchat.R;

public class VideoViewHolder extends CircleViewHolder{

    public ImageView videothumbnial, img_loading;
    public FrameLayout layoutPlayer;
    public LinearLayout ll_loading;
    public CircleProgress loading;

    public VideoViewHolder(View itemView) {
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.viewstub_videobody);
        View itemView = viewStub.inflate();
        ImageView videothumbnial = itemView.findViewById(R.id.video_override);
        CircleProgress loading = itemView.findViewById(R.id.progress_bar);
        ImageView img_loading = itemView.findViewById(R.id.icon_play);
        LinearLayout ll_loading = itemView.findViewById(R.id.ll_loading);
        FrameLayout layoutPlayer = itemView.findViewById(R.id.container_video_play);
        if (layoutPlayer != null) {
            this.layoutPlayer = layoutPlayer;
            this.videothumbnial = videothumbnial;
            this.img_loading = img_loading;
            this.loading = loading;
            this.ll_loading = ll_loading;
        }
    }
}
