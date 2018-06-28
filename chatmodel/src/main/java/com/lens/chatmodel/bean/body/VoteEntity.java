package com.lens.chatmodel.bean.body;

import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/3/12.
 * 投票数据类
 */

public class VoteEntity extends BaseJsonEntity {

    int status;
    String voteId;
    String title;
    String option1;
    String option2;


    public VoteEntity(String json) {
        initJson(json);
    }

    private void initJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                setStatus(optInt("status", object));
                setTitle(optS("title", object));
                setVoteId(optS("voteid", object));
                setOption1(optS("option1", object));
                setOption2(optS("option2", object));
            }
        } catch (JSONException e) {

        }
    }

    public static VoteEntity fromJson(String json) {
        return new VoteEntity(json);
    }

    public static String toJson(VoteEntity entity) {
        if (entity != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("status",entity.getStatus());
                object.put("voteid",entity.getVoteId());
                object.put("title",entity.getTitle());
                object.put("option1",entity.getOption1());
                object.put("option2",entity.getOption2());
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return "";

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }
}
