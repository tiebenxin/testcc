package com.lens.chatmodel.bean;

import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.data.bean.LongImageBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/1/15.
 */

public class ImageEventBean {

    ArrayList<String> urls;
    ArrayList<String> msgIds;
    ArrayList<AnimationRect> rects;
    ArrayList<LongImageBean> booleanLists;
    int position;

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<String> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(ArrayList<String> msgIds) {
        this.msgIds = msgIds;
    }

    public ArrayList<AnimationRect> getRects() {
        return rects;
    }

    public void setRects(ArrayList<AnimationRect> rects) {
        this.rects = rects;
    }

    public ArrayList<LongImageBean> getBooleanLists() {
        return booleanLists;
    }

    public void setBooleanLists(ArrayList<LongImageBean> booleanLists) {
        this.booleanLists = booleanLists;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
