package com.lens.chatmodel.bean.body;

import com.lensim.fingerchat.commons.base.BaseJsonEntity;

/**
 * Created by LL130386 on 2018/6/13.
 */

public class PushBody extends BaseJsonEntity {

    String title;
    String content;
    String action_url;
    String img_url;
    int level;
    int status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActionUrl() {
        return action_url;
    }

    public void setActionUrl(String action_url) {
        this.action_url = action_url;
    }

    public String getImgUrl() {
        return img_url;
    }

    public void setImgUrl(String img_url) {
        this.img_url = img_url;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
