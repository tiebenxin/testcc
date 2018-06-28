package com.lens.chatmodel.bean;

/**
 * Created by xhdl0002 on 2018/2/9.
 */

public class ChatBgResBean {
    private int backId;
    private int resId;

    public ChatBgResBean(int backId, int resId) {
        this.backId = backId;
        this.resId = resId;
    }

    public ChatBgResBean() {
    }

    public int getBackId() {
        return backId;
    }

    public void setBackId(int backId) {
        this.backId = backId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
