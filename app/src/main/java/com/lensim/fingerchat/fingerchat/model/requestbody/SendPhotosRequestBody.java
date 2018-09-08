package com.lensim.fingerchat.fingerchat.model.requestbody;

/**
 * 发朋友圈参数封装
 */
public class SendPhotosRequestBody {
    private String creatorUserId;
    private String creatorUserName;
    private String photoContent;
    private int photoFileNum;
    private String photoFilenames;
    private String photoUrl;

    private SendPhotosRequestBody(Builder builder) {
        creatorUserId = builder.creatorUserId;
        creatorUserName = builder.creatorUserName;
        photoContent = builder.photoContent;
        photoFileNum = builder.photoFileNum;
        photoFilenames = builder.photoFilenames;
        photoUrl = builder.photoUrl;
    }


    public static final class Builder {
        private String creatorUserId;
        private String creatorUserName;
        private String photoContent;
        private int photoFileNum;
        private String photoFilenames;
        private String photoUrl;

        public Builder() {
        }

        public Builder creatorUserId(String val) {
            creatorUserId = val;
            return this;
        }

        public Builder creatorUserName(String val) {
            creatorUserName = val;
            return this;
        }

        public Builder photoContent(String val) {
            photoContent = val;
            return this;
        }

        public Builder photoFileNum(int val) {
            photoFileNum = val;
            return this;
        }

        public Builder photoFilenames(String val) {
            photoFilenames = val;
            return this;
        }

        public Builder photoUrl(String val) {
            photoUrl = val;
            return this;
        }

        public SendPhotosRequestBody build() {
            return new SendPhotosRequestBody(this);
        }
    }
}
