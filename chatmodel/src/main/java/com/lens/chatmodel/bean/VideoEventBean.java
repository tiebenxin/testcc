package com.lens.chatmodel.bean;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.helper.AnimationRect;

/**
 * Created by LL130386 on 2018/1/12.
 */

public class VideoEventBean {

    AnimationRect rect;
    String videoPath;
    String imageSize;

    public VideoEventBean(AnimationRect r, String url, String size) {
        rect = r;
        videoPath = url;
        imageSize = size;
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

    public String getImageSize() {
        if (TextUtils.isEmpty(imageSize)) {
            imageSize = "";
        }
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }
}
