package com.lens.chatmodel.bean;

import com.lensim.fingerchat.commons.helper.AnimationRect;

/**
 * Created by LL130386 on 2018/1/12.
 */

public class VideoEventBean {

    AnimationRect rect;
    String videoPath;

    public VideoEventBean(AnimationRect r, String url) {
        rect = r;
        videoPath = url;
    }

    public AnimationRect getRect() {
        return rect;
    }

    public void setRect(AnimationRect rect) {
        this.rect = rect;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
