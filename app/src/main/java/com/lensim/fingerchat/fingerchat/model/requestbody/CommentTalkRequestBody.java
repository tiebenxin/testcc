package com.lensim.fingerchat.fingerchat.model.requestbody;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class CommentTalkRequestBody {
    private String commentUserId;
    private String commentUserName;
    private String content;
    private String creatorUserId;
    private String creatorUserName;
    private String photoSerno;

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getCreatorUserName() {
        return creatorUserName;
    }

    public void setCreatorUserName(String creatorUserName) {
        this.creatorUserName = creatorUserName;
    }

    public String getPhotoSerno() {
        return photoSerno;
    }

    public void setPhotoSerno(String photoSerno) {
        this.photoSerno = photoSerno;
    }


    /*
    private CommentTalkRequestBody(Builder builder){
        commentUserId = builder.commentUserId;
        commentUserName = builder.commentUserName;
        content = builder.content;
        creatorUserId = builder.creatorUserId;
        creatorUserName = builder.creatorUserName;
        photoSerno = builder.photoSerno;
    }

    public static final class Builder{
        private String commentUserId;
        private String commentUserName;
        private String content;
        private String creatorUserId;
        private String creatorUserName;
        private String photoSerno;

        public Builder(){}

        public Builder commentUserId(String val){
            commentUserId = val;
            return this;
        }

        public Builder commentUserName(String val){
            commentUserName = val;
            return this;
        }

        public Builder content(String val){
            content = val;
            return this;
        }
        public Builder creatorUserId(String val){
            creatorUserId = val;
            return this;
        }

        public Builder creatorUserName(String val){
            creatorUserName = val;
            return this;
        }

        public Builder photoSerno(String val){
            photoSerno = val;
            return this;
        }
        public CommentTalkRequestBody build(){
            return new CommentTalkRequestBody(this);
        }
    }*/
}
