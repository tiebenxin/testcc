package com.lensim.fingerchat.fingerchat.model.bean;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class CommentResponse {

    /**
     * comment : {"commentId":null,"commentSerno":"6ae6e92e223646579e96e78c60948f70","photoSerno":"1","creatorUserid":"1091","creatorUsername":"王松清","commentUserid":"zze","commentUsername":"轻松","commentContent":"[得意][得意]","commentTime":1531791848804,"commentUserid2":"","commentUsername2":""}
     */

    private CommentBean comment;

    public CommentBean getComment() {
        return comment;
    }

    public void setComment(CommentBean comment) {
        this.comment = comment;
    }

}
