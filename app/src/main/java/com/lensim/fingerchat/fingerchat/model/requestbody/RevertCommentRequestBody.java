package com.lensim.fingerchat.fingerchat.model.requestbody;

public class RevertCommentRequestBody {

    private String commentUserId;
    private String commentUserId2;
    private String commentUserName;
    private String commentUserName2;
    private String content;
    private String creatorUserId;
    private String creatorUserName;
    private String photoSerno;

    private RevertCommentRequestBody(Builder builder) {
        commentUserId = builder.commentUserId;
        commentUserId2 = builder.commentUserId2;
        commentUserName = builder.commentUserName;
        commentUserName2 = builder.commentUserName2;
        content = builder.content;
        creatorUserId = builder.creatorUserId;
        creatorUserName = builder.creatorUserName;
        photoSerno = builder.photoSerno;
    }


    public static final class Builder {
        private String commentUserId;
        private String commentUserId2;
        private String commentUserName;
        private String commentUserName2;
        private String content;
        private String creatorUserId;
        private String creatorUserName;
        private String photoSerno;

        public Builder() {
        }

        public Builder commentUserId(String val) {
            commentUserId = val;
            return this;
        }

        public Builder commentUserId2(String val) {
            commentUserId2 = val;
            return this;
        }

        public Builder commentUserName(String val) {
            commentUserName = val;
            return this;
        }

        public Builder commentUserName2(String val) {
            commentUserName2 = val;
            return this;
        }

        public Builder content(String val) {
            content = val;
            return this;
        }

        public Builder creatorUserId(String val) {
            creatorUserId = val;
            return this;
        }

        public Builder creatorUserName(String val) {
            creatorUserName = val;
            return this;
        }

        public Builder photoSerno(String val) {
            photoSerno = val;
            return this;
        }

        public RevertCommentRequestBody build() {
            return new RevertCommentRequestBody(this);
        }
    }
}
