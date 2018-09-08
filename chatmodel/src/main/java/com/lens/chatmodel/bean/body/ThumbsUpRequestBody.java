package com.lens.chatmodel.bean.body;

public class ThumbsUpRequestBody {

    private String photoSerno;
    private String photoUserId;
    private String photoUserName;
    private String thumbsUserId;
    private String thumbsUserName;

    private ThumbsUpRequestBody(Builder builder) {
        photoSerno = builder.photoSerno;
        photoUserId = builder.photoUserId;
        photoUserName = builder.photoUserName;
        thumbsUserId = builder.thumbsUserId;
        thumbsUserName = builder.thumbsUserName;
    }


    public static final class Builder {
        private String photoSerno;
        private String photoUserId;
        private String photoUserName;
        private String thumbsUserId;
        private String thumbsUserName;

        public Builder() {
        }

        public Builder photoSerno(String val) {
            photoSerno = val;
            return this;
        }

        public Builder photoUserId(String val) {
            photoUserId = val;
            return this;
        }

        public Builder photoUserName(String val) {
            photoUserName = val;
            return this;
        }

        public Builder thumbsUserId(String val) {
            thumbsUserId = val;
            return this;
        }

        public Builder thumbsUserName(String val) {
            thumbsUserName = val;
            return this;
        }

        public ThumbsUpRequestBody build() {
            return new ThumbsUpRequestBody(this);
        }
    }
}
