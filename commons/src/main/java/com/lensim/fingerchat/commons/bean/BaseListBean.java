package com.lensim.fingerchat.commons.bean;

import com.fingerchat.proto.message.Muc;

import java.util.List;

/**
 * Created by xhdl0002 on 2018/3/2.
 */


public class BaseListBean {
    private List<Muc.MucMemberItem> results;

    public BaseListBean() {
    }

    public BaseListBean(List<Muc.MucMemberItem> results) {
        this.results = results;
    }

    public List<Muc.MucMemberItem> getResults() {
        return results;
    }

    public void setResults(List<Muc.MucMemberItem> results) {
        this.results = results;
    }

}
