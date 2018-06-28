package com.lensim.fingerchat.data.me.circle_friend;

/**
 * date on 2018/1/31
 * author ll147996
 * describe
 */

public class CommentConfig {

    public static enum Type{

        PUBLIC("public"), REPLY("reply"),REPLYUSER("replayuser");

        private String value;

        private Type(String value){
            this.value = value;
        }

    }

    public int circlePosition;
    public int commentPosition;
    public String createdid;
    public String createdName;
    public String id;
    public Type commentType;
    public String replyUserid;
    public String replyUsername;

    @Override
    public String toString() {

        return "circlePosition = " + circlePosition
            + "; commentPosition = " + commentPosition
            + "; commentType ＝ " + commentType
            + "; id = " + id
            + "; replyUserid = " + replyUserid
            + "; replyUsername = " + replyUsername;
    }

}
