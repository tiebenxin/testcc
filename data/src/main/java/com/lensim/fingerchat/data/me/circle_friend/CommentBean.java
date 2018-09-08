package com.lensim.fingerchat.data.me.circle_friend;

import java.io.Serializable;

/**
 * 朋友圈评论实体类
 */
public class CommentBean implements Serializable{

    private Long commentId;
    private String commentSerno;
    private String photoSerno;
    private String creatorUserid;
    private String creatorUsername;
    private String commentUserid;
    private String commentUsername;
    private String commentContent;
    private long commentTime;
    private String commentUserid2;
    private String commentUsername2;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentSerno() {
        return commentSerno;
    }

    public void setCommentSerno(String commentSerno) {
        this.commentSerno = commentSerno;
    }

    public String getPhotoSerno() {
        return photoSerno;
    }

    public void setPhotoSerno(String photoSerno) {
        this.photoSerno = photoSerno;
    }

    public String getCreatorUserid() {
        return creatorUserid;
    }

    public void setCreatorUserid(String creatorUserid) {
        this.creatorUserid = creatorUserid;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getCommentUserid() {
        return commentUserid;
    }

    public void setCommentUserid(String commentUserid) {
        this.commentUserid = commentUserid;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentUserid2() {
        return commentUserid2;
    }

    public void setCommentUserid2(String commentUserid2) {
        this.commentUserid2 = commentUserid2;
    }

    public String getCommentUsername2() {
        return commentUsername2;
    }

    public void setCommentUsername2(String commentUsername2) {
        this.commentUsername2 = commentUsername2;
    }
}
