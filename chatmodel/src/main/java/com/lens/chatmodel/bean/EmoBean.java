package com.lens.chatmodel.bean;

import com.lens.chatmodel.bean.body.ImageUploadEntity;

/**
 * Created by LL130386 on 2018/8/7.
 * 自定义表情数据bean
 */

public class EmoBean {

    String key;
    ImageUploadEntity value;
    long time;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ImageUploadEntity getValue() {
        return value;
    }

    public void setValue(ImageUploadEntity value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        if (value != null) {
            return ImageUploadEntity.toJson(value);
        }
        return "";
    }
}
