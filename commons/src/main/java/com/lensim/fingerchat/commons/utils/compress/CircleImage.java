package com.lensim.fingerchat.commons.utils.compress;

import android.support.annotation.NonNull;

/**
 * date on 2018/3/1
 * author ll147996
 * describe
 */

public class CircleImage implements ImageInterface<CircleImage>{

    private long id;

    private String path;

    private String thumb;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public int compareTo(@NonNull CircleImage circleImage) {
        return ((Long) this.id).compareTo(circleImage.id);
    }
}
